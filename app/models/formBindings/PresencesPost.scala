package models.formBindings

import models.{Activity, Child}

case class PresencesPost(child: Option[Child], activities: List[Activity])
