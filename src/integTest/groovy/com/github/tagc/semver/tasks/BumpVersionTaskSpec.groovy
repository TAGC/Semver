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
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.SemVerPlugin
import com.github.tagc.semver.Version
import com.github.tagc.semver.test.util.TestSetup
import com.github.tagc.semver.test.util.TestUtil

@Unroll
class BumpVersionTaskSpec extends Specification {

    private static final String RELEASE_BRANCH = 'release/1.0.0'
    private static final String HOTFIX_BRANCH = 'hotfix/1.0.0'
    private static final String MASTER_BRANCH = 'master'
    private static final String DEVELOP_BRANCH = 'develop'

    private Project project
    private Plugin plugin

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    def "Bumping project by #category should update #versionFilePath to represent #bumpedVersion when on #branch"() {
        given: "A copy of a file containing version data"
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)

        when: "We invoke the appropriate version bump task for a project on the given branch"
        TestUtil.beginConfiguringProjectWithBranch(plugin, project, branch)
        TestUtil.configureProjectVersionPath(project, versionFileCopy.toURI().toURL())
        TestUtil.configureProjectForceBump(project, false)
        TestUtil.finishConfiguringProject(project)
        def bumpVersionTask = getBumpVersionTaskForCategory(project, category)
        bumpVersionTask.execute()

        then: "The version data kept within the file should have been updated"
        notThrown(Exception)
        Version.Parser.instance.parse(versionFileCopy) == bumpedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        [category, versionFilePath, bumpedVersion, branch] << \
            getVersionTestData(RELEASE_BRANCH) + getVersionTestData(HOTFIX_BRANCH)
    }

    def "Bumping project should cause exception to be thrown when on #branch and forceBump is false"() {
        given: "A copy of a file containing version data and its text"
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)
        def originalText = versionFileCopy.text

        when: "We invoke the appropriate version bump task for a project on the given branch with forceBump set false"
        TestUtil.beginConfiguringProjectWithBranch(plugin, project, branch)
        TestUtil.configureProjectVersionPath(project, versionFileCopy.toURI().toURL())
        TestUtil.configureProjectForceBump(project, false)
        TestUtil.finishConfiguringProject(project)
        def bumpVersionTask = getBumpVersionTaskForCategory(project, category)
        bumpVersionTask.execute()

        then: "An exception should be thrown and the version file should not have been changed"
        TaskExecutionException e = thrown()
        e.cause in IllegalStateException
        versionFileCopy.text == originalText

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        [category, versionFilePath, bumpedVersion, branch] << \
            getVersionTestData(MASTER_BRANCH) + getVersionTestData(DEVELOP_BRANCH)
    }

    def "Bumping project by #category should update #versionFilePath to represent #bumpedVersion when on #branch and forceBump is true"() {
        given: "A copy of a file containing version data"
        URL url = TestUtil.getVersionFileAsResource(versionFilePath)
        File versionFileCopy = project.file("build/temp/$versionFilePath")
        FileUtils.copyURLToFile(url, versionFileCopy)

        when: "We invoke the appropriate version bump task for a project on the given branch with forceBump set true"
        TestUtil.beginConfiguringProjectWithBranch(plugin, project, branch)
        TestUtil.configureProjectVersionPath(project, versionFileCopy.toURI().toURL())
        TestUtil.configureProjectForceBump(project, true)
        TestUtil.finishConfiguringProject(project)
        def bumpVersionTask = getBumpVersionTaskForCategory(project, category)
        bumpVersionTask.execute()

        then: "The version data kept within the file should have been updated"
        notThrown(Exception)
        Version.Parser.instance.parse(versionFileCopy) == bumpedVersion

        cleanup:
        assert TestUtil.cleanupGitDirectory(project)
        versionFileCopy.delete()

        where:
        [category, versionFilePath, bumpedVersion, branch] << \
            getVersionTestData(MASTER_BRANCH) + getVersionTestData(DEVELOP_BRANCH)
    }

    /*
     * Return a data list like this:
     *  [
     *      [ PATCH, versionFile1, bumpedPatchFile1, branch ], [ PATCH, versionFile2, bumpedPatchFile2, branch ] ...
     *      [ MINOR, versionFile1, bumpedMinorFile1, branch ], [ MINOR, versionFile2, bumpedMinorFile2, branch ] ...
     *      [ MAJOR, versionFile1, bumpedMajorFile1, branch ], [ MAJOR, versionFile2, bumpedMajorFile2, branch ] ...
     *  ]
     */
    private getVersionTestData(String branch) {
        def testData = []
        def versionFiles = TestSetup.getTestVersionFilePaths()

        Version.Category.values().each { category ->
            def expectedReleases = TestSetup.getTestExpectedReleasesForCategory(category)
            def pairs = [
                versionFiles,
                expectedReleases
            ].transpose()

            pairs.each { versionFile, expectedRelease ->
                testData << [
                    category,
                    versionFile,
                    expectedRelease,
                    branch
                ]
            }
        }

        return testData
    }

    private getBumpVersionTaskForCategory(Project project, Version.Category category) {
        switch(category) {
            case Version.Category.MAJOR:
                return project.tasks.findByName(SemVerPlugin.getBumpMajorTaskName())
            case Version.Category.MINOR:
                return project.tasks.findByName(SemVerPlugin.getBumpMinorTaskName())
            case Version.Category.PATCH:
                return project.tasks.findByName(SemVerPlugin.getBumpPatchTaskName())
            default:
                throw new AssertionError("Invalid version category: $category")
        }
    }
}
