import AssemblyKeys._

assemblySettings

name := "predictionio-template-classification-dl4j"

libraryDependencies ++= Seq(
  "io.prediction"             %% "core"                 % pioVersion.value            % "provided",
  "org.apache.spark"          %% "spark-core"           % "1.3.0"                     % "provided",
  "org.apache.spark"          %% "spark-mllib"          % "1.3.0"                     % "provided",
  "org.deeplearning4j"         % "deeplearning4j-core"  % "0.0.3.3.3.alpha1",
  "org.nd4j"                   % "nd4j-jblas"           % "0.0.3.5.5.3"
)
