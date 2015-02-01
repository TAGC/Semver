package com.github.tagc.semver.tasks

import com.github.tagc.semver.Version

/**
 * Performs a major bump on the version represented by a
 * project's version data file.
 *
 * This method modifies the version file data, so the changes
 * made are persistent.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class BumpMajorTask extends AbstractBumpTask {

    public BumpMajorTask() {
        super(Version.Category.MAJOR)
        this.description = 'Performs a major bump of the project version.'
    }
}