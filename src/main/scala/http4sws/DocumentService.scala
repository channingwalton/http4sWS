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

import cats.effect.Effect
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers._
import org.http4s.multipart.Multipart
import org.log4s.Logger
import _root_.io.circe.syntax._

/**
  * This service expects POSTS to be multipart form data with the image in a part named 'doc'.
  *
  * You can test this with curl:
  *   curl -X POST -F 'doc=@picture.jpg' -H "Content-Type: image/jpeg" http://127.0.0.1:8080/document/1
  *   curl http://127.0.0.1:8080/document/1 > downloaded.jpg
  */
class DocumentService[F[_]](documentStore: DocumentStore[F])(implicit F: Effect[F])
    extends Http4sDsl[F] {
  @transient protected[this] val logger: Logger = org.log4s.getLogger

  def service: HttpService[F] = HttpService[F] {
    case GET -> Root / "document" / id =>
      handleError(documentStore.get(id).value >>= (_.fold(NotFound())(returnDocument)))

    case req @ POST -> Root / "document" / id =>
      handleError(req.decode[Multipart[F]](multi => storeDocument(multi, id)))

    case GET -> Root / "documents" ⇒
      handleError(
        Ok(documentStore.list.map(_.asJson.spaces2), `Content-Type`(MediaType.`application/json`))
      )
  }

  private def handleError[T](value: F[Response[F]]): F[Response[F]] =
    F.handleErrorWith(value) { t ⇒
      logger.error(t)("Disaster!!!")
      InternalServerError()
    }

  private def storeDocument(multi: Multipart[F], id: String): F[Response[F]] =
    multi.parts.find(_.name.contains("doc")) match {
      case None => BadRequest(s"No document")
      case Some(part) =>
        val content = part.body.compile.toVector.map(_.toArray)
        part.headers.get(`Content-Type`) match {
          case None ⇒ BadRequest(s"No content type")
          case Some(ct) ⇒
            content.flatMap(contents ⇒ store(id, contents, ct, part.filename))
        }
    }

  private def store(id: String,
                    array: Array[Byte],
                    ct: `Content-Type`,
                    fileName: Option[String]): F[Response[F]] =
    documentStore.put(Document(id, ct.toRaw.value, array, fileName)) >> Ok(id)

  private def returnDocument(document: Document): F[Response[F]] =
    `Content-Type`
      .parse(document.mimeType)
      .fold[F[Response[F]]](
        badMimeType(document),
        ct ⇒
          Ok(document.documentData, ct)
            .map(
              _.putHeaders(
                Header("Content-Disposition",
                       s"filename='${document.fileName.getOrElse(document.id)}'")
              )
          )
      )

  private def badMimeType(document: Document)(pf: ParseFailure): F[Response[F]] =
    InternalServerError(s"Mimetype '${document.mimeType}' was not understood: ${pf.details}")
}
