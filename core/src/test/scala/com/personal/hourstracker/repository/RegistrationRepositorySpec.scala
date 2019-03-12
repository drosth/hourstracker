package com.personal.hourstracker.repository

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.scalatest.mockito.MockitoSugar

class RegistrationRepositorySpec extends FlatSpec
  with BeforeAndAfter
  with Matchers
  with MockitoSugar
  with H2Component {

  behavior of "RegistrationRepositorySpec"

  it should "behave" in pending
}
