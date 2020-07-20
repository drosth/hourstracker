package com.personal.hourstracker.repository

trait BaseRepository[T, ID] {
  def findAll(): Source[T, NotUsed]
  def findByRequest(request: RegistrationRequest): Source[T, NotUsed]
  def save(entity: T): Either[String, T]
  def findById(id: ID): Option[T]
}
