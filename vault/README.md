# SETUP

## Set-up Vault
Follow these instructions to install vault:

https://learn.hashicorp.com/vault/getting-started/install

Complete the entire getting-started tutorial if you have time.

## Be Sure to update your path

Follow the Vault setup and update your path (e.g. .bash_profile).
```
export VAULT_HOME=[your path to directory containing the vault binary]
export PATH=$PATH:$VAULT_HOME
```

Open a terminal and navigate to the folder containing this README file.


## Self-signed Certificates

This project is NOT running Vault with a self-signed certificate because I ran into issues connect to Vault from a Spring Boot project.
An example script to create a keystore can be found in the Spring Cloud Vault GitHub project (https://github.com/spring-cloud/spring-cloud-vault) in src/test/bash/create_certificates.sh. For convenience, I included create_certificates.sh in the /vault directory of this project.
That script creates a /work directory. I renamed /work to [projects directory]/spring-cloud-config-vault/vault/certs.

# RUNNING VAULT

This section covers starting vault for the first time
(not counting if you ran it in Dev mode per https://learn.hashicorp.com/vault/getting-started/dev-server).

## Start the Server using the Config File

Open a terminal, navigate to this project's /vault directory, and start Vault.

```
cd [projects directory]/spring-cloud-config-vault/vault
vault server -config ./vault-config.hcl
```

If you want try TLS run "vault server -config ./vault-config-tls.hcl"
## Initialize Vault

Open a second terminal, change to directory [projects directory]/spring-cloud-config-vault/vault, then export the environment variable shown:

### Setup without TLS
```
export VAULT_ADDR=http://localhost:8200
export VAULT_SKIP_VERIFY=false
vault operator init
```

### Setup with TLS
```
export VAULT_ADDR=https://localhost:8200
export VAULT_SKIP_VERIFY=false
export VAULT_CAPATH=${PWD}/certs/ca/certs/ca.cert.pem
vault operator init
```

Your output will look like this *SAVE A COPY OF YOUR KEYS AND ROOT TOKEN*.
```
$ vault operator init
Unseal Key 1: FuC34l872lYb1tLKj1hGbo8kGXLO9OgKXtbd+txoI4Jm
Unseal Key 2: rKgVMScKwIUnAnKahu+OUdhNVRS92NOZmjTMgXuKDY24
Unseal Key 3: mGpGSH11uz2ED0t2A5LNRmROx8xspmiZGlJAbyrTvgxW
Unseal Key 4: EC/RnLNtvivZqMrZ29rpav6Tg/FDIIy13k6/0BM2MUPA
Unseal Key 5: JKGORcUNtqGSXbTfovin+Th9ZBpZv8R9EUPCfuyaqPOR

Initial Root Token: s.R6GdBgtUUcSrNC7teyAW0KWA

Vault initialized with 5 key shares and a key threshold of 3. Please securely
distribute the key shares printed above. When the Vault is re-sealed,
restarted, or stopped, you must supply at least 3 of these keys to unseal it
before it can start servicing requests.

Vault does not store the generated master key. Without at least 3 key to
reconstruct the master key, Vault will remain permanently sealed!

It is possible to generate new unseal keys, provided you have a quorum of
existing unseal keys shares. See "vault operator rekey" for more information.
```

## Export the Root Token

Vault commands that require authentication use the environment variable VAULT_TOKEN.
Export the token emitted to the console when you initialized Vault.
```
export VAULT_TOKEN=s.R6GdBgtUUcSrNC7teyAW0KWA
```

## Unseal Vault

Check the Vault status. It should be sealed:
```
$ vault status
Key                Value
---                -----
Seal Type          shamir
Initialized        true
Sealed             true
Total Shares       5
Threshold          3
Unseal Progress    0/3
Unseal Nonce       n/a
Version            1.2.3
HA Enabled         false
```

Use any three of the keys to unseal vault.
```
vault operator unseal rKgVMScKwIUnAnKahu+OUdhNVRS92NOZmjTMgXuKDY24
vault operator unseal EC/RnLNtvivZqMrZ29rpav6Tg/FDIIy13k6/0BM2MUPA
vault operator unseal JKGORcUNtqGSXbTfovin+Th9ZBpZv8R9EUPCfuyaqPOR
```

The output from the last unseal command should show it is now unsealed:
```
Key             Value
---             -----
Seal Type       shamir
Initialized     true
Sealed          false
Total Shares    5
Threshold       3
Version         1.2.3
Cluster Name    vault-cluster-a53bcec8
Cluster ID      cf19d6c1-24b7-e515-84eb-a7b74d622844
HA Enabled      false
```

# Create a Token to use with Spring Vault

Tokens can be used to partition secrets. In the example we use the same token
for all the services, but you could create separate tokens for each microservice.
In the event that an intruder got a hold of the Vault SSL Cert and a token,
they could not access secrets of other microservice.

```
$ vault token create -id="00000000-0000-0000-0000-000000000000" -policy="root"

WARNING! The following warnings were returned from Vault:

  * Supplying a custom ID for the token uses the weaker SHA1 hashing instead
  of the more secure SHA2-256 HMAC for token obfuscation. SHA1 hashed tokens
  on the wire leads to less secure lookups.

Key                  Value
---                  -----
token                00000000-0000-0000-0000-000000000000
token_accessor       NSW9ribsBzMsoR3FrXSEUZFh
token_duration       ∞
token_renewable      false
token_policies       ["root"]
identity_policies    []
policies             ["root"]
```

Export the new token:

```
export VAULT_TOKEN=00000000-0000-0000-0000-000000000000
```

# SETUP SECRETS FOR JWT RSA SIGNING KEYS

These steps add the RSA signing keys for JWT encoding to Vault.

## Enable the Key/Value Secrets Engine
```
vault secrets enable -path=secret kv
Success! Enabled the kv secrets engine at: secret/
```

## Add the RSA Private and Public Key

From a terminal, make sure that you are in the ./vault directory where the key files for
this project are stored.

### Add the Authentication Service private and public keys
```
vault kv put \
    secret/authentication-service \
    keyPair.private-key=@jwt_rsa_private.key \
    keyPair.public-key=@jwt_rsa_public.key
```

### Add the public key for the Proxy Service
Let's assume a unique client secret was set in the database on the Proxy OAuth2 consumer record.
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

Use the config-service endpoints to verify that placeholder values are in place.
Secrets are not exposed on these endpoints. Variable substitution takes place 
in the respective microservices, not in the configuration service.
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

Connect to the MySQL instance, and create these two databases.

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

Use the database role to dynamically generate a set of user credentials.
```
$ vault read database/creds/geography-all-privileges-accounts
Key                Value
---                -----
lease_id           database/creds/geography-all-privileges-accounts/BMpPgzfjam5YDMLBeTttSnNh
lease_duration     768h
lease_renewable    true
password           A1a-nvcb753HmF7JDPdi
username           v-geog-LoE0Cn7rI

$ vault read database/creds/authentication-all-privileges-accounts
```
