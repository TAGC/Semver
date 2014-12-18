package tagc.semver;

class Version {
    
    static class Builder {
        int major = 0
        int minor = 0
        int patch = 0
        boolean release = false

        Version getVersion() {
            new Version(major, minor, patch, release)
        }
    }

    final int major = 0
    final int minor = 0
    final int patch = 0
    final boolean release = false

    @Override
    String toString() {
        "$major.$minor.$patch{release? '': '-SNAPSHOT'}"
    }
}
