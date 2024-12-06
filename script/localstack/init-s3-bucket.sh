#!/bin/bash

BUCKET_NAME="guess-who-bucket-local"
DEFAULT_IMAGES_DIRECTORY="images/default"

awslocal s3 mb s3://$BUCKET_NAME

for image in /etc/localstack/init/images/default/*; do
  awslocal s3 cp "$image" "s3://$BUCKET_NAME/$DEFAULT_IMAGES_DIRECTORY/"
done