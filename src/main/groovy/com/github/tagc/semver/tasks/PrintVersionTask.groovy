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

import groovy.transform.PackageScope

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Prints the string representation of the current project version to standard output.
 *
 * @author davidfallah
 * @since 0.4.0
 */
class PrintVersionTask extends DefaultTask {

    PrintVersionTask() {
        this.group = 'semver'
        this.description = 'Prints the current project version.'
    }

    @TaskAction
    void start() {
        logger.quiet output
    }

    @PackageScope
    getOutput() {
        return "Project version: $project.version"
    }
}
