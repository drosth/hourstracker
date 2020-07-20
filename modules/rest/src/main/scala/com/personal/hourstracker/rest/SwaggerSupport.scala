package com.personal.hourstracker.rest

abstract class SwaggerSupport(url: String, path: String, apiVersion: String, override val apiClasses: Set[Class[_]])
  extends SwaggerHttpService {

  //  def apiClasses: Set[Class[_]]
  override val host: String = url
  override val apiDocsPath = "/api-docs"
  override val info = Info(version = apiVersion, description = "API Definition", title = "Hourstracker API")
  override val components: Option[Components] = None
  override val schemes = List("http", "https")
  //  def security: List[SecurityRequirement] = List()
  //  def securitySchemes: Map[String, SecurityScheme] = Map.empty
  override val externalDocs: Option[ExternalDocumentation] = None
  override val vendorExtensions: Map[String, Object] = Map.empty
  override val unwantedDefinitions = Seq("Function1RequestContextFutureRouteResult", "Function1")

  /*
  override val securitySchemeDefinitions = Map(
    "bearer" -> new ApiKeyAuthDefinition("Authorization", In.HEADER)
  )
   */

  private lazy val swaggerJson: String = super.generateSwaggerJson
  private lazy val swaggerYaml: String = super.generateSwaggerYaml

  override def generateSwaggerJson: String = swaggerJson

  override def generateSwaggerYaml: String = swaggerYaml

}
