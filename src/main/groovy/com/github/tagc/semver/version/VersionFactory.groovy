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
 * A factory for producing base and decorated {@code Version} objects.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class VersionFactory {

    /*
     * This factory produces immutable objects. At some point, if performance issues necessitate it,
     * we could cache Version objects and make use of the Flyweight
     * (http://www.tutorialspoint.com/design_pattern/flyweight_pattern.htm) design pattern.
     *
     * That said, it is unlikely we're ever going to be producing very many Version objects.
     */

    private VersionFactory() {
        throw new AssertionError('VersionFactory should not be instantiable')
    }

    /**
     * Returns an instance of {@link com.github.tagc.semver.version.BaseVersion BaseVersion} constructed
     * with the given parameters.
     *
     * @param major the major category value of the version instance
     * @param minor the minor category value of the version instance
     * @param patch the patch category value of the version instance
     * @param release the release setting of the version instance
     * @return an instance of {@code BaseVersion}
     */
    static BaseVersion makeBaseVersion(int major, int minor, int patch, boolean release) {
        return new BaseVersion(major, minor, patch, release)
    }

    /**
     * Returns an instance of {@link com.github.tagc.semver.version.BaseVersion BaseVersion} constructed
     * with the given parameters.
     *
     * @param m a map of parameter names and their corresponding values corresponding to the
     *        construction parameters of {@code BaseVersion}.
     *
     * @return an instance of {@code BaseVersion}
     */
    static BaseVersion makeBaseVersion(Map m) {
        return new BaseVersion(m)
    }

    /**
     * Returns an instance of {@link com.github.tagc.semver.version.BaseVersion BaseVersion} constructed
     * with the given parameters.
     *
     * @param l a list of parameter values corresponding to the construction parameters of {@code BaseVersion}.
     *
     * @return an instance of {@code BaseVersion}
     */
    static BaseVersion makeBaseVersion(List l) {
        return new BaseVersion(l)
    }

    /**
     * Returns a builder for {@link com.github.tagc.semver.version.BaseVersion BaseVersion} to specify
     * the construction parameters for the {@code BaseVersion} incrementally.
     *
     * @return an instance of {@code BaseVersion.Builder}
     */
    static BaseVersion.Builder makeBaseVersionBuilder() {
        return new BaseVersion.Builder()
    }

    /**
     * Returns a decorated version of {@code version} that is treated as a bumped, snapshot version of
     * itself. The wrapped version can be directly accessed through
     * {@link com.github.tagc.semver.version.Version#unwrap Version#unwrap}.
     *
     * @param version the {@code Version} to decorate
     * @param category the version category to bump by
     * @return a decorated instance of {@code version}
     */
    static SnapshotDecorator makeDecoratedSnapshotBumpedWithCategory(Version version,
            Version.Category category) {
        switch (category) {
            case Version.Category.PATCH:
                return makeDecoratedSnapshotBumpedWithPatch(version)
            case Version.Category.MINOR:
                return makeDecoratedSnapshotBumpedWithMinor(version)
            case Version.Category.MAJOR:
                return makeDecoratedSnapshotBumpedWithMajor(version)
            default:
                throw new IllegalArgumentException("Invalid version category: $category")
        }
    }

    /**
     * Returns a decorated version of {@code version} that is treated as a patch-bumped, snapshot version of
     * itself. The wrapped version can be directly accessed through
     * {@link com.github.tagc.semver.version.Version#unwrap Version#unwrap}.
     *
     * @param version the {@code Version} to decorate
     * @return a decorated instance of {@code version}
     */
    static SnapshotDecorator makeDecoratedSnapshotBumpedWithPatch(Version version) {
        return new SnapshotDecorator(version, Version.Category.PATCH)
    }

    /**
     * Returns a decorated version of {@code version} that is treated as a minor-bumped, snapshot version of
     * itself. The wrapped version can be directly accessed through
     * {@link com.github.tagc.semver.version.Version#unwrap Version#unwrap}.
     *
     * @param version the {@code Version} to decorate
     * @return a decorated instance of {@code version}
     */
    static SnapshotDecorator makeDecoratedSnapshotBumpedWithMinor(Version version) {
        return new SnapshotDecorator(version, Version.Category.MINOR)
    }

    /**
     * Returns a decorated version of {@code version} that is treated as a major-bumped, snapshot version of
     * itself. The wrapped version can be directly accessed through
     * {@link com.github.tagc.semver.version.Version#unwrap Version#unwrap}.
     *
     * @param version the {@code Version} to decorate
     * @return a decorated instance of {@code version}
     */
    static SnapshotDecorator makeDecoratedSnapshotBumpedWithMajor(Version version) {
        return new SnapshotDecorator(version, Version.Category.MAJOR)
    }
}
