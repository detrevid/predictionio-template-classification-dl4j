import AssemblyKeys._

assemblySettings

name := "predictionio-template-classification-dl4j"

organization := "io.prediction"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "io.prediction"             %% "core"                 % pioVersion.value            % "provided",
  "org.apache.spark"          %% "spark-core"           % "1.2.0"                     % "provided",
  "org.apache.spark"          %% "spark-mllib"          % "1.2.0"                     % "provided",
  "org.deeplearning4j"         % "deeplearning4j-core"  % "0.0.3.3.3.alpha1-SNAPSHOT",
  "org.nd4j"                   % "nd4j-jblas"           % "0.0.3.5.5.3-SNAPSHOT"
)

excludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  val excluded = Seq(
    "akka-actor_2.10-2.3.4.jar",
    "akka-remote_2.10-2.3.4.jar"
  )
  cp filter {jar => excluded.contains(jar.data.getName)}
}

mergeStrategy in assembly := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    xs map { _.toLowerCase } match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case PathList(_*) => MergeStrategy.first
}