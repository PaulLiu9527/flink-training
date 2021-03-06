/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.training.exercises.ridesandfares.scala

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.training.exercises.common.datatypes.{TaxiFare, TaxiRide}
import org.apache.flink.training.exercises.common.sources.{TaxiFareSource, TaxiRideSource}
import org.apache.flink.training.exercises.common.utils.ExerciseBase._
import org.apache.flink.training.exercises.common.utils.{ExerciseBase, MissingSolutionException}
import org.apache.flink.util.Collector

/**
  * The "Stateful Enrichment" exercise of the Flink training in the docs.
  *
  * The goal for this exercise is to enrich TaxiRides with fare information.
  *
  * Parameters:
  * -rides path-to-input-file
  * -fares path-to-input-file
  */
object RidesAndFaresExercise {

  def main(args: Array[String]) {

    // parse parameters
    val params = ParameterTool.fromArgs(args)
    val ridesFile = params.get("rides", ExerciseBase.PATH_TO_RIDE_DATA)
    val faresFile = params.get("fares", ExerciseBase.PATH_TO_FARE_DATA)

    val delay = 60;               // at most 60 seconds of delay
    val servingSpeedFactor = 1800 // 30 minutes worth of events are served every second

    // set up streaming execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(ExerciseBase.parallelism)

    val rides = env
      .addSource(rideSourceOrTest(new TaxiRideSource(ridesFile, delay, servingSpeedFactor)))
      .filter { ride => ride.isStart }
      .keyBy { ride => ride.rideId }

    val fares = env
      .addSource(fareSourceOrTest(new TaxiFareSource(faresFile, delay, servingSpeedFactor)))
      .keyBy { fare => fare.rideId }

    val processed = rides
      .connect(fares)
      .flatMap(new EnrichmentFunction)

    printOrTest(processed)

    env.execute("Join Rides with Fares (scala RichCoFlatMap)")
  }

  class EnrichmentFunction extends RichCoFlatMapFunction[TaxiRide, TaxiFare, (TaxiRide, TaxiFare)] {

    override def flatMap1(ride: TaxiRide, out: Collector[(TaxiRide, TaxiFare)]): Unit = {
      throw new MissingSolutionException()
    }

    override def flatMap2(fare: TaxiFare, out: Collector[(TaxiRide, TaxiFare)]): Unit = {
    }

  }

}
