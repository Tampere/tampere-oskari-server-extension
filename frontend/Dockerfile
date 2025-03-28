FROM docker.io/library/node:18-bullseye as buildimage
# Use bullseye, because python 3.11 causes problems. Bullseye has python 3.9

RUN  export DEBIAN_FRONTEND=noninteractive && apt-get update \
  && apt-get install --no-install-recommends --no-install-suggests -y \
  git python3

ENV NODE_OPTIONS="--openssl-legacy-provider --max-old-space-size=8192"

WORKDIR /opt/oskari

COPY package.json ./
# Skip package--lock...

RUN npm install

COPY apps ./apps
COPY bundles ./bundles
COPY .storybook ./.storybook
COPY .eslintrc.js ./

ARG BUILD_TARGET=build
RUN npm run $BUILD_TARGET

FROM docker.io/library/node:18-bullseye as certimage

WORKDIR /opt
# Build self signed cert for ssl
RUN openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/C=FI/ST=Pirkanmaa/L=Tampere/O=Tampere/OU=Paikkatieto/CN=kartat.tampere.fi"

FROM docker.io/library/nginx:1.27-alpine-slim

COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/copy-oskari-data.sh /docker-entrypoint.d/

COPY --from=certimage /opt/*.pem /etc/ssl/private/
COPY --from=buildimage /opt/oskari/dist /opt/oskari/dist



