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

package com.github.scrapcodes.scala.template

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

object SparkScalaCOSExample {
  private val logger: Logger = LoggerFactory.getLogger("SparkScalaCOSExample")

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .set("spark.kubernetes.file.upload.path",
        "s3a://<your-bucket>/temp")
      .set("spark.hadoop.fs.s3a.access.key", "<value>")
      .set("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .set("spark.hadoop.fs.s3a.fast.upload", "true")
      .set("spark.hadoop.fs.s3a.secret.key", "<value>")
      // required when using non AWS endpoint e.g. IBM Cloud Object Storage - us-south region.
      // comment the following line, if AWS S3 is in use.
      .set("spark.hadoop.fs.s3a.endpoint", "s3.us-south.cloud-object-storage.appdomain.cloud")
      .set("spark.driver.extraJavaOptions", "-Divy.cache.dir=/tmp -Divy.home=/tmp")

    val spark = SparkSession.builder
      .appName("SparkScalaCOSExample")
      .master("local[*]")
      .config(conf)
      .getOrCreate()

    // Load the data
    val tweetDataset = spark
      .read.option("header", "true").csv("s3a://<your-bucket>/data/Tweets.csv")
      .select("airline", "text", "airline_sentiment")
      .toDF("airline", "tweet", "sentiment")

    // Register temp table
    tweetDataset.createTempView("tweets")

    // Take a look at the data.
    val tweetPreview = spark.sql("SELECT * FROM tweets ORDER BY RAND() LIMIT 10")

    logger.info("Preview of the data.")
    tweetPreview.show()
    spark.stop()
  }
}
