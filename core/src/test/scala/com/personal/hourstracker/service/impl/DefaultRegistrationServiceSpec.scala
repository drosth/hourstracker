package com.personal.hourstracker.service.impl

import akka.actor.ActorSystem
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.{ ImporterService, RegistrationService }
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ fixture, BeforeAndAfter, Matchers, Outcome }
import org.slf4j.Logger

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

class DefaultRegistrationServiceSpec extends fixture.FlatSpec with BeforeAndAfter with Matchers with MockitoSugar {

  private implicit val logger: Logger = mock[Logger]
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val importerService = mock[ImporterService]
  private val registrationRepository = mock[RegistrationRepository]

  override type FixtureParam = RegistrationService

  override protected def withFixture(test: OneArgTest): Outcome = {
    reset(logger, importerService, registrationRepository)
    test(new DefaultRegistrationService(registrationRepository, importerService))
  }

  behavior of "DefaultRegistrationService"

  behavior of "Importing registrations"

  it should "return registrations" in { classUnderTest =>
    val registration = mock[Registration]
    val expectedRegistrations = List(registration)

    val fileName = "someFile"
    when(importerService.importRegistrationsFrom(fileName)).thenReturn(Future.successful(Right(expectedRegistrations)))

    val actual = Await.result(classUnderTest.importRegistrationsFrom(fileName), 1 second)
    actual shouldEqual Right(expectedRegistrations)
  }

  it should "return failure, given file could not be read" in { classUnderTest =>
    val fileName = "unreadableFile"
    when(importerService.importRegistrationsFrom(fileName)).thenReturn(Future.successful(Left(s"Could not import from '$fileName'")))

    val actual = Await.result(classUnderTest.importRegistrationsFrom(fileName), 1 second)
    actual shouldEqual Left("Could not import from 'unreadableFile'")
  }

  behavior of "Storing registrations"

  it should "return ids of stored registrations" in { classUnderTest =>
    val registration1 = mock[Registration]
    when(registrationRepository.save(registration1)).thenReturn(Right(1L))

    val registration2 = mock[Registration]
    when(registrationRepository.save(registration2)).thenReturn(Right(2L))

    val actual = Await.result(classUnderTest.storeRegistrations(List(registration1, registration2)), 1 second)

    actual shouldEqual Right(List(1L, 2L))
  }

  it should "return message, given could not store registration" in { classUnderTest =>
    val registration1 = mock[Registration]
    when(registrationRepository.save(registration1)).thenReturn(Right(1L))

    val registration2 = mock[Registration]
    when(registrationRepository.save(registration2)).thenReturn(Left("Could not store #2"))

    val actual = Await.result(classUnderTest.storeRegistrations(List(registration1, registration2)), 1 second)

    actual shouldEqual Left("Could not store #2")
  }

  object Fixtures {}
}
