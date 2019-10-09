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

This project runs Vault with a self-signed certificate created using scripts found in the Spring Cloud Vault GitHub project (https://github.com/spring-cloud/spring-cloud-vault) in src/test/bash/create_certificates.sh.

Aside from the Vault binary that you installed at your $VAULT_HOME, this project contains a /vault directory located at [projects directory]/spring-cloud-config-vault/vault. The Authentication, Geography, and Proxy services each reference /vault/work/keystore.jks. In a non-Dev environment this could be on a Docker Volume, for instance, outside of source control. 

A better option is to import the certificate into Java cacerts:
1) Find the location of cacerts on your machine. On mine, it is $JAVA_HOME/lib/security/cacerts.
2) Backup your cacerts file.
3) Navigate to the directory spring-cloud-config-vault/vault/work/ca/certs
4) Import the ca.cert.pem as shown below using the password "changeit"
```
$ sudo keytool -importcert -alias dev -file ./ca.cert.pem -keystore $JAVA_HOME/lib/security/cacerts

Warning: use -cacerts option to access cacerts keystore
Enter keystore password:
Owner: CN=CA Certificate, O=spring-cloud-vault-config, L=Unknown, ST=Unknown, C=NN
...
Trust this certificate? [no]:  yes
Certificate was added to keystore
```

If you chose to import ./ca.cert.pem into Java cacerts, then comment out the spring.cloud.vault.ssl block in each project bootstrap.yml file:
1) /spring-cloud-config-vault/authentication-service/src/main/src/main/resources/bootstrap.yml
2) /spring-cloud-config-vault/config-service/src/main/src/main/resources/bootstrap.yml
3) /spring-cloud-config-vault/geography-service/src/main/src/main/resources/bootstrap.yml
4) /spring-cloud-config-vault/proxy-service/src/main/src/main/resources/bootstrap.yml

```
# Comment out the spring.cloud.vault.ssl block if you import ca.cert.pem into $JAVA_HOME/lib/security/cacerts
#      ssl:
#        trust-store: file:../vault/work/keystore.jks
#        trust-store-password: changeit
```

The projects can then then be run normally and will successful connect to Vault with TLS.

## Start the Server using the Config File

Open a terminal, navigate to this project's /vault directory, and start Vault.
```
cd [projects directory]/spring-cloud-config-vault/vault
vault server -config ./vault-config.hcl
```

## Initialize Vault

Open a second terminal, change to the directory [projects directory]/spring-cloud-config-vault/vault, then export the environment variables shown below.

### Set the Environment Variables
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
Unseal Key 1: SUibnGmk9UtB8a98DHqBbWulj+4Phs0jVa42h/H6zSwY
Unseal Key 2: 2P5Z3sC/YwGqrWAY8OXAgNElD5NzlWF1RMc4y57GH7hg
Unseal Key 3: 07PbsKZLVzPNAUj2V69JFPc0t6ehWXL65NI5/9I6nqwn
Unseal Key 4: VPN6jIIrLEE+/QhHxe+J5hhQWqFiQkg5/yT7EBeQMghS
Unseal Key 5: zcBc/8tfl2YjVANUsoB7ZGF7rEzusnlb0w9AuuVConjq

Initial Root Token: s.us3mV4cMPRKqDO6z1rAvDjtM

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
Vault commands that require authentication use the environment variable VAULT_TOKEN. Export the token emitted to the console when you initialized Vault.
```
export VAULT_TOKEN=s.us3mV4cMPRKqDO6z1rAvDjtM
```

## Unseal Vault
Use any three of the keys to unseal vault.
```
vault operator unseal SUibnGmk9UtB8a98DHqBbWulj+4Phs0jVa42h/H6zSwY
vault operator unseal 07PbsKZLVzPNAUj2V69JFPc0t6ehWXL65NI5/9I6nqwn
vault operator unseal zcBc/8tfl2YjVANUsoB7ZGF7rEzusnlb0w9AuuVConjq
```

# Create a Token to use with Spring Vault

