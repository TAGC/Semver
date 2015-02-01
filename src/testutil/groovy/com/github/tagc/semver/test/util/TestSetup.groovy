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

    static Version[] getTestExpectedSnapshotsForCategory(Version.Category category) {
        switch(category) {
            case Version.Category.MAJOR:
                return getTestExpectedMajorSnapshots()
            case Version.Category.MINOR:
                return getTestExpectedMinorSnapshots()
            case Version.Category.PATCH:
                return getTestExpectedPatchSnapshots()
            default:
                throw new IllegalArgumentException("Invalid version category: $category")
        }
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

    static Version[] getTestExpectedReleasesForCategory(Version.Category category) {
        switch(category) {
            case Version.Category.MAJOR:
                return getTestExpectedMajorReleases()
            case Version.Category.MINOR:
                return getTestExpectedMinorReleases()
            case Version.Category.PATCH:
                return getTestExpectedPatchReleases()
            default:
                throw new IllegalArgumentException("Invalid version category: $category")
        }
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
