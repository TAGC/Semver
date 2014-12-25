package tagc.semver.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

class SetProjectVersionNumberSpec extends Specification {
    static final TASK_NAME = 'setProjectVersionNumber'
    Project project
    
    def setup() {
        project = ProjectBuilder.builder().build()
    }
    
    def "Adds project version number setting task"() {
        expect:
        project.tasks.findByName(TASK_NAME) == null
        
        when:
        project.task(TASK_NAME, type: SetProjectVersionNumber) {
            versionFilePath = 'version.properties'
        }
        
        then:
        Task task = project.tasks.findByName(TASK_NAME)
        task != null
        task.group == 'semver'
        task.versionFilePath == 'version.properties'
    }
}
