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

package com.github.tagc.semver.version

import groovy.transform.Immutable
import groovy.transform.PackageScope

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A concrete, base implementation of {@link com.github.tagc.semver.version.Version Version}.
 *
 * @author davidfallah
 * @since v0.1.0
 */
@Immutable
@PackageScope
final class BaseVersion implements Version {

    /**
     * Version parser - parses strings and constructs {@link com.github.tagc.semver.version.BaseVersion BaseVersion}
     * instances from them.
     *
     * @author davidfallah
     * @since 0.2.1
     */
    @Singleton
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

        /**
         * Parses the text within the file and replaces all occurrences of version data
         * with new version data based on {@code newVersion}.
         *
         * This method will not modify the given file.
         *
         * @param inputFile a file containing text that represents a version specifier
         * @param newVersion the version to replace the existing version data in the input with
         * @return a copy of the file's text with the replacements made, if any
         */
        String parseAndReplace(File inputFile, boolean strict=false, Version newVersion) {
            parseAndReplace(inputFile.text, strict, newVersion)
        }

        /**
         * Parses the specified input string and replaces all occurrences of version data
         * with new version data based on {@code newVersion}.
         *
         * @param input a string representing a version specifier
         * @param newVersion the version to replace the existing version data in the input with
         * @return a copy of the input string with the replacements made, if any
         */
        String parseAndReplace(String input, boolean strict=false, Version newVersion) {
            tryParseAndReplaceVersion(input, strict, newVersion)
        }

        /**
         * Parses the text within the given file and tries to construct an instance of
         * {@code Version} from it.
         *
         * @param inputFile a file containing text that represents a version specifier
         * @param strict set {@code true} if the parse attempt should succeed only if the entire string can be parsed
         * @return an instance of {@code BaseVersion} if the input could be parsed or {@code null} if it could not
         */
        BaseVersion parse(File inputFile, boolean strict=false) {
            parse(inputFile.text, strict)
        }

        /**
         * Parses the specified input string and tries to construct an instance of
         * {@code Version} from it.
         *
         * @param input a string representing a version specifier
         * @param strict set {@code true} if the parse attempt should succeed only if the entire string can be parsed
         * @return an instance of {@code BaseVersion} if the input could be parsed or {@code null} if it could not
         */
        BaseVersion parse(String input, boolean strict=false) {
            tryParseVersion(input, strict)
        }

        private BaseVersion tryParseVersion(String input, boolean strict) {
            BaseVersion version

            if ((version = tryParseFullSnapshotVersion(input, strict)) != null) {
                return version
            } else if ((version = tryParseShortSnapshotVersion(input, strict)) != null) {
                return version
            } else if ((version = tryParseFullReleaseVersion(input, strict)) != null) {
                return version
            } else if ((version = tryParseShortReleaseVersion(input, strict)) != null) {
                return version
            }

            return null
        }

        private String tryParseAndReplaceVersion(String input, boolean strict, Version replacement) {
            String updatedText
            String originalText = updatedText

            if ((updatedText = tryParseAndReplaceFullSnapshotVersion(input, strict, replacement)) != null) {
                return updatedText
            } else if ((updatedText = tryParseAndReplaceShortSnapshotVersion(input, strict, replacement)) != null) {
                return updatedText
            } else if ((updatedText = tryParseAndReplaceFullReleaseVersion(input, strict, replacement)) != null) {
                return updatedText
            } else if ((updatedText = tryParseAndReplaceShortReleaseVersion(input, strict, replacement)) != null) {
                return updatedText
            }

            return originalText
        }

