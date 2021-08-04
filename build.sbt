name := "email-client-test"

version := "0.1"

scalaVersion := "2.12.4"

lazy val `email-integration` = (project in file("."))
  .settings(
    libraryDependencies ++= CompileDeps
  )



val CompileDeps = Seq(
  "com.google.api-client" % "google-api-client" % "1.32.1",
  "com.google.http-client" % "google-http-client" % "1.39.2",
  "com.google.oauth-client" % "google-oauth-client" % "1.31.5",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.31.5",
  "com.google.apis" % "google-api-services-gmail" % "v1-rev110-1.25.0",
  "com.google.http-client" % "google-http-client-jackson2" % "1.39.2",
  "javax.mail" % "javax.mail-api" % "1.6.2",
  "org.jsoup" % "jsoup" % "1.14.1",
  "io.rest-assured" % "rest-assured" % "4.4.0" % Test,
  "io.rest-assured" % "rest-assured-common" % "3.0.0"

)
