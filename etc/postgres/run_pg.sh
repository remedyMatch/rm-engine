#!/bin/bash

DIRECTORY=$(cd `dirname $0` && pwd)

docker run --name camunda -p 5433:5432 --mount type=bind,source="$DIRECTORY"/pg_data,target=/data -d postgres:camunda