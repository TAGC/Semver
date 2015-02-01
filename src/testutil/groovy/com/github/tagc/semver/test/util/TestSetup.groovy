package com.github.tagc.semver.test.util

import com.github.tagc.semver.Version

/**
 * Provides common test setup configurations for use by test specifications.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class TestSetup {

    private static versionFilePaths = [
        'spaced v0_0_0.properties',
        'v0_0_0.properties',
        'v1_2_3.properties'
    ]

    private static expectedReleases = [
        new Version(0,0,0,true),
        new Version(0,0,0,true),
        new Version(1,2,3,true)
    ]

    private static expectedPatchSnapshots = [
        new Version(0,0,1,false),
        new Version(0,0,1,false),
        new Version(1,2,4,false)
    ]

    private static expectedMinorSnapshots = [
        new Version(0,1,0,false),
        new Version(0,1,0,false),
        new Version(1,3,0,false)
    ]

    private static expectedMajorSnapshots = [
        new Version(1,0,0,false),
        new Version(1,0,0,false),
        new Version(2,0,0,false)
    ]

    private TestSetup() {
        assert false : "Should not be instantiable"
    }

    static String[] getTestVersionFilePaths() {
        return versionFilePaths
    }

    static Version[] getTestExpectedReleases() {
        return expectedReleases
    }

    static Version[] getTestExpectedPatchSnapshots() {
        return expectedPatchSnapshots
    }

    static Version[] getTestExpectedMinorSnapshots() {
        return expectedMinorSnapshots
    }

    static Version[] getTestExpectedMajorSnapshots() {
        return expectedMajorSnapshots
    }

    static Version[] getTestExpectedPatchReleases() {
        return expectedPatchSnapshots.collect { it.toRelease() }
    }

    static Version[] getTestExpectedMinorReleases() {
        return expectedMinorSnapshots.collect { it.toRelease() }
    }

    static Version[] getTestExpectedMajorReleases() {
        return expectedMajorSnapshots.collect { it.toRelease() }
    }
}
