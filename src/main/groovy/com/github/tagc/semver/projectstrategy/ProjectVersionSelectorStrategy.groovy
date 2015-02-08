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

package com.github.tagc.semver.projectstrategy

import groovy.transform.PackageScope

import com.github.tagc.semver.version.Version

/**
 * Handles the selection of an instance of {@code Version} to
 * set for the version of the project that this plugin is applied
 * to.
 *
 * @author davidfallah
 * @since v0.5.0
 */
@PackageScope
interface ProjectVersionSelectorStrategy {

    /**
     * Tries to select a {@code Version} to apply for {@code project} if
     * capable and otherwise delegates to the next
     * {@code ProjectVersionSelectorStrategy} in line within {@code successors}.
     * <p>
     * If no instance of {@code ProjectVersionSelectorStrategy} in the chain
     * of responsibility is able to choose a version, {@code null} is returned.
     *
     * @param versionSelectionData an object containing information to allow
     *      a version to be selected
     * @param successors a chain of {@code ProjectVersionSelectorStrategy}
     *      instances to delegate to if this
     *      {@code ProjectVersionSelectorStrategy} cannot choose a version
     * @return an instance of {@code Version} to set for the project
     */
    Version handleProjectVersionSelection(VersionSelectionData versionSelectionData,
        ProjectVersionSelectorStrategy[] successors)

    /**
     * Returns an instance of {@code Version} that has been selected by this
     * {@code ProjectVersionSelectorStrategy} to set as {@code project}'s version.
     *
     * @param versionSelectionData an object containing information to allow
     *      a version to be selected
     * @return the {@code Version} to set for the project
     */
    Version chooseVersion(VersionSelectionData versionSelectionData)
}
