package com.personal.hourstracker.service.impl

import java.util.Locale

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class DefaultRegistrationServiceSpec extends FixtureAnyFlatSpec with BeforeAndAfter with should.Matchers {

  private implicit val logger: Logger = mock(classOf[Logger])
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val executionContext: ExecutionContext = system.dispatcher
  //  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit lazy val locale: Locale = new Locale("nl", "NL")

  private val importerService = mock(classOf[ImporterService])
  private val registrationRepository = mock(classOf[RegistrationRepository])
  private val facturationService: FacturationService = mock(classOf[FacturationService])
  private val consolidatedRegistrationService: ConsolidatedRegistrationService = mock(classOf[ConsolidatedRegistrationService])

  override type FixtureParam = RegistrationService

  override protected def withFixture(test: OneArgTest): Outcome = {
    reset(logger, importerService, registrationRepository, facturationService, consolidatedRegistrationService)
    test(new DefaultRegistrationService(registrationRepository, importerService, facturationService, consolidatedRegistrationService))
  }

  behavior of "Importing registrations"

  it should "return registrations" in { classUnderTest =>
    val registration = mock(classOf[Registration])
    val expectedRegistrations = List(registration)

    val fileName = "someFile"
    when(importerService.importRegistrationsFrom(fileName)).thenReturn(Future.successful(Right(expectedRegistrations)))

    val actual = Await.result(classUnderTest.importRegistrationsFrom(fileName), 1 second)
    actual shouldEqual Right(expectedRegistrations.size)
  }

  it should "return failure, given file could not be read" in { classUnderTest =>
    val fileName = "unreadableFile"
    when(importerService.importRegistrationsFrom(fileName)).thenReturn(Future.successful(Left(s"'$fileName' is unreadable")))

    val actual = Await.result(classUnderTest.importRegistrationsFrom(fileName), 1 second)
    actual shouldEqual Left("Could not import registrations")

    verify(logger).warn(s"Could not import from 'unreadableFile': ''unreadableFile' is unreadable'")
  }

  behavior of "Fetching registrations"

  it should "return all registrations" in { classUnderTest =>
    val registration = mock(classOf[Registration])
    when(registrationRepository.findAll()).thenReturn(Source.fromIterator(() => List(registration).iterator))

    classUnderTest
      .fetchRegistrations()
      .runWith(TestSink.probe[Registration])
      .request(1)
      .expectNext(registration)
      .expectComplete()
  }

  it should "return registrations selected by year" in { classUnderTest =>
    val request: RegistrationRequest = SelectByYear(2000)

    val registration = mock(classOf[Registration])
    when(registrationRepository.findByRequest(request)).thenReturn(Source.fromIterator(() => List(registration).iterator))

    classUnderTest
      .fetchRegistrations(request)
      .runWith(TestSink.probe[Registration])
      .request(1)
      .expectNext(registration)
      .expectComplete()
  }

  it should "return registrations selected by year and month" in { classUnderTest =>
    val request: RegistrationRequest = SelectByYearAndMonth(2000, 1)

    val registration = mock(classOf[Registration])
    when(registrationRepository.findByRequest(request)).thenReturn(Source.fromIterator(() => List(registration).iterator))

    classUnderTest
      .fetchRegistrations(request)
      .runWith(TestSink.probe[Registration])
      .request(1)
      .expectNext(registration)
      .expectComplete()
  }

  object Fixtures {}

}
