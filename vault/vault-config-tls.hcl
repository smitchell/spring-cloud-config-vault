storage "file" {
  path = "./vault-data"
}
listener "tcp" {
  address = "127.0.0.1:8200"
  tls_cert_file = "certs/ca/certs/localhost.cert.pem"
  tls_key_file = "certs/ca/private/localhost.decrypted.key.pem"
}
