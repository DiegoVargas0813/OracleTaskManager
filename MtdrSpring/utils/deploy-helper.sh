#!/bin/bash
set -e

# === CONFIGURATION ===
ARTIFACT_REPO_ID="ocid1.artifactrepository.oc1.mx-queretaro-1.0.amaaaaaalhbismya2mterbhffb35jmk42cieijgykbos4hv5eiunhswoej5q"
ARTIFACT_VERSION="v1-hellodevops"
RUN_NAME="hellodevops"
NAMESPACE="mtdrworkshop"

# === Ensure directories exist ===
mkdir -p MtdrSpring/wallet
mkdir -p MtdrSpring/tls

# === Download artifacts ===
declare -A ARTIFACT_PATHS=(
    [MtdrSpring/tls/tls.crt]="tls/tls.crt"
    [MtdrSpring/tls/tls.key]="tls/tls.key"
    [MtdrSpring/wallet/cwallet.sso]="wallet/cwallet.sso"
    [MtdrSpring/wallet/ewallet.p12]="wallet/ewallet.p12"
    [MtdrSpring/wallet/ewallet.pem]="wallet/ewallet.pem"
    [MtdrSpring/wallet/keystore.jks]="wallet/keystore.jks"
    [MtdrSpring/wallet/ojdbc.properties]="wallet/ojdbc.properties"
    [MtdrSpring/wallet/sqlnet.ora]="wallet/sqlnet.ora"
    [MtdrSpring/wallet/tnsnames.ora]="wallet/tnsnames.ora"
    [MtdrSpring/wallet/truststore.jks]="wallet/truststore.jks"
    [MtdrSpring/wallet/wallet.zip]="wallet/wallet.zip"
)

for local_path in "${!ARTIFACT_PATHS[@]}"; do
    artifact_path="artifacts/${RUN_NAME}/$(basename "${ARTIFACT_PATHS[$local_path]}")"
    mkdir -p "$(dirname "$local_path")"
    echo "Downloading $artifact_path to $local_path"
    oci artifacts generic-artifact download-by-path \
        --repository-id "$ARTIFACT_REPO_ID" \
        --artifact-path "$artifact_path" \
        --artifact-version "$ARTIFACT_VERSION" \
        --file "$local_path"
done

# === Create/update Kubernetes secrets ===
kubectl create secret generic db-wallet-secret \
  --from-file=cwallet.sso=MtdrSpring/wallet/cwallet.sso \
  --from-file=ewallet.p12=MtdrSpring/wallet/ewallet.p12 \
  --from-file=ewallet.pem=MtdrSpring/wallet/ewallet.pem \
  --from-file=keystore.jks=MtdrSpring/wallet/keystore.jks \
  --from-file=ojdbc.properties=MtdrSpring/wallet/ojdbc.properties \
  --from-file=sqlnet.ora=MtdrSpring/wallet/sqlnet.ora \
  --from-file=tnsnames.ora=MtdrSpring/wallet/tnsnames.ora \
  --from-file=truststore.jks=MtdrSpring/wallet/truststore.jks \
  --from-file=wallet.zip=MtdrSpring/wallet/wallet.zip \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic tls-secret \
  --from-file=tls.crt=MtdrSpring/tls/tls.crt \
  --from-file=tls.key=MtdrSpring/tls/tls.key \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -