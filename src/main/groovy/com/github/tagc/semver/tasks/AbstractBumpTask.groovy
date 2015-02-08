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
import com.github.tagc.semver.version.BaseVersion
import com.github.tagc.semver.version.Version

/**
 * Abstract class for tasks that perform bumps on the version data
 * represented by a project's version data file.
 *
 * @author davidfallah
 * @since v0.5.0
 */
protected class AbstractBumpTask extends DefaultTask {

    private static final TASK_GROUP = 'semver'
    private static final TASK_DESCRIPTION = 'Performs a bump of the project version.'

    private final versionBumpCategory

    /**
     * Whether to force a version update if not on
     * a Git release or hotfix branch.
     */
    @Input
    @Optional
    boolean forceBump

    /**
     * The file to interpret the current version from.
     */
    @InputFile
    File versionFileIn

    /**
     * The file to write the new version data to.
     */
    @OutputFile
    File versionFileOut

    /**
     * Constructs an {@code AbstractBumpTask} without specifying any version category to bump the
     * current version by when executed.
     * <p>
     * Subclasses using this constructor should override {@link #getBumpCategory()}.
     */
    protected AbstractBumpTask() {
        this.group = TASK_GROUP
        this.description = TASK_DESCRIPTION
        this.versionBumpCategory = null
    }

    /**
     * Constructs an {@code AbstractBumpTask} that will bump the current version by {@code bumpCategory}
     * when executed.
     *
     * @param bumpCategory the category to bump the current project version by
     */
    protected AbstractBumpTask(Version.Category bumpCategory) {
        this.group = TASK_GROUP
        this.description = TASK_DESCRIPTION
        this.versionBumpCategory = bumpCategory
    }

    @TaskAction
    void start() {
        checkValidGitBranch()

        def versionParser = BaseVersion.Parser.instance
        def currVersion = versionParser.parse(getVersionFileIn())
        def bumpedVersion = currVersion.bumpByCategory(bumpCategory)

        getVersionFileOut().text = versionParser.parseAndReplace(getVersionFileIn(), bumpedVersion)
        project.version = bumpedVersion

        logger.debug "Bumping project version ($currVersion -> $bumpedVersion)."
    }

    /**
     * Checks that the currently checked-out Git branch is a valid branch for bumping
     * the current version.
     * <p>
     * This method can be overridden by subclasses of this task class.
     *
     * @throws IllegalStateException if the currently checked-out Git branch is not valid
     */
    protected checkValidGitBranch() {
        def branchDetector = new GitBranchDetector(project)
        if (!branchDetector.isOnReleaseBranch() && !branchDetector.isOnHotfixBranch()) {
            if (isForceBump()) {
                logger.debug "On branch $branchDetector.branch but forcing version bump anyway."
            } else {
                throw new IllegalStateException(
                "Cannot bump version when not on release or hotfix branch (set 'forceBump' true to override).")
            }
        }
    }

    /**
     * Returns the {@code Version.Category} to bump the current version by.
     * <p>
     * Any subclass of this task class that does not construct this class with a non-null
     * instance of {@code Version.Category} should override this method.
     *
     * @return the version category to bump the current version by
     * @throws IllegalStateException if the version bump category is not set
     */
    protected Version.Category getBumpCategory() {
        if (versionBumpCategory == null) {
            throw new IllegalStateException('No category is specified to bump by.')
        }

        versionBumpCategory
    }
}
