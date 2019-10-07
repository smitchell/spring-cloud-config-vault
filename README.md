# Spring Cloud Config and Spring Vault

This project demonstrates Spring Vault. It is still a work in progress. Stay tuned.

## What Works
* Dynamically generated database credentials sourced from Vault (auth and geography services)
* JWT public and private keys stored in Vault (all projects)
* OAuth client secret stored in Vault (geography and proxy service)

## Known Issues
Running Vault with TLS breaks the geography service, so TLS is disabled for now.

## TODO
* Encrypted secrets
* Encryption as a service (e.g., encrypt a piece of PCI data, like SSN)
* Convert the protected website to be a front-end for the Geography Service
* Dynamic AWS credentials (I'll probably save for a dedicated AWS project)

## How to run this project locally

Before you start, open the /vault subdirectory and follow the README for setting up Vault.

### Set the configuration service path

That config-repo path is relative using the $PWD environment variable. If you are running
this on Windows you'll need to fix this path for your operating system. The YAML
snippet below is from the the config-service main application.yml and was tested on OSX.

```
spring:
  cloud:
    config:
      server:
        native:
          search-locations: file://${pwd}/../config-repo
```

As you can see, the Config Service is running in native mode, which is only suitable for testing.
Use a Git repository for non-Dev environments.

### Start the Config Service
1) cd ./spring-cloud-config-vault/config-service
2) mvn spring-boot:run

### Start the Authentication Service
1) cd ./spring-cloud-config-vault/authentication-service
2) mvn spring-boot:run

### Start the Geography Server
1) cd ./spring-cloud-config-vault/geography-service
2) mvn spring-boot:run

### Start the Protected Web site
1) cd ./spring-cloud-config-vault/protected-web-site
2) npm install
3) ng serve --baseHref=/protected-web-site/ --port=9001

### Start the Zuul Proxy
1) /spring-cloud-config-vault/proxy-service
2) mvn spring-boot:run


## Validation

1. *Angular* - Open the Angular App directly: http://localhost:9001/protected-web-site/
2. *Authentication Server* - Get an access token from the authentication server (see curl command below)
3. *Proxy with SSO* - Access the Angular App via the proxy: http://localhost:9000/protected-web-site

```   
curl --user zuul-proxy-example:client-secret \
http://localhost:9002/oauth/token \
-d 'grant_type=password&client_id=zuul-proxy-example&username=user&password=password'
```

## Testing
1) User tries to access the protected web site via authentication SSO-enabled proxy server, http://localhost:9000/protected-web-site/
2) Spring Security on the proxy server redirects the user's browser to the Login page on the authentication server (http://localhost:9002/login).
3) The user signs in and posts the Login form to the authentication server.
4) The authentication server redirects the user to the login page on the proxy service passing a JSESSIONID (http://localhost:9000/login)
5) The proxy server login page bypasses the 3rd Party Permission page and redirects the browser to the protected resource URL (/protected-web-site/).
6) The http://localhost:9000/protected-web-site/ is displayed.
