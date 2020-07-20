package models

import java.time.LocalDateTime

case class RegistrationViewModel(
                                  id: Option[Registration.RegistrationID] = None,
                                  job: String,
                                  clockedIn: Option[LocalDateTime] = None,
                                  clockedOut: Option[LocalDateTime] = None,
                                  duration: Option[Double] = None,
                                  hourlyRate: Option[Double] = None,
                                  earnings: Option[Double] = None,
                                  comment: Option[String] = None,
                                  tags: Option[Set[String]] = None,
                                  totalTimeAdjustment: Option[Double] = None,
                                  totalEarningsAdjustment: Option[Double] = None
                                )

//object RegistrationViewModel {
//
//  trait JsonProtocol extends CommonJsonSupport {
//    implicit val registrationViewModelFormat: RootJsonFormat[RegistrationViewModel] = jsonFormat11(RegistrationViewModel.apply)
//  }
//}
