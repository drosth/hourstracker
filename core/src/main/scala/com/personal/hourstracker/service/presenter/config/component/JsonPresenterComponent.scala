package com.personal.hourstracker.service.presenter.config.component

import spray.json.{JsValue, JsonWriter}

trait JsonPresenterComponent extends DefaultJsonPresenter

trait JsonPresenter {

  def jsonPresenter: JsonPresenter

  trait JsonPresenter {
    def renderRegistrationsTo[T](registrations: T)(implicit writer: JsonWriter[T]): JsValue
  }

}

trait DefaultJsonPresenter extends JsonPresenter {

  def jsonPresenter: JsonPresenter = new DefaultJsonPresenter()

  class DefaultJsonPresenter extends JsonPresenter {

    import spray.json._

    override def renderRegistrationsTo[T](registrations: T)(implicit writer: JsonWriter[T]): JsValue = {
      registrations.toJson
    }
  }

}
