package com.github.tagc.semver

import org.ajoberstar.grgit.Grgit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

class SemVerPluginSpec extends Specification {

    private static final String EXTENSION_NAME = "semver"
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
        Grgit.init(dir: project.projectDir)
        plugin.apply(project)
        project.semver.versionFilePath = url.getPath()
        project.evaluate()

        then:
        project.version == new Version(1,2,3,true)
        
        cleanup:
        assert new File("$project.projectDir/.git").deleteDir()
    }
}
