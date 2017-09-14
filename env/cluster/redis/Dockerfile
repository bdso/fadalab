# Pull base image.
FROM centos:latest
MAINTAINER leophan<leo.phan0411@gmail.com>

ARG USER_BDS=bdslab
RUN groupadd -r $USER_BDS && useradd -r -g $USER_BDS $USER_BDS
ENV DIR_HOME=/opt/src
WORKDIR $DIR_HOME

# Arguments redis eversion.
# ARG PKG_NAME=redis
# ARG REDIS_MAJOR=4
# ARG REDIS_UPDATE=0
# ARG REDIS_BUILD=1
# ARG REDIS_VERSION=${REDIS_MAJOR}.${REDIS_UPDATE}.${REDIS_BUILD}
# ARG PKG_VERSION=${PKG_NAME}-${REDIS_VERSION}
ARG REDIS_PORT=6379
# # Set ENV redis.
# ENV REDIS_HOME=$DIR_HOME/${PKG_NAME}-${REDIS_VERSION}
# ENV PATH $PATH:$REDIS_HOME/bin
# # Install redis.
# RUN curl -L -O http://download.redis.io/releases/${PKG_VERSION}.tar.gz \
#   && tar -zxf ${PKG_VERSION}.tar.gz \
#   && rm -rf ${PKG_VERSION}.tar.gz
# RUN cd ${PKG_VERSION} && make
# Install redis based on yum.
RUN yum -y install net-tools \
  && yum -y install epel-release \
  && yum -y install redis \
  && yum clean all && rm -rf /tmp/* /var/tmp/* && rm -rf /var/cache/*
# Config redis.
COPY script.d $DIR_HOME/script.d
COPY conf.d/ $REDIS_HOME/

# RUN chown -r $USER_BDS $DIR_HOME
EXPOSE $REDIS_PORT
COPY docker-entrypoint.sh $DIR_HOME
USER $USER_BDS
ENTRYPOINT ["/opt/src/docker-entrypoint.sh"]