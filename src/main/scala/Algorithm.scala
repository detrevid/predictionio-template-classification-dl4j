package detrevid.predictionio.template.classification

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params

import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.`override`.ClassifierOverride
import org.deeplearning4j.nn.conf.distribution.UniformDistribution
import org.deeplearning4j.nn.conf.layers.RBM
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.api.IterationListener
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.lossfunctions.LossFunctions

import scala.collection.JavaConverters._

case class AlgorithmParams(
  iterations: Int = 10,
  layers: Int = 2,
  hiddenLayersSizes: Seq[Int] = List(3),
  momentum: Double = 0.9,
  dropOut: Double = 0.8,
  learningRate: Double = 0.3
) extends Params

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): Model = {

    Nd4j.MAX_SLICES_TO_PRINT = -1
    Nd4j.MAX_ELEMENTS_PER_SLICE = -1
    val conf: MultiLayerConfiguration =
      new NeuralNetConfiguration.Builder().iterations(ap.iterations)
        .layer(new RBM).weightInit(WeightInit.DISTRIBUTION)
        .dist(new UniformDistribution(0, 1)).activationFunction("tanh")
        .momentum(ap.momentum).dropOut(ap.dropOut)
        .optimizationAlgo(OptimizationAlgorithm.LBFGS)
        .constrainGradientToUnitNorm(true).k(1).regularization(true)
        .l2(2e-4).visibleUnit(RBM.VisibleUnit.GAUSSIAN)
        .hiddenUnit(RBM.HiddenUnit.RECTIFIED)
        .lossFunction(LossFunctions.LossFunction.RMSE_XENT)
        .learningRate(ap.learningRate)
        .nIn(3).nOut(data.labels.length).list(ap.layers)
        .hiddenLayerSizes(ap.hiddenLayersSizes: _*).`override`(new ClassifierOverride(1)).build

    val net: MultiLayerNetwork = new MultiLayerNetwork(conf)
    net.init()
    net.setListeners(List[IterationListener](new ScoreIterationListener(2)).asJava)

    net.fit(data.dataSet)

    new Model(data.labels, net)
  }

  def predict(model: Model, query: Query): PredictedResult = {
    val label : String = model.predict(query.features)
    new PredictedResult(label)
  }
}

class Model(
    val labels: Array[String],
    val net: MultiLayerNetwork)
  extends Serializable {

  @transient lazy val logger = Logger[this.type]

  def predict(features: Array[Double]): String = {
    val features_array = Nd4j.create(features)
    val pred = net.predict(features_array)(0)
    labels(pred)
  }
}