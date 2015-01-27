package org.template.classification

import java.util

import scala.collection.mutable

import io.prediction.controller.PPreparator

import org.apache.spark.SparkContext
import org.nd4j.linalg.api.complex.{IComplexNDArray, IComplexNumber}
import org.nd4j.linalg.api.ndarray.{SliceOp, INDArray}
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.dataset.DataSet

import grizzled.slf4j.Logger

class MyPreparedData(
                      val dataSet: DataSet,
                      val labels: mutable.Map[Int, Double]
                      ) extends Serializable

class MyPreparator extends PPreparator[TrainingData, MyPreparedData] {

  @transient lazy val logger = Logger[this.type]

  def prepare(sc: SparkContext, trainingData: TrainingData): MyPreparedData = {
    //val dataAm: Long = trainingData.labeledPoints.count()
    this.logger.info("START PREPARE")
    var dFeatures: List[Array[Double]] = List()
    var dLabels: List[Double] = List()
    var labels: mutable.Map[Double, Int] = mutable.Map[Double, Int]()
    var inv_labels: mutable.Map[Int, Double] = mutable.Map[Int, Double]()
    //this.logger.info("A TEST")
    //var a = List[Int]() //TODO delete this
    //for (i <- 3 to 10) {
    //  a = 1 :: a
    //}
    //this.logger.info(a.length.toString)
    //this.logger.info("A TEST END")
    for (lb <- trainingData.labeledPoints.collect()) {
      //this.logger.info(lb.features.toArray.mkString(", "))
      dFeatures = lb.features.toArray :: dFeatures
      dLabels = lb.label :: dLabels
      //this.logger.info(dFeatures.head.mkString(", "))
      //this.logger.info(dFeatures.length.toString)
      //this.logger.info("SEX")
    }
    //this.logger.info("SIZE")
    //this.logger.info(dFeatures.length.toString)
    //this.logger.info(dFeatures.foldLeft("")((s, x) => s + x.mkString(", ") + "\n"))
    //this.logger.info("AFTER FIRST LOOP")
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
    //this.logger.info("AFTER SECOND LOOP")
    dLabelsPrep = dLabelsPrep.reverse
    val aDFeatures = dFeatures.toArray
    val aDLabels = dLabelsPrep.map(x => x.toArray).toArray
    //val asdasd = Nd4j.create(Array(Array(1.0,3.0),Array(1.0,2.0)))
    //this.logger.info("aDFeatures")
    //this.logger.info(aDFeatures.foldLeft("")((s,x) => s + x.mkString(", ") + "\n"))
    //this.logger.info("AFTER Last VALS")
    val dsFeatures = Nd4j.create(aDFeatures)
    //this.logger.info("AFTER dsFeatures")
    val dsLabels = Nd4j.create(aDLabels)
    //this.logger.info("AFTER dsFeatures")
    new MyPreparedData(new DataSet(dsFeatures, dsLabels), inv_labels)
  }
}