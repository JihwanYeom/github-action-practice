#!/bin/bash
set -e

cd /home/ubuntu/github-action-practice

echo "Start deployment"
git pull origin main

echo "Gradle Build (skipping tests)"
chmod +x ./gradlew
./gradlew build -x test

# --- ✨ 여기가 수정된 부분 ✨ ---
echo "--- Terminating existing process if any ---"
# pkill: pgrep(찾기)과 kill(죽이기)을 한 번에 수행합니다.
# || true: 종료할 프로세스가 없어도 오류로 간주하지 않습니다.
sudo pkill -f "java -jar.*github-action-practice.*\.jar" || true
echo "Waiting 5 seconds for shutdown..."
sleep 5
# --- ✨ 수정 끝 ✨ ---

echo "Deploy new application"

JAR_FILE=$(find build/libs -name 'github-action-practice-*.jar' ! -name '*-plain.jar' -print -quit)

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: JAR file not found."
    ls -l build/libs/
    exit 1
fi

echo "Found JAR file to deploy: $JAR_FILE"

LOG_PATH="/home/ubuntu/github-action-practice/logs/server_log_$(date +%Y-%m-%d).txt"

mkdir -p $(dirname "$LOG_PATH")

nohup java -jar --spring.profiles.active=dev "$JAR_FILE" >> "$LOG_PATH" 2>&1 &

echo "Deployment complete"
