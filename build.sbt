
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
      val http4s = "0.18.21"
      val circe = "0.9.3"
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
    scalaVersion := "2.12.6",
    organization := "default",
    organizationName := "channing",
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ypartial-unification",
      "-Ywarn-unused-import"
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )
