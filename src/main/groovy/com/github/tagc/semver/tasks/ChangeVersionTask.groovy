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

import org.gradle.api.DefaultTask

/**
 * All concrete instances of {@link com.github.tagc.semver.tasks.AbstractBumpTask AbstractBumpTask}
 * ought to depend on the execution of this task.
 * <p>
 * By default, this task performs no behaviour by itself, but additional actions can be added to it
 * and other tasks can be configured to depend on it.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class ChangeVersionTask extends DefaultTask {

    ChangeVersionTask() {
        this.group = 'semver'
        this.description = 'Performs additional actions related to project version changes.'
    }

}
