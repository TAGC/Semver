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

    static void evaluateProjectForReleaseTests(Plugin plugin, Project project, URL url) {
        initialiseGitDirectory(project)
        plugin.apply(project)
        project.semver.versionFilePath = url.path
        project.evaluate()
    }

    static void evaluateProjectForSnapshotTests(Plugin plugin, Project project, URL url, Version.Category bump) {
        initialiseGitDirectory(project)
        checkoutBranch(project, 'develop')
        plugin.apply(project)
        project.semver.versionFilePath = url.path
        project.semver.snapshotBump = bump
        project.evaluate()
    }

    static void initialiseGitDirectory(Project project) {
        Grgit.init(dir:project.projectDir)
    }

    static void checkoutBranch(Project project, String branch) {
        Grgit grgit = Grgit.open(project.projectDir)
        try {
            grgit.checkout(branch:branch, createBranch:true)
        } catch (GrgitException e) {
            grgit.add(patterns:['.'], update:true)
            grgit.commit(message:'Initial commit')
            grgit.checkout(branch:branch, createBranch:true)
        }
    }

    static boolean cleanupGitDirectory(Project project) {
        new File("$project.projectDir/.git").deleteDir()
    }
}

