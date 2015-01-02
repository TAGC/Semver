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

import groovy.transform.Immutable

import java.util.regex.Matcher
import java.util.regex.Pattern

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
     * Version category enumeration - enumerates the {@code major}, {@code minor} and {@code patch}
     * segments of version strings.
     *
     * @author davidfallah
     * @since 0.3.1
     */
    static enum Category {
        /**
         * Patch category (incremented for minor bug fixes that have no effect on public API).
         */
        PATCH,

        /**
         * Minor category (incremented for minor feature additions or major bug fixes. Backwards
         * compatibility is maintained).
         */
        MINOR,

        /**
         * Major category (incremented for major, breaking changes).
         */
        MAJOR
    }

    /**
     * Version parser - parses strings and constructs {@link com.github.tagc.semver.Version Version}
     * instances from them.
     *
     * @author davidfallah
     * @since 0.2.1
     */
    static class Parser {
        private static final Pattern WHITESPACE = ~/\s*/
        private static final Pattern VERSION_PATTERN = ~/(\d+)\.(\d+).(\d+)/
        private static final Pattern SHORT_VERSION_PATTERN = ~/(\d+)\.(\d+)/

        private static final Pattern RELEASE_VERSION_PATTERN =
        ~/$WHITESPACE$VERSION_PATTERN$WHITESPACE/

        private static final Pattern RELEASE_SHORT_VERSION_PATTERN =
        ~/$WHITESPACE$SHORT_VERSION_PATTERN$WHITESPACE/

        private static final Pattern SNAPSHOT_VERSION_PATTERN =
        ~/$WHITESPACE$VERSION_PATTERN$SNAPSHOT_IDENTIFIER$WHITESPACE/

        private static final Pattern SNAPSHOT_SHORT_VERSION_PATTERN =
        ~/$WHITESPACE$SHORT_VERSION_PATTERN$SNAPSHOT_IDENTIFIER$WHITESPACE/

        private static final int MATCHER_MAJOR = 1
        private static final int MATCHER_MINOR = 2
        private static final int MATCHER_PATCH = 3

        static final Parser INSTANCE = new Parser()

        /**
         * Returns a singleton instance of {@code Parser}.
         *
         * @return a reference to a {@code Parser}
         */
        static Parser getInstance() {
            return INSTANCE
        }

        private Parser() {
        }

        /**
         * Parses the specified input string and tries to construct an instance of {@code Version} from it.
         *
         * @param input a string representing a version specifier
         * @param strict set {@code true} if the parse attempt should succeed only if the entire string can be parsed
         * @return an instance of {@code Version} if the input could be parsed
         * @throw IllegalArgumentException if the input could not be parsed
         */
        Version parse(String input, boolean strict=false) {
            Version version

            if ((version = tryParseFullSnapshotVersion(input, strict)) != null) {
                return version
            } else if ((version = tryParseShortSnapshotVersion(input, strict)) != null) {
                return version
            } else if ((version = tryParseFullReleaseVersion(input, strict)) != null) {
                return version
            } else if ((version = tryParseShortReleaseVersion(input, strict)) != null) {
                return version
            }

            throw new IllegalArgumentException("Version parser: unable to parse input: $input")
        }

        private Version tryParseFullSnapshotVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, SNAPSHOT_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new Version.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
            builder.setMinor(m[0][MATCHER_MINOR].toInteger())
            builder.setPatch(m[0][MATCHER_PATCH].toInteger())
            builder.setRelease(false)

            builder.version
        }

        private Version tryParseShortSnapshotVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, SNAPSHOT_SHORT_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new Version.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
            builder.setMinor(m[0][MATCHER_MINOR].toInteger())
            builder.setRelease(false)

            builder.version
        }

        private Version tryParseFullReleaseVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, RELEASE_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new Version.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
            builder.setMinor(m[0][MATCHER_MINOR].toInteger())
            builder.setPatch(m[0][MATCHER_PATCH].toInteger())
            builder.setRelease(true)

            builder.version
        }

        private Version tryParseShortReleaseVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, RELEASE_SHORT_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new Version.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
            builder.setMinor(m[0][MATCHER_MINOR].toInteger())
            builder.setRelease(true)

            builder.version
        }

        private Matcher checkInputAgainstPattern(String input, Pattern pattern, boolean strict) {
            if (strict && !(input ==~ pattern)) {
                return null
            }

            input =~ pattern
        }
    }

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

    private static final String SNAPSHOT_IDENTIFIER = '-SNAPSHOT'

    final int major = 0
    final int minor = 0
    final int patch = 0
    final boolean release = false

    /**
     * Returns a new instance of {@code Version} based on this with incremented major number.
     *
     * @return an incremented {@code Version}
     */
    Version incrementMajor() {
        new Version(major + 1, minor, patch, release)
    }

    /**
     * Returns a new instance of {@code Version} based on this with incremented minor number.
     *
     * @return an incremented {@code Version}
     */
    Version incrementMinor() {
        new Version(major, minor + 1, patch, release)
    }

    /**
     * Returns a new instance of {@code Version} based on this with incremented patch number.
     *
     * @return an incremented {@code Version}
     */
    Version incrementPatch() {
        new Version(major, minor, patch + 1, release)
    }

    /**
     * Returns a new instance of {@code Version} with bumped major number.
     * <p>
     * The minor and patch number are reset to 0.
     *
     * @return a bumped {@code Version}
     */
    Version bumpMajor() {
        new Version(major + 1, 0, 0, release)
    }

    /**
     * Returns a new instance of {@code Version} with bumped minor number.
     * <p>
     * The patch number is reset to 0.
     *
     * @return a bumped {@code Version}
     */
    Version bumpMinor() {
        new Version(major, minor + 1, 0, release)
    }

    /**
     * Returns a new instance of {@code Version} with bumped patch number.
     *
     * @return a bumped {@code Version}
     */
    Version bumpPatch() {
        new Version(major, minor, patch + 1, release)
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

    /*   @Override
     boolean equals(Object o) {
     if (o == null) {
     return false
     } else if (! (o instanceof Version)) {
     return false
     } else if (this.major != o.major) {
     return false
     } else if (this.minor != o.minor) {
     return false
     } else if (this.release != o.release) {
     return false
     }
     this.patch == o.patch
     }
     @Override
     int hashCode() {
     final int factor = 31
     def result = 17
     result = factor * result + major
     result = factor * result + minor
     result = factor * result + patch
     result = factor * result + (release ? 1 : 0)
     return result
     }*/

    @Override
    int compareTo(Version that) {
        if (this.major == that.major) {
            if (this.minor == that.minor) {
                if (this.patch == that.patch) {
                    return -(this.release <=> that.release)
                }

                return this.patch <=> that.patch
            }

            return this.minor <=> that.minor
        }

        return this.major <=> that.major
    }

    @Override
    String toString() {
        "$major.$minor.$patch${release ? '' : SNAPSHOT_IDENTIFIER}"
    }
}
