package tagc.semver;

class Version {
    final int major = 0
    final int minor = 0
    final int patch = 0
    final boolean release = false

    @Override
    String toString() {
        "$major.$minor.$patch{release? '': '-SNAPSHOT'}"
    }
}
