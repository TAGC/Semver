package com.github.tagc.semver

import net.jcip.annotations.Immutable

/**
 * Represents a semantic version number for a project.
 * <p>
 * {@code Version} objects are immutable. They can be compared based on the standard for semantic version comparisons
 * (based on lexicographic comparison of major, minor and patch numbers).
 * 
 * @author davidfallah
 * @since v0.1.0
 */
@Immutable
class Version implements Comparable<Version> {

    /**
     * Version builder - allows for {@link com.github.tagc.semver.Version Version} construction parameters to be
     * selected incrementally.
     * 
     * @author davidfallah
     * @since 0.1.0
     */
    static class Builder {
        int major = 0
        int minor = 0
        int patch = 0
        boolean release = false

        /**
         * Constructs and returns a {@code Version} object based on this builder's configuration.
         * 
         * @return a new instance of {@code Version}
         */
        Version getVersion() {
            new Version(major, minor, patch, release)
        }
    }

    final int major = 0
    final int minor = 0
    final int patch = 0
    final boolean release = false

    /**
     * Constructs a {@code Version} with specified {@code major}, {@code minor},
     * {@code patch} and {@code release} properties.
     */
    Version(int major = 0, int minor = 0, int patch = 0, boolean release = false) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.release = release
    }

    /**
     * Returns a new instance of {@code Version} based on this with incremented major number.
     * 
     * @return an incremented {@code Version}
     */
    Version incrementMajor() {
        new Version(major+1, minor, patch, release)
    }

    /**
     * Returns a new instance of {@code Version} based on this with incremented minor number.
     * 
     * @return an incremented {@code Version}
     */
    Version incrementMinor() {
        new Version(major, minor+1, patch, release)
    }

    /**
     * Returns a new instance of {@code Version} based on this with incremented patch number.
     * 
     * @return an incremented {@code Version}
     */
    Version incrementPatch() {
        new Version(major, minor, patch+1, release)
    }

    /**
     * Returns a new instance of {@code Version} for releases.
     * 
     * @return a release-configured instance of {@code Version}
     */
    Version toRelease() {
        new Version(major, minor, patch, true)
    }

    /**
     * Returns a new instance of {@code Version} for snapshots.
     * 
     * @return a snapshot-configured instance of {@code Version}
     */
    Version toDevelop() {
        new Version(major, minor, patch, false)
    }

    @Override
    boolean equals(Object o) {
        if (o == null) return false
        if (! (o instanceof Version)) return false
        if (this.major != o.major) return false
        if (this.minor != o.minor) return false

        this.patch == o.patch
    }

    @Override
    int hashCode() {
        def result = 17
        result = 31 * result + major
        result = 31 * result + minor
        result = 31 * result + patch

        return result
    }

    @Override
    int compareTo(Version that) {
        if (this.major == that.major) {
            if (this.minor == that.minor) {
                return this.patch <=> that.patch
            } else {
                return this.minor <=> that.minor
            }
        } else {
            return this.major <=> that.major
        }
    }

    @Override
    String toString() {
        "$major.$minor.$patch${release? '': '-SNAPSHOT'}"
    }
}
