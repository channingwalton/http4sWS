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

import java.time.LocalDateTime
import java.util.concurrent.ScheduledThreadPoolExecutor

import fs2._
import cats.effect._
import fs2.async.mutable.Topic
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.server.websocket._
import org.http4s.websocket.WebsocketBits._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Http4sWS extends StreamApp[IO] {

  // a topic that will be used to push updates to all clients about important matters
  private val topic: Topic[IO, String] =
    async.topic[IO, String]("").unsafeRunSync() // unsafe is naughty

  private val scheduler: Scheduler =
    fs2.Scheduler.fromScheduledExecutorService(new ScheduledThreadPoolExecutor(1))

  // push the time to the topic using a scheduled publish
  val drip: fs2.Stream[IO, Unit] =
    scheduler
      .awakeEvery[IO](1.second)
      .map(_ ⇒ LocalDateTime.now().toString)
      .to(topic.publish)

  // run the drip to start publishing
  drip.compile.drain.unsafeRunAsync(println(_))


  private def subscribeClientToDrip: Stream[IO, WebSocketFrame] =
    topic.subscribe(10).map(Text(_))

  def clientStream: Stream[IO, WebSocketFrame] =
    // Just say Hello the client but here you can do more complex things
    Stream.eval(IO.pure(Text("Hello")))

  // the http4s service
  def service: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "ws" ⇒
      val toClient: Stream[IO, WebSocketFrame] =
      clientStream ++ subscribeClientToDrip // client specific stream merged with updates from the topic

      val fromClient: Sink[IO, WebSocketFrame] = _.map(msg ⇒ println(s"Received: $msg"))

      WebSocketBuilder[IO].build(toClient, fromClient)
  }

  def server: Stream[IO, StreamApp.ExitCode] =
    for {
      exitCode <- BlazeBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .mountService(service, "/")
        .serve
    } yield exitCode

  override def stream(args: List[String],
                      requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    server
}
