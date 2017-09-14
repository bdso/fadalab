#!/usr/bin/env bash

ps ax | grep -i 'kafka' | grep java | grep -v grep | awk '{print $1}' | xargs kill -9
ps ax | grep -i 'zookeeper' | grep java | grep -v grep | awk '{print $1}' | xargs kill -9
ps ax | grep -i 'elasticsearch' | grep java | grep -v grep | awk '{print $1}' | xargs kill -9
