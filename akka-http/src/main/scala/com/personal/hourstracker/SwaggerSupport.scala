package com.personal.hourstracker

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.swagger.v3.oas.models.ExternalDocumentation

abstract class SwaggerSupport(url: String, path: String, apiVersion: String, override val apiClasses: Set[Class[_]])
    extends SwaggerHttpService {

  override val host: String = url

  override val info = Info(version = apiVersion, description = "Some description", title = "Hourstracker API")

  //  override val unwantedDefinitions =
  //    Seq("Function1RequestContextFutureRouteResult", "Function1")

  override val externalDocs = Some(
    new ExternalDocumentation()
      .description("Core Docs")
      .url("http://acme.com/docs"))
}
