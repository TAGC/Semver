# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased][unreleased]

## [0.5.1] - 2015-02-08
### Changed
 - Make plugin depend on v0.5.0 of itself.

### Removed
 - `.bumpversion.cfg` as it's no longer needed.

## [0.5.0] - 2015-02-08
### Added
 - `CHANGELOG.md`.
 - `:printVersion` - task that prints the string representation of the version that the Semver plugin has set Gradle's `project.version` property to.
 - `:bump`, `:bumpMajor`, `:bumpMinor`, `:bumpPatch` - tasks that bump the project version and update the file containing the version data.
 - `:changeVersion` - task that is called whenever the project version is modified, which can perform custom actions or be depended upon by other tasks.
 - License headers to source files.

### Changed
 - Major refactoring of code.

## [0.4.0] - 2014-12-31
### Added
 - Additional functionality within `com.github.tagc.semver.Version`.

### Fixed
 - Snapshot version calculation.

## [0.3.1] - 2014-12-30
### Changed
 - Update version of its own plugin that Semver plugin depends on.

## [0.3.0] - 2014-12-30
### Added
 - Additional flexibility in parsing version data from files.

### Changed
 - Set `project.version` in configuration phase of the build lifecycle rather than in the execution phase.

### Removed
 - `:setProjectVersionNumber` task.

## [0.2.2] - 2014-12-28
### Added
 - `LICENSE.md`.

### Changed
 - Make plugin depend upon itself for its own versioning.

## [0.2.1] - 2014-12-28
### Fixed
 - Bug with version string representation.

## [0.2.0] - 2014-12-28
### Added
 - Version representation class.
 - `:setProjectVersionNumber` - task for setting `project.version` during the execution phase of the project lifecycle.
 - `README.md`.

## 0.1.0 - 2014-12-17

[unreleased]: https://github.com/TAGC/Semver/compare/v0.5.1...HEAD
[0.5.1]: https://github.com/TAGC/Semver/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/TAGC/Semver/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/TAGC/Semver/compare/v0.3.1...v0.4.0
[0.3.1]: https://github.com/TAGC/Semver/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/TAGC/Semver/compare/v0.2.2...v0.3.0
[0.2.2]: https://github.com/TAGC/Semver/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/TAGC/Semver/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/TAGC/Semver/compare/v0.1.0...v0.2.0
