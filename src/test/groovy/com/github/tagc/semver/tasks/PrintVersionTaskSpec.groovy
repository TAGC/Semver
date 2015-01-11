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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Unroll

import com.github.tagc.semver.SemVerPlugin
import com.github.tagc.semver.Version

@Unroll
class PrintVersionTaskSpec extends Specification {

    private Project project
    private Plugin plugin
    private PrintVersionTask printVersionTask

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new SemVerPlugin()
    }

    def "PrintVersion task should print appropriate string representation when project.version=#version"() {
        given: "A project with the semver plugin applied and project.version set to #version"
        plugin.apply(project)
        project.version = version
        def printVersionTask = project.tasks.findByName(SemVerPlugin.getPrintVersionTaskName())
        assert printVersionTask != null

        expect: "The version's string representation should be within the task's output"
        printVersionTask.getOutput().contains(version.toString())

        where:
        combination << GroovyCollections.combinations([
            (1..5),
            (1..5),
            (1..5),
            [true, false]
        ])
        version = new Version(major:combination[0], minor:combination[1], patch:combination[2], release:combination[3])
    }
}