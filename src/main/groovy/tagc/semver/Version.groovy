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

    Version(int major = 0, int minor = 0, int patch = 0, boolean release = false) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.release = release;
    }

    Version incrementMajor() {
        new Version(major+1, minor, patch, release)
    }

    Version incrementMinor() {
        new Version(major, minor+1, patch, release)
    }

    Version incrementPatch() {
        new Version(major, minor, patch+1, release)
    }

    Version toRelease() {
        new Version(major, minor, patch, true)
    }

    Version toDevelop() {
        new Version(major, minor, patch, false)
    }

    @Override
    String toString() {
        "$major.$minor.$patch${release? '': '-SNAPSHOT'}"
    }
}
