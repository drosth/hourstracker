package com.personal.common
import spray.json.DefaultJsonProtocol

trait CommonJsonSupport extends DefaultJsonProtocol with JsonEnumSupport with JsonDateTimeSupport with JsonMapSupport
