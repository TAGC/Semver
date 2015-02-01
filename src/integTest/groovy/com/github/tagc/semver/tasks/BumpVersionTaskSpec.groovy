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

package com.github.tagc.semver.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.SemVerPlugin
import com.github.tagc.semver.Version
import com.github.tagc.semver.test.util.TestSetup
import com.github.tagc.semver.test.util.TestUtil

@Unroll
class BumpVersionTaskSpec extends Specification {

    private Project project
    private Plugin plugin

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    def "Bumping project major version should update version file to represent #bumpedVersion"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)

        when:
        TestUtil.evaluateProjectForReleaseTests(plugin, project, versionFileCopy.toURI().toURL())
        def bumpMajorTask = project.tasks.findByName(SemVerPlugin.getBumpMajorTaskName())
        bumpMajorTask.execute()

        then:
        notThrown(Exception)
        Version.Parser.instance.parse(versionFileCopy) == bumpedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        bumpedVersion << TestSetup.getTestExpectedMajorReleases()
    }

    def "Bumping project minor version should update version file to represent #bumpedVersion"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)

        when:
        TestUtil.evaluateProjectForReleaseTests(plugin, project, versionFileCopy.toURI().toURL())
        def bumpMinorTask = project.tasks.findByName(SemVerPlugin.getBumpMinorTaskName())
        bumpMinorTask.execute()

        then:
        notThrown(Exception)
        Version.Parser.instance.parse(versionFileCopy) == bumpedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        bumpedVersion << TestSetup.getTestExpectedMinorReleases()
    }

    def "Bumping project patch version should update version file to represent #bumpedVersion"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)

        when:
        TestUtil.evaluateProjectForReleaseTests(plugin, project, versionFileCopy.toURI().toURL())
        def bumpPatchTask = project.tasks.findByName(SemVerPlugin.getBumpPatchTaskName())
        bumpPatchTask.execute()

        then:
        notThrown(Exception)
        Version.Parser.instance.parse(versionFileCopy) == bumpedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        bumpedVersion << TestSetup.getTestExpectedPatchReleases()
    }
}
