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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.test.util.TestSetup
import com.github.tagc.semver.test.util.TestUtil

/**
 * Test specification for {@link com.github.tagc.semver.SemVerPlugin SemVerPlugin}.
 *
 * @author davidfallah
 * @since 0.3.1
 */
@Unroll
class SemVerPluginSpec extends Specification {

    private static final String EXTENSION_NAME = SemVerPlugin.EXTENSION_NAME
    Project project
    Plugin<Project> plugin

    /*
     *  Test setups
     */
    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    /*
     * Tests
     */
    def "SemVer plugin extension should exist after applying plugin"() {
        assert project.extensions.findByName(EXTENSION_NAME) == null
        plugin.apply(project)

        expect:
        project.extensions.findByName(EXTENSION_NAME) != null
    }

    def "PrintVersion task should exist after applying plugin"() {
        assert project.tasks.findByName(SemVerPlugin.getPrintVersionTaskName()) == null
        plugin.apply(project)

        expect:
        project.tasks.findByName(SemVerPlugin.getPrintVersionTaskName()) != null
    }

    def "Release version for version data in #versionFilePath should be #expectedVersion"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)

        when:
        TestUtil.evaluateProjectForReleaseTests(plugin, project, url)

        then:
        project.version == expectedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        expectedVersion << TestSetup.getTestExpectedReleases()
    }

    def "Snapshot version for version data in #versionFilePath should be #expectedVersion when bumping by major"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)

        when:
        TestUtil.evaluateProjectForSnapshotTests(plugin, project, url, Version.Category.MAJOR)

        then:
        project.version == expectedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        expectedVersion << TestSetup.getTestExpectedMajorSnapshots()
    }

    def "Snapshot version for version data in #versionFilePath should be #expectedVersion when bumping by minor"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)

        when:
        TestUtil.evaluateProjectForSnapshotTests(plugin, project, url, Version.Category.MINOR)

        then:
        project.version == expectedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        expectedVersion << TestSetup.getTestExpectedMinorSnapshots()
    }

    def "Snapshot version for version data in #versionFilePath should be #expectedVersion when bumping by patch"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)

        when:
        TestUtil.evaluateProjectForSnapshotTests(plugin, project, url, Version.Category.PATCH)

        then:
        project.version == expectedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        expectedVersion << TestSetup.getTestExpectedPatchSnapshots()
    }

    /*
     * By default assume bump patch.
     */
    def "Snapshot version for version data in #versionFilePath should be #expectedVersion by default"() {
        given:
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)

        when:
        TestUtil.evaluateProjectForSnapshotTests(plugin, project, url, null)

        then:
        project.version == expectedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)

        where:
        versionFilePath << TestSetup.getTestVersionFilePaths()
        expectedVersion << TestSetup.getTestExpectedPatchSnapshots()
    }
}
