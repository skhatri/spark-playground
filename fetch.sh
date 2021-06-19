#!/usr/bin/env bash
if [[ ! -d dataset ]];then mkdir dataset; fi

if [[ ! -f dataset/sydney-listings.csv.gz ]];then
  curl -sL -o dataset/sydney-listings.csv.gz http://data.insideairbnb.com/australia/nsw/sydney/2021-04-10/data/listings.csv.gz
fi;

if [[ ! -f dataset/sydney-reviews.csv.gz ]];then
  curl -sL -o dataset/sydney-reviews.csv.gz http://data.insideairbnb.com/australia/nsw/sydney/2021-04-10/data/reviews.csv.gz
fi;

if [[ ! -f dataset/sydney-calendar.csv.gz ]];then
  curl -sL -o dataset/sydney-calendar.csv.gz http://data.insideairbnb.com/australia/nsw/sydney/2021-04-10/data/calendar.csv.gz
fi;

