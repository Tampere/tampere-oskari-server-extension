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

# Deployment and running

The application is deployed as a container image which containes both fronend and backend.
Both are stored in this git repository. Fronend will be built as a release resource, and backend
will be deployed as a container image.

## Updating oskari frontend

1. Commit changes to git
2. Trigger a new build of the frontend in [Github actions](https://github.com/Tampere/tampere-oskari-server-extension/actions).

### Frontend versioning
Frontend version can be changed manually by updating [package.json](frontend/package.json) version property.

## Updating Oskari Backend

1. Modify pom.xml in project root, and change property to match the wanted oskari version.
2. Commit the change to git.
3. Trigger a new build of the backend in [Github actions](https://github.com/Tampere/tampere-oskari-server-extension/actions). 

### backend versioning
 * Major version should match the oskari deployed version. For example 3.2.4 -> 324.0.0
 * Minor version should be increased when changes which might break something are added.
 * Patch version should be increased only when changes are safe enough to be deployed without major testing

Patch version is increased automatically by Github actions. Major and minor version can be changed manually with 
`mvn versions:set -DnewVersion=1.2.3-SNAPSHOT`. Version should be set to SNAPSHOT, so the github actions work
and increment the version correctly.

## Logging in to github container registry

Container images are private per global organisation rules.
An access token can be created to allow read-only access to the container registry: https://github.com/settings/tokens

1. Generate classic access token with permission `read:packages`.
    * Note: You might want to extend the expiration time of the access token
    * Remember to copy the access token when displayed. If it is lost, a new token must be generated.
2. Install the token to servers `podman login ghcr.io -u your-github-username-here`
    * Paste the copied access token.

