import sbt._

object MyScalafmtPlugin extends AutoPlugin {
  override def trigger  = allRequirements
  override def requires = plugins.JvmPlugin
  override def buildSettings: Seq[Def.Setting[_]] =
    SettingKey[Unit]("scalafmtGenerateConfig") :=
      IO.write(
        // writes to file once when build is loaded
        file(".scalafmt.conf"),
        """
style = defaultWithAlign

align.openParenCallSite = false
align.openParenDefnSite = false
align.tokens = [{code = "->"}, {code = "<-"}, {code = "=>", owner = "Case"}]
continuationIndent.callSite = 2
continuationIndent.defnSite = 2
danglingParentheses = true
indentOperator = spray
maxColumn = 140
newlines.alwaysBeforeTopLevelStatements = true
project.excludeFilters = [".*\\.sbt", ".*\\.html"]
rewrite.rules = [RedundantParens, SortImports]
spaces.inImportCurlyBraces = false
unindentTopLevelOperators = true
        """.stripMargin.getBytes("UTF-8")
      )
}
