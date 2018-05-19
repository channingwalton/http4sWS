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

import doobie.implicits._
import doobie.util.query

object DocumentDao {

  def size: query.Query0[Long] =
    sql"SELECT COUNT(*) FROM DOCUMENT".query[Long]

  def list: query.Query0[DocumentSummary] =
    sql"SELECT id, mimetype, fileName FROM DOCUMENT".query[DocumentSummary]

  def get(id: String): query.Query0[Document] =
    sql"SELECT id, mimetype, documentData, fileName FROM DOCUMENT WHERE id=$id".query[Document]

  def create(document: Document): doobie.util.update.Update0 = {
    import document._
    sql"INSERT INTO DOCUMENT (id, mimetype, documentData, fileName) VALUES ($id, $mimeType, $documentData, $fileName)".update
  }

  def update(document: Document): doobie.util.update.Update0 =
    sql"UPDATE DOCUMENT SET mimetype=${document.mimeType}, documentData=${document.documentData}, fileName=${document.fileName} where id=${document.id}".update

  def delete(id: String): doobie.util.update.Update0 =
    sql"DELETE FROM DOCUMENT where id=$id".update

  def clear(): doobie.util.update.Update0 =
    sql"DELETE FROM DOCUMENT".update

}
