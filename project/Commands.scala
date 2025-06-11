import sbt.*

object Commands {
  val additionalCommands: Seq[Command] = Seq(
    aggregateCoverage,
    formatAll,
    formatCheckAll,
    prepare,
    prepareCommit,
    reportCoverage,
    withCoverage
  )

  def aggregateCoverage: Command = Command.command("aggregate-coverage") { state =>
    "with-coverage" ::
    "coverageAggregate" ::
    state
  }

  def formatAll: Command = Command.command("format-all") { state =>
    "scalafmtAll" ::
    "scalafmtSbt" ::
    state
  }

  def formatCheckAll: Command = Command.command("format-check-all") { state =>
    "scalafmtCheckAll" ::
    "scalafmtSbtCheck" ::
    state
  }

  def prepare: Command = Command.command("prepare") { state =>
    "format-all" ::
    "testQuick" ::
    state
  }

  def prepareCommit: Command = Command.command("prepare-commit") { state =>
    "format-all" ::
    "Test / compile" ::
    "coverage" ::
    "test" ::
    "coverageOff" ::
    "coverageReport" ::
    state
  }

  def reportCoverage: Command = Command.command("report-coverage") { state =>
    "with-coverage" ::
    "coverageReport" ::
    state
  }

  def withCoverage: Command = Command.command("with-coverage") { state =>
    "coverage" ::
    "test" ::
    "coverageOff" ::
    state
  }
}
