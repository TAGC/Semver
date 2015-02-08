package com.github.tagc.semver.projectstrategy

import org.gradle.api.Project

import com.github.tagc.semver.version.Version

/**
 * Manages the selection of an instance of {@code Version} to set
 * as the version of the project that this plugin is applied to by
 * delegating the responsibility to a chain of
 * {@code ProjectVersionSelectionStrategy} instances.
 *
 * @author davidfallah
 * @since 0.5.0
 */
@Singleton
class VersionSelectionManager {

    private static final ProjectVersionSelectorStrategy[] STRATEGY_CHAIN = [
        new MasterVersionSelectorStrategy(),
        new HotfixReleaseSelectorStrategy(),
        new DefaultVersionSelectorStrategy(),
    ]

    /**
     * Returns an instance of {@code Version} to set for the project
     * given the specified parameters or {@code null} if no appropriate
     * instance could be computed.
     *
     * @param project the project to set the version for
     * @param rawVersion an instance of {@code Version} derived from a
     *      version data file
     * @param snapshotBump the category to assume the snapshot instance
     *      of the raw version should be bumped by if necessary
     * @return the chosen instance of {@code Version} or {@code null}
     */
    Version selectVersionForProject(Project project, Version rawVersion,
            Version.Category snapshotBump) {

        if (STRATEGY_CHAIN) {
            def versionSelectionData = new VersionSelectionData(project, rawVersion, snapshotBump)
            return STRATEGY_CHAIN.head().handleProjectVersionSelection(versionSelectionData,
                    STRATEGY_CHAIN.tail())
        }

        return null
    }
}
