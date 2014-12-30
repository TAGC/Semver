package com.github.tagc.semver

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
}
