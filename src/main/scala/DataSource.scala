package org.template.classification

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.Params
import io.prediction.data.storage.Storage

import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

case class DataSourceParams(
  appId: Int,
  evalK: Option[Int]  // define the k-fold parameter.
) extends Params

class DataSource(val dsp: DataSourceParams)
  extends PDataSource[TrainingData, EmptyEvaluationInfo, Query, ActualResult] {

  @transient lazy val logger = Logger[this.type]

  override
  def readTraining(sc: SparkContext): TrainingData = {
    val eventsDb = Storage.getPEvents()
    val labeledPoints: RDD[LabeledPoint] = eventsDb.aggregateProperties(
      appId = dsp.appId,
      entityType = "user",
      // only keep entities with these required properties defined
      required = Some(List("plan", "attr0", "attr1", "attr2")))(sc)
      // aggregateProperties() returns RDD pair of
      // entity ID and its aggregated properties
      .map { case (entityId, properties) =>
        try {
          LabeledPoint(properties.get[Double]("plan"),
            Vectors.dense(Array(
              properties.get[Double]("attr0"),
              properties.get[Double]("attr1"),
              properties.get[Double]("attr2")
            ))
          )
        } catch {
          case e: Exception =>
            logger.error(s"Failed to get properties $properties of" +
              s" $entityId. Exception: $e.")
            throw e
        }
      }.cache()

    new TrainingData(labeledPoints)
  }

  override
  def readEval(sc: SparkContext)
  : Seq[(TrainingData, EmptyEvaluationInfo, RDD[(Query, ActualResult)])] = {
    require(dsp.evalK.nonEmpty, "DataSourceParams.evalK must not be None")

    // The following code reads the data from data store. It is equivalent to
    // the readTraining method. We copy-and-paste the exact code here for
    // illustration purpose, a recommended approach is to factor out this logic
    // into a helper function and have both readTraining and readEval call the
    // helper.
    val eventsDb = Storage.getPEvents()
    val labeledPoints: RDD[LabeledPoint] = eventsDb.aggregateProperties(
      appId = dsp.appId,
      entityType = "user",
      // only keep entities with these required properties defined
      required = Some(List("plan", "attr0", "attr1", "attr2")))(sc)
      // aggregateProperties() returns RDD pair of
      // entity ID and its aggregated properties
      .map { case (entityId, properties) =>
        try {
          LabeledPoint(properties.get[Double]("plan"),
            Vectors.dense(Array(
              properties.get[Double]("attr0"),
              properties.get[Double]("attr1"),
              properties.get[Double]("attr2")
            ))
          )
        } catch {
          case e: Exception =>
            logger.error(s"Failed to get properties $properties of" +
              s" $entityId. Exception: $e.")
            throw e
        }
      }.cache()
    // End of reading from data store

    // K-fold splitting
    val evalK = dsp.evalK.get
    val indexedPoints: RDD[(LabeledPoint, Long)] = labeledPoints.zipWithIndex()

    (0 until evalK).map { idx => 
      val trainingPoints = indexedPoints.filter(_._2 % evalK != idx).map(_._1)
      val testingPoints = indexedPoints.filter(_._2 % evalK == idx).map(_._1)

      (
        new TrainingData(trainingPoints),
        new EmptyEvaluationInfo(),
        testingPoints.map { 
          p => (new Query(p.features.toArray), new ActualResult(p.label.toString))
        }
      )
    }
  }
}

class TrainingData(
  val labeledPoints: RDD[LabeledPoint]
) extends Serializable
