package com.github.tagc.semver.tasks

import com.github.tagc.semver.Version

/**
 * Performs a minor bump on the version represented by a
 * project's version data file.
 *
 * This method modifies the version file data, so the changes
 * made are persistent.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class BumpMinorTask extends AbstractBumpTask {

    public BumpMinorTask() {
        super(Version.Category.MINOR)
        this.description = 'Performs a minor bump of the project version.'
    }
}