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

case class Document(id: String,
                    mimeType: String,
                    documentData: Array[Byte],
                    fileName: Option[String]) {

  override def equals(other: Any): Boolean = other match {
    case that: Document => id.equals(that.id)
    case _              => false
  }

  override def hashCode: Int = id.hashCode

  def summary: DocumentSummary = DocumentSummary(id, mimeType, fileName)
}
