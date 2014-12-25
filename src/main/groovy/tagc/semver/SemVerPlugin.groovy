package tagc.semver

import org.gradle.api.Plugin
import org.gradle.api.Project

class SemVerPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = 'semver'
    
    @Override
    public void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, SemVerPluginExtension)
        addTasks(project)
    }
    
    private void addTasks(Project project) {
        project.tasks.withType(GetProjectVersionNumber) {
            def extension = project.extensions.findByName(EXTENSION_NAME)
            conventionMapping.versionFile = { extension.versionFile }
        }
        
        project.task('GetProjectVersionNumber', type: GetProjectVersionNumber)
    }
}
