package com.github.tagc.semver

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class VersionSpec extends Specification {

    static def exampleVersions = [
        new Version(major: 1, minor: 2, patch: 3),
        new Version(major: 0, minor: 0, patch: 0),
        new Version(major: 5, minor: 4, patch: 3),
        new Version(major: 1, minor: 16, patch: 2),
        new Version(major: 4, minor: 5, patch: 8),
        ]

    def "Version should be constructable by major and retain state"() {
        given:
        def version = new Version(major: 1)

        expect:
        version.getMajor() == 1
    }

    def "Version should be constructable by major and minor and retain state"() {
        given:
        def version = new Version(major: 1, minor: 2)

        expect:
        version.getMajor() == 1
        version.getMinor() == 2
    }

    def "Version should be constructable by major, minor and patch and retain state"() {
        given:
        def version = new Version(major: 1, minor: 2, patch: 3)

        expect:
        version.getMajor() == 1
        version.getMinor() == 2
        version.getPatch() == 3
    }

    def "Version should be constructable with major, minor, patch and release and retain state"() {
        given:
        def devVersion = new Version(major: 1, minor: 2, patch: 3, release: false)
        def releaseVersion = new Version(major: 1, minor: 2, patch: 3, release: true)

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
        version.getMajor() == 1
        version.getMinor() == 2
        version.getPatch() == 3
        version.isRelease()
    }

    def "Incrementing major of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major: currMajor, minor: currMinor, patch: currPatch)

        expect:
        currVersion.incrementMajor() == new Version(major: currMajor+1, minor: currMinor, patch: currPatch)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Incrementing minor of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major: currMajor, minor: currMinor, patch: currPatch)

        expect:
        currVersion.incrementMinor() == new Version(major: currMajor, minor: currMinor+1, patch: currPatch)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Incrementing patch of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major: currMajor, minor: currMinor, patch: currPatch)

        expect:
        currVersion.incrementPatch() == new Version(major: currMajor, minor: currMinor, patch: currPatch+1)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Bumping major of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major: currMajor, minor: currMinor, patch: currPatch)

        expect:
        currVersion.bumpMajor() == new Version(major: currMajor+1, minor: 0, patch: 0)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Bumping minor of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major: currMajor, minor: currMinor, patch: currPatch)

        expect:
        currVersion.bumpMinor() == new Version(major: currMajor, minor: currMinor+1, patch: 0)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Bumping patch of version #currMajor-#currMinor-#currPatch should return appropriate bumped version"() {
        given:
        def currVersion = new Version(major: currMajor, minor: currMinor, patch: currPatch)

        expect:
        currVersion.bumpPatch() == new Version(major: currMajor, minor: currMinor, patch: currPatch+1)

        where:
        version << exampleVersions
        currMajor = version.major
        currMinor = version.minor
        currPatch = version.patch
    }

    def "Switching to release should return new version with release state"() {
        given:
        def devVersion = new Version(major: 1, minor: 2, patch: 3, release: false)

        when:
        def releaseVersion = devVersion.toRelease()

        then:
        releaseVersion.isRelease()
    }

    def "Switching to develop should return new version with develop state"() {
        given:
        def releaseVersion = new Version(major: 1, minor: 2, patch: 3, release: true)

        when:
        def devVersion = releaseVersion.toDevelop()

        then:
        !devVersion.isRelease()
    }

    def "Non-release versions should have -SNAPSHOT in printed representation"() {
        given:
        def version = new Version(major: 1, minor: 2, patch: 3, release: false)

        expect:
        version ==~ ".*-SNAPSHOT\$"
    }

    // Comparison and equality tests.
    def "Versions should compare lexicographically based on major number"() {
        given:
        def oldVersion = new Version(major: 1, minor: 0, patch: 0)
        def newVersion = new Version(major: 2, minor: 0, patch: 0)

        expect:
        oldVersion < newVersion
    }

    def "Versions should compare lexicographically based on minor number"() {
        given:
        def oldVersion = new Version(major: 1, minor: 0, patch: 0)
        def newVersion = new Version(major: 1, minor: 1, patch: 0)

        expect:
        oldVersion < newVersion
    }

    def "Versions should compare lexicographically based on patch number"() {
        given:
        def oldVersion = new Version(major: 1, minor: 0, patch: 0)
        def newVersion = new Version(major: 1, minor: 0, patch: 1)

        expect:
        oldVersion < newVersion
    }

    def "Snapshot versions should not be considered equal to release versions"() {
        given:
        def devVersion = new Version(major: 1, minor: 2, patch: 3, release: false)
        def releaseVersion = new Version(major: 1, minor: 2, patch: 3, release: true)

        expect:
        devVersion != releaseVersion
    }

    def "Snapshot versions should be considered newer than release versions with same specifier"() {
        given:
        def devVersion = new Version(major: 1, minor: 2, patch: 3, release: false)
        def releaseVersion = new Version(major: 1, minor: 2, patch: 3, release: true)

        expect:
        devVersion > releaseVersion
    }

    def "Equal versions should have the same hash code"() {
        given:
        def aVersion = new Version(major: 1, minor: 2, patch: 3, release: false)
        def anotherVersion = new Version(major: 1, minor: 2, patch: 3, release: false)

        assert aVersion == anotherVersion

        expect:
        aVersion.hashCode() == anotherVersion.hashCode()
    }
}
