package com.personal.hourstracker.storage.repository

trait Converter[A] {
  def convert: A
}
