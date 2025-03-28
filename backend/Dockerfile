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
RUN mvn test verify package clean dependency:resolve dependency:resolve-plugins --fail-never && rm -Rf ./target ./*/target


COPY file-download file-download
COPY server-extension server-extension
COPY tampere-resources tampere-resources
COPY webapp-map webapp-map

RUN mvn package

FROM docker.io/library/jetty:9.4-jre17-alpine

COPY --from=buildimage /opt/oskari/webapp-map/target/oskari-map.war webapps/
COPY --from=buildimage /opt/oskari/webapp-map/target/oskari-map/WEB-INF/lib/postgresql-*.jar lib/ext/
COPY ../../ubigu/oskari-backend-shared/docker/oskari-map.xml webapps/
COPY ../../ubigu/oskari-backend-shared/docker/oskari.ini start.d/
COPY ../../ubigu/oskari-backend-shared/docker/jetty-oskari.xml etc/

COPY ../../ubigu/oskari-backend-shared/docker/oskari-ext.properties resources/
COPY ../../ubigu/oskari-backend-shared/docker/log4j2.xml resources/

# context path in oskari-map.xml
ENV OSKARI_CONTEXT_PATH="/oskari"
