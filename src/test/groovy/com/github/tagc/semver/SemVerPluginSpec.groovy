package com.github.tagc.semver

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.exception.GrgitException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

class SemVerPluginSpec extends Specification {

    private static final String EXTENSION_NAME = SemVerPlugin.EXTENSION_NAME
    Project project
    Plugin<Project> plugin

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    def "SemVer plugin extension should exist after applying project"() {
        assert project.extensions.findByName(EXTENSION_NAME) == null
        plugin.apply(project)

        expect:
        project.extensions.findByName(EXTENSION_NAME) != null
    }

    def "SemVer plugin should set project.version property after reading valid version file"() {
        given:
        URL url = Thread.currentThread().getContextClassLoader().getResource("v1_2_3.properties")
        File versionFile = new File(url.getPath())
        assert versionFile.text.replaceAll("\\s","") == "version='1.2.3'"

        when:
        createGitDirectory(project)
        plugin.apply(project)
        project.semver.versionFilePath = url.getPath()
        project.evaluate()

        then:
        project.version == new Version(1,2,3,true)
        
        cleanup:
        assert cleanupGitDirectory(project)
    }
    
    def "SemVer plugin should set project.version to appropriate snapshot version"() {
        given:
        URL url = Thread.currentThread().getContextClassLoader().getResource("v1_2_3.properties")
        File versionFile = new File(url.getPath())
        assert versionFile.text.replaceAll("\\s","") == "version='1.2.3'"
        
        when:
        createGitDirectory(project)
        checkoutBranch(project, 'develop')
        plugin.apply(project)
        project.semver.versionFilePath = url.getPath()
        project.semver.snapshotBump = Version.Category.PATCH
        project.evaluate()

        then:
        project.version == new Version(1,2,4,false)
        
        cleanup:
        assert cleanupGitDirectory(project)
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
