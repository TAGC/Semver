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

package com.github.tagc.semver

/**
 * Plugin extension for {@link com.github.tagc.semver.SemVerPlugin SemVerPlugin}.
 * <p>
 * This extension allows for configuration of the semantic versioning plugin
 * from Gradle build scripts.
 *
 * @author davidfallah
 * @since v0.1.0
 */
class SemVerPluginExtension {

    /**
     * The path to the file containing version data.
     */
    String versionFilePath

    /**
     *  The category to assume the snapshot version should be bumped by
     *
     *  Example: if the in-development version will have significant but
     *  non-breaking changes to its public API, {@code snapshotBump} should be
     *  set to {@code true}.
     */
    Version.Category snapshotBump

    /**
     * Whether or not to permit version bumping when the project is
     * not on the Git master branch.
     */
    boolean forceBump
}
