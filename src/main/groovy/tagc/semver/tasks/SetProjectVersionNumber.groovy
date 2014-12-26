package tagc.semver.tasks

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.ajoberstar.grgit.Grgit
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import tagc.semver.Version

class SetProjectVersionNumber extends DefaultTask {

    private static final String MASTER_BRANCH = "master"
    private static final Pattern VERSION_PATTERN = ~/(\d+)\.(\d+).(\d+)/

    @Input String versionFilePath
    private Grgit repo

    SetProjectVersionNumber() {
        this.group = 'semver'
        this.description = "Sets the semantic version number of the project."
    }

    @TaskAction
    void start() {
        try {
            this.repo = Grgit.open(project.file("$project.projectDir"))
        } catch(RepositoryNotFoundException e) {
            throw new GradleException("No Git repository can be found for this project")
        }

        def rawVersion = readRawVersion()

        if (isOnMasterBranch()) {
            project.version = rawVersion.toRelease().toString()
        } else {
            project.version = rawVersion.toDevelop().toString()
        }
        
        logger.quiet "Set project version to $project.version"
    }

    private Version readRawVersion() {
        def versionFile = new File(getVersionFilePath())
        if (!versionFile.exists()) {
            throw new GradleException("Missing version file: $versionFile.canonicalPath")
        }

        Matcher m = versionFile.text =~ VERSION_PATTERN

        if(!m) {
            throw new GradleException("Cannot extract version information from specified version file")
        }

        def builder = new Version.Builder()
        builder.setMajor(m[0][1].toInteger())
        builder.setMinor(m[0][2].toInteger())
        builder.setPatch(m[0][3].toInteger())
        builder.getVersion()
    }

    private boolean isOnMasterBranch() {
        def currentBranch = getCurrentBranch()
        logger.quiet "Current Git branch: $currentBranch"
        currentBranch == MASTER_BRANCH
    }

    private String getCurrentBranch() {
        repo.branch.current.name
    }
}
