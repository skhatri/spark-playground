package airbnb

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

object AirBnbListingsSync extends App {
  val sparkConfigMap = Map[String, String](
    "spark.hadoop.fs.hdfs.impl" -> "org.apache.hadoop.hdfs.DistributedFileSystem",
    "spark.hadoop.fs.s3a.connection.ssl.enabled" -> "false",
    "spark.hadoop.fs.file.impl" -> "org.apache.hadoop.fs.LocalFileSystem",
    "spark.hadoop.fs.s3a.path.style.access" -> "true",
    "spark.hadoop.fs.s3a.impl" -> "org.apache.hadoop.fs.s3a.S3AFileSystem",
    "spark.hadoop.fs.s3a.aws.credentials.provider" -> "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider",
    "spark.hadoop.fs.s3a.connection.timeout" -> "30000",
    "spark.hadoop.fs.s3a.connection.establish.timeout" -> "10000"
  )

  val secretsConfig = if (Option(System.getenv("AWS_ACCESS_KEY_ID")).isDefined) {
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

  val spark = SparkSession.builder().master("local[1]").appName("airbnb-spark-listing")
    .config("spark.sql.catalogImplementation", "hive")
    .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
    .config(new SparkConf().setAll(sparkConfigMap))
    .config(new SparkConf().setAll(secretsConfig))
    .getOrCreate()

  val area = "sydney"
  val listings = spark.read.format("parquet")
    .load(s"s3a://spark-data-lake/output/travel/listings/area=$area")

  /*
  accommodates, amenities, availability_30, availability_365, availability_60, availability_90, , bathrooms_text,
  bathrooms
  bedrooms, beds,
  calculated_host_listings_count, calculated_host_listings_count_entire_homes, calculated_host_listings_count_private_rooms, calculated_host_listings_count_shared_rooms, calendar_last_scraped, calendar_updated,
  description,
  first_review, has_availability, host_about, host_acceptance_rate, host_has_profile_pic,
  host_identity_verified,
  host_is_superhost, host_listings_count,
  host_id,
  host_location, host_name,
  host_neighbourhood, host_picture_url, host_response_rate, host_response_time, host_since, host_thumbnail_url, host_total_listings_count, host_url, host_verifications,
  id, instant_bookable, last_review, last_scraped,
  latitude, license, listing_url,
  longitude, maximum_maximum_nights, maximum_minimum_nights, maximum_nights, maximum_nights_avg_ntm, minimum_maximum_nights, minimum_minimum_nights, minimum_nights, minimum_nights_avg_ntm,
  name, neighborhood_overview,
  neighbourhood,
  neighbourhood_cleansed, neighbourhood_group_cleansed,
  number_of_reviews,
  number_of_reviews_l30d, number_of_reviews_ltm, picture_url,
  price,
  property_type, review_scores_accuracy, review_scores_checkin, review_scores_cleanliness, review_scores_communication, review_scores_location, review_scores_rating, review_scores_value, reviews_per_month,
  room_type, scrape_id
   */
  val listingData = listings.select( "id",
     "name", "neighbourhood", "latitude", "longitude",
    "accommodates", "bathrooms", "bedrooms", "beds",
    "host_id", "host_is_superhost",
    "number_of_reviews", "number_of_reviews_l30d", "number_of_reviews_ltm",
    "price", "property_type", "room_type", "scrape_id")

  listingData.write.format("parquet")
    .mode(SaveMode.Overwrite)
    .save(s"s3a://spark-data-lake/output/travel/listings-sync/area=$area")

  spark.stop()
}
