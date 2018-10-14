package com.personal.hourstracker.api.v1.registration

import akka.http.scaladsl.server.Route
import com.personal.hourstracker.api.v1.registration.RegistrationApi.RegistrationModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.{GET, Path, Produces}

@Path("/registrations")
@Produces(Array("application/json"))
protected[registration] trait RegistrationApiDoc {

  @GET
  @Operation(
    summary = "Retrieve all registrations",
    description = "Retrieves all known registrations",
    responses = Array(
      new ApiResponse(
        description = "The registrations",
        content = Array(
          new Content(mediaType = "application/json", schema = new Schema(implementation = classOf[List[RegistrationModel]])))),
      new ApiResponse(responseCode = "404", description = "Registrations not found")))
  def getRegistrations: Route
}
