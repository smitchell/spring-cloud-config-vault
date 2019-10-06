# SETUP

## Set-up Vault
Follow these instructions to install vault: https://learn.hashicorp.com/vault/getting-started/install.  There is an excellent Getting Started tutorial there too.
Be sure to update your PATH variable (e.g. in .bash_profile).
```
export VAULT_HOME=[path to the directory containing the Vault binary]
export PATH=$PATH:$VAULT_HOME
```
Open a terminal and navigate to the folder containing this README file.

# RUNNING VAULT

This section covers starting vault for the first time
(not counting if you ran it in Dev mode per https://learn.hashicorp.com/vault/getting-started/dev-server).

## Self-signed Certificates

This project should run Vault with a self-signed certificate created using scripts found in the Spring Cloud Vault GitHub project (https://github.com/spring-cloud/spring-cloud-vault) in src/test/bash/create_certificates.sh. Unfortunately, I ran into problems, so it is running over HTTP.

## Start the Server using the Config File

Open a terminal, navigate to this project's /vault directory, and start Vault.
```
cd [projects directory]/spring-cloud-config-vault/vault
vault server -config ./vault-config.hcl
```
If you want try TLS run "vault server -config ./vault-config-tls.hcl"

## Initialize Vault

Open a second terminal, change to the directory [projects directory]/spring-cloud-config-vault/vault, then export the environment variables shown below, with or without TLS depending on the ".hcl" file you used above.

### Setup without TLS
```
export VAULT_ADDR=http://localhost:8200
export VAULT_SKIP_VERIFY=false
```

### Setup with TLS
```
export VAULT_ADDR=https://localhost:8200
export VAULT_SKIP_VERIFY=false
export VAULT_CAPATH=${PWD}/work/ca/certs/ca.cert.pem
```
Run the initialization command:
```
vault operator init
```

Your output will look like this *SAVE A COPY OF YOUR KEYS AND ROOT TOKEN*.
```
Unseal Key 1: cNnFyloC4HcRuQY0YgumFFmXzKZGD1zxPMa+PTyPC1rI
Unseal Key 2: wsi8Gb8o7iS6Lecq9vxuJNG94TAqgq8fhZ0AAFZbgEpy
Unseal Key 3: AmSgIQXQoOvEg4seqgJfnqdt8/Z4pAKluY2YhHeiunX7
Unseal Key 4: J90vt/b+uUiVuyrJ1WxT3ms/J5kVtWCziZiL+GTMEVfe
Unseal Key 5: +/bN44FFbXRNGdDIc/TkiunauMth+ERqyMXnAyPZmfHr

Initial Root Token: s.O7KR9Wk2DsVDKhR6b0mLeP0q

Vault initialized with 5 key shares and a key threshold of 3. Please securely distribute the key shares printed above. When the Vault is re-sealed, restarted, or stopped, you must supply at least 3 of these keys to unseal it before it can start servicing requests.

Vault does not store the generated master key. Without at least 3 key to
reconstruct the master key, Vault will remain permanently sealed!

It is possible to generate new unseal keys, provided you have a quorum of existing unseal keys shares. See "vault operator rekey" for more information.
```

## Export the Root Token
Vault commands that require authentication use the environment variable VAULT_TOKEN. Export the token emitted to the console when you initialized Vault.
```
export VAULT_TOKEN=s.O7KR9Wk2DsVDKhR6b0mLeP0q
```

## Unseal Vault
Use any three of the keys to unseal vault.
```
vault operator unseal cNnFyloC4HcRuQY0YgumFFmXzKZGD1zxPMa+PTyPC1rI
vault operator unseal AmSgIQXQoOvEg4seqgJfnqdt8/Z4pAKluY2YhHeiunX7
vault operator unseal wsi8Gb8o7iS6Lecq9vxuJNG94TAqgq8fhZ0AAFZbgEpy
```

# Create a Token to use with Spring Vault

Tokens can be used to partition secrets. In the example, we use the same token for all the services, but you could create separate tokens for each microservice. In the event that an intruder got ahold of the Vault SSL Cert and a token, they could not access the secrets of other microservice. Of course, for a partitioning strategy you wouldn't use a policy of "root." See the documentation on Policies for more details: https://www.vaultproject.io/docs/concepts/policies.html.
```
$vault token create -id="00000000-0000-0000-0000-000000000000" -policy="root"
```

Export the new token:
```
export VAULT_TOKEN=00000000-0000-0000-0000-000000000000
```

You can verify the token with this curl command:
```
curl -k -X GET -H "X-Vault-Token:00000000-0000-0000-0000-000000000000" 'https://localhost:8200/v1/auth/token/lookup-self'
```

# SETUP SECRETS FOR JWT RSA SIGNING KEYS

These steps add the RSA signing keys for JWT encoding to Vault.

## Enable the Key/Value Secrets Engine
```
vault secrets enable -path=secret kv
```

## Add the RSA Private and Public Key

From a terminal, make sure that you are in the ./vault directory where the key files for this project are stored.

### Add the Authentication Service private and public keys
```
vault kv put \
    secret/authentication-service \
    keyPair.private-key=@jwt_rsa_private.key \
    keyPair.public-key=@jwt_rsa_public.key
```

### Add the public key for the Proxy Service
```
vault kv put secret/proxy-service \
    security.oauth2.resource.jwt.keyValue=@jwt_rsa_public.key \
    security.oauth2.client.clientSecret="bcN2?NrNF"
```

### Add the public key for the Geography Service
Let's assume a unique client secret was set in the database on the Geography OAuth2 consumer record.
```
vault kv put secret/geography-service \
    security.oauth2.resource.jwt.keyValue=@jwt_rsa_public.key \
    security.oauth2.client.clientSecret="bcN2?NrNF"
```

### Verify your work
Use these commands to display the secrets that you create.
```
vault read secret/authentication-service
vault read secret/proxy-service
vault read secret/geography-service
```

Use the config-service endpoints to verify that placeholder values are in place. You can't see the secret values. Variable substitution takes place in the respective microservices, not in the configuration service.
* http://localhost:8888/application/default
* http://localhost:8888/authentication-service/default
* http://localhost:8888/geography-service/default
* http://localhost:8888/proxy-service/default


# DATABASE SETUP
If you don't have MySQL installed, then launch an instance using Docker.
```
docker \
 run \
 --detach \
 --env MYSQL_ROOT_PASSWORD='password1' \
 --name mysql \
 --publish 3306:3306 \
 mysql
```

Connect to the MySQL instance and create the two databases.
```
CREATE DATABASE IF NOT EXISTS `geography`;
CREATE DATABASE IF NOT EXISTS `authentication`;
```

## Enable database secrets.
```
vault secrets enable database
```

## Add Database Configuration
Create Database Configuration Resources using an Id with privileges to add new users.
```
vault write database/config/mysql-geography \
  plugin_name=mysql-legacy-database-plugin \
  connection_url="{{username}}:{{password}}@tcp(127.0.0.1:3306)/geography" \
  allowed_roles="*" \
  username="root" \
  password="password1"

vault write database/config/mysql-authentication \
  plugin_name=mysql-legacy-database-plugin \
  connection_url="{{username}}:{{password}}@tcp(127.0.0.1:3306)/authentication" \
  allowed_roles="*" \
  username="root" \
  password="password1"
```

## Create Database Role
Create Database Roles containing user templates to generate users. For instance, one read-only and one read-write.
```
vault write database/roles/geography-all-privileges-accounts \
    db_name=mysql-geography \
    creation_statements="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';GRANT ALL ON geography.* TO '{{name}}'@'%';"

vault write database/roles/authentication-all-privileges-accounts \
    db_name=mysql-authentication \
    creation_statements="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';GRANT ALL ON authentication.* TO '{{name}}'@'%';"
```

## Test Dynamically Generated User Credentials
Use the new database role to dynamically generate user credentials.
```
vault read database/creds/geography-all-privileges-accounts
vault read database/creds/authentication-all-privileges-accounts
```
