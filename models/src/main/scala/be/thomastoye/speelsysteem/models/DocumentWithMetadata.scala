package be.thomastoye.speelsysteem.models

case class DocumentWithMetadata[T](id: String, rev: Option[String], doc: T)
