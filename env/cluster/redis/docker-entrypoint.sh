#!/bin/bash

SCRIPT_DIR=/opt/src/script.d
# Start the elastichsearch process
$SCRIPT_DIR/reStart.sh -D
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start Elasticsearch: $status"
  exit $status
fi

# Naive check runs checks once a minute to see if either of the processes exited.
# This illustrates part of the heavy lifting you need to do if you want to run
# more than one service in a container. The container will exit with an error
# if it detects that either of the processes has exited.
# Otherwise it will loop forever, waking up every 60 seconds

while /bin/true; do
  PROCESS_3_STATUS=$(ps aux | grep -q redis | grep -v grep)
  # If the greps above find anything, they will exit with 0 status
  # If they are not both 0, then something is wrong
  if [ -z "$PROCESS_3_STATUS" ]; then
    echo "Ignoring empty string."
  elif [ "$PROCESS_3_STATUS" -ne 0 ] ; then
    echo "One of the processes has already exited."
    exit -1
  fi
  sleep 60
done