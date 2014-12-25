package tagc.semver;

class Version implements Comparable<Version> {
    
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
    boolean equals(Object o) {
        if (o == null) return false
        if (! (o instanceof Version)) return false
        if (this.major != o.major) return false
        if (this.minor != o.minor) return false
        
        this.patch == o.patch
    }

    @Override
    int hashCode() {
        def result = 17
        result = 31 * result + major
        result = 31 * result + minor
        result = 31 * result + patch

        return result
    }

    @Override
    int compareTo(Version that) {
        if (this.major == that.major) {
            if (this.minor == that.minor) {
                return this.patch <=> that.patch
            } else {
                return this.minor <=> that.minor
            }
        } else {
            return this.major <=> that.major
        }
    }

    @Override
    String toString() {
        "$major.$minor.$patch${release? '': '-SNAPSHOT'}"
    }
}
