package com.github.tagc.semver

import com.github.tagc.semver.Version;

import spock.lang.Specification

class VersionSpec extends Specification {
    
    def "Version should be constructable by major and retain state"() {
        given:
        def version = new Version(1)

        expect:
        version.getMajor() == 1
    }

    def "Version should be constructable by major and minor and retain state"() {
        given:
        def version = new Version(1, 2)

        expect:
        version.getMajor() == 1
        version.getMinor() == 2
    }

    def "Version should be constructable by major, minor and patch and retain state"() {
        given:
        def version = new Version(1, 2, 3)

        expect:
        version.getMajor() == 1
        version.getMinor() == 2
        version.getPatch() == 3
    }

    def "Version should be constructable with major, minor, patch and release and retain state"() {
        given:
        def devVersion = new Version(1,2,3,false)
        def releaseVersion = new Version(1,2,3,true)

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

    def "Incrementing major number should return new version with incremented major"() {
        given:
        def version = new Version(1,2,3)

        when:
        def newVersion = version.incrementMajor()
        def oldMajor = version.getMajor()

        then:
        newVersion.getMajor() == oldMajor + 1
    }

    def "Incrementing minor number should return new version with incremented minor"() {
        given:
        def version = new Version(1,2,3)

        when:
        def newVersion = version.incrementMinor()
        def oldMinor = version.getMinor()

        then:
        newVersion.getMinor() == oldMinor + 1
    }

    def "Incrementing patch number should return new version with incremented minor"() {
        given:
        def version = new Version(1,2,3)

        when:
        def newVersion = version.incrementPatch()
        def oldPatch = version.getPatch()

        then:
        newVersion.getPatch() == oldPatch + 1
    }

    def "Switching to release should return new version with release state"() {
        given:
        def devVersion = new Version(1,2,3,false)

        when:
        def releaseVersion = devVersion.toRelease()

        then:
        releaseVersion.isRelease()
    }

    def "Switching to develop should return new version with develop state"() {
        given:
        def releaseVersion = new Version(1,2,3,true)

        when:
        def devVersion = releaseVersion.toDevelop()

        then:
        !devVersion.isRelease()
    }

    def "Non-release versions should have -SNAPSHOT in printed representation"() {
        given:
        def version = new Version(1,2,3,false)

        expect:
        version ==~ ".*-SNAPSHOT\$"
    }

    // Comparison and equality tests.
    def "Versions should compare lexicographically based on major number"() {
        given:
        def oldVersion = new Version(1,0,0)
        def newVersion = new Version(2,0,0)

        expect:
        oldVersion < newVersion
    }

    def "Versions should compare lexicographically based on minor number"() {
        given:
        def oldVersion = new Version(1,0,0)
        def newVersion = new Version(1,1,0)

        expect:
        oldVersion < newVersion
    }

    def "Versions should compare lexicographically based on patch number"() {
        given:
        def oldVersion = new Version(1,0,0)
        def newVersion = new Version(1,0,1)

        expect:
        oldVersion < newVersion
    }

    def "Versions with equal major, minor and patch should be equal"() {
        given:
        def devVersion = new Version(1,2,3,false)
        def releaseVersion = new Version(1,2,3,true)

        expect:
        devVersion == releaseVersion
    }

    def "Equal versions should have the same hash code"() {
        given:
        def devVersion = new Version(1,2,3,false)
        def releaseVersion = new Version(1,2,3,true)

        assert devVersion == releaseVersion

        expect:
        devVersion.hashCode() == releaseVersion.hashCode()
    }
}
