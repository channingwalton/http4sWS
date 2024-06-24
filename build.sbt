
// *****************************************************************************
// Projects
// *****************************************************************************

lazy val http4sws =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.http4s,
	      library.http4sServer,
        library.doobieCore,
        library.doobieH2,
        library.doobieHikari,
        library.doobieRefined,
        library.refined,
        library.refinedCats,
        library.flyway,
        library.cirisCore,
        library.cirisCats,
        library.cirisCatsEffect,
        library.cirisGeneric,
        library.cirisRefined,
        library.logback,
        library.log4s,
        library.circeCore,
        library.circeGenericExtras,
        library.circeParser,
        library.scalaTest,
        library.doobieScalaTest,
        library.jaxb
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val http4s = "0.18.9"
      val circe = "0.14.3"
      val doobie = "0.5.2"
      val refined = "0.9.0"
      val flyway = "5.0.7"
      val ciris = "0.9.2"
      val logback = "1.2.3"
      val log4s = "1.6.1"
      val scalaTest = "3.0.5"
      val jaxbapi = "2.2.11"
    }
    val http4s                    = "org.http4s"              %% "http4s-dsl"           % Version.http4s
    val http4sServer              = "org.http4s"              %% "http4s-blaze-server"  % Version.http4s
    val doobieCore                = "org.tpolecat"            %% "doobie-core"          % Version.doobie
    val doobieH2                  = "org.tpolecat"            %% "doobie-h2"            % Version.doobie
    val doobieHikari              = "org.tpolecat"            %% "doobie-hikari"        % Version.doobie
    val doobieRefined             = "org.tpolecat"            %% "doobie-refined"       % Version.doobie
    val doobieScalaTest           = "org.tpolecat"            %% "doobie-scalatest"     % Version.doobie
    val refined                   = "eu.timepit"              %% "refined"              % Version.refined
    val refinedCats               = "eu.timepit"              %% "refined-cats"         % Version.refined
    val flyway                    = "org.flywaydb"             % "flyway-core"          % Version.flyway
    val cirisCore                 = "is.cir"                  %% "ciris-core"           % Version.ciris
    val cirisCats                 = "is.cir"                  %% "ciris-cats"           % Version.ciris
    val cirisCatsEffect           = "is.cir"                  %% "ciris-cats-effect"    % Version.ciris
    val cirisGeneric              = "is.cir"                  %% "ciris-generic"        % Version.ciris
    val cirisRefined              = "is.cir"                  %% "ciris-refined"        % Version.ciris
    val logback                   = "ch.qos.logback"           % "logback-classic"      % Version.logback
    val circeCore                 = "io.circe"                %% "circe-core"           % Version.circe
    val circeGenericExtras        = "io.circe"                %% "circe-generic-extras" % Version.circe
    val circeParser               = "io.circe"                %% "circe-parser"         % Version.circe
    val log4s                     = "org.log4s"               %% "log4s"                % Version.log4s
    val jaxb                      = "javax.xml.bind"           % "jaxb-api"             % Version.jaxbapi
    val scalaTest                 = "org.scalatest"           %% "scalatest"            % Version.scalaTest % Test
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    scalaVersion := "2.12.8",
    organization := "default",
    organizationName := "channing",
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-language:postfixOps", // Allow post fix ops
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xfuture", // Turn on future language features.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
      "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      "-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Xlint:unsound-match", // Pattern match may not be typesafe.
      "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      "-Ypartial-unification", // Enable partial unification in type constructor inference
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      "-Ywarn-unused-import", // Warn if an import selector is not referenced (Scala pre 2.12).
      "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", // Warn if a local definition is unused.
      "-Ywarn-unused:params", // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", // Warn if a private member is unused.
      "-Ybackend-parallelism","8", // Scala 2.12.5 compiler flag to run some compilation tasks in parallel
      "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
    ),
    scalacOptions in (Test) --= Seq("-Ywarn-unused:params"),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )

lazy val buildInfoSettings =
  Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "info",
    buildInfoKeys ++= Seq[BuildInfoKey](
      BuildInfoKey.action("commit") {
        git.gitHeadCommit.value
      }
    ),
    buildInfoOptions += BuildInfoOption.ToMap,
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoOptions += BuildInfoOption.BuildTime,
  )