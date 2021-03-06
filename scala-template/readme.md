# Spark sql sample in scala

This repository(Spark Templates) can be used as a template for spark application.
 A user can simply clone/copy/download this repository and start deploying spark sql application
 written in scala with SBT as build tool. It includes templates for dockerfile and all the build related
  information for deploying to kubernetes.
  
 ### Build instructions
 
 1. Prerequisite: SBT should be installed.
  More details on: https://www.scala-sbt.org/download.html)
 
 
 2. Build a package which can be deployed via
[spark submit](http://spark.apache.org/docs/latest/submitting-applications.html)
  to a Spark Cluster.
  
        sbt package.
  
  ```
      bin/spark-submit \
      --name spark-scala-template-sql-app \
      --class com.github.scrapcodes.scala.template.SparkScalaSQLExample \
      /<path_to_dir>/target/scala-2.12/scalatemplate_2.12-0.0.1-SNAPSHOT.jar
  ```

 All the extra dependencies must be included using `--packages`,
  or one can even build a jar with dependencies, whenever possible prefer the `--packages` switch.
  
  ```
      bin/spark-submit \
      --name spark-scala-template-cos-app \
      --packages com.amazonaws:aws-java-sdk:1.7.4,org.apache.hadoop:hadoop-aws:2.7.6 \
      --class com.github.scrapcodes.scala.template.SparkScalaCOSExample \
      /<path_to_dir>/scala-template/target/scala-2.12/scalatemplate_2.12-0.0.1-SNAPSHOT.jar
  ```

To build jar with all the dependencies, use `sbt assembly`.
 Please see provided `build.sbt`, if you need to see how this is configured.
 Once the assembly is ready, run it using:

 ```
bin/spark-submit --class com.github.scrapcodes.scala.template.SparkScalaCOSExample \
 /<path_to_dir>/scala-template/target/scala-2.12/scalaTemplate-assembly-0.0.1-SNAPSHOT.jar 

```
 
  Please note, to deploy via spark on k8s option, we need either an image
  containing this jar or we can deploy via an HCFS(Hadoop compatible file system) using
  [spark-files](http://spark.apache.org/docs/latest/running-on-kubernetes.html#dependency-management)
   or dependency management option.
   
 3. How to build an image from Spark's docker image as base, containing
    this jar. 
        
    1. Edit the `docker/Dockerfile` to specify the correct docker repo.
    
    2. Run the script below.
    
        docker/build_and_push.sh


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
    -c spark.kubernetes.container.image=my-repo/custom-spark:v0.0.1 \
    local:///opt/scalatemplate_2.12-0.0.1-SNAPSHOT.jar
```

For more information take a look at, [spark documentation](http://spark.apache.org/)

* How to get the value of master-url?

```
    # kubectl cluster-info
    Kubernetes master is running at https://master-url/
    Heapster is running at https://master-url/api/v1/namespaces/kube-system/services/heapster/proxy
    
    To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
    
```    
   Copy the value of `https://master-url/` and replace with `<master-url>`

* You may run as is, or use your own published image by replacing the value in, with your own image.
    `-c spark.kubernetes.container.image=my-repo/custom-spark:v0.0.1`
