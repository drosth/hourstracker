package com.personal.hourstracker.repository

trait BaseRepository[T, ID] {
  def findAll(): List[T]
  def save(entity: T): Either[String, ID]
  def findById(id: ID): Option[T]
}