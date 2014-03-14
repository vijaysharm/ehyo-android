Ehyo
====
An android scaffolding command line tool.

Developing using Eclipse
------------------------
`mvn dependency:sources`
`mvn dependency:resolve -Dclassifier=javadoc`
`mvn eclipse:eclipse`

Building the jar
----------------
`mvn package`

Running the command-line
------------------------
`java -jar ehyo.jar`

TODO
----
+ Provide a way to create a new Android application
++ Might create a project with basic pieces
+++ Dependency Injection
+++ Unit testing Framework
+++ etc...
+ Provide a way to add a new Component to the Android project 
++ Includes adding the new Component to the Manifest
+ Provide a way to add permissions to the project
+ Provide a way to search and add libraries to the project
+ Provide a way to add new plugins to do more command line stuff
