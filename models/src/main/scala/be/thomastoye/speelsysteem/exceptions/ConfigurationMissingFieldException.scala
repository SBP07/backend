package be.thomastoye.speelsysteem.exceptions

case class ConfigurationMissingFieldException(fieldName: String) extends Exception(s"Missing field in configuration: $fieldName")
