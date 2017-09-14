#!/bin/bash
$SPARK_HOME/sbin/start-master.sh -h localhost

$SPARK_HOME/sbin/start-slave.sh spark://localhost:7077 -h localhost