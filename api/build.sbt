organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"

val switchToServer: State => State =
  (state: State) =>
    state.copy(remainingCommands = Exec("project server", None) +: state.remainingCommands)

lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val parser = ZetaBuild.parser
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server

// TODO  remove this in the future
lazy val images = project
