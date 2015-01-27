package org.template.classification

import java.io._
import java.util
import java.util.concurrent.ConcurrentHashMap

import org.junit.Assert._

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import org.apache.commons.lang3.SerializationUtils
import org.apache.spark.SparkContext

import scala.collection.mutable

import java.util.LinkedHashMap
import java.util.Collections

//IRIS//

import org.apache.commons.math3.random.MersenneTwister
import org.apache.commons.math3.random.RandomGenerator
import org.deeplearning4j.datasets.iterator.DataSetIterator
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.models.featuredetectors.rbm.RBM
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.`override`.ClassifierOverride
import org.deeplearning4j.nn.layers.factory.PretrainLayerFactory
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.SplitTestAndTrain
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.lossfunctions.LossFunctions

////

import grizzled.slf4j.Logger


case class MyAlgorithmParams(
                              lambda: Double
                              ) extends Params

// extends P2LAlgorithm because the MLlib's NaiveBayesModel doesn't contain RDD.
class MyAlgorithm(val ap: MyAlgorithmParams)
  extends P2LAlgorithm[MyPreparedData, MyModel, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: MyPreparedData): MyModel = {

    //IRIS//
    this.logger.info("TRAIN")
    val conf: MultiLayerConfiguration =
      new NeuralNetConfiguration.Builder().iterations(100)
        .layerFactory(new PretrainLayerFactory(classOf[RBM])).weightInit(WeightInit.SIZE)
        .dist(Nd4j.getDistributions.createNormal(1e-5, 1)).activationFunction("tanh")
        .momentum(0.9).dropOut(0.8)
        .optimizationAlgo(OptimizationAlgorithm.GRADIENT_DESCENT)
        .constrainGradientToUnitNorm(true).k(5).regularization(true)
        .l2(2e-4).visibleUnit(RBM.VisibleUnit.GAUSSIAN)
        .hiddenUnit(RBM.HiddenUnit.RECTIFIED)
        .lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
        .learningRate(1e-1f).nIn(3).nOut(4).list(2)
        .useDropConnect(false)
        .hiddenLayerSizes(3).`override`(new ClassifierOverride(1)).build

    val d: MultiLayerNetwork = new MultiLayerNetwork(conf)
    this.logger.info("MultiLayerNetwork created")

    d.fit(data.dataSet)
    
    val features = Array(0.0,0.0,1.0)
    logger.info("1")
    val pred = d.output(Nd4j.create(features))
    logger.info("2")
    var max = 0.0
    logger.info("3")
    var max_index = 0
    logger.info("4")
    for (i: Int <- 0 to pred.length() - 1) {
      logger.info("5")
      logger.info(i)
      logger.info("6")
      logger.info(pred.data().getDouble(i))
      logger.info("7")
      if (max < pred.data().getDouble(i)) {
        logger.info("MAX")
        max = pred.data().getDouble(i)
        max_index = i
      }
      logger.info("8")
      logger.info(max_index)
    }
    logger.info("MAX_INDEX")
    logger.info(max_index)
    
    /*this.logger.info("MultiLayerNetwork FIT")
    
    val layers = d.getLayers
    val f_layer = layers(1).paramTable()
    val class_layer = f_layer.getClass
    this.logger.info("Layer class")
    this.logger.info(class_layer.toString)
    this.logger.info("Layers size")
    this.logger.info(layers.length.toString)*/

    //TEST
    // (2) write the instance out to a file
    /*val oos = new ObjectOutputStream(new FileOutputStream("/tmp/nflx"))
    oos.writeObject(new MyModel(data.labels,d))
    oos.close()
    this.logger.info("Serialization done")
    this.logger.info("Serialization done2")*/
  /*
    // (3) read the object back in
    val ois = new ObjectInputStream(new FileInputStream("/tmp/nflx"))
    val d2: MyModel = ois.readObject().asInstanceOf[MyModel]
    
    this.logger.info(d2.toString)
    this.logger.info("DeSerialization done")
    //TEST
    
    //TEST2
    val toSer = new MyModel(data.labels,d)
    val original: Serializable  = toSer
    val copy: Serializable = SerializationUtils.clone(original)
    this.logger.info(original.asInstanceOf[MyModel].toString)
    this.logger.info(copy.asInstanceOf[MyModel].toString)
    
    //this.logger.info("TEST 2")
    //
    */
    /*val mp : java.util.HashMap[String, Int] = new java.util.HashMap[String, Int]()
    
    val ar = Array(1.0, 2.0, 3.0)
    val arnd = Nd4j.create(ar)
    //mp.put("XXX", 5)
    
    val params : util.Map[String, Int] = Collections.synchronizedMap[String, Int](mp)
    params.put("XXX", 5)
    val oos = new ObjectOutputStream(new FileOutputStream("/tmp/nflx"))
    oos.writeObject(params)
    oos.close()
    this.logger.info("Serialization done")
    this.logger.info("Serialization done2")

    // (3) read the object back in
    val ois = new ObjectInputStream(new FileInputStream("/tmp/nflx"))
    val d2: util.Map[String, Int] = ois.readObject().asInstanceOf[util.Map[String, Int]]*/

    //new MyModel(data.labels, params)
    new MyModel(data.labels, d) //TODO uncomment

  //w hiddenLayerSizes ma by array ale wtedy wybucha
  ////
  /*
  MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
              .iterations(100).layerFactory(new PretrainLayerFactory(RBM.class))
              .weightInit(WeightInit.SIZE).dist(Distributions.normal(gen,1e-1))
              .activationFunction("tanh").momentum(0.9).dropOut(0.8)
              .optimizationAlgo(OptimizationAlgorithm.GRADIENT_DESCENT)
              .constrainGradientToUnitNorm(true).k(5).regularization(true).l2(2e-4)
              .visibleUnit(RBM.VisibleUnit.GAUSSIAN).hiddenUnit(RBM.HiddenUnit.RECTIFIED)
              .lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
              .rng(gen)
              .learningRate(1e-1f)
              .nIn(4).nOut(3).list(2).useDropConnect(false)
              .hiddenLayerSizes(new int[]{3})
              .override(new ClassifierOverride(1)).build();

   */
  //this.logger.info("\n\n\n DATA\n")
  //for (el <- data.labeledPoints) { this.logger.info(el.toString); this.logger.info('\n'); }
  //this.logger.info("\n\n\nKONIEC DATA\n")

  // MLLib NaiveBayes cannot handle empty training data.
  //require(data.labeledPoints.take(1).nonEmpty,
  //  s"RDD[labeldPoints] in PreparedData cannot be empty." +
  //    " Please check if DataSource generates TrainingData" +
  //    " and Preprator generates PreparedData correctly.")

  //NaiveBayes.train(data.labeledPoints, ap.lambda)
  }

  def predict(model: MyModel, query: Query): PredictedResult = {
    val label = model.predict(query.features)
    new PredictedResult(label)
  }
}

class MyModel(
             val labels: mutable.Map[Int, Double],
             /*val testMap: util.Map[String, Int],*/
             val net: MultiLayerNetwork)  //TODO uncomment
extends Serializable {
  @transient lazy val logger = Logger[this.type]
  
  def predict(features: Array[Double]): Double = {
    Nd4j.create(features)
    val pred = net.output(Nd4j.create(features))
    var max = 0.0
    var max_index = 0
    for (i: Int <- 0 to pred.length() - 1) {
      logger.info(i)
      logger.info(pred.data().getDouble(i))
      if (max < pred.data().getDouble(i)) {
        logger.info("MAX")
        max = pred.data().getDouble(i)
        max_index = i
      }
      logger.info(max_index)
    }
    logger.info("MAX_INDEX")
    logger.info(max_index)
    labels.getOrElse(max_index, 1.0) //TODO uncomment
  }
}