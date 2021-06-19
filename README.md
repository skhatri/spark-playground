### Spark Playground

Spark Playground

### Choosing Spark
spark.set can be set to a particular version of spark. It will bring with it the version of hadoop and aws sdk.


### Downloading Data
Fetch airbnb data 
```
./fetch.sh
```
### Preparing Environment
Start a Minio container for S3
```
docker-compose up -d
```
create test bucket

```
./s3.sh mb s3://spark-data-lake
```

### Running Tests
Runs various read/write tests against each version of spark
```
./test.sh
```

```
./s3.sh ls s3://spark-data-lake --recursive
```

View through
http://localhost:9000/minio/spark-data-lake/

### Cleaning up

```
docker-compose down
```

