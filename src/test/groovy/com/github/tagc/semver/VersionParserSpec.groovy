package com.github.tagc.semver

import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.Version.Parser

@Unroll
class VersionParserSpec extends Specification {

    private static final Parser PARSER = Version.Parser.getInstance()

    def "Valid version representations should be parsed successfully"() {
        expect:
        PARSER.parse(input) == version

        where:
        input               | version
        '0.1'               | new Version(0,1,0,true)
        '1.3-SNAPSHOT'      | new Version(1,3,0,false)
        '1.1.1'             | new Version(1,1,1,true)
        '0.2.7'             | new Version(0,2,7,true)
        '0.4.9-SNAPSHOT'    | new Version(0,4,9,false)
        '6.3.16-SNAPSHOT'   | new Version(6,3,16,false)
        '  1.2.3-SNAPSHOT'  | new Version(1,2,3,false)
        ' 1.3.5-SNAPSHOT '  | new Version(1,3,5,false)
    }

    def "Invalid version representations should cause an exception to be thrown"() {
        when:
        PARSER.parser(input)

        then:
        thrown(InvalidArgumentException)

        where:
        input << [
            '1.2.a',
            '1,2,3',
            '2.4.-1',
            '3-4-9',
            '1.2',
            '1.4.5-SNPSHOT',
            '1.4.5-SNAPSHOTasd'
        ]
    }
}
