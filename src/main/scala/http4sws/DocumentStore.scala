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

import cats.data._
import doobie._
import org.log4s.Logger

/**
  * A document store.
  *
  * Be careful with transactionality here. A transaction is committed after .transact is called.
  * So if you want to perform a number of operations in a single transaction, you will need to
  * work with ConnectionIO and transact at the end.
  *
  * Note also, that if you call attemptSql, the transaction will commit even if the contained
  * Either is a left. You are expected to rollback yourself. If you don't want to manage that
  * yourself then don't call attemptSql, the resulting SQLException will be thrown after a]
  * rollback.
  */
object DocumentStore {
  @transient protected[this] val logger: Logger = org.log4s.getLogger

  def size: ConnectionIO[Long] =
    DocumentDao.size.unique

  def get(id: String): OptionT[ConnectionIO, Document] =
    OptionT(DocumentDao.get(id).option)

  def list: ConnectionIO[List[DocumentSummary]] =
    DocumentDao.list.to[List]

  def put(document: Document): ConnectionIO[Document] =
    DocumentDao.create(document).run.map(_ ⇒ document)

  def update(document: Document): ConnectionIO[Document] =
    DocumentDao.update(document).run.map(_ ⇒ document)

  def delete(id: String): ConnectionIO[Unit] =
    DocumentDao.delete(id).run.map(_ ⇒ ())

  def clear(): ConnectionIO[Unit] =
    DocumentDao.clear().run.map(_ ⇒ ())

}
