#!/usr/bin/env bash

# Submit production.

/Users/leo/Zimportant/src/bin_src/spark-2.1.1-bin-hadoop2.7/bin/spark-submit \
--class FastData \
--master spark://leos-MacBook.local:7077 \
--deploy-mode cluster \
--supervise \
--executor-memory 2G \
--total-executor-cores 2 \
./target/scala-2.11/fastdata_2.11-1.0.jar \
/Users/leo/Zimportant/src/fastdata/conf/spark-dev.conf