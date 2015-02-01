package com.github.tagc.semver.tasks

import com.github.tagc.semver.Version

/**
 * Performs a patch bump on the version represented by a
 * project's version data file.
 *
 * This method modifies the version file data, so the changes
 * made are persistent.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class BumpPatchTask extends AbstractBumpTask {

    public BumpPatchTask() {
        super(Version.Category.PATCH)
        this.description = 'Performs a patch bump of the project version.'
    }
}