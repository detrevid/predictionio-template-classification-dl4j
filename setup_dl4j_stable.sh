#!/bin/sh
cd $1

if [ ! -f deeplearning4j ]; then
    git clone https://github.com/detrevid/deeplearning4j
fi

cd deeplearning4j
git pull

./setup.sh
