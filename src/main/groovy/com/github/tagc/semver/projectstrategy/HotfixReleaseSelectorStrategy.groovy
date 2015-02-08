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
            " ($branchDetector.branch)"

            return rawVersion.toDevelop()
        } else if (branchVersion.equals(rawVersion)) {

            return rawVersion.toRelease()
        } else if (branchVersion > rawVersion) {
            project.logger.quiet "You should use :${SemVerPlugin.getBumpTaskName()} to match" + \
            " the version of the current Git branch ($branchDetector.branch)"

            return rawVersion.toDevelop()
        }

        project.logger.warn "Current project version ($rawVersion) is set greater than" + \
            " the version of the current Git branch ($branchDetector.branch)"

        return rawVersion.toDevelop()
    }

    @Override
    protected boolean canChooseVersionForGitBranch(VersionSelectionData versionSelectionData) {
        def project = versionSelectionData.project
        def branchDetector = new GitBranchDetector(project)

        branchDetector.isOnHotfixBranch() || branchDetector.isOnReleaseBranch()
    }
}
