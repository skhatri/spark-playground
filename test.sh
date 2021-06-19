#!/usr/bin/env bash


set -e
#write with small version
./gradlew clean run -Pspark.set=3.0.1_fix -Pclass.name=AirBnbListings
#sync with small version
./gradlew clean run -Pspark.set=3.0.1_fix -Pclass.name=AirBnbListingsSync

#sync with higher version
./gradlew clean run -Pspark.set=3.1.0 -Pclass.name=AirBnbListingsSync

#write with higher version
./gradlew clean run -Pspark.set=3.1.1 -Pclass.name=AirBnbListings

./gradlew clean run -Pspark.set=3.1.1 -Pclass.name=AirBnbListingsSync
#sync with lower version
./gradlew clean run -Pspark.set=3.1.0 -Pclass.name=AirBnbListingsSync
./gradlew clean run -Pspark.set=3.0.1_fix -Pclass.name=AirBnbListingsSync
