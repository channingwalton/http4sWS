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

import ciris._
import ciris.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string._

final case class DatabaseConfiguration(driver: NonEmptyString,
                                       url: NonEmptyString,
                                       user: NonEmptyString,
                                       password: String,
                                       schema: Option[NonEmptyString],
                                       maxConnectionPoolSize: PosInt)

object DatabaseConfiguration {
  val load: DatabaseConfiguration =
    loadConfig(
      env[Option[NonEmptyString]]("DB_DRIVER"),
      env[Option[NonEmptyString]]("DB_URL"),
      env[Option[NonEmptyString]]("DB_SCHEMA"),
      env[Option[NonEmptyString]]("DB_USER"),
      env[Option[String]]("DB_PASSWORD"),
    ) { (dbDriver, dbUrl, dbSchema, dbUser, dbPwd) =>
      DatabaseConfiguration(
        driver = dbDriver getOrElse "org.h2.Driver",
        url = dbUrl getOrElse "jdbc:h2:mem:http4sws;DB_CLOSE_DELAY=-1;LOCK_MODE=3;MODE=Oracle;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE;TRACE_LEVEL_SYSTEM_OUT=2", // TRACE_LEVEL_SYSTEM_OUT=2
        user = dbUser getOrElse "sa",
        password = dbPwd getOrElse "",
        schema = dbSchema,
        maxConnectionPoolSize = 10
      )
    }.fold(throwConfigErrors, identity)

  def throwConfigErrors(err: ConfigErrors): Nothing =
    throw new RuntimeException(s"Errors loading configuration: \n ${err.messages.mkString("\n")}",
                               err.toException)
}
