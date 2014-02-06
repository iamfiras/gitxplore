name := "gitxplore"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

// enable improved (experimental) incremental compilation algorithm called "name hashing"
incOptions := incOptions.value.withNameHashing(true)
