Ehyo
====
An Android scaffolding command line tool. Heavyily inspired by and loosely named after the yeoman web scaffolding tool. The goal of this project is to provide a command line tool for developers looking to perform worry free boiler-plate operations to their Android projects.

Developing using Eclipse
------------------------
`mvn clean package dependency:sources dependency:resolve -Dclassifier=javadoc eclipse:eclipse`

Building the jar
----------------
`mvn package`

Running the command-line
------------------------
`java -jar ehyo.jar`

Limitations
-----------
This project was designed around the default Android Studio project structure, and therefore makes a lot of assumptions based on the way it structures its code. 

Usage
-----
ehyo manage-permissions add internet
ehyo manage-permissions delete internet
ehyo search butterknife
ehyo --plugins <ns> --directory <dir> search add flow
ehyo --plugins <ns> --directory <dir> --plugin search --lib flow --add

TODO
----
##High Level Goals
+ Provide a way to create a new Android application
++ Might create a project with basic pieces
+++ Dependency Injection
+++ Unit testing Framework
+++ etc...
+ Provide a way to add a new Component to the Android project (Activity, Services, etc..)
++ Includes adding the new Component to the Manifest
+ Provide a plugin that lets you rename your package namespace
+ Provide a plugin that add the 'big cookie model to your project'

##Tasks
+ Show user formatted error when the name given in plugin is not found
+ Show better version information
+ Have the custom logger respect debug mode
+ Broke Usage
++ Better option description for usage
+ Check if the project structure is valid
++ Maybe have the DirectoryCommandLineConverter load the project
+ Have Permissions plugin take the permission type from the command line, or search a repository of known permissions
+ Improve ManifestActionHandler to only add permissions if defined in manifest
+ Improve ManifestActionHandler to support removing permissions (defined in the xml)
+ Have the ManifestChangeManagerFactory not modify the formatting of the XML file
+ Read the gradle build to understand the project structure better.
+ Improve dry-run diff
+ FIX: ApplicationRunActionFactoryTest
