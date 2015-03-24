package org.template.classification

import io.prediction.controller.IEngineFactory
import io.prediction.controller.Engine

class Query(
  val features: Array[Double]
) extends Serializable

class PredictedResult(
  val label: String
) extends Serializable

class ActualResult(
  val label: String
) extends Serializable

object ClassificationEngine extends IEngineFactory {
  def apply() = {
    new Engine(
      classOf[DataSource],
      classOf[MyPreparator],
      Map("my" -> classOf[MyAlgorithm]),
      classOf[Serving])
  }
}
