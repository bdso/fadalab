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
  && yum -y install net-tools nc && yum clean all && rm -rf /tmp/* /var/tmp/* && rm -rf /var/cache/*
# Check java.
RUN ["java","-version"]

USER $USER_BDS

# SPARK
ARG SPARK_MAJOR_VERSION=2
ARG SPARK_UPDATE_VERSION=1
ARG SPARK_BUILD_VERSION=1
ARG SPARK_VERSION=${SPARK_MAJOR_VERSION}.${SPARK_UPDATE_VERSION}.${SPARK_BUILD_VERSION}

#ARG SPARK_ARCHIVE=https://d3kbcqa49mib13.cloudfront.net/spark-2.1.1-bin-hadoop2.7.tgz
ARG SPARK_ARCHIVE=https://d3kbcqa49mib13.cloudfront.net/spark-${SPARK_VERSION}-bin-hadoop2.7.tgz
RUN curl -L -O $SPARK_ARCHIVE \
  && tar -xzf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz \
  && rm -rf spark-${SPARK_VERSION}-bin-hadoop2.7.tgz

ENV SPARK_HOME $DIR_HOME/spark-${SPARK_VERSION}-bin-hadoop2.7
ENV PATH $PATH:$SPARK_HOME/bin

# for High-availability like zoo-keeper's leader election
# COPY ha.conf $SPARK_HOME/conf

EXPOSE 4040 6066 7077 8080

WORKDIR $SPARK_HOME