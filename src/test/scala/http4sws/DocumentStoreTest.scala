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
import doobie._
import doobie.implicits._
import org.scalatest.{ FreeSpec, MustMatchers }

class DocumentStoreTest extends FreeSpec with MustMatchers {

  private val transactor =
    HikariTransactorBuilder[IO](DatabaseConfiguration.load, FlywayDBMigration)
  "CRUD" in {
    val document = Document("123", "a/b", "doc".getBytes, Some("myFile.doc"))
    DocumentStore.put(document).transact(transactor).unsafeRunSync() mustBe document
    DocumentStore.get(document.id).value.transact(transactor).unsafeRunSync() mustBe Some(document)
    DocumentStore.size.transact(transactor).unsafeRunSync() mustBe 1

    val updatedDocument = document.copy(documentData = "updated".getBytes)
    DocumentStore
      .update(updatedDocument)
      .transact(transactor)
      .unsafeRunSync() mustBe updatedDocument
    DocumentStore.get(document.id).value.transact(transactor).unsafeRunSync() mustBe Some(
      updatedDocument
    )
    DocumentStore.size.transact(transactor).unsafeRunSync() mustBe 1

    DocumentStore.delete(document.id).transact(transactor).unsafeRunSync()

    DocumentStore.get(document.id).value.transact(transactor).unsafeRunSync() mustBe None
    DocumentStore.size.transact(transactor).unsafeRunSync() mustBe 0
  }

  // To see a transaction around both inserts, turn on H2's trace level logging
  // by adding ;TRACE_LEVEL_SYSTEM_OUT=2 to the H2 JDBC URL
  // You should something like this:
  //  2018-08-13 13:59:54 jdbc[3]:
  //  /*SQL l:79 #:1*/INSERT INTO DOCUMENT (id, mimetype, documentData, fileName) VALUES (?, ?, ?, ?) {1: '123', 2: 'a/b', 3: X'646f63', 4: 'myFile.doc'};
  //  2018-08-13 13:59:54 jdbc[3]:
  //  /*SQL l:79 #:1*/INSERT INTO DOCUMENT (id, mimetype, documentData, fileName) VALUES (?, ?, ?, ?) {1: '345', 2: 'a/b', 3: X'646f632032', 4: 'anoterFile.doc'};
  //  2018-08-13 13:59:54 jdbc[3]:
  //  /*SQL */COMMIT;
  "Contrived transaction example" in {
    val document1 = Document("123", "a/b", "doc".getBytes, Some("myFile.doc"))
    val document2 = Document("345", "a/b", "doc 2".getBytes, Some("anoterFile.doc"))

    val putBoth: ConnectionIO[Unit] = for {
      _ <- DocumentStore.put(document1)
      _ <- DocumentStore.put(document2)
    } yield ()

    putBoth.transact(transactor).unsafeRunSync()

    DocumentStore.get(document1.id).value.transact(transactor).unsafeRunSync() mustBe Some(
      document1
    )
    DocumentStore.get(document2.id).value.transact(transactor).unsafeRunSync() mustBe Some(
      document2
    )
  }
}
