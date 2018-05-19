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
import org.scalatest.{ FreeSpec, MustMatchers }

class DocumentStoreTest extends FreeSpec with MustMatchers {

  val store =
    new DocumentStore[IO](HikariTransactorBuilder(DatabaseConfiguration.load, FlywayDBMigration))

  "CRUD" in {
    val document = Document("123", "a/b", "doc".getBytes, Some("myFile.doc"))
    store.put(document).unsafeRunSync() mustBe document
    store.get(document.id).value.unsafeRunSync() mustBe Some(document)
    store.size.unsafeRunSync() mustBe 1

    val updatedDocument = document.copy(documentData = "updated".getBytes)
    store.update(updatedDocument).unsafeRunSync() mustBe updatedDocument
    store.get(document.id).value.unsafeRunSync() mustBe Some(updatedDocument)
    store.size.unsafeRunSync() mustBe 1

    store.delete(document.id).unsafeRunSync()

    store.get(document.id).value.unsafeRunSync() mustBe None
    store.size.unsafeRunSync() mustBe 0
  }
}
