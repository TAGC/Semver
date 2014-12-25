package tagc.semver.tasks

import org.ajoberstar.grgit.Grgit
import org.gradle.api.GradleException
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
    
    def "Referencing non-existent file throws exception"() {
        final String nonExistentFilePath = '.nonexistent'
        
        expect:
        !new File(nonExistentFilePath).exists()
        project.tasks.findByName(TASK_NAME) == null
        
        when:
        final Task task = project.task(TASK_NAME, type: SetProjectVersionNumber) {
            versionFilePath = nonExistentFilePath
        }
        
        task.start()
        
        then:
        project.tasks.findByName(TASK_NAME) != null
        Exception e = thrown(GradleException)
        e.toString() =~ 'Git'
    }
    
    def "Referencing existent file with incorrect format throws exception"() {
        final String filePath = 'invalidTestVersion.properties'
        final Grgit grgit = Grgit.init(dir: new File("$project.projectDir"))
        
        expect:
        new File(filePath).exists()
        
        when:
        final Task task = project.task(TASK_NAME, type: SetProjectVersionNumber) {
            versionFilePath = filePath
        }
        
        task.start()
        
        then:
        project.tasks.findByName(TASK_NAME) != null
        Exception e = thrown(GradleException)
        e.toString() =~ 'extract'
        
        cleanup:
        grgit.close()
    }
    
    def "Referencing a valid version file should succeed"() {
        final String filePath = 'validTestVersion.properties'
        final Grgit grgit = Grgit.init(dir: new File("$project.projectDir"))
        
        expect:
        new File(filePath).exists()
        new File(filePath).text =~ /version='1\.2\.3'/
        
        when:
        final Task task = project.task(TASK_NAME, type: SetProjectVersionNumber) {
            versionFilePath = filePath
        }
        
        task.start()
        
        then:
        project.tasks.findByName(TASK_NAME) != null
        project.version ==~ /1\.2\.3.*/
        
        cleanup:
        grgit.close()
    }
}
