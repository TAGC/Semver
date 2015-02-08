
package com.github.tagc.semver.projectstrategy

import groovy.transform.PackageScope

import com.github.tagc.semver.version.Version

/**
 * An abstract class that provides default behaviour for concrete instances of
 * {@code ProjectVersionSelectorStrategy}.
 *
 * @author davidfallah
 * @since v0.5.0
 */
@PackageScope
abstract class AbstractProjectVersionSelectorStrategy implements ProjectVersionSelectorStrategy {

    @Override
    Version handleProjectVersionSelection(VersionSelectionData versionSelectionData,
            ProjectVersionSelectorStrategy[] successors) {
        if (canChooseVersionForGitBranch(versionSelectionData)) {
            return chooseVersion(versionSelectionData)
        } else if (!successors) {
            return null
        }

        return successors.head().handleProjectVersionSelection(versionSelectionData, successors.tail())
    }

    /**
     * Returns whether this {@code ProjectVersionSelectorStrategy} can choose a
     * {@code Version} to set for {@code project}.
     *
     * @param versionSelectionData an object containing information to allow
     *      a version to be selected
     * @return {@code true} if a {@code Version} can be chosen for the given
     *      Git branch, otherwise {@code false}
     */
    protected abstract boolean canChooseVersionForGitBranch(VersionSelectionData versionSelectionData)
}