Tokens can be used to partition secrets. In the example, we use the same token for all the services, but you could create separate tokens for each microservice. In the event that an intruder got ahold of the Vault SSL Cert and a token, they could not access the secrets of other microservice. Of course, for a partitioning strategy you wouldn't use a policy of "root." See the documentation on Policies for more details: https://www.vaultproject.io/docs/concepts/policies.html.
```
vault token create -id="00000000-0000-0000-0000-000000000000" -policy="root"
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

# Certificate Management with Vault

A complete tutorial is available from HashiCorp: https://www.hashicorp.com/blog/certificate-management-with-vault

vault secrets enable pki
Success! Enabled the pki secrets engine at: pki/

If the default one-month ttl is too short, adjust the global vault

vault secrets tune -max-lease-ttl=8760h pki
Success! Tuned the secrets engine at: pki/

## Configure Certificate Authority

There are three methods for configured a CA certificate and associated private key.

* Generate a self-signed root CA
* Generate an intermediate CA (with a Certificate Signing Request, CSR, for signing)
* Set a PEM-encoded certificate and private key bundle directly into the backend

We will generate a self-signed root CA:
```
vault write pki/root/generate/internal \
    common_name=example.com \
    ttl=8760h

    Key              Value
    ---              -----
    certificate      -----BEGIN CERTIFICATE-----
    MIIDNTCCAh2gAwIBAgIUUPkaJk/vc8pRxtwFrq9reX/8e2cwDQYJKoZIhvcNAQEL
    BQAwFjEUMBIGA1UEAxMLZXhhbXBsZS5jb20wHhcNMTkxMDA4MTYzNzEyWhcNMjAx
    MDA3MTYzNzQyWjAWMRQwEgYDVQQDEwtleGFtcGxlLmNvbTCCASIwDQYJKoZIhvcN
    AQEBBQADggEPADCCAQoCggEBANUsYuM6zT8vDDXBxUxgMADqaMtnkCfKWiw9wvbE
    v3MQR5i4fjZgl2JeQIQAQ591WjHvdadh9LBbnHnOHQIKmtBYQUru5nQrKYt8fT+m
    evC8CTVse5ysuN7Jt94rzWtTsVa7/I2QQSjcaNF0gj2DVejP34GEnxc9W79pDxc0
    t6E4ZRazEWVIeYW6cZa5j1nokaa4c4ff1mJbl8tMdOf90HPmYRg3+XJiX+H/R+Sy
    9IllIQgvvcvTVdhDncpT5PwihWU7ovffxrl2mzIwSgb6kqfqLGA1dzGqQCuEw0n8
    LeLxADHFDCDKh8OA0a2yeTS/E+9UL6xcjtiUFkEAlZPfwn0CAwEAAaN7MHkwDgYD
    VR0PAQH/BAQDAgEGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFCCyoqsJDG7M
    IBjMgF7sGwVLPdWDMB8GA1UdIwQYMBaAFCCyoqsJDG7MIBjMgF7sGwVLPdWDMBYG
    A1UdEQQPMA2CC2V4YW1wbGUuY29tMA0GCSqGSIb3DQEBCwUAA4IBAQBFsltobZe5
    2ZL3HwITSKptKO4u4LFCbTlZslPLHGq7+JIPv2IVchJUX3JdngtWFEahxUYS+MHp
    VuxaSMTMyIYqi2UaDLGdFVJ5yhvSyoThQdd3k9Q5JYTTGv7ZOrzTaKxV3XlZs0Ct
    v27Q03gM+ETUvCQ5TFJWrEA+zziiI653uwO0mNqwzNYwy1boBa+kVStzbxIRzCcn
    OBQtMvJQl1lzcvHkp3Act5GxGFQhzsUOS+k5/KJw9d9hz13qHAbq/geXRNLin0fQ
    zQ6is9IETkm3DuUDsGSIlWyA8AeeTAbpm5eqhPG0MHLEvd3F8JA2svWm+5uBbJXU
    /Vour2BpLxoh
    -----END CERTIFICATE-----
    expiration       1602088662
    issuing_ca       -----BEGIN CERTIFICATE-----
    MIIDNTCCAh2gAwIBAgIUUPkaJk/vc8pRxtwFrq9reX/8e2cwDQYJKoZIhvcNAQEL
    BQAwFjEUMBIGA1UEAxMLZXhhbXBsZS5jb20wHhcNMTkxMDA4MTYzNzEyWhcNMjAx
    MDA3MTYzNzQyWjAWMRQwEgYDVQQDEwtleGFtcGxlLmNvbTCCASIwDQYJKoZIhvcN
    AQEBBQADggEPADCCAQoCggEBANUsYuM6zT8vDDXBxUxgMADqaMtnkCfKWiw9wvbE
    v3MQR5i4fjZgl2JeQIQAQ591WjHvdadh9LBbnHnOHQIKmtBYQUru5nQrKYt8fT+m
    evC8CTVse5ysuN7Jt94rzWtTsVa7/I2QQSjcaNF0gj2DVejP34GEnxc9W79pDxc0
    t6E4ZRazEWVIeYW6cZa5j1nokaa4c4ff1mJbl8tMdOf90HPmYRg3+XJiX+H/R+Sy
    9IllIQgvvcvTVdhDncpT5PwihWU7ovffxrl2mzIwSgb6kqfqLGA1dzGqQCuEw0n8
    LeLxADHFDCDKh8OA0a2yeTS/E+9UL6xcjtiUFkEAlZPfwn0CAwEAAaN7MHkwDgYD
    VR0PAQH/BAQDAgEGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFCCyoqsJDG7M
    IBjMgF7sGwVLPdWDMB8GA1UdIwQYMBaAFCCyoqsJDG7MIBjMgF7sGwVLPdWDMBYG
    A1UdEQQPMA2CC2V4YW1wbGUuY29tMA0GCSqGSIb3DQEBCwUAA4IBAQBFsltobZe5
    2ZL3HwITSKptKO4u4LFCbTlZslPLHGq7+JIPv2IVchJUX3JdngtWFEahxUYS+MHp
    VuxaSMTMyIYqi2UaDLGdFVJ5yhvSyoThQdd3k9Q5JYTTGv7ZOrzTaKxV3XlZs0Ct
    v27Q03gM+ETUvCQ5TFJWrEA+zziiI653uwO0mNqwzNYwy1boBa+kVStzbxIRzCcn
    OBQtMvJQl1lzcvHkp3Act5GxGFQhzsUOS+k5/KJw9d9hz13qHAbq/geXRNLin0fQ
    zQ6is9IETkm3DuUDsGSIlWyA8AeeTAbpm5eqhPG0MHLEvd3F8JA2svWm+5uBbJXU
    /Vour2BpLxoh
    -----END CERTIFICATE-----
    serial_number    50:f9:1a:26:4f:ef:73:ca:51:c6:dc:05:ae:af:6b:79:7f:fc:7b:67
