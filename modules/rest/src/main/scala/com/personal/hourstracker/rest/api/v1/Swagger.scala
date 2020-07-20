package com.personal.hourstracker.rest.api.v1

class Swagger(url: String, path: String, version: Version)
  extends SwaggerSupport(url, path, apiVersion = version.toString, apiClasses = Set(classOf[RegistrationApi]))
