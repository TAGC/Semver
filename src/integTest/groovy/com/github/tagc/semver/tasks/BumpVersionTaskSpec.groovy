package com.github.tagc.semver.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.SemVerPlugin
import com.github.tagc.semver.Version
import com.github.tagc.semver.test.util.TestSetup
import com.github.tagc.semver.test.util.TestUtil

@Unroll
class BumpVersionTaskSpec extends Specification {

    private Project project
    private Plugin plugin

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    def "Bumping project major version should update version file to represent #bumpedVersion"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)

        when:
        TestUtil.evaluateProjectForReleaseTests(plugin, project, versionFileCopy.toURI().toURL())
        def bumpMajorTask = project.tasks.findByName(SemVerPlugin.getBumpMajorTaskName())
        bumpMajorTask.execute()

        then:
        notThrown(Exception)
        Version.Parser.getInstance().parse(versionFileCopy) == bumpedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        bumpedVersion << TestSetup.getTestExpectedMajorReleases()
    }
}
