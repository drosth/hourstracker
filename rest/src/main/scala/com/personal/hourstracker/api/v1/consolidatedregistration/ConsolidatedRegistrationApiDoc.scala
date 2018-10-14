package com.personal.hourstracker.api.v1.consolidatedregistration

import akka.http.scaladsl.server.Route
import com.personal.hourstracker.api.v1.consolidatedregistration.ConsolidatedRegistrationApi.ConsolidatedRegistrationModel
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.{GET, Path}

protected[consolidatedregistration] trait ConsolidatedRegistrationApiDoc {

  @GET
  @Path("registrations/consolidated")
  @Operation(
    summary = "Retrieve consolidated registrations between given start and end dates",
    description = "Retrieves consolidated registrations",

    parameters = Array(
      new Parameter(
        in = ParameterIn.QUERY,
        name = "startAt",
        required = true,
        schema = new Schema(implementation = classOf[String]),
        example = "01-09-2018"),
      new Parameter(
        in = ParameterIn.QUERY,
        name = "endAt",
        required = false,
        schema = new Schema(implementation = classOf[String]),
        example = "30-09-2018")),

    responses = Array(
      new ApiResponse(
        description = "The consolidated registrations",
        content = Array(
          new Content(mediaType = "application/json", schema = new Schema(implementation = classOf[List[ConsolidatedRegistrationModel]])))),
      new ApiResponse(responseCode = "404", description = "No consolidated registrations found in range")))
  def getConsolidatedRegistrations: Route
}
