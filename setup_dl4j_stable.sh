#!/bin/sh

cd $1

if [ ! -f deeplearning4j ]; then
    git clone https://github.com/detrevid/deeplearning4j
fi

cd deeplearning4j
git pull
LAST_COMMIT=echo git log -1 --format="%cd"
cd ..

if [ ! -f nd4j ]; then
    git clone https://github.com/deeplearning4j/nd4j.git
fi

if [ ! -f Canova ]; then
    git clone https://github.com/deeplearning4j/Canova.git
fi

cd nd4j
git pull
git checkout `git rev-list -n 1 --before=${LAST_COMMIT} master`
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
cd ..

cd Canova
git pull
git checkout `git rev-list -n 1 --before=${LAST_COMMIT} master`
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
cd ..

cd deeplearning4j
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