        private Version tryParseFullSnapshotVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, SNAPSHOT_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new BaseVersion.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
                    .setMinor(m[0][MATCHER_MINOR].toInteger())
                    .setPatch(m[0][MATCHER_PATCH].toInteger())
                    .setRelease(false)
                    .build()
        }

        private BaseVersion tryParseShortSnapshotVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, SNAPSHOT_SHORT_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new BaseVersion.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
                    .setMinor(m[0][MATCHER_MINOR].toInteger())
                    .setRelease(false)
                    .build()
        }

        private BaseVersion tryParseFullReleaseVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, RELEASE_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new BaseVersion.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
                    .setMinor(m[0][MATCHER_MINOR].toInteger())
                    .setPatch(m[0][MATCHER_PATCH].toInteger())
                    .setRelease(true)
                    .build()
        }

        private BaseVersion tryParseShortReleaseVersion(String input, boolean strict) {
            Matcher m = checkInputAgainstPattern(input, RELEASE_SHORT_VERSION_PATTERN, strict)
            if (!m) {
                return null
            }

            def builder = new BaseVersion.Builder()
            builder.setMajor(m[0][MATCHER_MAJOR].toInteger())
                    .setMinor(m[0][MATCHER_MINOR].toInteger())
                    .setRelease(true)
                    .build()
        }

        private String tryParseAndReplaceFullSnapshotVersion(String input, boolean strict, Version replacement) {
            Matcher m = checkInputAgainstPattern(input, SNAPSHOT_VERSION_PATTERN, strict)
            if (m) {
                return m.replaceAll(replacement.toString())
            }

            return null
        }

        private String tryParseAndReplaceShortSnapshotVersion(String input, boolean strict, Version replacement) {
            Matcher m = checkInputAgainstPattern(input, SNAPSHOT_SHORT_VERSION_PATTERN, strict)
            if (m) {
                return m.replaceAll(replacement.toString())
            }

            return null
        }

        private String tryParseAndReplaceFullReleaseVersion(String input, boolean strict, Version replacement) {
            Matcher m = checkInputAgainstPattern(input, RELEASE_VERSION_PATTERN, strict)
            if (m) {
                return m.replaceAll(replacement.toString())
            }

            return null
        }

        private String tryParseAndReplaceShortReleaseVersion(String input, boolean strict, Version replacement) {
            Matcher m = checkInputAgainstPattern(input, RELEASE_SHORT_VERSION_PATTERN, strict)
            if (m) {
                return m.replaceAll(replacement.toString())
            }

            return null
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

        BaseVersion.Builder setMajor(int major) {
            this.major = major
            return this
        }

        BaseVersion.Builder setMinor(int minor) {
            this.minor = minor
            return this
        }

        BaseVersion.Builder setPatch(int patch) {
            this.patch = patch
            return this
        }

        BaseVersion.Builder setRelease(boolean release) {
            this.release = release
            return this
        }

        /**
         * Constructs and returns a {@code Version} object based on this builder's configuration.
         *
         * @return a new instance of {@code Version}
         */
        BaseVersion build() {
            new BaseVersion(major, minor, patch, release)
        }
    }

    private static final String SNAPSHOT_IDENTIFIER = '-SNAPSHOT'

    /**
     * The major category of this version.
     */
    int major = 0

    /**
     * The minor category of this version.
     */
    int minor = 0

    /**
     * The patch category of this version.
     */
    int patch = 0

    /**
     * Whether this version is a release or snapshot version.
     */
    boolean release = false

    @Override
    Version incrementByCategory(Version.Category category) {
        switch (category) {
            case Version.Category.MAJOR:
                return incrementMajor()
            case Version.Category.MINOR:
                return incrementMinor()
            case Version.Category.PATCH:
                return incrementPatch()
            default:
                throw new IllegalArgumentException("Invalid increment category: $category")
        }
    }

    @Override
    Version incrementMajor() {
        new BaseVersion(major + 1, minor, patch, release)
    }

    @Override
    Version incrementMinor() {
        new BaseVersion(major, minor + 1, patch, release)
    }

    @Override
    Version incrementPatch() {
        new BaseVersion(major, minor, patch + 1, release)
    }

    @Override
    Version bumpByCategory(Version.Category category) {
        switch (category) {
            case Version.Category.MAJOR:
                return bumpMajor()
            case Version.Category.MINOR:
                return bumpMinor()
            case Version.Category.PATCH:
                return bumpPatch()
            default:
                throw new IllegalArgumentException("Invalid bump category: $category")
        }
    }

    @Override
    Version bumpMajor() {
        new BaseVersion(major + 1, 0, 0, release)
    }

    @Override
    Version bumpMinor() {
        new BaseVersion(major, minor + 1, 0, release)
    }

    @Override
    Version bumpPatch() {
        new BaseVersion(major, minor, patch + 1, release)
    }

    @Override
    Version toRelease() {
        new BaseVersion(major, minor, patch, true)
    }

    @Override
    Version toDevelop() {
        new BaseVersion(major, minor, patch, false)
    }

    @Override
    Version.Category distanceFrom(Version newerVersion) {
        if (newerVersion.toRelease() == this.bumpMajor().toRelease()) {
            return Version.Category.MAJOR
        } else if (newerVersion.toRelease() == this.bumpMinor().toRelease()) {
            return Version.Category.MINOR
        } else if (newerVersion.toRelease() == this.bumpPatch().toRelease()) {
            return Version.Category.PATCH
        }

        return null
    }

    @Override
    BaseVersion unwrap() {
        return this
    }

    @Override
    boolean equals(Object o) {
        if (o == null) {
            return false
        } else if (! (o instanceof Version)) {
            return false
        } else if (this.major != o.major) {
            return false
        } else if (this.minor != o.minor) {
            return false
        } else if (this.patch != o.patch) {
            return false
        }
        this.release == o.release
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
    }

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
