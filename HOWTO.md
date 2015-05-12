# Integrating [Deeplearning4j](https://github.com/deeplearning4j/deeplearning4j) with PredictionIO

##1. Getting DL4j

###Using the current maven repository version of [Deeplearning4j](http://mvnrepository.com/artifact/org.deeplearning4j/).
You can go to the next step.

###Using the current github version of [Deeplearning4j](https://github.com/deeplearning4j/deeplearning4j).

####Method 1: Quick Install
There are 2 scripts provided with the template:

1. Script that downloads the current version of DL4j from github into given directory and installs it.

     ```
     .\setup_dl4j.sh <DirectoryForDL4j>
     ```

2. Script that downloads and installs the newest version of DL4j that this template was checked to run properly with.

     ```
     .\setup_dl4j_stable.sh <DirectoryForDL4j>
     ```

Each time you want to update the version of the library, you can just run the chosen script again.

####Method 2: Manual
First you need to get deeplearning4j from github:

```
cd <DirectoryForDL4j>
git clone https://github.com/deeplearning4j/deeplearning4j
```

Then you can install the library:

```
cd <DirectoryForDL4j>/deeplearning4j
./setup.sh
```

Each time you want to update version of the library, you can just type in:

```
cd <DirectoryForDL4j>/deeplearning4j
git pull
./setup.sh
```

Since you are using this method, you are installing the library in your local maven repository, so you need to add the following line to your build.sbt file:

```
resolvers += Resolver.mavenLocal
```

##2. Setting up DL4j
It may be helpful if you use [build.sbt](build.sbt) file as a reference.

You need to add the right version of dl4j (it will depend on the decision you made in the 1. step of the guide) to libraryDependencies in your build.sbt file.

##3. Customize your engine
As far as setting up dl4j is concerned, you are good to go. Now, you can customize your engine using algorithms from the library.

You can learn how to do this studying the code of this template and official [dl4j examples](https://github.com/deeplearning4j/dl4j-0.0.3.3-examples).
