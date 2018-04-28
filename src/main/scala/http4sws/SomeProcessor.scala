/*
 * Copyright 2018 channing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package http4sws

import java.util.concurrent.ScheduledThreadPoolExecutor
import fs2._
import cats.effect.IO
import fs2.async.mutable.Topic

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

// something that does heavy work and tells subscribers about it
class SomeProcessor {

  private val scheduler: Scheduler =
    fs2.Scheduler.fromScheduledExecutorService(new ScheduledThreadPoolExecutor(1))

  // a topic that will be used to push updates to all clients about important matters
  val topic: Topic[IO, String] =
    async.topic[IO, String]("").unsafeRunSync() // unsafe is naughty

  // schedule the batch periodically
  scheduler
    .awakeEvery[IO](3.second)
    .map(_ ⇒ runImportantBatch())
    .compile
    .drain
    .unsafeRunAsync(println(_))

  // and we will also tell the client something about our important work
  def runImportantBatch(): Unit = {
    //... startup stuff
    //val data = extractData(args)
    sendMessage("Data extracted")
    //val calcResult = runCalculation(data)
    sendMessage("Calculations complete")
    //... more stuff
  }

  // publish a message to the topic
  def sendMessage(msg: String): Unit =
    Stream
      .emit(msg)
      .covary[IO]
      .to(topic.publish)
      .compile
      .drain
      .unsafeRunAsync(_.left.foreach(println(_)))
}
