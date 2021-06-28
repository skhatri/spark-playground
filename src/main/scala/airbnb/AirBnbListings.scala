package airbnb

import common.SparkSupport
import org.apache.spark.sql.{AnalysisException, SaveMode}

import scala.util.{Failure, Success, Try}


case class Field(name: String, typeName: String)

object AirBnbListings extends App with SparkSupport {

  val area = "sydney"
  val spark = getOrCreate("airbnb-listings", "local[1]")

  val existingFields = Try(spark.table("travel.listings")) match {
    case Success(existingDf) => Some(existingDf.schema.fields.map(sf => Field(sf.name, sf.dataType.typeName)).toSeq)
    case Failure(exception) if exception.isInstanceOf[AnalysisException] => None
  }

  val listings = spark.read.format("csv")
    .option("header", "true")
    .load(s"dataset/$area-listings.csv.gz")

  val listingData = listings.selectExpr("id",
    "name", "neighbourhood", "latitude", "longitude",
    "accommodates", "bathrooms", "bedrooms", "beds",
    "host_id", "host_is_superhost",
    "number_of_reviews", "number_of_reviews_l30d", "number_of_reviews_ltm",
    "price", "property_type", "room_type", "scrape_id", "'sydney' as area", "2021 as year")
    .filter("bedrooms=='1'")

  listingData.write.format("parquet")
    .mode(SaveMode.Overwrite)
    .partitionBy("area")
    .save(s"s3a://spark-by-example/output/travel/listings/")

  val tableSchema = listingData.schema
  val newFields: Seq[Field] = tableSchema.fields.map(sf => Field(sf.name, sf.dataType.typeName))

  val newFieldsToAdd = existingFields match {
    case Some(fieldNames) => newFields.diff(fieldNames)
    case None => Seq.empty[Field]
  }

  val cols = tableSchema.fields.map(sf => sf.name + " " + sf.dataType.typeName).mkString(",\n")
  println(cols)
  spark.sql("create database if not exists travel")

  spark.sql(s"create table if not exists travel.listings ($cols) partitioned by(area) stored as parquet location 's3a://spark-by-example/output/travel/listings/'")

  val alterCols = newFieldsToAdd.map(field => s"${field.name} ${field.typeName}").mkString(",")
  if (alterCols.nonEmpty) {
    spark.sql(s"alter table travel.listings add columns (${alterCols}) cascade")
  }

  spark.sql(s"alter table travel.listings add if not exists partition(area='$area')")
  //spark.sql("msck repair table travel.listings")
  spark.stop()
}
