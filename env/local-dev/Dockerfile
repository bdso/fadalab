# Pull base image.
FROM centos:latest
MAINTAINER leophan<leo.phan0411@gmail.com>

ARG USER_BDS=bdslab
RUN groupadd -r $USER_BDS && useradd -r -g $USER_BDS $USER_BDS
ENV DIR_HOME=/opt/src
WORKDIR $DIR_HOME

# Arguments java version.
ARG JAVA_MAJOR_VERSION=8
ARG JAVA_UPDATE_VERSION=131
ARG JAVA_BUILD_NUMBER=11
# Set ENV java.
ENV JAVA_HOME /usr/java/jdk1.${JAVA_MAJOR_VERSION}.0_${JAVA_UPDATE_VERSION}
ENV PATH $PATH:$JAVA_HOME/bin
# Install java.
RUN curl -L -O -H "Cookie: gpw_e24=http://www.oracle.com/; oraclelicense=accept-securebackup-cookie" \
  "http://download.oracle.com/otn-pub/java/jdk/${JAVA_MAJOR_VERSION}u${JAVA_UPDATE_VERSION}-b${JAVA_BUILD_NUMBER}/d54c1d3a095b4ff2b6607d096fa80163/jdk-${JAVA_MAJOR_VERSION}u${JAVA_UPDATE_VERSION}-linux-x64.rpm" \
  # && yum -y install jdk-8u131-linux-x64.rpm \
  && rpm -ivh jdk-8u131-linux-x64.rpm \
  && rm -rf jdk-8u131-linux-x64.rpm \
  && yum -y install net-tools && yum clean all && rm -rf /tmp/* /var/tmp/* && rm -rf /var/cache/*
# Check java.
RUN ["java","-version"]

# Arguments zookeeper version.
ARG ZOO_MAJOR=3
ARG ZOO_UPDATE=4
ARG ZOO_BUILD=6
ARG ZOO_PORT=2181
ARG ZOO_VERSION=${ZOO_MAJOR}.${ZOO_UPDATE}.${ZOO_BUILD}
# Set ENV zookeeper.
ENV ZOO_HOME $DIR_HOME/zookeeper-${ZOO_VERSION}
ENV PATH $PATH:$ZOO_HOME/bin
# Install zookeeper.
RUN curl -L -O http://mirrors.viethosting.com/apache/zookeeper/zookeeper-${ZOO_VERSION}/zookeeper-${ZOO_VERSION}.tar.gz \
  && tar -xzf zookeeper-${ZOO_VERSION}.tar.gz \
  && rm -rf zookeeper-${ZOO_VERSION}.tar.gz
# Config zookeeper.
RUN mv $DIR_HOME/zookeeper-${ZOO_VERSION}/conf/zoo_sample.cfg $DIR_HOME/zookeeper-${ZOO_VERSION}/conf/zoo.cfg 
EXPOSE $ZOO_PORT

# Arguments kafka version.
ARG KAFKA_MAJOR=0.8
ARG KAFKA_UPDATE=2
ARG KAFKA_BUILD=2
ARG KAFKA_SBT=2.11
ARG KAFKA_PORT=9092
ARG KAFKA_JMX_PORT=9090
ARG KAFKA_VERSION=${KAFKA_MAJOR}.${KAFKA_UPDATE}.${KAFKA_BUILD}
# Set ENV kafka.
ENV KAFKA_HOME=$DIR_HOME/kafka_${KAFKA_SBT}-${KAFKA_VERSION}
ENV PATH $PATH:$KAFKA_HOME/bin
# Install kafka.
RUN curl -L -O http://mirrors.viethosting.com/apache/kafka/${KAFKA_VERSION}/kafka_${KAFKA_SBT}-${KAFKA_VERSION}.tgz \
  && tar -xzf kafka_${KAFKA_SBT}-${KAFKA_VERSION}.tgz \
  && rm -rf kafka_${KAFKA_SBT}-${KAFKA_VERSION}.tgz
# Config kafka.
COPY conf.d/kafka $DIR_HOME/conf.d/kafka
EXPOSE $KAFKA_PORT $KAFKA_JMX_PORT

# Arguments spark version.
# SPARK
ARG SPARK_MAJOR_VERSION=2
ARG SPARK_UPDATE_VERSION=1
ARG SPARK_BUILD_VERSION=1
ARG SPARK_MASTER_PORT=7070
ARG SPARK_MASTER_REST_PORT=6066
ARG SPARK_MASTER_WEBUI_PORT=8080
ARG SPARK_WORKER_WEBUI_PORT=8081
ARG SPARK_MASTER_JOB_PORT=4040
ARG SPARK_VERSION=${SPARK_MAJOR_VERSION}.${SPARK_UPDATE_VERSION}.${SPARK_BUILD_VERSION}
# Set ENV spark.
ENV SPARK_HOME $DIR_HOME/spark-${SPARK_VERSION}-bin-hadoop2.7
ENV PATH $PATH:$SPARK_HOME/bin
# Install spark.
#ARG SPARK_ARCHIVE=https://d3kbcqa49mib13.cloudfront.net/spark-2.1.1-bin-hadoop2.7.tgz
ARG SPARK_ARCHIVE=https://d3kbcqa49mib13.cloudfront.net/spark-${SPARK_VERSION}-bin-hadoop2.7.tgz
RUN curl -L -O $SPARK_ARCHIVE \
  && tar -xzf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz \
  && rm -rf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz
# Config spark.
# for High-availability like zoo-keeper's leader election
# COPY ha.conf $SPARK_HOME/conf

EXPOSE $SPARK_MASTER_PORT $SPARK_MASTER_REST_PORT \
  $SPARK_MASTER_WEBUI_PORT $SPARK_WORKER_WEBUI_PORT $SPARK_MASTER_JOB_PORT

# Arguments elasticsearch version.
ARG ELASTIC_MAJOR=5
ARG ELASTIC_UPDATE=4
ARG ELASTIC_BUILD=1
ARG ELASTIC_VERSION=${ELASTIC_MAJOR}.${ELASTIC_UPDATE}.${ELASTIC_BUILD}
ARG ELASTIC_PORT=9200
ARG ELASTIC_TRANSPORT_PORT=9300
# Set ENV elasticsearch.
ENV ELASTIC_HOME=$DIR_HOME/elasticsearch-${ELASTIC_VERSION}
ENV PATH $PATH:$ELASTIC_HOME/bin
# Install elastichsearch.
RUN curl -L -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-${ELASTIC_VERSION}.tar.gz \
  && tar -zxf elasticsearch-${ELASTIC_VERSION}.tar.gz \
  && rm -rf elasticsearch-${ELASTIC_VERSION}.tar.gz
# Config elasticsearch
COPY conf.d/elasticsearch $ELASTIC_HOME/config
EXPOSE $ELASTIC_PORT $ELASTIC_TRANSPORT_PORT

# Arguments kibana version.
ARG KIBANA_MAJOR=5
ARG KIBANA_UPDATE=4
ARG KIBANA_BUILD=1
ARG KIBANA_VERSION=${KIBANA_MAJOR}.${KIBANA_UPDATE}.${KIBANA_BUILD}
ARG KIBANA_PORT=5601
# Set ENV kibana.
ENV KIBANA_HOME=$DIR_HOME/kibana-${KIBANA_VERSION}-linux-x86_64
ENV PATH $PATH:$KIBANA_HOME/bin
# Install kibana.
RUN curl -L -O https://artifacts.elastic.co/downloads/kibana/kibana-${KIBANA_VERSION}-linux-x86_64.tar.gz \
  && tar -zxf kibana-${KIBANA_VERSION}-linux-x86_64.tar.gz \
  && rm -rf kibana-${KIBANA_VERSION}-linux-x86_64.tar.gz
# Config kibana.
RUN set -x \
# The default "server.host" is "localhost" in 5+
	&& sed -ri "s!^(\#\s*)?(server\.host:).*!\2 '0.0.0.0'!" $KIBANA_HOME/config/kibana.yml \
	&& grep -q "^server\.host: '0.0.0.0'\$" $KIBANA_HOME/config/kibana.yml \
	\
# Ensure the default configuration is useful when using --link
	&& sed -ri "s!^(\#\s*)?(elasticsearch\.url:).*!\2 'http://localhost:9200'!" $KIBANA_HOME/config/kibana.yml \
	&& grep -q "^elasticsearch\.url: 'http://localhost:9200'\$" $KIBANA_HOME/config/kibana.yml
EXPOSE $KIBANA_PORT

COPY script.d $DIR_HOME/script.d
COPY docker-entrypoint.sh $DIR_HOME
RUN chown -R $USER_BDS $DIR_HOME
USER $USER_BDS
ENTRYPOINT ["/opt/src/docker-entrypoint.sh"]
# CMD ["/opt/src/docker-entrypoint.sh"]