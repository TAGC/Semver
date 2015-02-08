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

import groovy.transform.PackageScope

/**
 * A decorator {@code Version} class that wraps around another
 * {@code Version} class and makes it appear as a snapshot version of itself with
 * a bumped version category.
 * <p>
 * Because this is a decorator, it makes it straightforward to retrieve the original,
 * unwrapped instance of {@code Version}.
 *
 * @author davidfallah
 * @see {@link #unwrap}
 * @since v0.5.0
 */
@PackageScope
class SnapshotDecorator implements Version {

    private final Version originalDelegateVersion
    @Delegate private final Version delegateVersion

    SnapshotDecorator(Version wrappedVersion, Version.Category bumpedCategory) {
        this.originalDelegateVersion = wrappedVersion
        this.delegateVersion = wrappedVersion.bumpByCategory(bumpedCategory).toDevelop()
    }

    @Override
    Version unwrap() {
        originalDelegateVersion
    }

    @Override
    boolean equals(Object o) {
        delegateVersion.equals(o)
    }

    @Override
    int hashCode() {
        delegateVersion.hashCode()
    }

    @Override
    int compareTo(Version that) {
        delegateVersion.compareTo(that)
    }

    String toString() {
        delegateVersion.toString()
    }
}
