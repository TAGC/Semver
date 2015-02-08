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

package com.github.tagc.semver.projectstrategy

import groovy.transform.PackageScope

import com.github.tagc.semver.GitBranchDetector
import com.github.tagc.semver.SemVerPlugin
import com.github.tagc.semver.version.Version

/**
 * Handles version selection for projects that are on a hotfix or release Git branch.
 *
 * @author davidfallah
 * @since v0.5.0
 */
@PackageScope
class HotfixReleaseSelectorStrategy extends AbstractProjectVersionSelectorStrategy {

    /*
     * We use the release version of the raw version if it is up-to-date with the branch version
     * or the snapshot version if it isn't.
     */
    @Override
    Version chooseVersion(VersionSelectionData versionSelectionData) {
        def project = versionSelectionData.project
        def rawVersion = versionSelectionData.rawVersion
        def branchDetector = new GitBranchDetector(project)
        Version branchVersion = branchDetector.tryParseBranchVersion()
        if (branchVersion == null) {
            project.logger.warn 'Unable to parse version associated with current Git branch' + \
            " ($branchDetector.branch)."

            return rawVersion.toDevelop()
        } else if (branchVersion.equals(rawVersion)) {

            return rawVersion.toRelease()
        } else if (branchVersion > rawVersion) {
            project.gradle.taskGraph.whenReady { taskGraph ->
                def modifiedTasksPresent = taskGraph.allTasks.findAll { task ->
                    task.hasProperty(SemVerPlugin.getModifiesVersionIndicatorProperty()) \
                        && task."${SemVerPlugin.getModifiesVersionIndicatorProperty()}"
                }.size() > 0

                if (!modifiedTasksPresent) {
                    project.logger.quiet "You should use :${SemVerPlugin.getBumpTaskName()} to match" + \
                        " the version of the current Git branch ($branchDetector.branch)."
                }
            }

            return rawVersion.toDevelop()
        }

        project.logger.warn "Current project version ($rawVersion) is set greater than" + \
            " the version of the current Git branch ($branchDetector.branch)."

        return rawVersion.toDevelop()
    }

    @Override
    protected boolean canChooseVersionForGitBranch(VersionSelectionData versionSelectionData) {
        def project = versionSelectionData.project
        def branchDetector = new GitBranchDetector(project)

        branchDetector.isOnHotfixBranch() || branchDetector.isOnReleaseBranch()
    }
}
