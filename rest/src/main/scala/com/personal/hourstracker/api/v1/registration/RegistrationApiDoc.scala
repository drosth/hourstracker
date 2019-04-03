package com.personal.hourstracker.api.v1.registration

import akka.http.scaladsl.server.Route
import com.personal.hourstracker.api.v1.domain.RegistrationModel.RegistrationModels
import io.swagger.v3.oas.annotations.{ Operation, Parameter }
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{ Content, Schema }
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.{ GET, Path, Produces }

@Path("/api/v1/registration")
@Produces(Array("application/json"))
protected[registration] trait RegistrationApiDoc {

  @GET
  @Operation(
    summary = "Retrieve all registrations",
    description = "Retrieves all known registrations",
    parameters = Array(
      new Parameter(
        in = ParameterIn.QUERY,
        name = "startAt",
        required = false,
        schema = new Schema(implementation = classOf[String]),
        example = "2018-09-01"),
      new Parameter(
        in = ParameterIn.QUERY,
        name = "endAt",
        required = false,
        schema = new Schema(implementation = classOf[String]),
        example = "2018-09-30")),
    responses = Array(
      new ApiResponse(
        description = "The registrations",
        content = Array(new Content(mediaType = "application/json", schema = new Schema(implementation = classOf[RegistrationModels])))),
      new ApiResponse(responseCode = "404", description = "Registrations not found")))
  def getRegistrations: Route

  @GET
  @Path("/import")
  @Operation(
    summary = "Import registrations",
    description = "Import and store registrations",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Registrations are imported"),
      new ApiResponse(responseCode = "404", description = "Could not import registrations")))
  def importRegistrations: Route
}
