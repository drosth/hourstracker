package com.personal.hourstracker.rest.api.v1.registration

@Path("/api/v1")
@Produces(Array("application/json"))
protected[registration] trait RegistrationApiDoc {

  @GET
  @Path("/registrations/{year}/{month}")
  @Operation(
    summary = "Retrieve registrations",
    parameters = Array(
      new Parameter(
        in = ParameterIn.PATH,
        name = "year",
        required = false,
        schema = new Schema(implementation = classOf[String]),
        example = "2019"
      ),
      new Parameter(
        in = ParameterIn.PATH,
        name = "month",
        required = false,
        schema = new Schema(implementation = classOf[String]),
        example = "1"
      )
    ),
    responses = Array(
      new ApiResponse(
        description = "The registrations",
        content = Array(new Content(mediaType = "application/json", schema = new Schema(implementation = classOf[Seq[RegistrationModel]])))
      ),
      new ApiResponse(responseCode = "404", description = "Registrations not found")
    )
  )
  @Tags(Array(new Tag(name = "Registration")))
  def getRegistrations: Route

  @GET
  @Path("/registrations/{year}/{month}/consolidated")
  @Operation(
    summary = "Retrieve consolidated registrations",
    parameters = Array(
      new Parameter(
        in = ParameterIn.PATH,
        name = "year",
        required = true,
        schema = new Schema(implementation = classOf[String]),
        example = "2019"
      ),
      new Parameter(
        in = ParameterIn.PATH,
        name = "month",
        required = false,
        schema = new Schema(implementation = classOf[String]),
        example = "1"
      )
    ),
    responses = Array(
      new ApiResponse(description = "The filenames of the generated, consolidated registrations"),
      new ApiResponse(responseCode = "404", description = "Registrations not found")
    )
  )
  @Tags(Array(new Tag(name = "Registration")))
  def getConsolidatedRegistrations: Route

  @GET
  @Path("/registrations/import")
  @Operation(
    summary = "Import and store registrations",
    responses = Array(
      new ApiResponse(responseCode = "202", description = "Registrations will be imported"),
      new ApiResponse(responseCode = "404", description = "Could not import registrations")
    )
  )
  @Tags(Array(new Tag(name = "Registration")))
  def importRegistrationsFromSource: Route

  @POST
  @Path("/registrations/upload")
  @Operation(summary = "Upload registrations")
  @Tags(Array(new Tag(name = "Registration")))
  def uploadRegistrations: Route
}
