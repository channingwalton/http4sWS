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

import java.sql.SQLException

import cats.effect._
import cats.implicits._

trait StoreUtils {

  def lift[F[_], A, B](term: F[Either[SQLException, A]], onOk: A ⇒ B)(implicit F: Effect[F]): F[B] =
    term.flatMap {
      case Left(err) ⇒ F.raiseError(err)
      case Right(ok) ⇒ F.pure(onOk(ok))
    }

  def liftId[F[_], A](term: F[Either[SQLException, A]])(implicit F: Effect[F]): F[A] =
    lift(term, identity[A])

  def liftReturning[F[_], A, B](term: F[Either[SQLException, A]],
                                ok: B)(implicit F: Effect[F]): F[B] =
    lift(term, (_: A) ⇒ ok)

  def liftToUnit[F[_], A](term: F[Either[SQLException, A]])(implicit F: Effect[F]): F[Unit] =
    liftReturning(term, ())
}
