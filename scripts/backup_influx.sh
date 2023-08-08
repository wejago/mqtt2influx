#!/bin/bash

usage() {
    echo
    echo "usage: $0 [-d <deployment_directory>] [-b <backup_directory>]"
    echo
    echo "example:"
    echo "./backup_influx.sh -d $HOME/mqtt2influx/deployment -b $HOME/backups"
    echo
    exit 1
}

# this code block checks, if all mandatory parameters are passed with the command execution
# if not - the user gets an output how to use this command
while getopts "b:d:" opt; do
  case $opt in
  (b)
    b=$OPTARG
    ;;
  (d)
    d=$OPTARG
  case $i in
    (*/) ;;
    (*) i=$i/ ;;
    esac
    ;;
  (*)
    usage
    ;;
  esac
done
shift $((OPTIND-1))

if [[ -z $d || -z $b ]]; then
    usage
fi

DEPLOYMENT_DIR=$d

INFLUX_TOKEN_SPLIT=$(cat $DEPLOYMENT_DIR/.env | grep INFLUX_TOKEN | tr '=' '\n')
INFLUX_TOKEN="${INFLUX_TOKEN_SPLIT[*]:13}"

INFLUX_LINE=$(docker ps | grep influxd | tr " " "\n")
CURRENT_DATE=$(date +'%Y-%m-%d')

# split the output
IFS=' ' read -r -a CONTAINER <<< "$INFLUX_LINE"

# shellcheck disable=SC2128
docker exec -it "$CONTAINER" rm -rf $CURRENT_DATE
# shellcheck disable=SC2128
docker exec -it "$CONTAINER" influx backup $CURRENT_DATE -t $INFLUX_TOKEN
# shellcheck disable=SC2128
docker cp $CONTAINER:$CURRENT_DATE $b/$CURRENT_DATE
