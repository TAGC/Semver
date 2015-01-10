package com.github.tagc.semver.tasks

import groovy.transform.PackageScope

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Prints the string representation of the current project version to standard output.
 *
 * @author davidfallah
 * @since 0.4.0
 */
class PrintVersionTask extends DefaultTask {

    public PrintVersionTask() {
        this.group = 'semver'
        this.description = 'Prints the current project version.'
    }

    @TaskAction
    void start() {
        println getOutput()
    }

    @PackageScope
    getOutput() {
        return "Project version: $project.version"
    }
}
