/*
 * Copyright 2014-2015 David Fallah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tagc.semver.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import com.github.tagc.semver.GitBranchDetector
import com.github.tagc.semver.Version

/**
 * Abstract class for tasks that perform bumps on the version data
 * represented by a project's version data file.
 *
 * @author davidfallah
 * @since v0.5.0
 */
protected class AbstractBumpTask extends DefaultTask {

    private final bumpCategory

    protected AbstractBumpTask(Version.Category bumpCategory) {
        this.group = 'semver'
        this.description = 'Performs a bump of the project version.'
        this.bumpCategory = bumpCategory
    }

    @Input
    @Optional
    def boolean forceBump

    @InputFile
    def File versionFileIn

    @OutputFile
    def File versionFileOut

    @TaskAction
    void start() {
        try {
            def branchDetector = new GitBranchDetector(project)
            if (!branchDetector.isOnMasterBranch()) {
                if (isForceBump()) {
                    logger.debug "On branch ${branchDetector.getBranch()} but forcing version bump anyway"
                } else {
                    throw new IllegalStateException(
                        "Cannot bump version when not on master branch (set 'forceBump' true to override)")
                }
            }

            def versionParser = Version.Parser.instance
            def currVersion = versionParser.parse(getVersionFileIn())
            def bumpedVersion = currVersion.bumpByCategory(bumpCategory)

            getVersionFileOut().text = versionParser.parseAndReplace(getVersionFileIn(), bumpedVersion)
            project.version = bumpedVersion

            logger.debug "Bumping project version ($currVersion -> $bumpedVersion)"
        } catch (IllegalArgumentException e) {
            throw e
        }
    }
}
