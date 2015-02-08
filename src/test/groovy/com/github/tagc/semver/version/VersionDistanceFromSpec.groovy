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

/**
 * Test specification focusing on validating the behaviour of
 * {@link com.github.tagc.semver.version.Version#distanceFrom Version#distanceFrom}.
 *
 * @author davidfallah
 */
@Unroll
class VersionDistanceFromSpec extends Specification {

    private static final VALID_EXAMPLES = [
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(0,0,1,true),
            Version.Category.PATCH
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(0,1,0,true),
            Version.Category.MINOR
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(1,0,0,true),
            Version.Category.MAJOR
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(1,2,4,true),
            Version.Category.PATCH
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(1,3,0,true),
            Version.Category.MINOR
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(2,0,0,true),
            Version.Category.MAJOR
        ],
    ]

    private static final INVALID_EXAMPLES = [
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(0,0,0,true),
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(0,0,2,true),
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(0,2,0,true),
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(2,0,0,true),
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(0,1,1,true),
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(1,1,0,true),
        ],
        [
            VersionFactory.makeBaseVersion(0,0,0,true),
            VersionFactory.makeBaseVersion(1,1,1,true),
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(1,2,3,true),
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(1,3,1,true),
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(2,2,3,true),
        ],
        [
            VersionFactory.makeBaseVersion(1,2,3,true),
            VersionFactory.makeBaseVersion(3,0,0,true),
        ],
    ]

    def "Distance from #version to #newerVersion should be #category"() {
        expect: "The distance between the two versions to be the given category, regardless of either's release status"

        [version.toDevelop(), version.toRelease()].each { v1 ->
            [newerVersion.toDevelop(), newerVersion.toRelease()].each { v2 ->
                assert v1.distanceFrom(v2) == category
            }
        }

        where:
        [version, newerVersion, category] << VALID_EXAMPLES
    }

    def "Distance from #version to #newerVersion should be null"() {
        expect: "The distance between the two versions to be null, regardless of either's release status"

        [version.toDevelop(), version.toRelease()].each { v1 ->
            [newerVersion.toDevelop(), newerVersion.toRelease()].each { v2 ->
                assert v1.distanceFrom(v2) == null
            }
        }

        where:
        [version, newerVersion] << INVALID_EXAMPLES
    }
}
