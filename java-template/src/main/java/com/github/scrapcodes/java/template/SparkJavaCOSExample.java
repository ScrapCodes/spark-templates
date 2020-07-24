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

package com.github.scrapcodes.java.template;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkJavaCOSExample {
    private static final Logger logger = LoggerFactory.getLogger("SparkJavaCOSExample");

    public static void main(String[] args) throws Exception {
        SparkConf conf = new SparkConf()
                .set("spark.kubernetes.file.upload.path",
                        "s3a://<your-bucket>/temp")
                .set("spark.hadoop.fs.s3a.access.key", "<value>")
                .set("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .set("spark.hadoop.fs.s3a.fast.upload", "true")
                .set("spark.hadoop.fs.s3a.secret.key", "<value>")
                // required when using non AWS endpoint e.g. IBM Cloud Object Storage - us-south region.
                // comment the following line, if AWS S3 is in use.
                .set("spark.hadoop.fs.s3a.endpoint", "s3.us-south.cloud-object-storage.appdomain.cloud")
                .set("spark.driver.extraJavaOptions", "-Divy.cache.dir=/tmp -Divy.home=/tmp");
        SparkSession spark = SparkSession.builder()
                .appName("SparkJavaSQLExample")
                .master("local[*]")
                .config(conf)
                .getOrCreate();
        Dataset<Row> tweetDataframe = spark.read()
                .option("header", "true")
                .csv("s3a://<your-bucket>/data/Tweets.csv")
                .select("airline", "text", "airline_sentiment")
                .toDF("airline", "tweet", "sentiment");

        // Persist as a temporary table.
        tweetDataframe.createTempView("tweets");

        logger.info("Preview some random rows using SQL API.");
        Dataset<Row> tweetPreview = spark.sql(
                "SELECT * FROM tweets WHERE airline IS NOT NULL ORDER BY RAND() LIMIT 10");
        tweetPreview.show();

        spark.stop();

    }


}
