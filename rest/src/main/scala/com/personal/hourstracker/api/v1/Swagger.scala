package com.personal.hourstracker.api.v1
import com.personal.hourstracker.SwaggerSupport
import com.personal.hourstracker.api.v1.registration.RegistrationApi
import com.personal.hourstracker.api.Version

class Swagger(url: String, path: String, version: Version)
  extends SwaggerSupport(url, path, apiVersion = version.fullValue, apiClasses = Set(classOf[RegistrationApi]))
