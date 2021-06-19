package airbnb

import common.SparkSupport
import org.apache.spark.sql.SaveMode

object AirBnbListings extends App with SparkSupport {

  val area = "sydney"
  val spark = getOrCreate("airbnb-listings", "local[1]")
  val listings = spark.read.format("csv")
    .option("header", "true")
    .load(s"dataset/$area-listings.csv.gz")

  val listingData = listings.select("id",
    "name", "neighbourhood", "latitude", "longitude",
    "accommodates", "bathrooms", "bedrooms", "beds",
    "host_id", "host_is_superhost",
    "number_of_reviews", "number_of_reviews_l30d", "number_of_reviews_ltm",
    "price", "property_type", "room_type", "scrape_id")

  listingData.write.format("parquet")
    .mode(SaveMode.Overwrite)
    .save(s"s3a://spark-data-lake/output/travel/listings/area=$area")

  spark.stop()
}

