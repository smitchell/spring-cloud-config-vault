#!/bin/bash
export VAULT_TOKEN=00000000-0000-0000-0000-000000000000
export VAULT_ADDR=https://localhost:8200
export VAULT_SKIP_VERIFY=false
export VAULT_CAPATH=${PWD}/work/ca/certs/ca.cert.pem
echo BE SURE TO EDIT WITH YOUR VAULT KEYS
vault operator unseal SUibnGmk9UtB8a98DHqBbWulj+4Phs0jVa42h/H6zSwY
vault operator unseal 07PbsKZLVzPNAUj2V69JFPc0t6ehWXL65NI5/9I6nqwn
vault operator unseal zcBc/8tfl2YjVANUsoB7ZGF7rEzusnlb0w9AuuVConjq
