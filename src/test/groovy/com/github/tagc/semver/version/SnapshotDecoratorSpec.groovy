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
import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.test.util.TestSetup


/**
 * Test specification for {@link com.github.tagc.semver.version.SnapshotDecorator SnapshotDecorator}.
 *
 * @author davidfallah
 */
@Unroll
class SnapshotDecoratorSpec extends Specification {

    def "Decorated version instances should be unwrapped to #releaseVersion"() {
        when: "We produce decorated versions of a base version"
        def decoratedVersions = Version.Category.values().collect { category ->
            VersionFactory.makeDecoratedSnapshotBumpedWithCategory(releaseVersion, category)
        }

        then: "We should be able to unwrap the decorated versions to get the base version back"
        decoratedVersions.each {
            assert it.unwrap().equals(releaseVersion)
        }

        where:
        releaseVersion << TestSetup.getTestExpectedReleases()
    }

    def "#decoratedVersion should be considered equal to patch-bumped #releaseVersion snapshot"() {
        given: "The patch-bumped, snapshot equivalent of the release version"
        def bumpedVersion = releaseVersion.bumpPatch().toDevelop()

        expect: "The decorated version to be treated as equal to it"
        decoratedVersion.equals(bumpedVersion)

        where:
        releaseVersion << TestSetup.getTestExpectedReleases()
        decoratedVersion = VersionFactory.makeDecoratedSnapshotBumpedWithPatch(releaseVersion)
    }

    def "#decoratedVersion should be considered equal to minor-bumped #releaseVersion snapshot"() {
        given: "The minor-bumped, snapshot equivalent of the release version"
        def bumpedVersion = releaseVersion.bumpMinor().toDevelop()

        expect: "The decorated version to be treated as equal to it"
        decoratedVersion.equals(bumpedVersion)

        where:
        releaseVersion << TestSetup.getTestExpectedReleases()
        decoratedVersion = VersionFactory.makeDecoratedSnapshotBumpedWithMinor(releaseVersion)
    }

    def "#decoratedVersion should be considered equal to major-bumped #releaseVersion snapshot"() {
        given: "The major-bumped, snapshot equivalent of the release version"
        def bumpedVersion = releaseVersion.bumpMajor().toDevelop()

        expect: "The decorated version to be treated as equal to it"
        decoratedVersion.equals(bumpedVersion)

        where:
        releaseVersion << TestSetup.getTestExpectedReleases()
        decoratedVersion = VersionFactory.makeDecoratedSnapshotBumpedWithMajor(releaseVersion)
    }
}
