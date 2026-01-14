FROM docker.io/library/node:24 AS buildimage

RUN  export DEBIAN_FRONTEND=noninteractive && apt-get update \
  && apt-get install --no-install-recommends --no-install-suggests -y \
  git python3 brotli parallel

WORKDIR /opt/oskari

COPY package.json ./
# Skip package--lock...

RUN npm install

COPY apps ./apps
COPY bundles ./bundles
COPY .storybook ./.storybook

RUN nice npm run test

RUN nice npm run build

# Precompress images for tomcat to serve
RUN MAX_PARALLEL="$(($(nproc)>10?10:$(nproc)))" && echo "Running with $MAX_PARALLEL threads" && \
    nice find dist -type f -size +1000c \
    \( -name '*.css' -o -name '*.js' -o -name "*.svg" -o -name "*.json" -o -name "*.map" \) \
    -print0 | parallel -0 -j${MAX_PARALLEL} 'gzip -k9 {} && brotli -kZ {}'






