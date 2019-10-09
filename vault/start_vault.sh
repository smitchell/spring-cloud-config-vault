#!/bin/bash
export VAULT_TOKEN=00000000-0000-0000-0000-000000000000
export VAULT_ADDR=https://localhost:8200
export VAULT_SKIP_VERIFY=false
export VAULT_CAPATH=${PWD}/work/ca/certs/ca.cert.pem
vault server -config ./vault-config.hcl

