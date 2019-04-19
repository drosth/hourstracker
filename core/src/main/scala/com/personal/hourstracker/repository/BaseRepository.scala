package com.personal.hourstracker.repository

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.personal.hourstracker.service.RegistrationService.RegistrationRequest

trait BaseRepository[T, ID] {
  def findAll(): Source[T, NotUsed]
  def findByRequest(request: RegistrationRequest): Source[T, NotUsed]
  def save(entity: T): Either[String, ID]
  def findById(id: ID): Option[T]
}
