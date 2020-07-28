/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

organization := "com.github.scrapcodes"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.10"

val sparkVersion = "3.0.0"

lazy val scalaTemplate = project.in(file(".")).settings(
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case _ => MergeStrategy.first
  },
  // Exclude scala libs from assembly jar.
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  // enable provided jar to be present during run.
  run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated,
  // Spark is set to provided as, we do not want it to be inlcuded in our assembly jar.
  libraryDependencies += ("org.apache.spark" %% "spark-sql" % sparkVersion % "provided"),
  libraryDependencies += ("org.apache.hadoop" % "hadoop-aws" % "2.7.6" excludeAll (
    ExclusionRule(organization = "org.apache.hadoop")))
)
