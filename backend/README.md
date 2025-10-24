# Installation for tampere

Oskari is deployed as containers in Tampere environment. Image is generated in
GitHub actions. To update Oskari-core version, use the following instructions.

## Properties
Some properties are set to sensible defaults in the oskari-ext.properties file.
Some are generated at the container build time, depending on the building targets
and the following properties should be configured in the environment:

| Property             | Description                | Example                                   |
|----------------------|----------------------------|-------------------------------------------|
| `OSKARI_DB_USERNAME` | Database username          | `oskari`                                  |
| `OSKARI_DB_PASSWORD` | Database password          | `pwd1234`                                 |
| `OSKARI_DB_URL`      | Database connection string | `jdbc:postgresql://127.0.0.1:5432/dbname` |


## Updating Oskari version in container image

1. Modify pom.xml in project root, and change property to match the wanted oskari version.
2. Commit the version change, and other possible changes to git
3. Create git tag: `git tag v2.13.1-tre3`.
    * NOTE: Version must start with `v` for github actions to trigger image generation
4. Push the tag to git: `git push origin v2.13.1-tre3`
5. GitHub actions will now generate the container image.

## Logging in to github container registry

Container images are private per global organisation rules.
An access token can be created to allow read-only access to the container registry: https://github.com/settings/tokens

1. Generate classic access token with permission `read:packages`.
    * Note: You might want to extend the expiration time of the access token
    * Remember to copy the access token when displayed. If it is lost, a new token must be generated.
2. Install the token to servers `podman login ghcr.io -u your-github-username-here`
    * Paste the copied access token.



# tampere-oskari-server-extension

Tampere Oskari server extension

Extends Oskari server functionality to serve WFS search.

## Releases

### 1.1

Upgraded Oskari to 1.40.0. 

Drop oskari_status_tampere table from the database to reinit the module. Code from 1.0 has been moved to oskari-server.

### 1.0

Initial release with Oskari 1.34.0

## Prerequisites

These uses Oskari 1.40.0 version.

### Front-end

This extension needs new front-end codes of Oskari (see tampere bundles in https://github.com/dimenteq/tampere-oskari).

### Back-end

This version upgrade drops all ready defined search channels (database upgrades has moved to Flyway).

##### Database

Nothing need to be done. This application upgrades it's database automatically.

##### oskari-ext.properties file changes

```Shell
actionhandler.GetAppSetup.dynamic.bundles = admin-layerselector, admin-layerrights, admin, admin-users, admin-wfs-search-channel
actionhandler.GetAppSetup.dynamic.bundle.admin.roles = Admin
actionhandler.GetAppSetup.dynamic.bundle.admin-wfs-search-channel.roles = Admin
search.channel.WFSSEARCH_CHANNEL.service.url= [URL_FOR_SERVICE]
search.channel.WFSSEARCH_CHANNEL.maxFeatures = 100
search.channels.default=WFSSEARCH_CHANNEL
actionhandler.GetSearchResult.channels=WFSSEARCH_CHANNEL
db.additional.modules=tampere
```

## Installation

* Clone https://github.com/dimenteq/tampere-oskari-server-extension/tree/develop
```Bash
git clone https://github.com/dimenteq/tampere-oskari-server-extension.git
```
* Change develop branch
```
cd tampere-oskari-server-extension
git checkout develop
```
* Run mvn clean install in tampere-oskari-server-extension folder
```Bash
mvn clean install
```

* If you see following error "Could not resolve dependencies for project fi.nls.oskari.spring:webapp.map:w
ar:1.2.0-SNAPSHOT: Could not find artifact", fix this to edit again oskari-spring/webapp-spring/pom.xml file (remove line above):
```Bash
# Remove following line
<version>1.2.0-SNAPSHOT</version>
```
* Stop Jetty
* Remove Oskari spring-map folder if exists (github clone)
* Remove <JETTY>/webapps/spring-map.war
* Remove <JETTY>/webapp/transport.war
* Rename from <JETTY>/contexts/spring-map.xml to <JETTY>/contexts/oskari-map.xml
* Edit <JETTY>/contexts/oskari-map.xml -file and change /webapps/spring-map.war to /webapps/oskari-map.war
* Copy oskari-server-extension/webapp-map/target/oskari-map.war to <JETTY>/webapps/oskari-map.war
* Copy oskari-server-extension/webapp-transport/target/transport.war to <JETTY>/webapps/transport.war
* Start Jetty
