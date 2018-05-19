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

import cats.implicits._
import javax.sql.DataSource
import org.flywaydb.core.Flyway

trait DBMigration {
  //returns an error or the number of db.migrations applied
  def migrateDB(ds: DataSource): Either[Throwable, Int]
}

object FlywayDBMigration extends DBMigration {
  def migrateDB(ds: DataSource): Either[Throwable, Int] = Either.catchNonFatal {
    val flyway = new Flyway()
    flyway.setDataSource(ds)
    flyway.setInstalledBy("installed by me")
    flyway.setLocations("/db/migrations")
    flyway.migrate()
  }
}
