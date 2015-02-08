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

import com.github.tagc.semver.GitBranchDetector
import com.github.tagc.semver.SemVerException
import com.github.tagc.semver.SemVerPlugin
import com.github.tagc.semver.version.Version

/**
 * Tries to bump the current project version based on the version
 * specified within the current hotfix or release Git branch that
 * the project is on.
 * <p>
 * If a {@link com.github.tagc.semver.Version version} cannot be
 * interpreted for the Git branch, this task will fail.
 * <p>
 * This method modifies the version file data, so the changes
 * made are persistent.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class BumpTask extends AbstractBumpTask {

    BumpTask() {
        this.description = 'Bumps the project version to match the version in the current release or hotfix branch.'

        /*
         * Skip if the branch version and raw project version are the same.
         */
        onlyIf {
            GitBranchDetector branchDetector = new GitBranchDetector(project)

            try {
                return branchDetector.tryParseBranchVersion() != project.version.unwrap()
            } catch (IllegalStateException e) {

                /*
                 * Allow task to be run so informative error message can be presented to user.
                 */
                return true
            }
        }
    }

    protected checkValidGitBranch() {
        def branchDetector = new GitBranchDetector(project)
        if (!branchDetector.isOnReleaseBranch() && !branchDetector.isOnHotfixBranch()) {
            throw new IllegalStateException(
                "Cannot bump version with :${SemVerPlugin.getBumpTaskName()} when not on" + \
                ' release or hotfix branch.')
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code BumpTask} overrides this method to try to figure out which category
     * the current project should be bumped by based on the name of the currently
     * checked-out hotfix or release branch.
     *
     * @throws GrgitException if no Git repository exists for the project
     * @throws IllegalStateException if the currently checked-out branch is not a
     *         hotfix or release branch
     * @throws RuntimeException if no version information could be extracted from
     *         the Git branch
     * @throws RuntimeException if the version associated with the Git branch is
     *         not a valid distance from the current project version
     */
    @Override
    protected Version.Category getBumpCategory() {
        GitBranchDetector branchDetector = new GitBranchDetector(project)

        def branchVersion = branchDetector.tryParseBranchVersion()
        if (branchVersion == null) {
            def bumpPatchTaskName = SemVerPlugin.getBumpPatchTaskName()
            def bumpMinorTaskName = SemVerPlugin.getBumpMinorTaskName()
            def bumpMajorTaskName = SemVerPlugin.getBumpMajorTaskName()

            throw new SemVerException('Could not figure out how to bump the project version based on ' + \
                "the current Git branch ($branchDetector.branch). Try manually bumping the project " + \
                "using :$bumpPatchTaskName, :$bumpMinorTaskName or :$bumpMajorTaskName instead.")
        }

        logger.debug "Interpreting version $branchVersion for Git branch ${branchDetector.branch}."

        def rawProjectVersion = project.version.unwrap()
        logger.debug "Raw project version = $rawProjectVersion"

        def bumpCategory = rawProjectVersion.distanceFrom(branchVersion)
        if (bumpCategory == null) {
            throw new SemVerException('The version associated with the current Git branch ' + \
                "($branchDetector.branch) isn't considered valid given the current project " + \
                "version ($project.version). Set 'forceBump' true to override.")
        }

        return bumpCategory
    }
}
