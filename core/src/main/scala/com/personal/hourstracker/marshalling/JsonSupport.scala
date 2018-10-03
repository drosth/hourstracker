package com.personal.hourstracker.marshalling

import spray.json.DefaultJsonProtocol

trait JsonSupport
    extends DefaultJsonProtocol
    with JsonDateTimeSupport
    with JsonMapSupport
