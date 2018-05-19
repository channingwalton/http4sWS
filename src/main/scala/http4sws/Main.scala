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

import cats.effect.IO
import fs2.{ Stream, StreamApp }
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.staticcontent.{ MemoryCache, ResourceService, resourceService }

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {

  val batchJobs: BatchJobs      = new BatchJobs()
  val webSocketService: HttpService[IO] = new Http4sWS(batchJobs).service

  def server: Stream[IO, StreamApp.ExitCode] =
    for {
      exitCode <- BlazeBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .mountService(webSocketService, "/")
        .mountService(staticFiles)
        .serve
    } yield exitCode

  private def staticFiles: HttpService[IO] =
    resourceService(
      ResourceService
        .Config[IO](basePath = "/public", pathPrefix = "/", cacheStrategy = MemoryCache())
    )

  override def stream(args: List[String],
                      requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    server
}
