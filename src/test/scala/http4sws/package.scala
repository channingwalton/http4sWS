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

import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

import scala.util.Random

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

package object http4sws {
  @specialized def discard[A](evaluateForSideEffectOnly: A): Unit =
    () //Return unit to prevent warning due to discarding value

  def testConfig: DatabaseConfiguration = {
    val name = Random.alphanumeric.take(5).mkString
    println(name)
    DatabaseConfiguration(
      driver = NonEmptyString("org.h2.Driver"),
      url = NonEmptyString.unsafeFrom(s"jdbc:h2:mem:${name};DB_CLOSE_DELAY=-1"), // TRACE_LEVEL_SYSTEM_OUT=2
      user = NonEmptyString("sa"),
      password = "",
      schema = None,
      maxConnectionPoolSize = PosInt(2)
    )
  }
}
