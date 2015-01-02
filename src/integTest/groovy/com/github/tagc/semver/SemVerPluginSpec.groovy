package com.github.tagc.semver

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.exception.GrgitException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SemVerPluginSpec extends Specification {

    private static final String EXTENSION_NAME = SemVerPlugin.EXTENSION_NAME
    Project project
    Plugin<Project> plugin

    /*
     *  Test data
     */
    static def versionFiles = [
        'v0_0_0.properties',
        'v1_2_3.properties'
    ]

    static def expectedReleases = [
        new Version(0,0,0,true),
        new Version(1,2,3,true)
    ]

    static def expectedPatchSnapshots = [
        new Version(0,0,1,false),
        new Version(1,2,4,false)
    ]

    static def expectedMinorSnapshots = [
        new Version(0,1,0,false),
        new Version(1,3,0,false)
    ]

    static def expectedMajorSnapshots = [
        new Version(1,0,0,false),
        new Version(2,0,0,false)
    ]

    /*
     *  Test setups
     */
    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    /*
     * Tests
     */
    def "SemVer plugin extension should exist after applying project"() {
        assert project.extensions.findByName(EXTENSION_NAME) == null
        plugin.apply(project)

        expect:
        project.extensions.findByName(EXTENSION_NAME) != null
    }

    def "Release version for version data in #versionFilePath should be #expectedVersion"() {
        given:
        URL url = getVersionFileAsResource(project, versionFilePath)

        when:
        evaluateProjectForReleaseTests(project, url)

        then:
        project.version == expectedVersion

        cleanup:
        assert cleanupGitDirectory(project)

        where:
        versionFilePath << versionFiles
        expectedVersion << expectedReleases
    }

    def "Snapshot version for version data in #versionFilePath should be #expectedVersion when bumping by major"() {
        given:
        URL url = getVersionFileAsResource(project, versionFilePath)

        when:
        evaluateProjectForSnapshotTests(project, url, Version.Category.MAJOR)

        then:
        project.version == expectedVersion

        cleanup:
        assert cleanupGitDirectory(project)

        where:
        versionFilePath << versionFiles
        expectedVersion << expectedMajorSnapshots
    }

    def "Snapshot version for version data in #versionFilePath should be #expectedVersion when bumping by minor"() {
        given:
        URL url = getVersionFileAsResource(project, versionFilePath)

        when:
        evaluateProjectForSnapshotTests(project, url, Version.Category.MINOR)

        then:
        project.version == expectedVersion

        cleanup:
        assert cleanupGitDirectory(project)

        where:
        versionFilePath << versionFiles
        expectedVersion << expectedMinorSnapshots
    }

    def "Snapshot version for version data in #versionFilePath should be #expectedVersion when bumping by patch"() {
        given:
        URL url = getVersionFileAsResource(project, versionFilePath)

        when:
        evaluateProjectForSnapshotTests(project, url, Version.Category.PATCH)

        then:
        project.version == expectedVersion

        cleanup:
        assert cleanupGitDirectory(project)

        where:
        versionFilePath << versionFiles
        expectedVersion << expectedPatchSnapshots
    }

    /*
     * By default assume bump patch.
     */
    def "Snapshot version for version data in #versionFilePath should be #expectedVersion by default"() {
        given:
        URL url = getVersionFileAsResource(project, versionFilePath)

        when:
        evaluateProjectForSnapshotTests(project, url, null)

        then:
        project.version == expectedVersion

        cleanup:
        assert cleanupGitDirectory(project)

        where:
        versionFilePath << versionFiles
        expectedVersion << expectedPatchSnapshots
    }

    private URL getVersionFileAsResource(Project project, String versionFilePath) {
        Thread.currentThread().getContextClassLoader().getResource(versionFilePath)
    }

    private void evaluateProjectForReleaseTests(Project project, URL url) {
        createGitDirectory(project)
        plugin.apply(project)
        project.semver.versionFilePath = url.getPath()
        project.evaluate()
    }

    private void evaluateProjectForSnapshotTests(Project project, URL url, Version.Category bump) {
        createGitDirectory(project)
        checkoutBranch(project, 'develop')
        plugin.apply(project)
        project.semver.versionFilePath = url.getPath()
        project.semver.snapshotBump = bump
        project.evaluate()
    }

    private void createGitDirectory(Project project) {
        Grgit.init(dir: project.projectDir)
    }

    private void checkoutBranch(Project project, String branch) {
        Grgit grgit = Grgit.open(project.projectDir)
        try {
            grgit.checkout(branch: branch, createBranch: true)
        } catch (GrgitException e) {
            grgit.add(patterns: ['.'], update: true)
            grgit.commit(message: "Initial commit")
            grgit.checkout(branch: branch, createBranch: true)
        }
    }

    private boolean cleanupGitDirectory(Project project) {
        new File("$project.projectDir/.git").deleteDir()
    }
}
