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
Make a copy of .env.template and save it as .env.
Update AWS key if not using minio.
Start relevant containers like minio, hive, postgres depending on the properties in .env

```
./start.sh
```

create test bucket
```
./s3.sh mb s3://spark-by-example
```

### Running Tests
Runs various read/write tests against each version of spark
```
./test.sh
```

```
./s3.sh ls s3://spark-by-example --recursive
```

View through
http://localhost:9000/minio/spark-by-example/

### Cleaning up

```
docker-compose down
```

### Running against Hive and S3

```
AWS_ACCESS_KEY_ID=A****
AWS_SECRET_ACCESS_KEY=***
HIVE_METASTORE_URI=thrift://localhost:9083
```
