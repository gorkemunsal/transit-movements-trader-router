import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val catsVersion = "2.1.1"

  val compile = Seq(

    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.4.0",
    "org.typelevel"           %% "cats-core"                % catsVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.4.0" % Test classifier "tests",
    "org.scalatest"           %% "scalatest"                % "3.0.8"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.2"                 % "test, it",
    "org.mockito"             %  "mockito-all"               % "1.10.19" % "test, it",
    "org.scalacheck"          %% "scalacheck"               % "1.14.0" % "test, it",
    "com.github.tomakehurst"  %  "wiremock-standalone"       % "2.25.0" % "test, it",
    "org.typelevel"          %% "cats-laws"                 % catsVersion

  )

}
