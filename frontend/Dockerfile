FROM docker.io/library/node:20-bullseye AS buildimage
# Use bullseye, because python 3.11 causes problems. Bullseye has python 3.9

RUN  export DEBIAN_FRONTEND=noninteractive && apt-get update \
  && apt-get install --no-install-recommends --no-install-suggests -y \
  git python3 brotli

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
RUN nice npm run "$BUILD_TARGET"

# Precompress images for tomcat to serve
RUN nice find dist -type f \( -name '*.css' -o -name '*.js' -o -name "*.svg" -o -name "*.json" -o -name "*.map" \) -print0 -exec gzip -k9 {} \; -exec brotli -kZ {} \;






