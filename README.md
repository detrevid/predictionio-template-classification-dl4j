# PredictionIO-template-classification-dl4j
## Overview
A classification engine template that uses Deeplearning4j library.

The template was based on [template-scala-parallel-classification](http://templates.prediction.io/PredictionIO/template-scala-parallel-classification).

##1. Install and run PredictionIO
First you need to [install PredictionIO 0.9.1](http://docs.prediction.io/install/) (if you haven't done it).
Let's say you have installed PredictionIO at /home/yourname/PredictionIO/. For convenience, add PredictionIO's binary command path to your PATH, i.e. /home/yourname/PredictionIO/bin

```
PATH=$PATH:/home/yourname/PredictionIO/bin; export PATH
```

Once you have completed the installation process, please make sure all the components (PredictionIO Event Server, Elasticsearch, and HBase) are up and running.

```
pio-start-all
```
For versions before 0.9.1, you need to individually get the PredictionIO Event Server, Elasticsearch, and HBase up and running.

You can check the status by running:

```
pio status
```

##2. Get the template

```
pio template get detrevid/template-classification-dl4j <YourEngineDir>
cd <YourEngineDir>
```

##3. Set up deeplearning4j
The template is using the current github version of [Deeplearning4j](https://github.com/deeplearning4j/deeplearning4j).

###Method 1: Quick Install
There are 2 scripts provided with the template:

1. Script that downloads the current version of DL4j from github into given directory and installs it.

     ```
     .\setup_dl4j.sh <DirectoryForDL4j>
     ```

2. Script that downloads and installs the newest version of DL4j that template was checked to run properly with.

     ```
     .\setup_dl4j_stable.sh <DirectoryForDL4j>
     ```

Each time you want to update the version of the library, you can just run the chosen script again.

###Method 2: Manual
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

##4. Next steps
Follow [Quick Start - Classification Engine Template](http://docs.prediction.io/templates/classification/quickstart/) starting from the 3. subsection.

##5. Learn more
You can learn more about Deeplearning4j studying [examples](https://github.com/deeplearning4j/dl4j-0.0.3.3-examples).