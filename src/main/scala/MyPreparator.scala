package org.template.classification

import io.prediction.controller.PPreparator

import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.dataset.DataSet

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.util.Sorting.stableSort

class MyPreparedData(
                      val dataSet: DataSet,
                      val labels: Array[String]
                      ) extends Serializable

class MyPreparator extends PPreparator[TrainingData, MyPreparedData] {

  @transient lazy val logger = Logger[this.type]

  def prepare(sc: SparkContext, trainingData: TrainingData): MyPreparedData = {
    this.logger.info("START PREPARE")
    var dFeatures: List[Array[Double]] = List()
    var dLabels: List[String] = List()
    var labels_names: List[String] = List()

    for (lb <- trainingData.labeledPoints.collect().sortWith(_.label.toString < _.label.toString)) {
      dFeatures = lb.features.toArray :: dFeatures
      dLabels = lb.label.toString :: dLabels
    }

    val labelsAm = dLabels.distinct.length
    var dLabelsPrep: List[List[Double]] = List()
    var biggestL: Int = 0
    var last: String = ""
    for (lb <- dLabels) {
      var labPrep: List[Double] = List.fill(labelsAm)(0.0)
      if (lb == last) {
        labels_names = lb :: labels_names
        last = lb
        biggestL += 1
      }
      labPrep = labPrep.updated(biggestL, 1.0)
      dLabelsPrep = labPrep :: dLabelsPrep
    }
    labels_names = labels_names.reverse
    dLabelsPrep = dLabelsPrep.reverse
    val dsFeatures = Nd4j.create(dFeatures.toArray)
    val dsLabels = Nd4j.create(dLabelsPrep.map(x => x.toArray).toArray)
    val dataSet = new DataSet(dsFeatures, dsLabels)
    new MyPreparedData(new DataSet(dsFeatures, dsLabels), labels_names.toArray)
  }
}