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
COPY conf.d/ $ELASTIC_HOME/config
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
ENTRYPOINT ["/opt/src/docker-entrypoint.sh"]
# CMD ["/opt/src/docker-entrypoint.sh"]