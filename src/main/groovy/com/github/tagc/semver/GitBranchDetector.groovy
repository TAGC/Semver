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
import org.ajoberstar.grgit.exception.GrgitException
import org.gradle.api.Project

/**
 * This entity is used to attempt to retrieve the name of the currently
 * checked-out Git branch within a Git repository.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class GitBranchDetector {

    private static final String MASTER_BRANCH = 'master'

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
        try {
            repo = Grgit.open(project.file("$project.projectDir"))
        } catch (GrgitException e) {
            throw e
        }
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
        try {
            repo = Grgit.open(repoDir)
        } catch (GrgitException e) {
            throw e
        }
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
        return getBranch() == MASTER_BRANCH
    }
}
