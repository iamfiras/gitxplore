name := "gitxplore"

version := "1.0-SNAPSHOT"

play.Project.playScalaSettings

play.Keys.templatesTypes ++= Map("stream" -> "utils.HtmlStreamFormat")

play.Keys.templatesImport ++= Vector("_root_.utils.HtmlStream", "_root_.utils.HtmlStream._")

libraryDependencies ++= Seq()

// enable improved (experimental) incremental compilation algorithm called "name hashing"
incOptions := incOptions.value.withNameHashing(true)