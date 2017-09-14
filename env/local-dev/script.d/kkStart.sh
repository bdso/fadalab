#!/bin/bash
kafka-server-start.sh $KAFKA_HOME/config/server.properties &

$DIR_HOME/conf.d/kafka/create-topic.sh &