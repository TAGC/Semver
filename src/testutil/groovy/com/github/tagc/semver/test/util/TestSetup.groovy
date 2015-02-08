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

import com.github.tagc.semver.version.Version
import com.github.tagc.semver.version.VersionFactory


/**
 * Provides common test setup configurations for use by test specifications.
 *
 * @author davidfallah
 */
class TestSetup {

    /**
     * Enumeration of different common Git branch types for testing with.
     *
     * @author davidfallah
     */
    enum TestBranchType {
        MASTER {
            String getName(Version version) {
                return "master"
            }
        },
        DEVELOP {
            String getName(Version version) {
                return "develop"
            }
        },
        RELEASE {
            String getName(Version version) {
                return "release-${version.toRelease()}"
            }
        },
        BAD_RELEASE {
            String getName(Version version) {
                return "release/no-version-data"
            }
        },
        HOTFIX {
            String getName(Version version) {
                return "hotfix-${version.toRelease()}"
            }
        },
        BAD_HOTFIX {
            String getName(Version version) {
                return "hotfix/no-version-data"
            }
        },

        abstract String getName(Version version)
    }

    private static final VERSION_FILE_PATHS = [
        'spaced v0_0_0.properties',
        'v0_0_0.properties',
        'v1_2_3.properties',
        'v3_5.properties'
    ]

    private static final EXPECTED_RELEASES = [
        VersionFactory.makeBaseVersion(0,0,0,true),
        VersionFactory.makeBaseVersion(0,0,0,true),
        VersionFactory.makeBaseVersion(1,2,3,true),
        VersionFactory.makeBaseVersion(3,5,0,true),
    ]

    private static final EXPECTED_PATCH_SNAPSHOTS = [
        VersionFactory.makeBaseVersion(0,0,1,false),
        VersionFactory.makeBaseVersion(0,0,1,false),
        VersionFactory.makeBaseVersion(1,2,4,false),
        VersionFactory.makeBaseVersion(3,5,1,false),
    ]

    private static final EXPECTED_MINOR_SNAPSHOTS = [
        VersionFactory.makeBaseVersion(0,1,0,false),
        VersionFactory.makeBaseVersion(0,1,0,false),
        VersionFactory.makeBaseVersion(1,3,0,false),
        VersionFactory.makeBaseVersion(3,6,0,false),
    ]

    private static final EXPECTED_MAJOR_SNAPSHOTS = [
        VersionFactory.makeBaseVersion(1,0,0,false),
        VersionFactory.makeBaseVersion(1,0,0,false),
        VersionFactory.makeBaseVersion(2,0,0,false),
        VersionFactory.makeBaseVersion(4,0,0,false),
    ]

    private TestSetup() {
        assert false : "Should not be instantiable"
    }

    static String[] getTestVersionFilePaths() {
        return VERSION_FILE_PATHS
    }

    static Version[] getTestExpectedReleases() {
        return EXPECTED_RELEASES
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
        return EXPECTED_PATCH_SNAPSHOTS
    }

    static Version[] getTestExpectedMinorSnapshots() {
        return EXPECTED_MINOR_SNAPSHOTS
    }

    static Version[] getTestExpectedMajorSnapshots() {
        return EXPECTED_MAJOR_SNAPSHOTS
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
        return EXPECTED_PATCH_SNAPSHOTS.collect { it.toRelease() }
    }

    static Version[] getTestExpectedMinorReleases() {
        return EXPECTED_MINOR_SNAPSHOTS.collect { it.toRelease() }
    }

    static Version[] getTestExpectedMajorReleases() {
        return EXPECTED_MAJOR_SNAPSHOTS.collect { it.toRelease() }
    }

    static String[] getTestExpectedBranches(Version.Category category, TestBranchType branchType) {
        println "Category: $category, branchType: $branchType"
        println "Expected releases: ${getTestExpectedReleasesForCategory(category)}"
        return getTestExpectedReleasesForCategory(category).collect { version -> "${branchType.getName(version)}" }
    }
}
