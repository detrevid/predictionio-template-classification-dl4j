package org.template.classification

import io.prediction.controller.PPreparator

import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.dataset.DataSet

import scala.collection.mutable
import scala.collection.JavaConverters._

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
    val labels = mutable.Map[String, Int]()
    val inv_labels = mutable.Map[Int, String]()
    var labels_names: List[String] = List()

    for (lb <- trainingData.labeledPoints.collect()) {
      dFeatures = lb.features.toArray :: dFeatures
      dLabels = lb.label.toString :: dLabels
    }

    val labelsAm = dLabels.distinct.length
    var dLabelsPrep: List[List[Double]] = List()
    var biggestL: Int = 0
    for (lb <- dLabels) {
      var labPrep: List[Double] = List.fill(labelsAm)(0.0)
      if (!(labels contains lb)) {
        labels.update(lb, biggestL)
        labPrep = labPrep.updated(labels(lb), 1.0)
        inv_labels.update(biggestL, lb)
        labels_names = lb :: labels_names
        biggestL += 1
      }
      labPrep = labPrep.updated(labels(lb), 1.0)
      dLabelsPrep = labPrep :: dLabelsPrep
    }
    labels_names = labels_names.reverse
    dLabelsPrep = dLabelsPrep.reverse
    val aDFeatures = dFeatures.toArray
    val aDLabels = dLabelsPrep.map(x => x.toArray).toArray
    val dsFeatures = Nd4j.create(aDFeatures)
    val dsLabels = Nd4j.create(aDLabels)
    val dataSet = new DataSet(dsFeatures, dsLabels)
    dataSet.setLabelNames(labels_names.asJava)
    new MyPreparedData(new DataSet(dsFeatures, dsLabels), labels_names.toArray)
  }
}