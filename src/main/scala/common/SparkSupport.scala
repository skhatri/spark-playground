package common

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkSupport {


  lazy private val sparkConfigMap = Map[String, String](
    "spark.hadoop.fs.hdfs.impl" -> "org.apache.hadoop.hdfs.DistributedFileSystem",
    "spark.hadoop.fs.s3a.connection.ssl.enabled" -> "false",
    "spark.hadoop.fs.file.impl" -> "org.apache.hadoop.fs.LocalFileSystem",
    "spark.hadoop.fs.s3a.path.style.access" -> "true",
    "spark.hadoop.fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.S3AFileSystem",
    "spark.hadoop.fs.s3a.aws.credentials.provider" -> "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider",
    "spark.hadoop.fs.s3a.connection.timeout" -> "30000",
    "spark.hadoop.fs.s3a.connection.establish.timeout" -> "10000"
  )

  lazy private val secretsConfig = if (Option(System.getenv("AWS_ACCESS_KEY_ID")).isDefined) {
    Map(
      "spark.hadoop.fs.s3a.access.key" -> System.getenv("AWS_ACCESS_KEY_ID"),
      "spark.hadoop.fs.s3a.secret.key" -> System.getenv("AWS_SECRET_ACCESS_KEY"),
    )
  } else {
    Map(
      "spark.hadoop.fs.s3a.access.key" -> "minio",
      "spark.hadoop.fs.s3a.secret.key" -> "minio123",
      "spark.hadoop.fs.s3a.endpoint" -> "http://localhost:9000"
    )
  }

  def getOrCreate(name:String, master:String) = SparkSession.builder().master(master).appName(name)
    .config("spark.sql.catalogImplementation", "hive")
    .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
    .config(new SparkConf().setAll(sparkConfigMap))
    .config(new SparkConf().setAll(secretsConfig))
    .getOrCreate()

}
