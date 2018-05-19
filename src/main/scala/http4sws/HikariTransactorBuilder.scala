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

import cats._
import cats.effect._
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import org.log4s.Logger

object HikariTransactorBuilder {

  @transient protected[this] val logger: Logger = org.log4s.getLogger

  def apply[F[_]: Monad](configuration: DatabaseConfiguration,
                         dbMigration: DBMigration)(implicit F: Effect[F]): HikariTransactor[F] = {
    val ds = createDataSource(configuration)

    val transactor = HikariTransactor[F](ds)

    logger.info("Starting Flyway db.migrations...")
    dbMigration
      .migrateDB(ds)
      .fold(
        err ⇒ throwMigrationErrrors(err),
        n ⇒ logger.info(s"Successfully applied $n db.migrations to the database")
      )

    transactor
  }

  private def throwMigrationErrrors(err: Throwable): Nothing = {
    logger.error(err)("Failed to apply database db.migrations. Application will terminate.")
    throw new RuntimeException(err)
  }

  private def createDataSource(configuration: DatabaseConfiguration): HikariDataSource = {
    val ds = new HikariDataSource()

    ds.setJdbcUrl(configuration.url.toString)
    ds.setDriverClassName(configuration.driver.value)
    ds.setUsername(configuration.user.value)
    ds.setPassword(configuration.password)

    ds.setMaximumPoolSize(configuration.maxConnectionPoolSize.value)
    ds.setInitializationFailTimeout(30000L) // scalastyle:ignore
    ds.setMinimumIdle(1)
    ds.setAutoCommit(false)

    ds
  }

}
