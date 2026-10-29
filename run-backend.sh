#!/usr/bin/env bash
set -e

# Change directory to backend folder where pom.xml and mvnw reside
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="${ROOT_DIR}/back_resources/backend"

if [ ! -d "${BACKEND_DIR}" ]; then
  echo "Error: Backend directory not found at ${BACKEND_DIR}"
  exit 1
fi

cd "${BACKEND_DIR}"

echo "Starting Halo Kazi Spring Boot backend from ${BACKEND_DIR}..."
./mvnw spring-boot:run "$@"
