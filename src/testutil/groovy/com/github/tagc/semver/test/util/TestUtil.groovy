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

package com.github.tagc.semver.test.util

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.exception.GrgitException
import org.gradle.api.Plugin
import org.gradle.api.Project

import com.github.tagc.semver.Version

/**
 * Static utility class for test specifications.
 *
 * @author davidfallah
 * @since v0.5.0
 */
public class TestUtil {

    private TestUtil() {
        assert false : "Should not be instantiable"
    }

    static URL getVersionFileAsResource(String versionFilePath) {
        Thread.currentThread().contextClassLoader.getResource(versionFilePath)
    }

    static void evaluateProjectForReleaseTests(Plugin plugin, Project project, URL versionPathUrl) {
        beginConfiguringProject(plugin, project, false)
        configureProjectVersionPath(project, versionPathUrl)
        finishConfiguringProject(project)
    }

    static void evaluateProjectForSnapshotTests(Plugin plugin, Project project, URL versionPathUrl, Version.Category snapshotBump) {
        beginConfiguringProject(plugin, project, true)
        configureProjectVersionPath(project, versionPathUrl)
        configureProjectSnapshotBump(project, snapshotBump)
        finishConfiguringProject(project)
    }

    static void beginConfiguringProject(Plugin plugin, Project project, boolean forSnapshots) {
        initialiseGitDirectory(project)
        if (forSnapshots)
        {
            checkoutBranch(project, 'develop')
        }
        plugin.apply(project)
    }

    static void finishConfiguringProject(Project project) {
        project.evaluate()
    }

    static void configureProjectVersionPath(Project project, URL versionPathUrl) {
        project.semver.versionFilePath = versionPathUrl.path
    }

    static void configureProjectSnapshotBump(Project project, Version.Category snapshotBump) {
        project.semver.snapshotBump = snapshotBump
    }

    static void configureProjectForceBump(Project project, boolean forceBump) {
        project.semver.forceBump = forceBump
    }

    static boolean cleanupGitDirectory(Project project) {
        new File("$project.projectDir/.git").deleteDir()
    }

    private static void initialiseGitDirectory(Project project) {
        Grgit.init(dir:project.projectDir)
    }

    private static void checkoutBranch(Project project, String branch) {
        Grgit grgit = Grgit.open(project.projectDir)
        try {
            grgit.checkout(branch:branch, createBranch:true)
        } catch (GrgitException e) {
            grgit.add(patterns:['.'], update:true)
            grgit.commit(message:'Initial commit')
            grgit.checkout(branch:branch, createBranch:true)
        }
    }
}

