<img src='http://tagc.github.io/Semver/images/semverLogo.svg' height='175px'/>

A simple semantic versioning Gradle plugin for Git-controlled projects. You can use this plugin to help Gradle keep track of which version to consider the current state of your project as. This plugin has been primarily developed just so that I get experience with developing Gradle plugins.

As of Version 0.3.0, this plugin now depends on itself for its own version numbering management. If you would like a practical example demonstrating usage of this plugin, just look in this project's build script!

[![Build Status](https://travis-ci.org/TAGC/Semver.svg)](https://travis-ci.org/TAGC/Semver) [ ![Download](https://api.bintray.com/packages/tagc/gradle-plugins/gradle-semver-plugin/images/download.svg) ](https://bintray.com/tagc/gradle-plugins/gradle-semver-plugin/_latestVersion)

Is this plugin for you?
---
You might consider using this plugin or one like it if you're developing projects using Git as your version control system and Gradle as your build automation tool. This plugin relieves some of the need to manually update the `project.version` property of your Gradle build scripts as your projects pass through various stages of development - instead, the plugin looks at version data within a file you specify and the Git branch your project is on and uses that information to work out what to set `project.version` to.

This plugin is designed with Vincent Driessen's [Git Flow workflow strategy](http://nvie.com/posts/a-successful-git-branching-model/) in mind, and later versions of this plugin can be expected to provide better support for this style of development.

Setup
---

To configure your project to use this plugin, add the following to your Gradle build script:

```
apply plugin: 'semver'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        // Replace <version> with preferred plugin version
        classpath 'com.github.tagc:semver-plugin:<version>'
    }
}

semver {
    versionFilePath = 'version.properties' // Or whatever other file stores version information

    // Optional
    snapshotBump = 'PATCH' // Or 'MINOR' or 'MAJOR'

    // Optional
    forceBump = false // Or true
}
```

The `versionFilePath` property must point to a file that stores the 'raw' version string for your project in the form `<major>.<minor>.<patch>`, e.g. `version='1.2.3'` or simply `1.2.3`.

The `snapshotBump` property is optional and specifies what snapshot versions the plugin should derive from the latest released versions. For instance, if the last released version of a project was `v0.1.0` and `snapshotBump=PATCH` was specified, any development work will be treated as `v0.1.1-SNAPSHOT`. If `snapshotBump=MINOR` is specified instead, development work will be treated as `v0.2.0-SNAPSHOT`, and for `snapshotBump=MAJOR` it would be treated as `v1.0.0-SNAPSHOT`. This helps reconcile the differences between workflow strategies such as [Git Flow](http://nvie.com/posts/a-successful-git-branching-model/) which expect release versions to be decided only as releases are about to be made with [Maven's philosophy of always deciding on (or guessing) what the next release version will be](http://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN401). Predict whether your next release will introduce minor bug fixes, new (but backwards-compatible) features, or breaking changes, and set `snapshotBump` accordingly. By default (when this property is not explicitly set), `snapshotBump` will be treated as set to `PATCH`.

Version 0.5.0 of this plugin introduces tasks that allow you to bump the version of your projects without the need to rely on third-party tools or do so manually. These tasks are opinionated about whether a particular version bump should be allowed under a given set of circumstances. Setting `forceBump` to true overrides this behaviour and forces these tasks to bump the version. By default, this setting is false.

Description
---

This plugin will attempt to extract a 'raw' version string from a specified file (e.g. `1.2.3`) and then use the following rules to convert this into a version string that is ultimately set for the project:
  - if the plugin detects that you are currently on the **master** Git branch, it will save that version string as-is to your project's `project.version` property.
  - else if the plugin detects that you are on a **hotfix** or **release** branch, it will try to parse the version associated with that branch and use the following sub-rules to set the project version:
    - if the interpreted version of the branch is greater than the raw version (e.g. `hotfix/1.2.4` with raw version `1.2.3`), the plugin will encourage you to bump your project version with `:bump` to match the branch version but until then use the appropriately-bumped *snapshot* equivalent of the raw version (see below).
    - else if the interpreted version of the branch is equal to the raw version (e.g. `release/2.0.0` with raw version `2.0.0`), the plugin will use the *release* equivalent of the raw version.
    - otherwise (if the branch version is less than the raw version or if the branch version cannot be interpreted), the plugin will use the appropriately-bumped *snapshot* equivalent of the raw version (see below).
  - else the plugin detects that you are currently on any other Git branch, it will generate an appropriately-bumped version with '-SNAPSHOT' appended to the corresponding version string (e.g. `1.2.4-SNAPSHOT`). This is then applied to your project's `project.version` property.

This behaviour is performed during the configuration stage of the build lifecycle to allow the `project.version` property to be evaluated properly by other parts of the project's build script. If you find that `version.property` is being evaluated as `unspecified` in other parts of your build script, you can try wrapping instances of it in a `project.afterEvaluate` closure, as follows:

```
project.afterEvaluate {
    assert project.version
    version = project.version
}
```

#### Tasks

The following tasks are included with this plugin under the 'semver' group:


- **:bump** - Using Git Flow, you should bump your project version whenever you start a new release or hotfix branch. With a branch like this checked-out, you can invoke this task to interpret the version associated with the branch and update the current project version to it. This will overwrite the data stored in the specified version file. This task will fail for any of the following reasons:
  - a version cannot be interpreted from the name of the checked-out branch.
  - the currently checked-out Git branch is neither a hotfix or release branch. This can be overridden with `forceBump`.
  - the version associated with the branch cannot be reached from the current project version through a single major, minor or patch bump (e.g. from a current version of `1.2.3`, only `1.2.4`, `1.3.0` and `2.0.0` are considered 'reachable'). This can be overridden with `forceBump`.
- **:bumpMajor** - This task can be used to perform a major bump of the current version. This task is useful if `:bump` fails to interpret the current branch version or you want more control over how the version is bumped. This task will fail for any of the following reasons:
  - the currently checked-out Git branch is neither a hotfix or release branch. This can be overridden with `forceBump`.
- **:bumpMinor** - This task can be used to perform a minor bump of the current version. This task is useful if `:bump` fails to interpret the current branch version or you want more control over how the version is bumped. This task will fail for any of the following reasons:
  - the currently checked-out Git branch is neither a hotfix or release branch. This can be overridden with `forceBump`.
- **:bumpPatch** - This task can be used to perform a patch bump of the current version. This task is useful if `:bump` fails to interpret the current branch version or you want more control over how the version is bumped. This task will fail for any of the following reasons:
  - the currently checked-out Git branch is neither a hotfix or release branch. This can be overridden with `forceBump`.
- **:printVersion** - This task will print the string representation of the current project version. It will always run after any of the preceding tasks are executed.
- **:changeVersion** - By default, this task has no behaviour. However, all tasks that modify the project version depend on this task to execute, so you can add custom behaviour to this task or make other tasks depend on it to perform any setup activity before the project version gets modified.

#### Recommended Usage

The recommended usage for this plugin is different depending on the version of the plugin you wish to use. Read the recommendations for the section that includes the plugin version you're interested in.

##### For versions >= 0.5.0

 1. Adopt the [Git Flow workflow strategy](http://nvie.com/posts/a-successful-git-branching-model/) or something similar. The [Git Flow Github project](https://github.com/nvie/gitflow) can facilitate this workflow strategy by providing Git flow commands to the terminal.
 2. After creating a release or hotfix branch, run `gradle bump` through terminal. This should update the project version to the appropriate point. If this doesn't work, try `gradle bumpMajor|bumpMinor|bumpPatch` instead.
 3. When on a development branch, make sure to set the `snapshotBump` property based on what you estimate the next release version to be.

##### For versions < 0.5.0

  1. Download and install [bumpversion](https://pypi.python.org/pypi/bumpversion/0.5.0). This can be done from terminal via the Python package installer (pip).
  2. Add a `.bumpversion.cfg` to your project directory, such as the following:
  ```
  [bumpversion]
  current_version = 0.1.0
  commit = False
  tag = False
  
  [bumpversion:file:version.properties]
  ```
  3. Add a `version.properties` file to your project directory, such as the following:

  ```
  version='0.1.0'
  ```
  4. Adopt the [Git Flow workflow strategy](http://nvie.com/posts/a-successful-git-branching-model/) or something similar, where release versions are committed on **master** and snapshot versions are pulled from other branches. The [Git Flow Github project](https://github.com/nvie/gitflow) can facilitate this workflow strategy by providing Git flow commands to the terminal.

You can use the command `bumpversion major|minor|patch` to bump your project version. This will update the version string stored in `version.properties` (or any other file you prefer), which can then be accessed and processed by the SemVer plugin. If you're on any branch but **master**, this plugin will append `-SNAPSHOT` to that string and save it to your project's `project.version` property, as described above.

Project Health Metrics
---

This project is continuously analysed by Jacoco (to calculate test code coverage) and CodeNarc (to identify coding issues). These analyses are automatically performed every 24 hours by Jenkins and the reports published to this project's Github page. You can check when the last set of reports was generated by reviewing the generation date in the CodeNarc report below.

Information about code coverage can be found in the [Jacoco report](http://tagc.github.io/Semver/health/jacoco/index.html).

Information about coding issues can be found in the [CodeNarc report](http://tagc.github.io/Semver/health/codenarc/main.html).


Documentation
---

The documentation for this project is automatically regenerated every 24 hours by Jenkins and published to this project's Github page. This is done at the same time as the project health reports are generated, so you can see when this documentation was last generated by checking the generation date for the CodeNarc report.

You can access the documentation for this project from [here](http://tagc.github.io/Semver/docs/groovydoc/index.html).

Acknowledgements
---

The development of this project has been largely based on work done by:
 - [Andrew Oberstar](https://github.com/ajoberstar) for his [Gradle-Git](https://github.com/ajoberstar/gradle-git) project, which this plugin relies on to query the current Git branch.
 - [Benjamin Muschko](http://www.gradleware.com/team/benjamin-muschko/) for his book, *Gradle in Action*, from which I've been trying to learn Gradle.