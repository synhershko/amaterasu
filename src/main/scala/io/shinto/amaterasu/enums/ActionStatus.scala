package io.shinto.amaterasu.enums

object ActionStatus extends Enumeration {
  type ActionStatus = Value
  val queued = Value("queued")
  val started = Value("started")
  val complete = Value("complete")
  val failed = Value("failed")
}