#!/bin/bash
for i in $(seq 1 100); do
  curl -s -o /dev/null -X POST http://localhost:8082/auth/register \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"loadtest-user-${i}@example.com\",\"password\":\"Passw0rd123\"}"
done
echo "Registered 100 test users"
