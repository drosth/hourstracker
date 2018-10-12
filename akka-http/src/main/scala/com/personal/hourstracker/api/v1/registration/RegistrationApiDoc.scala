package com.personal.hourstracker.api.v1.registration

import akka.http.scaladsl.server.Route
import io.swagger.v3.oas.annotations.Operation
import javax.ws.rs.{ GET, Path, Produces }

@Path("/api/v1/registrations")
@Produces(Array("application/json"))
protected[registration] trait RegistrationApiDoc {

  @GET
  @Operation(summary = "Retrieve all registrations")
  def getRegistrations: Route
}
