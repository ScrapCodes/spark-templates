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

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.slf4j.{Logger, LoggerFactory}


object SparkScalaSQLExample {

  private val logger: Logger = LoggerFactory.getLogger("SparkScalaSQLExample")

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("SparkScalaSQLExample").master("local[*]")
      .getOrCreate()
    import spark.implicits._

    val tweetDataframe = spark
      .read.option("header", "true").csv("data/Tweets.csv")
      .select("airline", "text", "airline_sentiment")
      .toDF("airline", "tweet", "sentiment")

    // Persist as a temporary table.
    tweetDataframe.createTempView("tweets")

    val tweetPreview =
      spark.sql("SELECT * FROM tweets WHERE airline IS NOT NULL ORDER BY RAND() LIMIT 10")

    // Compute counts for all the positive tweets for each airline.
    val tweetStats =
      spark.sql(
        "SELECT airline, count(airline) AS positive_feedback_count FROM tweets " +
          "WHERE sentiment in ('positive', 'neutral' ) AND airline IS NOT NULL " +
          "GROUP BY airline")
    tweetStats.createTempView("tweet_stats_positive")

    // Compute counts for all the tweets for each airline.
    val tweetStats2 =
      spark.sql(
        "SELECT airline, count(airline) AS total_tweets_count FROM tweets " +
          "WHERE sentiment in ('positive', 'neutral', 'negative' ) AND airline IS NOT NULL " +
          "GROUP BY airline")

    tweetStats2.createTempView("tweet_stats_total")
    val tweetStatsCompare =
      spark.sql(
        "SELECT t1.airline, t1.positive_feedback_count, t2.total_tweets_count, " +
          "printf('%.2f%%', ( t1.positive_feedback_count / t2.total_tweets_count ) * 100) as " +
          " positive_percentage FROM" +
          " tweet_stats_positive as t1 JOIN tweet_stats_total as t2 ON t1.airline=t2.airline")

    logger.info("Preview of the data.")
    tweetPreview.show()
    logger.info("Some interesting stats around the airline tweets.")
    tweetStatsCompare.show()

    // Using dataframe APIs
    import org.apache.spark.sql.functions._
    logger.info("Results using the dataframe API.")

    val tweetStatsDF: DataFrame = tweetDataframe.select($"airline")
      .where($"sentiment" isin("neutral", "positive"))
      .groupBy($"airline").agg(count($"airline") as "+ve feedback")

    // Using dataframe API, a dataframe can be joined with a table as follows.
    val tweetsCompare = tweetStatsDF.join(tweetStats2,
      tweetStatsDF("airline") === tweetStats2("airline"))
      .select(tweetStatsDF("airline"), $"+ve feedback", $"total_tweets_count",
        ($"+ve feedback" / $"total_tweets_count" * 100) as "+ve %")

    tweetStatsDF.show()
    tweetsCompare.show()
    spark.stop()
  }
}