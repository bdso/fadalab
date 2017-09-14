#!/usr/bin/env bash

KAFKA_HOME=/Users/leo/Zimportant/src/bin_src/kafka_2.11-0.8.2.2
ELASTIC_HOME=/Users/leo/Zimportant/src/bin_src/elasticsearch-5.4.1

cd $KAFKA_HOME

nohup bin/zookeeper-server-start.sh config/zookeeper.properties > zk.out &

nohup bin/kafka-server-start.sh config/server.properties > kk.out &

cd $ELASTIC_HOME

nohup bin/elasticsearch > es.out &