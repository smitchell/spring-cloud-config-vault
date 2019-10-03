#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

###########################################################################
# Vault environment settings. Source this file.                           #
###########################################################################

export VAULT_ADDR=https://localhost:8200
export VAULT_SKIP_VERIFY=false
export VAULT_CAPATH=${PWD}/certs/ca/certs/ca.cert.pem
