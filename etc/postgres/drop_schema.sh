#!/bin/bash

DIRECTORY=$(cd `dirname $0` && pwd)

docker stop camunda
docker rm camunda

$DIRECTORY/run_pg.sh

