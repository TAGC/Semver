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

package com.github.tagc.semver

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test specification for {@link com.github.tagc.semver.Version Version}.
 *
 * @author davidfallah
 * @since 0.1.0
 */
@Unroll
class VersionSpec extends Specification {

    static exampleVersions = [
        new Version(major:1, minor:2, patch:3),
        new Version(major:0, minor:0, patch:0),
        new Version(major:5, minor:4, patch:3),
        new Version(major:1, minor:16, patch:2),
        new Version(major:4, minor:5, patch:8),
        ]

    def "Version should be constructable by major and retain state"() {
        given:
        def version = new Version(major:1)

        expect:
        version.major == 1
    }

    def "Version should be constructable by major and minor and retain state"() {
        given:
        def version = new Version(major:1, minor:2)

        expect:
        version.major == 1
        version.minor == 2
    }

    def "Version should be constructable by major, minor and patch and retain state"() {
        given:
        def version = new Version(major:1, minor:2, patch:3)

        expect:
        version.major == 1
        version.minor == 2
        version.patch == 3
    }

    def "Version should be constructable with major, minor, patch and release and retain state"() {
        given:
        def devVersion = new Version(major:1, minor:2, patch:3, release:false)
        def releaseVersion = new Version(major:1, minor:2, patch:3, release:true)

        expect:
        !devVersion.isRelease()
        releaseVersion.isRelease()
    }

    def "Version should be constructable with builder and retain state"() {
        given:
        def builder = new Version.Builder()

        when:
        builder.setMajor(1)
        builder.setMinor(2)
        builder.setPatch(3)
        builder.setRelease(true)

        then:
        def version = builder.getVersion()
        version.major == 1
        version.minor == 2
        version.patch == 3
        version.isRelease()
    }

    def "Incrementing major of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major:currMajor, minor:currMinor, patch:currPatch)

        expect:
        currVersion.incrementMajor() == new Version(major:currMajor + 1, minor:currMinor, patch:currPatch)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Incrementing minor of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major:currMajor, minor:currMinor, patch:currPatch)

        expect:
        currVersion.incrementMinor() == new Version(major:currMajor, minor:currMinor + 1, patch:currPatch)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Incrementing patch of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major:currMajor, minor:currMinor, patch:currPatch)

        expect:
        currVersion.incrementPatch() == new Version(major:currMajor, minor:currMinor, patch:currPatch + 1)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Bumping major of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major:currMajor, minor:currMinor, patch:currPatch)

        expect:
        currVersion.bumpMajor() == new Version(major:currMajor + 1, minor:0, patch:0)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Bumping minor of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major:currMajor, minor:currMinor, patch:currPatch)

        expect:
        currVersion.bumpMinor() == new Version(major:currMajor, minor:currMinor + 1, patch:0)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Bumping patch of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major:currMajor, minor:currMinor, patch:currPatch)

        expect:
        currVersion.bumpPatch() == new Version(major:currMajor, minor:currMinor, patch:currPatch + 1)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Switching to release should return new version with release state"() {
        given:
        def devVersion = new Version(major:1, minor:2, patch:3, release:false)

        when:
        def releaseVersion = devVersion.toRelease()

        then:
        releaseVersion.isRelease()
    }

    def "Switching to develop should return new version with develop state"() {
        given:
        def releaseVersion = new Version(major:1, minor:2, patch:3, release:true)

        when:
        def devVersion = releaseVersion.toDevelop()

        then:
        !devVersion.isRelease()
    }

    def "Non-release versions should have -SNAPSHOT in printed representation"() {
        given:
        def version = new Version(major:1, minor:2, patch:3, release:false)

        expect:
        version ==~ ".*-SNAPSHOT\$"
    }

    // Comparison and equality tests.
    def "Versions should compare lexicographically based on major number"() {
        given:
        def oldVersion = new Version(major:1, minor:0, patch:0)
        def newVersion = new Version(major:2, minor:0, patch:0)

        expect:
        oldVersion < newVersion
    }

    def "Versions should compare lexicographically based on minor number"() {
        given:
        def oldVersion = new Version(major:1, minor:0, patch:0)
        def newVersion = new Version(major:1, minor:1, patch:0)

        expect:
        oldVersion < newVersion
    }

    def "Versions should compare lexicographically based on patch number"() {
        given:
        def oldVersion = new Version(major:1, minor:0, patch:0)
        def newVersion = new Version(major:1, minor:0, patch:1)

        expect:
        oldVersion < newVersion
    }

    def "Snapshot versions should be considered newer than release versions with same specifier"() {
        given:
        def devVersion = new Version(major:1, minor:2, patch:3, release:false)
        def releaseVersion = new Version(major:1, minor:2, patch:3, release:true)

        expect:
        devVersion > releaseVersion
    }

    def "Equal versions should have the same hash code"() {
        given:
        def aVersion = new Version(major:1, minor:2, patch:3, release:false)
        def anotherVersion = new Version(major:1, minor:2, patch:3, release:false)

        assert aVersion == anotherVersion

        expect:
        aVersion.hashCode() == anotherVersion.hashCode()
    }

    def "Non-Version object should not be equal to #version"() {
        expect:
        !version.equals(new Object())

        where:
        version << exampleVersions
    }

    def "Null object should not be equal to #version"() {
        expect:
        !version.equals(null)

        where:
        version << exampleVersions
    }

    def "Versions should be equal if they have the same major, minor, patch and release status"(
            int major, int minor, int patch, boolean release) {

        def version1 = new Version(major, minor, patch, release)
        def version2 = new Version(major, minor, patch, release)

        expect:
        version1.equals(version2)
        version2.equals(version1)

        where:
        major << (1..5)
        minor << (1..5)
        patch << (1..5)
        release << [true,false,true,false,true]
    }

    def "Versions should not be equal if they differ by major number"() {
        def version1 = new Version(major+1, minor, patch, release)
        def version2 = new Version(major, minor, patch, release)

        expect:
        !version1.equals(version2)
        !version2.equals(version1)

        where:
        major << (1..5)
        minor << (1..5)
        patch << (1..5)
        release << [true,false,true,false,true]
    }

    def "Versions should not be equal if they differ by minor number"() {
        def version1 = new Version(major, minor+1, patch, release)
        def version2 = new Version(major, minor, patch, release)

        expect:
        !version1.equals(version2)
        !version2.equals(version1)

        where:
        major << (1..5)
        minor << (1..5)
        patch << (1..5)
        release << [true,false,true,false,true]
    }

    def "Versions should not be equal if they differ by patch number"() {
        def version1 = new Version(major, minor, patch+1, release)
        def version2 = new Version(major, minor, patch, release)

        expect:
        !version1.equals(version2)
        !version2.equals(version1)

        where:
        major << (1..5)
        minor << (1..5)
        patch << (1..5)
        release << [true,false,true,false,true]
    }

    def "Snapshot versions should not be considered equal to release versions"() {
        given:
        def devVersion = new Version(major:1, minor:2, patch:3, release:false)
        def releaseVersion = new Version(major:1, minor:2, patch:3, release:true)

        expect:
        !devVersion.equals(releaseVersion)
        !releaseVersion.equals(devVersion)
    }
}
