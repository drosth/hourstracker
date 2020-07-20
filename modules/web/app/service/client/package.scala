package service

package object client {

  implicit class EmployeeModelOps(employeeModel: EmployeeModel) {

    def convert(): Employee = Employee(
      id = employeeModel.id,
      name = employeeModel.name
    )
  }

  object ToDomainAdapter {

    implicit def toDomain(source: PersonNameModel): PersonName =
      PersonName(firstName = source.firstName, lastName = source.lastName)
  }

  implicit class RegistrationModelOps(registrationModel: RegistrationModel) {

    def convert(): Registration = Registration(
      id = registrationModel.id,
      job = registrationModel.job,
      clockedIn = registrationModel.clockedIn,
      clockedOut = registrationModel.clockedOut,
      duration = registrationModel.duration,
      hourlyRate = registrationModel.hourlyRate,
      earnings = registrationModel.earnings,
      comment = registrationModel.comment,
      tags = registrationModel.tags,
      totalTimeAdjustment = registrationModel.totalTimeAdjustment,
      totalEarningsAdjustment = registrationModel.totalEarningsAdjustment
    )
  }

  case class PersonNameModel(firstName: String, lastName: String)

  object PersonNameModel {

    trait JsonProtocol extends CommonJsonSupport with SprayJsonSupport {
      implicit val personNameModelFormat: RootJsonFormat[PersonNameModel] = jsonFormat2(PersonNameModel.apply)
    }

  }

  case class EmployeeModel(id: String, name: PersonNameModel)

  object EmployeeModel {

    trait JsonProtocol extends PersonNameModel.JsonProtocol with SprayJsonSupport {
      implicit val employeeModelFormat: RootJsonFormat[EmployeeModel] = jsonFormat2(EmployeeModel.apply)
    }

  }

}
