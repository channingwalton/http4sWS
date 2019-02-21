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
import org.scalatest.FreeSpec
import doobie.scalatest._

class DocumentDaoTest extends FreeSpec with IOChecker {

  // note parameters to the methods are not used by the tests, they just need to type-check
  "size" in check(DocumentDao.size)
  "list" in check(DocumentDao.list)
  "get" in check(DocumentDao.get("1"))
  "create" in check(DocumentDao.create(Document("1", "A", "1234".getBytes(), None)))
  "update" in check(DocumentDao.update(Document("2", "B", "1234".getBytes(), None)))
  "delete" in check(DocumentDao.delete("1"))
  "clear" in check(DocumentDao.clear())

  override def transactor: doobie.Transactor[IO] =
    HikariTransactorBuilder(testConfig, FlywayDBMigration)
}
