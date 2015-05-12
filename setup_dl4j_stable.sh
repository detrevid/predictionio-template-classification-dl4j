#!/bin/sh

cd $1

if [ ! -f deeplearning4j ]; then
    git clone https://github.com/deeplearning4j/deeplearning4j
fi

cd deeplearning4j
git pull
git checkout 876086d08e90fe2efcd284fb8f3bc524a1f9b618
cd ..

if [ ! -f nd4j ]; then
    git clone https://github.com/deeplearning4j/nd4j.git
fi

if [ ! -f Canova ]; then
    git clone https://github.com/deeplearning4j/Canova.git
fi

cd nd4j
git pull
git checkout 2680739a512a034c8df41d06d7fe367fba1af3e5
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
cd ..

cd Canova
git pull
git checkout 7f2aa2944ed1ae6bac91f1a6bbf197803fdd156a
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
cd ..

cd deeplearning4j
mvn clean install -DskipTests -Dmaven.javadoc.skip=true
