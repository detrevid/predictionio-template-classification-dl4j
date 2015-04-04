# PredictionIO-template-classification-dl4j
## Overview
An classification engine template that uses Deeplearning4j library.

##1. Install and Run PredictionIO
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

##2. Get the Template
```
pio template get detrevid/template-classification-dl4j <YourEngineDir>
cd <YourEngineDir>
```

##3. Set Up Deeplearning4j
The template at this moment is using the current github version of [Deeplearning4j](https://github.com/deeplearning4j/deeplearning4j).

###Method 1: Quick Install
There are 2 scripts provided with the template:

1. Script that downloads current version of DL4j from github into given directory and installs it.

     ```
     .\setup_dl4j.sh <DirectoryForDL4j>
     ```

2. Script that downloads newest version of DL4j, that template was checked to run with properly, from github into given directory and installs it.

     ```
     .\setup_dl4j_stable.sh <DirectoryForDL4j>
     ```

Each time you want update the version of the library you can just run a script again.

###Metdod 2: Manual
First you need to get deeplearning4j from github.

If you want the very newest version you should type in:
```
cd <DirectoryForDL4j>
git clone https://github.com/deeplearning4j/deeplearning4j
```

If you want the newest version, that template was checked to run with properly, you should type in:
```
cd <DirectoryForDL4j>
git clone https://github.com/detrevid/deeplearning4j
```

Then you can install the library:
```
cd <DirectoryForDL4j>/deeplearning4j
./setup.sh
```

Each time you want update version of the library you can just type in:
```
cd <DirectoryForDL4j>/deeplearning4j
git pull
./setup.sh
```

##3. Follow [Quick Start - Classification Engine Template](http://docs.prediction.io/templates/classification/quickstart/) from the 3. subsection.

