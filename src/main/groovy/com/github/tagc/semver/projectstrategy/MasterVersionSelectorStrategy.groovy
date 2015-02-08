package com.github.tagc.semver.projectstrategy

import groovy.transform.PackageScope

import com.github.tagc.semver.GitBranchDetector
import com.github.tagc.semver.version.Version

/**
 * Handles version selection for projects that are on the master Git branch.
 *
 * @author davidfallah
 * @since v0.5.0
 */
@PackageScope
class MasterVersionSelectorStrategy extends AbstractProjectVersionSelectorStrategy {

    @Override
    Version chooseVersion(VersionSelectionData versionSelectionData) {
        def rawVersion = versionSelectionData.rawVersion
        return rawVersion.toRelease()
    }

    @Override
    protected boolean canChooseVersionForGitBranch(VersionSelectionData versionSelectionData) {
        def project = versionSelectionData.project
        def branchDetector = new GitBranchDetector(project)

        branchDetector.isOnMasterBranch()
    }
}
