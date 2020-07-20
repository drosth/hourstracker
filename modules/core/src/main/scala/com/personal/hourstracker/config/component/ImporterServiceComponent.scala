package com.personal.hourstracker.config.component

trait ImporterServiceComponent {
  def importerService: ImporterService
}

trait CSVImporterServiceComponent extends ImporterServiceComponent {
  this: SystemComponent =>

  override lazy val importerService: ImporterService = new CSVImportService()
}
