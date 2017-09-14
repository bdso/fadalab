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

USER $USER_BDS

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

COPY script.d $DIR_HOME/script.d
COPY docker-entrypoint.sh $DIR_HOME
ENTRYPOINT ["/opt/src/docker-entrypoint.sh"]
# CMD ["/opt/src/docker-entrypoint.sh"]