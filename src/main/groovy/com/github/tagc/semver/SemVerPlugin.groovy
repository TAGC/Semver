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

import groovy.transform.PackageScope

import org.ajoberstar.grgit.Grgit
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger

import com.github.tagc.semver.tasks.BumpMajorTask
import com.github.tagc.semver.tasks.BumpMinorTask
import com.github.tagc.semver.tasks.BumpPatchTask
import com.github.tagc.semver.tasks.PrintVersionTask

/**
 * An {@link org.gradle.api.Plugin} class that handles the application of semantic
 * versioning logic to an {@link org.gradle.api.Project}
 *
 * @author davidfallah
 * @since v0.1.0
 */
class SemVerPlugin implements Plugin<Project> {
    @PackageScope static final String EXTENSION_NAME = 'semver'

    private static final String PRINT_VERSION_TASK_NAME = 'printVersion'
    private static final String BUMP_MAJOR_TASK_NAME = 'bumpMajor'
    private static final String BUMP_MINOR_TASK_NAME = 'bumpMinor'
    private static final String BUMP_PATCH_TASK_NAME = 'bumpPatch'
    private static final String MASTER_BRANCH = 'master'

    static String getPrintVersionTaskName() {
        return PRINT_VERSION_TASK_NAME
    }

    static String getBumpMajorTaskName() {
        return BUMP_MAJOR_TASK_NAME
    }

    static String getBumpMinorTaskName() {
        return BUMP_MINOR_TASK_NAME
    }

    static String getBumpPatchTaskName() {
        return BUMP_PATCH_TASK_NAME
    }

    private Grgit repo
    private Logger logger

    @Override
    void apply(Project project) {
        if (!project) {
            throw new GradleException('Plugin cannot be applied to null project')
        }

        logger = project.logger
        project.extensions.create(EXTENSION_NAME, SemVerPluginExtension)
        addTasks(project)

        project.afterEvaluate { setVersionProjectNumber(project) }
    }

    private void addTasks(Project project) {
        def extension = project.extensions.findByName(EXTENSION_NAME)

        project.task(getPrintVersionTaskName(), type:PrintVersionTask)

        def majorBumpTask = project.task(getBumpMajorTaskName(), type:BumpMajorTask)
        def minorBumpTask = project.task(getBumpMinorTaskName(), type:BumpMinorTask)
        def patchBumpTask = project.task(getBumpPatchTaskName(), type:BumpPatchTask)

        [majorBumpTask, minorBumpTask, patchBumpTask].each { task ->
            task.conventionMapping.map('versionFileIn') {
                project.file(URLDecoder.decode(extension.versionFilePath, 'UTF-8'))
            }
            task.conventionMapping.map('versionFileOut') {
                project.file(URLDecoder.decode(extension.versionFilePath, 'UTF-8'))
            }
        }
    }

    private void setVersionProjectNumber(Project project) {
        assert project : 'Null project is illegal'

        try {
            this.repo = Grgit.open(project.file("$project.projectDir"))
        } catch (RepositoryNotFoundException e) {
            throw new GradleException('No Git repository can be found for this project')
        }

        def rawVersion = readRawVersion(project)

        def extension = project.extensions.findByName(EXTENSION_NAME)
        Version.Category snapshotBump = extension.snapshotBump

        if (isOnMasterBranch()) {
            project.version = rawVersion.toRelease()
        } else {
            project.version = getAppropriateSnapshotVersion(project, rawVersion, snapshotBump)
        }

        logger.info "Set project version to $project.version"
    }

    private Version readRawVersion(Project project) {
        assert project : 'Null project is illegal'

        def extension = project.extensions.findByName(EXTENSION_NAME)
        final String versionFilePath = URLDecoder.decode(extension.versionFilePath, 'UTF-8')

        if (!versionFilePath) {
            throw new GradleException('Version file has not been specified')
        }

        def versionFile = new File(versionFilePath)
        if (!versionFile.exists()) {
            throw new GradleException("Missing version file: $versionFile.canonicalPath")
        }

        Version.Parser.instance.parse(versionFile.text)
    }

    /*
     * By default, bump by patch.
     */
    private Version getAppropriateSnapshotVersion(Project project, Version currVersion, Version.Category snapshotBump) {
        assert currVersion : 'Null currVersion is illegal'
        assert project : 'Null project is illegal'
        if (snapshotBump == null)
        {
            snapshotBump = Version.Category.PATCH
        }

        return currVersion.bumpByCategory(snapshotBump).toDevelop()
    }

    private boolean isOnMasterBranch() {
        logger.info "Current Git branch: $currentBranch"
        currentBranch == MASTER_BRANCH
    }

    private String getCurrentBranch() {
        assert repo : 'Repo does not exist'
        repo.branch.current.name
    }
}
