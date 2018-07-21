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
import cats.effect._
import doobie.implicits._
import doobie.util.transactor.Transactor
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
class DocumentStore[F[_]](transactor: Transactor[F])(implicit F: Effect[F]) extends StoreUtils {
  @transient protected[this] val logger: Logger = org.log4s.getLogger

  def size: F[Long] =
    liftId(DocumentDao.size.unique.attemptSql.transact(transactor))

  def get(id: String): OptionT[F, Document] =
    OptionT(DocumentDao.get(id).option.transact(transactor))

  def list: F[List[DocumentSummary]] =
    DocumentDao.list.to[List].transact(transactor)

  def put(document: Document): F[Document] =
    liftReturning(DocumentDao.create(document).run.attemptSql.transact(transactor), document)

  def update(document: Document): F[Document] =
    liftReturning(DocumentDao.update(document).run.attemptSql.transact(transactor), document)

  def delete(id: String): F[Unit] =
    liftToUnit(DocumentDao.delete(id).run.attemptSql.transact(transactor))

  def clear(): F[Unit] =
    liftToUnit(DocumentDao.clear().run.attemptSql.transact(transactor))

}