```

## Configure URL values for issue certificate endpoints

```
vault write pki/config/urls \
    issuing_certificates="http://127.0.0.1:8200/v1/pki/ca" \
    crl_distribution_points="http://127.0.0.1:8200/v1/pki/crl"
Success! Data written to: pki/config/urls
```

```
vault write pki/roles/example-dot-com \
    allowed_domains=example.com \
    allow_subdomains=true max_ttl=72h
Success! Data written to: pki/roles/example-dot-com
```

```
vault write pki/issue/example-dot-com \
   common_name=my.example.com
Key                 Value
---                 -----
certificate         -----BEGIN CERTIFICATE-----
MIIDuzCCAqOgAwIBAgIUdp6UBH2vb3+KZ/mH/xzykMTcXLEwDQYJKoZIhvcNAQEL
BQAwFjEUMBIGA1UEAxMLZXhhbXBsZS5jb20wHhcNMTkxMDA4MTY0NDE2WhcNMTkx
MDExMTY0NDQ2WjAZMRcwFQYDVQQDEw5teS5leGFtcGxlLmNvbTCCASIwDQYJKoZI
hvcNAQEBBQADggEPADCCAQoCggEBAJtRmjZhDXdS4Nl+5MoLvm+BQ83x4eSI8A33
HsaPynKthQbZ8Kn3XKbrMSAbamGvO/pBWvoPdhTg/74q2K3o8qsY2+Wia9vPS7Hq
CPNS41IddIAtwzKpLhg+ABj5Ln8mS3KgI0P/mGbdPYX1cj4Rkr8TioAZfOn1tIYQ
5lqDd4lybmR43wqIESivN9M/R8mcVOizJE5vOKqcIvbPae1usLL32GJIEI22WpY1
f9SwIasChXZb9CNP5sDmhAEoUsOY5IEg+OMsEB8b9NyDYJbl6FWuVeYqqEPtGckd
DlHA4Z/UodNMAJqjt0WQ9XIbtIGw0ye99z31RkegZFQ9yFV90RsCAwEAAaOB/TCB
+jAOBgNVHQ8BAf8EBAMCA6gwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMC
MB0GA1UdDgQWBBSbbensyH6PK3hz4H+UHONLVkp3+TAfBgNVHSMEGDAWgBQgsqKr
CQxuzCAYzIBe7BsFSz3VgzA7BggrBgEFBQcBAQQvMC0wKwYIKwYBBQUHMAKGH2h0
dHA6Ly8xMjcuMC4wLjE6ODIwMC92MS9wa2kvY2EwGQYDVR0RBBIwEIIObXkuZXhh
bXBsZS5jb20wMQYDVR0fBCowKDAmoCSgIoYgaHR0cDovLzEyNy4wLjAuMTo4MjAw
L3YxL3BraS9jcmwwDQYJKoZIhvcNAQELBQADggEBAGdhIX/MdDCHs4EzY3IZYF2P
C6yvcc6YaJ6zpsFrfSumgf/XIQ4b6yNH9u07zyUVfiH2gLwR0N31wKNrUbDK/eZi
rTy0EU0fsiANGH5qmM7p/5u27AQ0JFCVHqvvLKJcYZafDUuOoqDA/QgVMklsp0dv
jWwi48Y6+dxtWWQFWABDE+4tUni7bodaJGW9Tv1XWQkzGn1BtH8z4rhMf0MxdNtj
1aJ2P/uDFyWZvAfDAak7CLhnLHfmBVifi1cBm6HojGw6KiwhSESV7sKRTmL4s/wz
1cGYHR2G1Er5SdsLzJflF7Z1pOl1ep5OrrgqwPhEJmg94fzlq+1PYZJyE4vMzAc=
-----END CERTIFICATE-----
expiration          1570812286
issuing_ca          -----BEGIN CERTIFICATE-----
MIIDNTCCAh2gAwIBAgIUUPkaJk/vc8pRxtwFrq9reX/8e2cwDQYJKoZIhvcNAQEL
BQAwFjEUMBIGA1UEAxMLZXhhbXBsZS5jb20wHhcNMTkxMDA4MTYzNzEyWhcNMjAx
MDA3MTYzNzQyWjAWMRQwEgYDVQQDEwtleGFtcGxlLmNvbTCCASIwDQYJKoZIhvcN
AQEBBQADggEPADCCAQoCggEBANUsYuM6zT8vDDXBxUxgMADqaMtnkCfKWiw9wvbE
v3MQR5i4fjZgl2JeQIQAQ591WjHvdadh9LBbnHnOHQIKmtBYQUru5nQrKYt8fT+m
evC8CTVse5ysuN7Jt94rzWtTsVa7/I2QQSjcaNF0gj2DVejP34GEnxc9W79pDxc0
t6E4ZRazEWVIeYW6cZa5j1nokaa4c4ff1mJbl8tMdOf90HPmYRg3+XJiX+H/R+Sy
9IllIQgvvcvTVdhDncpT5PwihWU7ovffxrl2mzIwSgb6kqfqLGA1dzGqQCuEw0n8
LeLxADHFDCDKh8OA0a2yeTS/E+9UL6xcjtiUFkEAlZPfwn0CAwEAAaN7MHkwDgYD
VR0PAQH/BAQDAgEGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFCCyoqsJDG7M
IBjMgF7sGwVLPdWDMB8GA1UdIwQYMBaAFCCyoqsJDG7MIBjMgF7sGwVLPdWDMBYG
A1UdEQQPMA2CC2V4YW1wbGUuY29tMA0GCSqGSIb3DQEBCwUAA4IBAQBFsltobZe5
2ZL3HwITSKptKO4u4LFCbTlZslPLHGq7+JIPv2IVchJUX3JdngtWFEahxUYS+MHp
VuxaSMTMyIYqi2UaDLGdFVJ5yhvSyoThQdd3k9Q5JYTTGv7ZOrzTaKxV3XlZs0Ct
v27Q03gM+ETUvCQ5TFJWrEA+zziiI653uwO0mNqwzNYwy1boBa+kVStzbxIRzCcn
OBQtMvJQl1lzcvHkp3Act5GxGFQhzsUOS+k5/KJw9d9hz13qHAbq/geXRNLin0fQ
zQ6is9IETkm3DuUDsGSIlWyA8AeeTAbpm5eqhPG0MHLEvd3F8JA2svWm+5uBbJXU
/Vour2BpLxoh
-----END CERTIFICATE-----
private_key         -----BEGIN RSA PRIVATE KEY-----
MIIEogIBAAKCAQEAm1GaNmENd1Lg2X7kygu+b4FDzfHh5IjwDfcexo/Kcq2FBtnw
qfdcpusxIBtqYa87+kFa+g92FOD/virYrejyqxjb5aJr289LseoI81LjUh10gC3D
MqkuGD4AGPkufyZLcqAjQ/+YZt09hfVyPhGSvxOKgBl86fW0hhDmWoN3iXJuZHjf
CogRKK830z9HyZxU6LMkTm84qpwi9s9p7W6wsvfYYkgQjbZaljV/1LAhqwKFdlv0
I0/mwOaEAShSw5jkgSD44ywQHxv03INgluXoVa5V5iqoQ+0ZyR0OUcDhn9Sh00wA
mqO3RZD1chu0gbDTJ733PfVGR6BkVD3IVX3RGwIDAQABAoIBAEE/ytVFeG7Edolo
cZ0fyeT2HRILp6ZmkNKNGnS4O4hptRTnwd7leBr3ey/N1KctArljc2DR1f13eHjf
bx/AWRHf46FY1o8FSvt3IkN2cuEwVpzynKpPwHmWslvLcukfoVqKQ8ZxPgkYsJvy
PQguQcPb5bdi/cMb4bbgqqUjzbgDnyY2HUxBWKUtWxbXVjjgzlA4q7X6y8Mg7VZD
RJ5RyDazAJAGacu9Y1GX3cu+IPL8KNHNfXn4PJLHsZsjCVgx2oHDZYl8QhRDL9JP
RmEptfalCzbSrCq+q4WvZr7AHk9MzGZYUBhRl8AIYODMI0t69Pyo/EhlkSfrQfou
vb6xp+ECgYEAyjPZzjQmTdUjvUyC68xOaTBTDK49S9X6QdCy8SFxOhF/BRQnX2QU
q+Prd0c3X5xZEyF8QMncaaynq+JmcX1i9HC1dIRaKp9Njb1CKS8EkzoA+DtE8HCf
WbTrNwNMOTvgLr7a8+2V3C2oOLHa8AqrcjqO/zBZIqcr/zUu5Rgm+7cCgYEAxKR3
T5jqETju5KxtUGugy2POx4/FAfA5I7HgkG/XKwzIBC+aCUDF9FTUntp/0Xtnd8fa
doszFW5cod3Gab4qdQhxZmv4NZu5+6hIUFu3nCTC9TGEzWJb795i5QxDN+MDxp+c
ivDN4hIRkRzRL/TciUjYmQyWqEfbXXI3GBPf3b0CgYAj+Y2qzkRyXJV7PH2ejj71
IA/mlal6MJvR0dvEYvrOJga/VJKcuiWjX22pTxZfAGYorTB6Jm/8rSc3wZQQZQqS
Cut9y35vTuBxc1auFRtvRkad442kaaUwRKwEoaWSGUENkA9Mjty2y7i+PYaCZqOS
MzD13hg9vhhp04CAageliQKBgH8V17IOnn+g7uvE8CUAWfv4tiPNK8KT8Sm7hOl0
DnhZG09xQDjorsa4qG09w0usWHNWKz9fuRUJrT5DaT7f0uquGTPbZZ+n1CvrV0jv
7ZsxOvpiFXTzM32zmPB022BcDA2rC+3TsQuBJ+9D04IN2xWyiaFoPFnnjsM21Jpw
Bcd1AoGAcVScQ3cMHZyFqSpmIrcmHsx0YhzlvF15CRVxbTpE9qXtsSiO4Qs8ECF0
XvxDcqW0J94DBLOR2Fwuo5EY3N8qIsah6Tw0B/CK9EULf7Aj26XnDf2+I4+WCcAH
/YWIOgOfFe7nj108H8+3mEPAP48CkcmdmfDkG+PePK0yuMwFdog=
-----END RSA PRIVATE KEY-----
private_key_type    rsa
serial_number       76:9e:94:04:7d:af:6f:7f:8a:67:f9:87:ff:1c:f2:90:c4:dc:5c:b1
```
