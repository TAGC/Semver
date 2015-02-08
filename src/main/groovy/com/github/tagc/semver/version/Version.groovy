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

/**
 * Represents a semantic version number for a project.
 * <p>
 * {@code Version} objects are immutable. They can be compared based on the standard for semantic version comparisons
 * (based on lexicographic comparison of major, minor and patch numbers).
 * <p>
 * Instances of {@code Version} ought to be compared using Java's {@link Object#equals} method in preference to
 * Groovy's '==' equality checks.
 *
 * @author davidfallah
 * @since v0.1.0
 */
interface Version extends Comparable<Version> {

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
     * Returns the value for the major category of this version.
     *
     * @return this version's major category value
     */
    int getMajor()

    /**
     * Returns the value for the minor category of this version.
     *
     * @return this version's minor category value
     */
    int getMinor()

    /**
     * Returns the value for the patch category of this version.
     *
     * @return this version's patch category value
     */
    int getPatch()

    /**
     * Returns whether this version is a release or snapshot version.
     *
     * @return {@code true} if this is a release version, {@code false}
     *         if it is a snapshot version.
     */
    boolean isRelease()

    /**
     * Returns a new instance of {@code Version} with incremented {@code category}.
     *
     * @param category the version category to increment
     * @return an incremented {@code Version}
     */
    Version incrementByCategory(Version.Category category)

    /**
     * Returns a new instance of {@code Version} based on this with incremented major number.
     *
     * @return an incremented {@code Version}
     */
    Version incrementMajor()

    /**
     * Returns a new instance of {@code Version} based on this with incremented minor number.
     *
     * @return an incremented {@code Version}
     */
    Version incrementMinor()

    /**
     * Returns a new instance of {@code Version} based on this with incremented patch number.
     *
     * @return an incremented {@code Version}
     */
    Version incrementPatch()

    /**
     * Returns a new instance of {@code Version} with bumped {@code category}.
     *
     * @param category the version category to bump
     * @return a bumped {@code Version}
     */
    Version bumpByCategory(Version.Category category)

    /**
     * Returns a new instance of {@code Version} with bumped major number.
     * <p>
     * The minor and patch number are reset to 0.
     *
     * @return a bumped {@code Version}
     */
    Version bumpMajor()

    /**
     * Returns a new instance of {@code Version} with bumped minor number.
     * <p>
     * The patch number is reset to 0.
     *
     * @return a bumped {@code Version}
     */
    Version bumpMinor()

    /**
     * Returns a new instance of {@code Version} with bumped patch number.
     *
     * @return a bumped {@code Version}
     */
    Version bumpPatch()

    /**
     * Returns a new instance of {@code Version} for releases.
     *
     * @return a release-configured instance of {@code Version}
     */
    Version toRelease()

    /**
     * Returns a new instance of {@code Version} for snapshots.
     *
     * @return a snapshot-configured instance of {@code Version}
     */
    Version toDevelop()

    /**
     * Returns the category of this version that would have to be
     * bumped in order to get {@code newerVersion}.
     * <p>
     * This method will return a particular {@code Version.Category}
     * <i>if and only if</i> bumping this version by <i>only</i> that
     * category <i>by one increment</i> will result in {@code newVersion}.
     * <p>
     * In the case that no category satisfies these criteria, {@code null}
     * is returned.
     *
     * @param newerVersion an instance of {@code Version} that is newer according
     *        to Semantic versioning logic
     * @return the difference in category that separates this version and {@code newerVersion}
     *         or {@code null} if none satisfy the criteria
     */
    Version.Category distanceFrom(Version newerVersion)

    /**
     * Returns an undecorated instance of this {@code Version}.
     * <p>
     * If this is a base {@code Version} class, this method returns a reference to the class itself.
     * If this is a decorator class, this method returns a reference to the wrapped {@code Version} class.
     *
     * @return a reference to an undecorated instance of this class
     */
    Version unwrap()
}
