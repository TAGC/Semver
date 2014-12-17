import spock.lang.Specification
import tagc.semver.Version

class VersionSpec extends Specification {
    
    def "Version should be constructable by major and retain state"() {
        def version = new Version(1)

        expect:
            version.getMajor() == 1
    }

    def "Version should be constructable by major and minor and retain state"() {
        def version = new Version(1, 2)

        expect:
            version.getMajor() == 1
            version.getMinor() == 2
    }

    def "Version should be constructable by major, minor and patch and retain state"() {
        def version = new Version(1, 2, 3)

        expect:
            version.getMajor() == 1
            version.getMinor() == 2
            version.getPatch() == 3
    }
}
