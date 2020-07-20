

package object models {

  implicit class PersonOps(person: Person) {
    def convert(): PersonViewModel = PersonViewModel(
      id = person.id,
      name = PersonNameViewModel(person.name.firstName, person.name.lastName)
    )
  }

  implicit class RegistrationOps(registration: Registration) {

    def convert(): RegistrationViewModel = RegistrationViewModel(
      id = registration.id,
      job = registration.job,
      clockedIn = registration.clockedIn,
      clockedOut = registration.clockedOut,
      duration = registration.duration,
      hourlyRate = registration.hourlyRate,
      earnings = registration.earnings,
      comment = registration.comment,
      tags = registration.tags,
      totalTimeAdjustment = registration.totalTimeAdjustment,
      totalEarningsAdjustment = registration.totalEarningsAdjustment
    )
  }

}
