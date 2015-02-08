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
