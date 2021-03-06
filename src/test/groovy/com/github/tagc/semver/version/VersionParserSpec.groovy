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

import com.github.tagc.semver.version.BaseVersion.Parser

/**
 * Test specification for {@link com.github.tagc.semver.version.BaseVersion.Parser BaseVersion.Parser}.
 *
 * @author davidfallah
 * @since 0.2.1
 */
@Unroll
class VersionParserSpec extends Specification {

    private static final Parser PARSER = BaseVersion.Parser.getInstance()

    def "Version information should be extractable from files if parsing is not strict"() {
        given:
        def versionFileText = "version='$versionString'"

        expect:
        PARSER.parse(versionFileText, false) == version

        where:
        versionString      | version
        '0.1.2-SNAPSHOT'   | VersionFactory.makeBaseVersion(0,1,2,false)
        '1.2.4'            | VersionFactory.makeBaseVersion(1,2,4,true)
        '1.3-SNAPSHOT'     | VersionFactory.makeBaseVersion(1,3,0,false)
        '0.4'              | VersionFactory.makeBaseVersion(0,4,0,true)
    }

    def "'#input' should be parsed successfully to #version"() {
        expect:
        PARSER.parse(input, true) == version

        where:
        input               | version
        '0.1'               | VersionFactory.makeBaseVersion(0,1,0,true)
        '1.3-SNAPSHOT'      | VersionFactory.makeBaseVersion(1,3,0,false)
        '1.1.1'             | VersionFactory.makeBaseVersion(1,1,1,true)
        '0.2.7'             | VersionFactory.makeBaseVersion(0,2,7,true)
        '0.4.9-SNAPSHOT'    | VersionFactory.makeBaseVersion(0,4,9,false)
        '6.3.16-SNAPSHOT'   | VersionFactory.makeBaseVersion(6,3,16,false)
        '  1.2.3-SNAPSHOT'  | VersionFactory.makeBaseVersion(1,2,3,false)
        ' 1.3.5-SNAPSHOT '  | VersionFactory.makeBaseVersion(1,3,5,false)
    }

    def "'#input' should cause null to be returned when parsed (strict)"() {
        expect:
        PARSER.parse(input, true) == null

        where:
        input << [
            '1.2.a',
            '1,2,3',
            '2.4.-1',
            '3-4-9',
            '1.4.5-SNPSHOT',
            '1.4.5-SNAPSHOTasd'
        ]
    }

    def "'#input' should cause null to be returned when parsed (non-strict)"() {
        expect:
        PARSER.parse(input, false) == null

        where:
        input << [
            '1,2,a',
            '1-2-3',
            'wasadg',
            'v3 9 1',
            'test123',
            '13-SNAPSHOT'
        ]
    }
}
