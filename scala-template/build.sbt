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
version      := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.10"

val sparkVersion = "3.0.0"

lazy val scalaTemplate = project.in(file(".")).settings(
    libraryDependencies += ("org.apache.spark" %% "spark-sql" % sparkVersion),
    libraryDependencies += ("com.amazonaws" % "aws-java-sdk" % "1.7.4"),
    libraryDependencies += ("org.apache.hadoop" % "hadoop-aws" % "2.7.6")
  )
