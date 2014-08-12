import AssemblyKeys._

assemblySettings

name := "crazy-eights"

organization := "com.hasanozgan"

version := "0.0.1-SNAPSHOT"

scalaVersion  := "2.11.1"

scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-encoding", "utf8",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions")

resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "com.github.scopt"        %%  "scopt"                 % "3.2.0",                          
  "com.typesafe.akka"       %%  "akka-actor"            % "2.3.4",
  "com.typesafe.akka"       %%  "akka-remote"           % "2.3.4",
  "org.scala-stm"          %% "scala-stm"          % "0.7",
  "org.scalatest"           %   "scalatest_2.11"        % "2.2.1"   % "test",
  "com.typesafe.akka"       %%  "akka-testkit"          % "2.3.4"   % "test"
)


