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

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.not;
import static org.apache.spark.sql.functions.isnull;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.rand;

public class SparkJavaSQLExample {
    private static final Logger logger = LoggerFactory.getLogger("SparkScalaSQLExample");

    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("SparkJavaSQLExample")
                .master("local[*]")
                .getOrCreate();

        Dataset<Row> tweetDataframe = spark.read()
                .option("header", "true")
                .csv("data/Tweets.csv")
                .select("airline", "text", "airline_sentiment")
                .toDF("airline", "tweet", "sentiment");

        // Persist as a temporary table.
        tweetDataframe.createTempView("tweets");

        logger.info("Preview some random rows using SQL API.");
        Dataset<Row> tweetPreview = spark.sql(
                "SELECT * FROM tweets WHERE airline IS NOT NULL ORDER BY RAND() LIMIT 10");
        tweetPreview.show();

        logger.info("Preview some random rows using Dataframe API.");
        tweetDataframe.select(col("airline"), col("tweet"), col("sentiment"))
                .where(not(isnull(col("airline")))).orderBy(rand()).limit(10).show();
        // Find more examples, in scala-template's SparkScalaSQLExample.scala and
        // Apache spark examples on
        // https://github.com/apache/spark/tree/master/examples/src/main/java/org/apache/spark/examples.

        spark.stop();
    }
}
