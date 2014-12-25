package tagc.semver.tasks

import org.ajoberstar.grgit.Grgit
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import tagc.semver.Version

class SetProjectVersionNumber extends DefaultTask {
    
    private static final String MASTER_BRANCH = "master"
    
    @Input String versionFilePath
    private Grgit repo
    
    SetProjectVersionNumber() {
        this.group = 'semver'
        this.description = "Sets the semantic version number of the project."
    }
    
    @TaskAction
    void setProjectVersionNumber() {
        try {
            this.repo = Grgit.open(project.file('.'))
        } catch(RepositoryNotFoundException e) {
            throw new GradleException("No Git repository can be found for this project")
        }
        
        def rawVersion = readRawVersion()
        
        if (isOnMasterBranch()) {
            project.version = rawVersion.toRelease().toString()   
        } else {
            project.version = rawVersion.toDevelop().toString()
        }
    }
    
    private Version readRawVersion() {
        def versionFile = new File(getVersionFilePath())
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
