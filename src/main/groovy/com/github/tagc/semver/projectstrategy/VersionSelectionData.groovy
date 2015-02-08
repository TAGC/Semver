package com.github.tagc.semver.projectstrategy

import groovy.transform.PackageScope

import org.gradle.api.Project

import com.github.tagc.semver.version.Version

/**
 * A class for producing immutable objects that capture the information
 * required by instances of {@code ProjectVersionSelectionStrategy} to
 * choose which {@code Version} to select for the project that this plugin
 * is applied to.
 *
 * @author davidfallah
 * @since v0.5.0
 */
@PackageScope
class VersionSelectionData {
    /**
     * The project that the version is being selected for.
     */
    final Project project

    /**
     * An instance of {@code Version} interpreted from a version data file.
     */
    final Version rawVersion

    /**
     * The category to assume the snapshot instance of the raw version
     * should be bumped by.
     */
    final Version.Category snapshotBump

    VersionSelectionData(Project project, Version rawVersion, Version.Category snapshotBump) {
        this.project = project
        this.rawVersion = rawVersion
        this.snapshotBump = snapshotBump
    }
}
