package airbnb

import common.SparkSupport
import org.apache.spark.sql.SaveMode

object AirBnbListingsSync extends App with SparkSupport {

  val area = "sydney"
  val spark = getOrCreate("airbnb-listings-sync", "local[1]")
  val listings = spark.read.format("parquet")
    .load(s"s3a://spark-data-lake/output/travel/listings/area=$area")

  listings.write.format("parquet")
    .mode(SaveMode.Overwrite)
    .save(s"s3a://spark-data-lake/output/travel/listings-sync/area=$area")

  spark.stop()
}
