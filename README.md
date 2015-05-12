# PredictionIO-template-classification-dl4j
## Overview
A classification engine template that uses Deeplearning4j library.

The template was based on [template-scala-parallel-classification](http://templates.prediction.io/PredictionIO/template-scala-parallel-classification).

##1. Install and run PredictionIO
First you need to [install PredictionIO 0.9.2](http://docs.prediction.io/install/) (if you haven't done it).
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
Since the newest release the template uses dl4j from official maven repository, so there is no need for manually setting up the library.

Be aware that it may change in the future and the template may go back to using latest github version of dl4j.

If you want to use latest version of dl4j anyway, you can fallow the instructions here: [HOWTO](HOWTO.md).

##4. Next steps
Follow [Quick Start - Classification Engine Template](http://docs.prediction.io/templates/classification/quickstart/) starting from the 3. subsection.

##5. Learn more
You can learn more about Deeplearning4j studying [examples](https://github.com/deeplearning4j/dl4j-0.0.3.3-examples).
