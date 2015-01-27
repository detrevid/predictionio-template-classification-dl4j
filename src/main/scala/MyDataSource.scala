package org.template.classification

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EmptyActualResult
import io.prediction.controller.Params
import io.prediction.data.storage.Event
import io.prediction.data.storage.Storage

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

import grizzled.slf4j.Logger

case class MyDataSourceParams(appId: Int) extends Params

class MyDataSource(val dsp: MyDataSourceParams)
  extends PDataSource[MyTrainingData,
    EmptyEvaluationInfo, Query, EmptyActualResult] {

  @transient lazy val logger = Logger[this.type]

  override
  def readTraining(sc: SparkContext): MyTrainingData = {
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
        case e: Exception => {
          logger.error(s"Failed to get properties ${properties} of" +
            s" ${entityId}. Exception: ${e}.")
          throw e
        }
      }
    }.cache()
    new MyTrainingData(labeledPoints)
  }
}

class MyTrainingData(
                    val labeledPoints: RDD[LabeledPoint]
                    ) extends Serializable
