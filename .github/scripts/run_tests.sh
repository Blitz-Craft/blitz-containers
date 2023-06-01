#!/bin/bash

changed_folders=$(git diff-tree --name-only --diff-filter=AM --no-commit-id -r HEAD | grep '/' | cut -d/ -f1 | uniq)

echo "Running test for ${changed_folders}"
for folder in $changed_folders; do
  if [ -f "${folder}/build.gradle.kts" ]; then
    echo "########"
    echo "# Running 'gradle clean check' in $folder"
    echo "########"
    ./gradlew :${folder}:clean :${folder}:check
    exit_status=$?
    echo
    if [ "$exit_status" -ne 0 ]; then
      echo "'gradle check' failed in $folder with exit status $exit_status"
      exit "$exit_status"
    fi
  else
    echo "########"
    echo "# Skipping $folder: build.gradle.kts not found"
    echo "########"
    echo
  fi
done