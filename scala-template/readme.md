# Spark sql sample in scala

This repository(Spark Templates) can be used as a template for spark application.
 A user can simply clone this repository and start deploying spark sql application
 written in scala. For other languages and combinations, checkout other templates
 available in this repository.
 
 ### Build instructions
 
 1. Directly run the main using SBT
 
        sbt run
    
 2. Build a package which can be deployed via
  [spark submit](http://spark.apache.org/docs/latest/submitting-applications.html)
  to a Spark Cluster.
  
        sbt package.
  
  Please note, to deploy via spark on k8s option, we need either an image
  containing this jar or via an HCFS(Hadoop compatible file system) using
  [spark-files](http://spark.apache.org/docs/latest/running-on-kubernetes.html#dependency-management)
   or dependency management option.
 3. How to build an image from Spark's docker image as base, containing
    this jar. 
        
    1. Edit the docker/Dockerfile to specify the correct docker repo.
    
    2. Run the script below.
    
    
        docker/build_and_push.sh


### Running via sbt

```
sbt run

[info] welcome to sbt 1.3.13 (N/A Java 14.0.1)
[info] loading global plugins from /Users/user/.sbt/1.0/plugins
[info] .... 
Multiple main classes detected, select one to run:

 [1] com.github.scrapcodes.scala.template.SparkScalaAWSExample
 [2] com.github.scrapcodes.scala.template.SparkScalaSQLExample

2

[info] running com.github.scrapcodes.scala.template.SparkScalaSQLExample 
20/07/24 15:24:48 INFO SparkScalaSQLExample: Preview of the data.
+----------+--------------------+---------+
|   airline|               tweet|sentiment|
+----------+--------------------+---------+
|  American|@AmericanAir lost...| negative|
|US Airways|@USAirways Any up...|  neutral|
| Southwest|@SouthwestAir YES...| positive|
|  American|@AmericanAir @dfw...| positive|
| Southwest|@SouthwestAir you...| positive|
|  American|@AmericanAir wht ...| negative|
| Southwest|@SouthwestAir has...| negative|
|US Airways|@USAirways seems ...| negative|
|  American|@AmericanAir that...| negative|
| Southwest|@SouthwestAir gre...| positive|
+----------+--------------------+---------+

20/07/24 15:24:48 INFO SparkScalaSQLExample: Some interesting stats around the airline tweets.
+--------------+-----------------------+------------------+-------------------+
|       airline|positive_feedback_count|total_tweets_count|positive_percentage|
+--------------+-----------------------+------------------+-------------------+
|         Delta|                   1267|              2222|             57.02%|
|Virgin America|                    323|               504|             64.09%|
|        United|                   1189|              3822|             31.11%|
|    US Airways|                    650|              2913|             22.31%|
|     Southwest|                   1234|              2420|             50.99%|
|      American|                    799|              2759|             28.96%|
+--------------+-----------------------+------------------+-------------------+

20/07/24 15:24:50 INFO SparkScalaSQLExample: Results using the dataframe API.
+--------------+------------+
|       airline|+ve feedback|
+--------------+------------+
|         Delta|        1267|
|Virgin America|         323|
|        United|        1189|
|    US Airways|         650|
|     Southwest|        1234|
|      American|         799|
+--------------+------------+

+--------------+-----------------------+------------------+------------------+
|       airline|positive_feedback_count|total_tweets_count|             +ve %|
+--------------+-----------------------+------------------+------------------+
|         Delta|                   1267|              2222| 57.02070207020702|
|Virgin America|                    323|               504|  64.0873015873016|
|        United|                   1189|              3822| 31.10936682365254|
|    US Airways|                    650|              2913|22.313765877102643|
|     Southwest|                   1234|              2420| 50.99173553719009|
|      American|                    799|              2759|28.959768031895617|
+--------------+-----------------------+------------------+------------------+
```

### Running via spark on k8s plugin.
Edit the `src/docker/Dockerfile` and `bin/build_and_push.sh`, provide the relevant values for your
docker repository and spark base image.
        
1. Run the docker deploy script to build and publish image to docker repo.
        
    
      bin/build_and_push.sh 
        
2. Deploy on K8s cluster with spark-submit as follows.
    
```
    bin/spark-submit \
    --master k8s://<master-url> \
    --deploy-mode cluster \
    --name spark-scala-template-app \
    --class com.github.scrapcodes.scala.template.SparkScalaSQLExample \
    -c spark.kubernetes.authenticate.driver.serviceAccountName=spark \
    -c spark.kubernetes.container.image.pullPolicy=Always \
    -c spark.kubernetes.container.image=scrapcodes/custom-spark:v0.0.1 \
    local:///opt/scalatemplate_2.12-0.0.1-SNAPSHOT.jar
```

* How to get the value of master-url?

```
    # kubectl cluster-info
    Kubernetes master is running at https://master-url/
    Heapster is running at https://master-url/api/v1/namespaces/kube-system/services/heapster/proxy
    
    To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
    
```    
   Copy the value of `https://master-url/` and replace with `<master-url>`

* You may run as is, or use your own published image by replacing the value in,
    `-c spark.kubernetes.container.image=scrapcodes/custom-spark:v0.0.1`
