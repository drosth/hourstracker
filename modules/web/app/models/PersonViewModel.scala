package models

case class PersonNameViewModel(firstName: String, lastName: String)

case class PersonViewModel(id: String, name: PersonNameViewModel)


