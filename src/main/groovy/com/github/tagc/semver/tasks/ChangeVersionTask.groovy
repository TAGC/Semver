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
