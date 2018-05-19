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

import cats.instances.string.catsKernelStdMonoidForString
import cats.effect.IO
import org.http4s.{Method, Request, Response, Uri}
import org.scalatest.{Assertion, EitherValues, FreeSpec, MustMatchers}
import io.circe.syntax._
import io.circe.parser._

class DocumentServiceTest extends FreeSpec with MustMatchers with EitherValues {
  private val store =
    new DocumentStore[IO](HikariTransactorBuilder(DatabaseConfiguration.load, FlywayDBMigration))
  private val service = new DocumentService(store).service

  "CRUD" in {
    assertDocumentSummaries(Nil)

    val document = Document("123", "a/b", "doc".getBytes, Some("myFile.doc"))
    store.put(document).unsafeRunSync()

    assertDocumentSummaries(
      List[DocumentSummary](DocumentSummary("123", "a/b", Some("myFile.doc")))
    )
  }

  private def assertDocumentSummaries(expected: List[DocumentSummary]): Unit =
    service(Request[IO](Method.GET, Uri.unsafeFromString(s"/documents")))
      .map { response ⇒
        val json = getBodyText(response)
        parse(json).right.value mustEqual expected.asJson
      }
      .value
      .unsafeRunSync()

  private def getBodyText(response: Response[IO]): String =
    response.bodyAsText.compile.foldMonoid.unsafeRunSync()
}
