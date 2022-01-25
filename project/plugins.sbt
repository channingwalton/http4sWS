addSbtPlugin("com.dwijnand"      % "sbt-dynver"      % "3.1.0")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"    % "1.1.3")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"    % "1.5.1")
addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.1")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.3.4")
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo"   % "0.9.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"         % "1.0.0")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.1.0-M9")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.0.0")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.34" // Needed by sbt-git
