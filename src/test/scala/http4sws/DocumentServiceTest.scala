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

import cats.data.OptionT
import cats.instances.string.catsKernelStdMonoidForString
import cats.effect.IO
import cats.instances.list._
import cats.syntax.traverse._
import org.http4s.{Method, Request, Response, Uri}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import io.circe.syntax._
import io.circe.parser._
import org.http4s.headers.`Content-Type`

class DocumentServiceTest extends FreeSpec with MustMatchers with EitherValues {
  private val store =
    new DocumentStore[IO](HikariTransactorBuilder(DatabaseConfiguration.load, FlywayDBMigration))
  private val service = new DocumentService(store).service

  "CRUD" in {
    assertDocumentSummaries(Nil)

    val documents = List(Document("123", "a/b", "doc".getBytes, Some("myFile.doc")),
                         Document("321", "c/d", "another doc".getBytes, Some("important.doc")))
    val summaries = documents.map(_.summary)

    documents.map(store.put).sequence.unsafeRunSync()

    assertDocumentSummaries(summaries)

    documents.map { doc ⇒
      mkGet(s"/document/${doc.id}")
        .map { response ⇒
          assertContentType(response, doc.mimeType)
          getBodyText(response) mustEqual new String(doc.documentData)
        }
        .value
        .unsafeRunSync()
    }
  }

  private def assertContentType(response: Response[IO], mimeType: String): Unit =
    response.headers.get(`Content-Type`).map(_.mediaType.renderString) mustBe Some(
      mimeType
    )

  private def assertDocumentSummaries(expected: List[DocumentSummary]): Unit =
    mkGet("/documents")
      .map { response ⇒
        val json = getBodyText(response)
        parse(json).right.value mustEqual expected.asJson
      }
      .value
      .unsafeRunSync()

  private def mkGet(path: String): OptionT[IO, Response[IO]] =
    service(Request[IO](Method.GET, Uri.unsafeFromString(path)))

  private def getBodyText(response: Response[IO]): String =
    response.bodyAsText.compile.foldMonoid.unsafeRunSync()
}
