FROM node:10-alpine

# enable community repo
RUN echo -e "@community http://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories \
    && apk add --update --no-cache shadow@community \
    && rm -rf /var/cache/apk/*

# working dir
ARG TARGET_DIR

WORKDIR ${TARGET_DIR}

# add current user
ARG USER_ID
ARG GROUP_ID

# add host unix name
ARG UNIX_NAME

ENV UNIX_NAME=${UNIX_NAME}

RUN deluser --remove-home node \
    && groupadd -o -g ${GROUP_ID} user \
    && useradd -o -m -s /bin/bash -u ${USER_ID} -g ${GROUP_ID} user

# run as node
USER user

CMD ["yarn", "dev"]
