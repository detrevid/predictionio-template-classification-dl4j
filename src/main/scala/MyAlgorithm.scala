package org.template.classification

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params

import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.deeplearning4j.models.featuredetectors.rbm.RBM
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.`override`.ClassifierOverride
import org.deeplearning4j.nn.layers.factory.PretrainLayerFactory
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.lossfunctions.LossFunctions

import scala.collection.mutable

import java.io._

case class MyAlgorithmParams(
                              lambda: Double
                              ) extends Params

class MyAlgorithm(val ap: MyAlgorithmParams)
  extends P2LAlgorithm[MyPreparedData, MyModel, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: MyPreparedData): MyModel = {

    val conf: MultiLayerConfiguration =
      new NeuralNetConfiguration.Builder().iterations(10)
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

    d.fit(data.dataSet)

    new MyModel(data.labels, d)
  }

  def predict(model: MyModel, query: Query): PredictedResult = {
    val label : String = model.predict(query.features)
    new PredictedResult(label)
  }
}

class MyModel(
             val labels: mutable.Map[Int, String],
             val net: MultiLayerNetwork)
extends Serializable {
  @transient lazy val logger = Logger[this.type]
  
  def predict(features: Array[Double]): String = {
    val pred = net.predict(Nd4j.create(features))(0)
    labels.getOrElse(pred, "") //ToDO make it better
  }
}