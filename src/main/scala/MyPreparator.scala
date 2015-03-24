package org.template.classification

import scala.collection.mutable

import io.prediction.controller.PPreparator

import org.apache.spark.SparkContext
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.dataset.DataSet

import grizzled.slf4j.Logger

class MyPreparedData(
                      val dataSet: DataSet,
                      val labels: mutable.Map[Int, String]
                      ) extends Serializable

class MyPreparator extends PPreparator[TrainingData, MyPreparedData] {

  @transient lazy val logger = Logger[this.type]

  def prepare(sc: SparkContext, trainingData: TrainingData): MyPreparedData = {
    this.logger.info("START PREPARE")
    var dFeatures: List[Array[Double]] = List()
    var dLabels: List[String] = List()
    val labels = mutable.Map[String, Int]()
    val inv_labels = mutable.Map[Int, String]()

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
        biggestL += 1
      }
      labPrep = labPrep.updated(labels(lb), 1.0)
      dLabelsPrep = labPrep :: dLabelsPrep
    }
    dLabelsPrep = dLabelsPrep.reverse
    val aDFeatures = dFeatures.toArray
    val aDLabels = dLabelsPrep.map(x => x.toArray).toArray
    val dsFeatures = Nd4j.create(aDFeatures)
    val dsLabels = Nd4j.create(aDLabels)
    new MyPreparedData(new DataSet(dsFeatures, dsLabels), inv_labels)
  }
}