package tagc.semver.tasks

import org.ajoberstar.grgit.Grgit
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import tagc.semver.Version

class GetProjectVersionTask extends DefaultTask {
    
    private static final String MASTER_BRANCH = "master"
    
    @Input private final File versionFile
    private final Grgit repo
    
    GetProjectVersionTask() {
        this.description = "Returns the current semantic version number of the project"
        this.repo = Grgit.open(project.file('.'))
    }
    
    @TaskAction
    void setProjectVersionNumber() {
        def rawVersion = readRawVersion()
        
        if (isOnMasterBranch()) {
            project.version = rawVersion.toRelease().toString()   
        } else {
            project.version = rawVersion.toDevelop().toString()
        }
    }
    
    private Version readRawVersion() {
        def versionFile = getVersionFile()
        if (!versionFile.exists()) {
            throw new GradleException("Missing version file: $versionFile.canonicalPath")
        }
        
        final Properties versionProps = new Properties()
        
        versionFile.withInputStream { stream ->
            versionProps.load(stream)
        }
        
        def builder = new Version.Builder()
        builder.setMajor(versionProps.major.toInteger())
        builder.setMinor(versionProps.minor.toInteger())
        builder.setPatch(versionProps.patch.toInteger())
        builder.getVersion()
    }
    
    private boolean isOnMasterBranch() {
        getCurrentBranch() == MASTER_BRANCH
    }

    private String getCurrentBranch() {
        repo.branch.current
    }   
}
