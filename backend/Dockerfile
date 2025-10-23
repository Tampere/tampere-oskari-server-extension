FROM docker.io/library/maven:3.9-eclipse-temurin-17-alpine as buildimage
#FROM maven:3-openjdk-8-slim as buildimage
WORKDIR /opt/oskari/

COPY docker/maven-settings /root/.m2/

COPY pom.xml pom.xml
COPY file-download/pom.xml file-download/pom.xml
COPY server-extension/pom.xml server-extension/pom.xml
COPY tampere-resources/pom.xml tampere-resources/pom.xml
COPY webapp-map/pom.xml webapp-map/pom.xml

# Cache maven dependencies, so we don't need to fetch them every time any file changes
RUN mvn test verify package clean dependency:resolve dependency:resolve-plugins --fail-never --no-transfer-progress  && rm -Rf ./target ./*/target


COPY file-download file-download
COPY server-extension server-extension
COPY tampere-resources tampere-resources
COPY webapp-map webapp-map


COPY oskari-frontend*.tar.gz ./
RUN mkdir -p ./webapp-map/src/main/webapp/Oskari/ \
    && tar -xz -C ./webapp-map/src/main/webapp/Oskari/ -f /opt/oskari/oskari-frontend*.tar.gz
    # add the frontend to oskari-override, which is loaded in webapp-map/src/main/java/org.oskari/ClientResourceConfiguration
RUN echo "oskari.client.version=dist/$(ls ./webapp-map/src/main/webapp/Oskari/dist )" > /opt/oskari/webapp-map/src/main/resources/oskari-override.properties


RUN mvn --no-transfer-progress package

FROM docker.io/library/tomcat:10-jdk17

COPY --from=buildimage /opt/oskari/webapp-map/target/oskari-map/WEB-INF/lib/postgresql-*.jar lib/

ARG OSKARI_CONTEXT="oskaritest"
COPY --from=buildimage /opt/oskari/webapp-map/target/oskari-map.war webapps/${OSKARI_CONTEXT}.war

# Oskari configurations we can apply at build time ( path, etc )
ENV OSKARI_OSKARI_AJAX_URL_PREFIX="/${OSKARI_CONTEXT}/action?"
ENV OSKARI_OSKARI_CLIENT_VERSION="dist/3.1.0-tre01"
ENV OSKARI_OSKARI_CLIENT_DOMAIN="http://localhost:8080/${OSKARI_CONTEXT}"

# COPY ./docker/tomcat-descriptor.xml conf/Catalina/localhost/${OSKARI_CONTEXT}.xml
#COPY ./docker/tomcat-descriptor.xml webapps/oskari-map.xml
#COPY ./docker/oskari.ini start.d/
#COPY ./docker/jetty-oskari.xml etc/
# Trust X-Forwarded headers
#COPY docker/forwarded-customizer.ini start.d/
#COPY docker/forwarded-customizer.xml etc/
COPY ./docker/log4j2.xml lib/
#COPY ./docker/oskari-ext.properties lib/

# context path in tomcat-descriptor.xml
#ENV OSKARI_CONTEXT_PATH="$}"
