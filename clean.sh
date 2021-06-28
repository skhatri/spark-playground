docker volume ls|grep spark-by-example|awk '{print $2}'|xargs docker volume remove
rm -rf hive/data/*
