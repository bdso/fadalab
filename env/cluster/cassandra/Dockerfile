# Pull base image.
FROM centos:latest
MAINTAINER leophan<leo.phan0411@gmail.com>

ARG USER_BDS=bdslab
RUN groupadd -r $USER_BDS && useradd -r -g $USER_BDS $USER_BDS
WORKDIR /opt/src
RUN chown $USER_BDS /opt/src
ENV DIR_HOME=/opt/src
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

# Arguments kafka version.
ARG CASSANDRA_MAJOR=3
ARG CASSANDRA_UPDATE=11
ARG CASSANDRA_BUILD=0
ARG CASSANDRA_PORT=9042
ARG CASSANDRA_VERSION=${CASSANDRA_MAJOR}.${CASSANDRA_UPDATE}.${CASSANDRA_BUILD}
# Set ENV kafka.
ENV CASSANDRA_HOME=$DIR_HOME/apache-cassandra-${CASSANDRA_VERSION}
ENV PATH $PATH:$CASSANDRA_HOME/bin
# Install kafka.

# RUN curl -L -O http://mirrors.viethosting.com/apache/cassandra/3.11.0/apache-cassandra-3.11.0-bin.tar.gz
RUN curl -L -O http://mirrors.viethosting.com/apache/cassandra/3.11.0/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz \
  && tar -xzf apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz \
  && rm -rf apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz
# Config kafka.
# COPY conf.d/ $DIR_HOME/conf.d/kafka
EXPOSE $CASSANDRA_PORT 7000

COPY script.d $DIR_HOME/script.d
COPY docker-entrypoint.sh $DIR_HOME
RUN chown -R $USER_BDS $DIR_HOME
USER $USER_BDS
ENTRYPOINT ["/opt/src/docker-entrypoint.sh"]
# CMD ["/opt/src/docker-entrypoint.sh"]