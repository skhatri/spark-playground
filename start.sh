#!/usr/bin/env bash

source .env
if [[ -z ${AWS_ACCESS_KEY_ID} ]] || [[ -z ${AWS_SECRET_ACCESS_KEY} ]] ;then
  echo "AWS_ACCESS_KEY_ID is required. Add [AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY] in .env file"
  exit 1;
fi;


: "${HIVE_SERVER:='no'}"

snippets=()

if [[ "${HIVE_SERVER}" == "yes" ]];
then
  if [[ "${AWS_ACCESS_KEY_ID}" == "minio" ]]; then
    echo "using minio"
    cp hive/conf/hive-minio.xml hive/conf/hive-site.xml
    snippets+=("minio")
  else
    echo "using s3"
    cat hive/conf/hive-template.xml|sed -e "s/__AWS_ACCESS_KEY_ID__/${AWS_ACCESS_KEY_ID}/g;s/__AWS_SECRET_ACCESS_KEY__/${AWS_SECRET_ACCESS_KEY}/" > hive/conf/hive-site.xml
  fi;
  snippets+=("postgres" "hive")
else
  snippets+=("minio")
fi;

cp snippets/docker-compose.yaml docker-compose.yaml
for container in "${snippets[@]}";
do
  cat snippets/${container}.yaml >> docker-compose.yaml
done;

docker-compose --env-file .env up -d
