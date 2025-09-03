FROM docker.io/library/tomcat:11.0

COPY webapp-map/target/oskari-map.war webapps/
COPY webapp-map/target/oskari-map/WEB-INF/lib/postgresql-*.jar lib/ext/

COPY ./docker/oskari-map.xml webapps/
COPY ./docker/oskari.ini start.d/
COPY ./docker/jetty-oskari.xml etc/
# Trust X-Forwarded headers
COPY docker/forwarded-customizer.ini start.d/
COPY docker/forwarded-customizer.xml etc/

COPY ./docker/oskari-ext.properties resources/
COPY ./docker/log4j2.xml resources/

# context path in oskari-map.xml
ENV OSKARI_CONTEXT_PATH="/"
