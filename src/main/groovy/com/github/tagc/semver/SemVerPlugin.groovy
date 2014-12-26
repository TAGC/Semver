package com.github.tagc.semver

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import com.github.tagc.semver.tasks.SetProjectVersionNumber;

/**
 * An {@link org.gradle.api.Plugin} class that handles the application of semantic
 * versioning logic to an {@link org.gradle.api.Project}
 * 
 * @author davidfallah
 * @since v0.1.0
 */
class SemVerPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = 'semver'

    @Override
    public void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, SemVerPluginExtension)
        addTasks(project)
    }

    private void addTasks(Project project) {
        project.tasks.withType(SetProjectVersionNumber) {
            def extension = project.extensions.findByName(EXTENSION_NAME)
            conventionMapping.versionFilePath = { extension.versionFilePath }
        }

        final Task setVersionTask = project.task('setProjectVersionNumber', type: SetProjectVersionNumber)

        project.afterEvaluate {
            if(project.plugins.hasPlugin('java')) {
                project.tasks.findByName('compileJava').dependsOn(setVersionTask)
            }

            if(project.plugins.hasPlugin('groovy')) {
                project.tasks.findByName('compileGroovy').dependsOn(setVersionTask)
            }
        }
    }
}
