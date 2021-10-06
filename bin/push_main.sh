#!/bin/bash

echo -e "\033[32mCheckout to v1-main\033[0m"
git checkout v1-main

echo -e "\033[32mMerge v1-dev branch\033[0m"
git merge v1-dev -m 'Prepare release'

echo -e "\033[32mPush to origin v1-main\033[0m"
git push origin v1-main

