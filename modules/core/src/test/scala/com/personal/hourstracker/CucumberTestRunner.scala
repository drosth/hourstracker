package com.personal.hourstracker

import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("../features", "./src/test/features"),
  glue = Array("com.personal.hourstracker.stepdefinitions"),
  //  tags = Array("@wip"),
  plugin = Array("pretty", "html:target/cucumber", "json:target/cucumber/test-report.json", "junit:target/cucumber/test-report.xml")
)
class CucumberTestRunner {}
