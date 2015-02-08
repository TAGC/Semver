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

package com.github.tagc.semver

import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project

import com.github.tagc.semver.version.BaseVersion
import com.github.tagc.semver.version.Version

/**
 * This entity is used to attempt to retrieve the name of the currently
 * checked-out Git branch within a Git repository.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class GitBranchDetector {

    private static final String MASTER_BRANCH = ~/master/
    private static final String RELEASE_BRANCH = ~/release.*/
    private static final String HOTFIX_BRANCH = ~/hotfix.*/

    private final Grgit repo

    /**
     * Instantiates a {@code GitBranchDetector} that attempts to
     * open a Git repository in the project base directory.
     *
     * @param project a Gradle project
     * @throws GrgitException if no Git repository exists in the project
     *         base directory to be opened
     */
    GitBranchDetector(Project project) {
        repo = Grgit.open(project.file("$project.projectDir"))
    }

    /**
     * Instantiates a {@code GitBranchDetector} that attempts to
     * open a Git repository in the given directory.
     *
     * @param repoDir the file location of the Git repository to open
     * @throws GrgitException if no Git repository exists in the given
     *         file location to be opened
     */
    GitBranchDetector(File repoDir) {
        repo = Grgit.open(repoDir)
    }

    /**
     * Returns the name of the currently checked-out Git branch in
     * the Git repository being tracked.
     *
     * @return the current Git branch name
     */
    String getBranch() {
        return repo.branch.current.name
    }

    /**
     * Returns whether the currently checked-out Git branch in the
     * Git repository being tracked is the master branch.
     *
     * @return {@code true} if the currently checked-out branch is the master branch
     */
    boolean isOnMasterBranch() {
        return branch ==~ MASTER_BRANCH
    }

    /**
     * Returns whether the currently checked-out Git branch in the
     * Git repository being tracked is a release branch.
     *
     * @return {@code true} if the currently checked-out branch is a release branch
     */
    boolean isOnReleaseBranch() {
        return branch ==~ RELEASE_BRANCH
    }

    /**
     * Returns whether the currently checked-out Git branch in the
     * Git repository being tracked is a hotfix branch.
     *
     * @return {@code true} if the currently checked-out branch is a hotfix branch
     */
    boolean isOnHotfixBranch() {
        return branch ==~ HOTFIX_BRANCH
    }

    /**
     * Tries to parse the version of the currently checked-out release or hotfix Git branch.
     * <p>
     * Before calling this method, it is advised to check that the project is on a release or
     * hotfix branch.
     *
     * @return an instance of {@code Version} matching the branch version if it could be parsed,
     *      otherwise {@code null}
     * @throws IllegalStateException if the project is not on a hotfix or release Git branch
     * @see {@link #isOnReleaseBranch}
     * @see {@link #isOnHotfixBranch}
     */
    Version tryParseBranchVersion() {
        def versionParser = BaseVersion.Parser.instance

        if (isOnReleaseBranch() || isOnHotfixBranch()) {
            return versionParser.parse(branch, false)
        }

        throw new IllegalStateException('Not on release or hotfix Git branch')
    }
}
