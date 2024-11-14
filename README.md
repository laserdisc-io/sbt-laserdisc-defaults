# sbt-laserdisc-defaults

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/laserdisc-io/sbt-laserdisc-defaults/build.yaml)](https://github.com/laserdisc-io/sbt-laserdisc-defaults/actions/workflows/build.yaml)
[![GitHub Release](https://img.shields.io/github/v/release/laserdisc-io/sbt-laserdisc-defaults)](https://github.com/laserdisc-io/sbt-laserdisc-defaults/releases/latest)

A plugin to reduce the boilerplate in many of the simpler laserdisc projects (but can be used in any sbt project).  
It auto-configures things like sbt & scala versioning, cross compiling, scalafmt & git configuration and more.

The plugin can be used as-is, or extended into your own custom plugin, picking and choosing the defaults you like!

## Direct Usage

Add the following to `project/plugins.sbt` :

```sbt
addSbtPlugin("io.laserdisc" % "sbt-laserdisc-defaults" % LATEST-VERSION-HERE)

// note: this plugin brings in sbt-scalafmt, sbt-git and sbt-native-packager automatically!
```

Then, in your `build.sbt` enable the plugin (either on the single _project_ it contains, or in the case of a multi-module build, the root _project_)

```sbt
lazy val root = (project in file("."))
	// other project configuration 
	.enablePlugins(LaserDiscDefaultsPlugin)
```

> **Note**
> Any settings in your `build.sbt` **override** those set by this plugin. 


## What it Does

When SBT loads your project, the [LaserDiscDefaultsPlugin](src/main/scala/laserdisc/sbt/LaserDiscDefaultsPlugin.scala) will automatically perform the following:

**Adds Additional Plugins**

* [sbt-scalafmt](https://github.com/scalameta/sbt-scalafmt) - for applying formatting standards automatically to source code
* [sbt-git](https://github.com/sbt/sbt-git) - automatic versioning based on the current commit hash/tag
* [sbt-native-packager](https://github.com/sbt/sbt-native-packager) - 

### Categories

**Apply [Core](src/main/scala/laserdisc/sbt/category/Core.scala) Settings**

* Apply a bunch of default values: 
    * `scalaVersion` to a recent version
    * `organization` to `io.laserdisc`  
    * `organizationName` to `LaserDisc`

* Add some common command aliases:
    * **`sbt format`** - formats all Scala and SBT sources (According to `.scalafmt.conf`, see below)
    * **`sbt checkFormat`** - ensures all Scala and SBT sources are formatted correctly
    * **`sbt build`** - shortcut for `checkFormat`, `clean`, then `test`
    * **`sbt release`** - shortcut for `build` (above) then `publish`.


**Apply [Compiler](src/main/scala/laserdisc/sbt/category/Compiler.scala) Settings**

* Sets the compiler to build for scala 3.  This can be changed using `CompileTarget`:
  * ```sbt
    import laserdisc.sbt.CompileTarget

    ThisBuild / laserdiscCompileTarget := CompileTarget.Scala2Only   // builds only for scala 2
    ThisBuild / laserdiscCompileTarget := CompileTarget.Scala3Only   // builds only for scala 3 (default) 
    ThisBuild / laserdiscCompileTarget := CompileTarget.Scala2And3   // cross compile both
    ```
    Remember that you need to use `+` in front of compilation-triggering tasks to trigger cross-compilation.
    The `build` alias added by this plugin automatically invokes `+test`.
* Apply our standard set of `scalacOptions` compiler and linting configurations (for each scala version). 
  * This includes `-Xfatal-warnings` which fails the build by default if warnings are present.
    * This can be disabled via two mechanisms:
        * `set ThisBuild / laserdiscFailOnWarn := false` in the top level of your `build.sbt` (don't check this in!)
        * passing `-DlaserdiscFailOnWarn=false` as [a SBT option](https://www.scala-sbt.org/1.x/docs/Command-Line-Reference.html#sbt+JVM+options+and+system+properties) (useful for local dev)
  * For Scala 2, this includes the [better-monadic-for](https://github.com/oleg-py/better-monadic-for) and [kind-projector](https://github.com/typelevel/kind-projector) compiler plugins, which aren't necessary for Scala 3.

**Generate [.gitignore](src/main/scala/laserdisc/sbt/category/GitIgnore.scala)** 

* This file should be checked in (for instant IDE support when opening the project before sbt has initialized)
* The templating source is the [`.gitignore`](.gitignore) that configures this project.
*  It is possible to disable this functionality (temporarily, please!) by doing the following
    * Set `ThisBuild / laserdiscGitConfigGenOn:=false` in the top level of your `build.sbt` 
    * Pass `-DlaserdiscGitConfigGenOn=false` as [a SBT option](https://www.scala-sbt.org/1.x/docs/Command-Line-Reference.html#sbt+JVM+options+and+system+properties)

**Generate [.scalafmt.conf](src/main/scala/laserdisc/sbt/category/ScalaFmt.scala)**

* This file should be checked in (for instant IDE support when opening the project before sbt has initialized)
* The templating source is the  [`.scalafmt.conf`](.scalafmt.conf) that configures this project.
* It can be useful to disable this generation when trialing new scalafmt configurations locally.
    * However, please **commit updated configurations to this project** to maintain consistency!
    * Set `ThisBuild / laserdiscScalaFmtGenOn:=false` in the top level of your `build.sbt` 
    * Pass `-DlaserdiscScalaFmtGenOn=false` as [a SBT option](https://www.scala-sbt.org/1.x/docs/Command-Line-Reference.html#sbt+JVM+options+and+system+properties)

**Set/Upgrade the sbt version in [project/build.properties](src/main/scala/laserdisc/sbt/category/SbtVersion.scala)**

* This file should be checked in.  
  * **You should reload `sbt` if the plugin changes the `sbt.version` value**
* The templating source is this plugin's  [`project/build.properties`](project/build.properties) file.
* If the `sbt.version` in the consuming project is newer that what is in [`project/build.properties`](project/build.properties), the file will not be templated. 
  * However, be a good citizen in that case, and **upgrade `sbt` in this project** so others get the upgrade!
* Set `ThisBuild / laserdiscSBTVersionGenOn:=false` in the top level of your `build.sbt` to disable this functionality (only if it causes issues). 


**Validate compliance with LaserDisc [Standards](src/main/scala/laserdisc/sbt/category/Standards.scala) Settings**

* Fails the `dist` task if [CODEOWNERS](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners) file is missing or empty.  

## Extending Your Own

This SBT build comprises two modules:

* [plugin](plugin)<br>
  * This module builds and publishes the `sbt-laserdisc-defaults` SBT plugin.
  * There is minimal code in this module, just the [concrete implementation](./plugin/src/main/scala/laserdisc/sbt/LaserDiscDefaultsPlugin.scala) of the shared code (next)

* [plugin-shared](plugin-shared)<br>
  * All the logic of the plugin is here, rolling up under an extendable `LaserDiscDefaultsPluginBase` abstract implementation.
  * This library is published as a dependency JAR `io.laserdisc:sbt-laserdisc-defaults-shared` for custom implementations to extend.

To build your own sbt plugin:

1. Define an SBT plugin project, with a dependency on the shared library, and enable all the relevant plugins:
   ```sbt
   lazy val root = project
    .in(file("."))
    .settings(
        sbtPlugin    := true,
        organization := "com.modaoperandi",
        name         := "sbt-moda-defaults",                        
        addSbtPlugin("io.laserdisc" % "sbt-laserdisc-defaults-shared" % "<version>")
        .... etc ...            
   ```
2. Then, create your implementation of [`LaserDiscDefaultsPluginBase`](./plugin-shared/src/main/scala/laserdisc/sbt/LaserDiscDefaultsPluginBase.scala).   The `sbt-laserdisc-defaults` plugin does this, so look [at the source](./plugin/src/main/scala/laserdisc/sbt/LaserDiscDefaultsPlugin.scala) for the actual code, but at a high level, it looks like this: 
   ```scala

   object LaserDiscDefaultsPlugin extends LaserDiscDefaultsPluginBase {
    
    // define the settings keys with your desired naming strategy
    object autoImport {
      lazy val laserdiscFailOnWarn      = settingKey[Boolean](Compiler.FailOnWarnKeyDesc)
      lazy val laserdiscCompileTarget   = settingKey[CompileTarget](Compiler.CompileTargetKeyDesc)
      // .. etc ..    
    }
    
    // define the publishing settings everyone who uses this plugin should have
    private val laserdiscDefaults = new GithubPublishDefaults {
      override def githubOrg: String          = "springfield-nuclear"
      override def orgName: String            = "Springfield Nuclear Power Plant"
      override def groupId: String            = "com.simpsonsarchive.nuclear"
      override def licenseCheck: LicenseCheck = LicenseRequired
    }
    
    // for context if logging/errors is neccessary (use https://github.com/sbt/sbt-buildinfo) 
    override implicit val pluginCtx: PluginContext = PluginContext(
      pluginName = PluginBuildInfo.name,
      pluginVersion = PluginBuildInfo.version,
      pluginHomepage = "https://github.com/springfield-nuclear/springfield-nuclear-plant"
    )
    
    // select all the category implementation you want (or create your own), passing in the key defs from above
    override val categories: Seq[DefaultsCategory] = Seq(
      Publishing(laserdiscDefaults, laserdiscPublishDefaults, laserdiscRepoName),      
      Compiler(laserdiscFailOnWarn, laserdiscCompileTarget),
      Standards(),      
      Core() // keep last, so the warning message about defaults being used shows first
    )
    
   }


   ```
   
And that's it! 


## Releasing This Plugin

Draft [a new release](https://github.com/laserdisc-io/sbt-laserdisc-defaults/releases/new), ensuring the format of the release follows the `v1.2.3` format (note the `v` prefix), and the appropriate [Github Action](.github/workflows/release.yaml) will publish version `1.2.3` (without the v) to sonatype.

## Developing Tips

An SBT plugin is developed as an SBT project just like a regular scala app, but some notes for anyone wanting to contribute:

* It helps to have some familiarity with [using existing scala plugins](https://www.scala-sbt.org/1.x/docs/Using-Plugins.html) 
 
* The statement `sbtPlugin := true` causes SBT to configuring this project to build as a plugin. 
    * One of the implications of this is that you are **limited to Scala 2.12.x** usage (sbt itself is currently built against 2.12)
    
* Testing is a little different than what standard projects use:

    * The [scripted test framework](https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html) provides a `scripted` command, that runs the plugin tests (instead of `test`)
    * Each [plugin test](src/sbt-test/sbt-laserdisc-defaults) is an actual sbt project configured in a particular way, with a set of assertions
    * Change `scriptedBufferLog := false` in [build.sbt](build.sbt) to show full test output when troubleshooting tests
    
* As you start to develop new defaults, understand the [distinction](https://www.scala-sbt.org/1.x/docs/Setting-Initialization.html) between `buildSettings` and `projectSettings` 

    * This plugin primarily defines the more global `buildSettings` 
    * I have attempted to break down the functionality by area to keep things tidier, so check out the [`DefaultsCategory`](src/main/scala/laserdisc/sbt/DefaultsCategory.scala) trait and its [implementations](src/main/scala/laserdisc/sbt/category) to see how the current configuration is applied. 

* One common gotcha is trying to access `someSetting.value` outside of a task or setting macro 

    * those values are only available when SBT [computes its task graph](https://www.scala-sbt.org/1.x/docs/Task-Graph.html#Inlining+.value+calls)
        ```sbt
        val optionKey = settingKey[Boolean]("Enable secret option")

        // fails with "`value` can only be used within a task or setting macro.."
        val allowSecretOption = optionKey.value  
        scalacOptions ++= {
           if (allowSecretOption) Seq("-Xallow-secret-feature") else Seq()
        }

        // access the value _inside_ the definition
        scalacOptions ++= {
          val allowSecretOption = optionKey.value
          if (allowSecretOption) Seq("-Xallow-secret-feature") else Seq()
        }
        ```

  * Even accessing the logger (`Keys.sLog`) requires use of `value`:
      ```sbt
        Keys.sLog.value.info("foo") // must be inside a task/setting macro!
      ```

## Help

This plugin was developed by [@barryoneill](https://github.com/barryoneill)

In addition to creating issues on this repo, **please use the [#laserdisc](https://laserdisc-io.slack.com/archives/C013QDL1G7Q) slack** for discussion about this plugin! 

