import sbt._

object Commands {
  val additionalCommands: Seq[Command] = Seq(withCoverage, prepare, prepareCommit, prepareCommitWithIT)

  def withCoverage: Command = Command.command("with-coverage") { state =>
    "Test / compile" ::
    "coverage" ::
    "test" ::
    "coverageReport" ::
    state
  }

  def prepare: Command = Command.command("prepare") { state =>
    "scalafmtAll" ::
    "integration-test / scalafmtAll" ::
    "scalafmtSbt" ::
    "testQuick" ::
    state
  }

  def prepareCommit: Command = Command.command("prepare-commit") { state =>
    "scalafmtAll" ::
    "integration-test / scalafmtAll" ::
    "scalafmtSbt" ::
    "Test / compile" ::
    "coverage" ::
    "test" ::
    "coverageOff" ::
    "coverageReport" ::
    state
  }

  def prepareCommitWithIT: Command = Command.command("prepare-commit-with-IT") { state =>
    "scalafmtAll" ::
    "integration-test / scalafmtAll" ::
    "scalafmtSbt" ::
    "Test / compile" ::
//    "application / Docker / publishLocal" ::
    "coverage" ::
    "test" ::
    "integration-test / test" ::
    "coverageOff" ::
    "coverageReport" ::
    state
  }
}
