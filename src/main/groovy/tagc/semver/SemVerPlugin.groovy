package tagc.semver

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import tagc.semver.tasks.SetProjectVersionNumber

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
