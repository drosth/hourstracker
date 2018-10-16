package com.personal.hourstracker.api.v1.consolidatedregistration

import akka.http.scaladsl.server.Route
import com.personal.hourstracker.api.v1.consolidatedregistration.ConsolidatedRegistrationApi.ConsolidatedRegistrationModel
import io.swagger.v3.oas.annotations.{ Operation, Parameter }
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{ Content, Schema }
import io.swagger.v3.oas.annotations.responses.ApiResponse
import javax.ws.rs.{ GET, Path }

@Path("/api/v1/registrations/consolidated")
protected[consolidatedregistration] trait ConsolidatedRegistrationApiDoc {

  @GET
  @Operation(
    summary = "Retrieve consolidated registrations between given start and end dates",
    description = "Retrieves consolidated registrations",

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
        description = "The consolidated registrations",
        content = Array(
          new Content(mediaType = "application/json", schema = new Schema(implementation = classOf[List[ConsolidatedRegistrationModel]])))),
      new ApiResponse(responseCode = "404", description = "No consolidated registrations found in range")))
  def getConsolidatedRegistrations: Route
}
