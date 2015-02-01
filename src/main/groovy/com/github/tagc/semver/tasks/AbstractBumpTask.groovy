package com.github.tagc.semver.tasks

import groovy.transform.PackageScope

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import com.github.tagc.semver.Version

/**
 * Abstract class for tasks that perform bumps on the version data
 * represented by a project's version data file.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class AbstractBumpTask extends DefaultTask {

    private final bumpCategory

    public AbstractBumpTask(Version.Category bumpCategory) {
        this.group = 'semver'
        this.description = 'Performs a bump of the project version.'

        this.bumpCategory = bumpCategory
    }

    @InputFile
    def File versionFileIn

    @OutputFile
    def File versionFileOut

    @TaskAction
    void start() {
        try {
            def versionParser = Version.Parser.instance
            def currVersion = versionParser.parse(getVersionFileIn())
            def bumpedVersion = currVersion.bumpByCategory(bumpCategory)

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
