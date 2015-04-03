package org.template.classification

import io.prediction.controller.PPreparator

import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.dataset.DataSet

class PreparedData(
                      val dataSet: DataSet,
                      val labels: Array[String]
                      ) extends Serializable

class Preparator extends PPreparator[TrainingData, PreparedData] {

  @transient lazy val logger = Logger[this.type]

  def prepare(sc: SparkContext, trainingData: TrainingData): PreparedData = {
    this.logger.info("START PREPARE")
    var dFeatures: List[Array[Double]] = List()
    var dLabels: List[String] = List()
    var labelsNames: List[String] = List()

    for (lb <- trainingData.labeledPoints.collect().sortWith(_.label.toString < _.label.toString)) {
      dFeatures = lb.features.toArray :: dFeatures
      dLabels = lb.label.toString :: dLabels
    }

    val labelsAm = dLabels.distinct.length
    var dLabelsPrep: List[Array[Double]] = List()
    var biggestL: Int = -1
    var last: String = ""
    for (lb <- dLabels) {
      if (lb != last) {
        labelsNames ::= lb
        last = lb
        biggestL += 1
      }
      dLabelsPrep ::= Array.fill(labelsAm)(0.0).updated(biggestL, 1.0)
    }

    val dsFeatures = Nd4j.create(dFeatures.reverse.toArray)
    val dsLabels = Nd4j.create(dLabelsPrep.toArray)
    new PreparedData(new DataSet(dsFeatures, dsLabels), labelsNames.toArray)
  }
}