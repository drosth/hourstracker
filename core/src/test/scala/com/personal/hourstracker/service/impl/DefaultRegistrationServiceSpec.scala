package com.personal.hourstracker.service.impl

import java.util.Locale

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.RegistrationService.{ RegistrationRequest, SelectByYear, SelectByYearAndMonth }
import com.personal.hourstracker.service.{ FacturationService, ImporterService, RegistrationService }
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfter, Matchers, Outcome, fixture }
import org.slf4j.Logger

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

class DefaultRegistrationServiceSpec extends fixture.FlatSpec with BeforeAndAfter with Matchers with MockitoSugar {

  private implicit val logger: Logger = mock[Logger]
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit lazy val locale: Locale = new Locale("nl", "NL")

  private val importerService = mock[ImporterService]
  private val registrationRepository = mock[RegistrationRepository]
  private val facturationService: FacturationService = mock[FacturationService]

  override type FixtureParam = RegistrationService

  override protected def withFixture(test: OneArgTest): Outcome = {
    reset(logger, importerService, registrationRepository, facturationService)
    test(new DefaultRegistrationService(registrationRepository, importerService, facturationService))
  }

  behavior of "Importing registrations"

  it should "return registrations" in { classUnderTest =>
    val registration = mock[Registration]
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
    val registration = mock[Registration]
    when(registrationRepository.findAll()).thenReturn(Source.fromIterator(() => List(registration).iterator))

    classUnderTest.fetchRegistrations().runWith(TestSink.probe[Registration]).request(1)
      .expectNext(registration)
      .expectComplete()
  }

  it should "return registrations selected by year" in { classUnderTest =>
    val request: RegistrationRequest = SelectByYear(2000)

    val registration = mock[Registration]
    when(registrationRepository.findByRequest(request)).thenReturn(Source.fromIterator(() => List(registration).iterator))

    classUnderTest.fetchRegistrations(request).runWith(TestSink.probe[Registration]).request(1)
      .expectNext(registration)
      .expectComplete()
  }

  it should "return registrations selected by year and month" in { classUnderTest =>
    val request: RegistrationRequest = SelectByYearAndMonth(2000, 1)

    val registration = mock[Registration]
    when(registrationRepository.findByRequest(request)).thenReturn(Source.fromIterator(() => List(registration).iterator))

    classUnderTest.fetchRegistrations(request).runWith(TestSink.probe[Registration]).request(1)
      .expectNext(registration)
      .expectComplete()
  }

  object Fixtures {}
}
