package com.personal.hourstracker.config.component

import com.personal.hourstracker.importer.service.CSVImportService
import com.personal.hourstracker.service.ImporterService

trait ImporterServiceComponent {
  def importerService: ImporterService
}

trait CSVImporterServiceComponent extends ImporterServiceComponent {
  this: SystemComponent =>

  override lazy val importerService: ImporterService = new CSVImportService()
}
