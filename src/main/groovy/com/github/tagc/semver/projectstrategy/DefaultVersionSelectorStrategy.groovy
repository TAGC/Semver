package com.github.tagc.semver.projectstrategy

import groovy.transform.PackageScope

import com.github.tagc.semver.version.Version
import com.github.tagc.semver.version.VersionFactory

/**
 * Handles version selection for projects if no other
 * {@code ProjectVersionSelectorStrategy} can.
 *
 * @author davidfallah
 * @since v0.5.0
 */
@PackageScope
class DefaultVersionSelectorStrategy extends AbstractProjectVersionSelectorStrategy {

    @Override
    Version chooseVersion(VersionSelectionData versionSelectionData) {
        def rawVersion = versionSelectionData.rawVersion
        def snapshotBump = versionSelectionData.snapshotBump

        getAppropriateSnapshotVersion(rawVersion, snapshotBump)
    }

    @Override
    protected boolean canChooseVersionForGitBranch(VersionSelectionData versionSelectionData) {
        return true
    }

    /*
     * By default, bump by patch.
     */
    private Version getAppropriateSnapshotVersion(Version currVersion, Version.Category snapshotBump) {
        assert currVersion : 'Null currVersion is illegal'

        Version.Category assumedSnapshotBump = snapshotBump
        if (assumedSnapshotBump == null) {
            assumedSnapshotBump = Version.Category.PATCH
        }

        return VersionFactory.makeDecoratedSnapshotBumpedWithCategory(currVersion, assumedSnapshotBump)
    }
}
