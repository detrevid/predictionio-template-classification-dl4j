#!/bin/sh

cd $1

if [ ! -f deeplearning4j ]; then
    git clone https://github.com/detrevid/deeplearning4j
fi

cd deeplearning4j
git pull
git checkout e61d34120c9940c4d3abab7fd3021ab912c75a7f
cd ..

if [ ! -f nd4j ]; then
    git clone https://github.com/deeplearning4j/nd4j.git
fi

if [ ! -f Canova ]; then
    git clone https://github.com/deeplearning4j/Canova.git
fi

cd nd4j
git pull
git checkout 4cc1aedc3e41b27a882290bf048e127ae1f9635d
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
cd ..

cd Canova
git pull
git checkout 84f1bfd6fec03c7b9f3f9fee05481c656421c8c2
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
cd ..

cd deeplearning4j
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
