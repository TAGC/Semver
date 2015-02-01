package com.github.tagc.semver.tasks

import groovy.transform.PackageScope

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import com.github.tagc.semver.Version

/**
 * Performs a major bump on the version represented by a
 * project's version data file.
 *
 * This method modifies the version file data, so the changes
 * made are persistent.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class BumpMajorTask extends DefaultTask {

    public BumpMajorTask() {
        this.group = 'semver'
        this.description = 'Performs a major bump of the project version.'
    }

    @InputFile
    def File versionFileIn

    @OutputFile
    def File versionFileOut

    @TaskAction
    void start() {
        try {
            def versionParser = Version.Parser.getInstance()
            def currVersion = versionParser.parse(getVersionFileIn())
            def bumpedVersion = currVersion.bumpMajor()

            logger.info versionParser.parseAndReplace(getVersionFileIn(), bumpedVersion)
            getVersionFileOut().text = versionParser.parseAndReplace(getVersionFileIn(), bumpedVersion)

            logger.debug "Bumping project version ($currVersion -> $bumpedVersion)"
        } catch (IllegalArgumentException e) {
            throw e
        }
    }

    @PackageScope
    getOutput() {
        return "Project version: $project.version"
    }
}
