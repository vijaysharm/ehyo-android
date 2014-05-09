Ehyo
====
A command line tool for scaffolding Android Applications. Heavyily inspired by and loosely named after the [yeoman][4] web scaffolding tool. The goal of this project is to provide a command line tool for developers looking to perform worry free boiler-plate operations to their Android projects. Its meant to compliment existing tools namely the [android command line tool][1], and [templates][2].

The tool allows you to perform common tasks to your Android project
+ Search/Add/Remove permissions from your manifest
+ Search/Add/Upgrade/Remove maven repository dependecies to/from your build
+ Apply common Android templates to your project
+ Apply templates provided by [jgilfelt][3]

Usage
-----
To view the list of actions ehyo supports, run the `list` command in an existing Android gradle project
`ehyo list`

To add or remove a permission to a manifest, run the permissions command. By default, ehyo assumes you're performing modifications to a project in the current directory. To tell ehyo which project to apply to, include the --directory <dir> argument.

`ehyo permissions --add internet`
`ehyo --directory <dir> permissions --remove internet`

To search, add or remove dependencies from a build, run the dependencies command. If you'd like to see how this will affect your project file, ehyo can be run with a --dry-run option 
`ehyo dependencies -s butterknife --add`
`ehyo dependencies -g com.jakewharton`
`ehyo dependencies --remove retrofit --dry-run`

To apply a template to your project, run the templates command.
`ehyo templates --list`
`ehyo templates -l`
`ehyo templates -a [templatename] --dry-run`
`ehyo templates --apply [templatename]`

After downloading ehyo, a script to use ehyo is provided in ./bin/ehyo ... Add this to your path or give the full path to the script in bin and enjoy.

Limitations
-----------
TONS. But here are some high-level ones that I can think of.

+ This project was designed around the default Android gradle project structure, and therefore makes a lot of assumptions based on the way it structures its code. It looks for build.gradle and settings.gradle files to determine the project structure.
++ Overridden source set paths in builds are not supported

+ Merging with templates
++ Merging is done in a very naive way. With XML (or any file for that matter), we simply append elements into the expected parent. We do not look for any existing elements that might be similar and attempt to merge them. This can lead to duplicate entries in your resources.

+ Ruins the formatting of the AndroidManifest.xml

+ Everything is prompt heavy. Sadly, either I prompt you for what you'd like to do, or I ask you for a large number of arguments. One of my goals was to limit the number of arguments a developer has to remember, so the tradeoff was to just prompt you there and then.

+ There's no rollback during failures. If part of a template is applied, and an exception occurs, then there's no way to undo what failed. As a suggestion, run ehyo within a version controlled project with a fresh checkout. That will allow you to rollback your changes when ehyo craps out on you.

+ Developed on and only tested on OSX with Java 1.6. There isn't any OS specific stuff in the code, per-se, but consider this sufficient warning if things don't work ask you expect.

Building the jar
----------------
`mvn package`

TODO
----
##Bugs
+ FIX: ApplicationRunActionFactoryTest, AndroidManifestDocumentTest

+ GradleBuildDocumentModel doesn't support the following kind of dependency
++ This was seen in the Muzei Gradle build
++ compile ('de.greenrobot:eventbus:2.2.0') { exclude group:'com.google.android', module: 'support-v4' // already included below }
+++ Current model will think its a context
++ Fails for certain types (e.g. tasks)

+ There's a bug displaying the diff: 
++ the line below the added line is incorrect, it shows the line after that.

+ Ugly exception when no internet connection with dependency command

+ JDOM2's org.jdom2.output.Format modifies the formatting of XML (doesn't respect the desired Android formatting)

##Tasks
+ Show better version information
++ Better option description for usage

+ Improve dry-run diff
++ Remove line numbers
++ Should print the number of documents modified (look a the way git does commits)

+ Check if the project structure is valid
++ You don't want to throw an exception when they run ./bin/ehyo ... you want them to see the usage. 

+ Add support for a --debug for verbose printing

+ Should all actions have names so that output is more legible?
++ Might be useful with --debug (Track when an action is added, whether its executed or not, etc...)

+ Make all core models package protected
++ Document public API

+ All commands
++ Support --project or --variant or --sourceset or --buildtype so you can distinctly select the right item to apply a command to (avoiding prompts)

+ Finish template command
++ Add support for an "interactive" mode. Allows for users to step and select what is applied.
+++ Need to support a non-existent manifest (as an empty file)
++ Support adding build variables in the freemaker template (minApiLevel, build version, etc...)
++ Support loading templates from disk or remote (and not just ones from the packaged JAR)
++ Support --<parameter> where the argument is the same as the parameter id in the template
++ Read the min- supported properties on the template object and filter out non-applicable ones during execution
+ Will not upgrade your dependency, will simply add a second instance of it

+ Write integration tests 
++ for templates
++ for entire application

+ Doesn't cope well with dependencies defined as "project(':libraries:lib1')" or "files('libs/foo.jar')"

+ Move all TODOs to Issues list.

##Roadmap
+ Provide a way to create a new Android application
++ Create a project with basic pieces
+++ Dependency Injection
+++ Testing Framework
+++ Commonly used libraries

+ Create a template that add the 'big cookie model to your project'

+ Provide a command that lets you rename your package namespace

+ Read templates from github
++ From a gist?

+ Provide a way to easily create templates from a diff

+ Provide a way to save templates 
++ As a gist?

+ Provide support for android projects not created with gradle
++ Eclipse? Maven?

+ Support merging Java files
++ Improve merging as a whole

+ Improve Command line
++ Need to be able to multi-select
++ Inquire.js for java?

+ Provide ehyo through Brew

+ Tab completion

Open Source Libraries
---------------------
+ Guava
+ jdom2
+ commons-io
+ commons-lang3
+ reflections
+ retrofit
+ java-diff-utils
+ freemarker
+ junit
+ mockito

License
=======

    Copyright 2014 Vijay Sharma

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: http://developer.android.com/tools/projects/projects-cmdline.html
 [2]: http://developer.android.com/tools/projects/templates.html
 [3]: https://github.com/jgilfelt/android-adt-templates
 [4]: http://yeoman.io/
