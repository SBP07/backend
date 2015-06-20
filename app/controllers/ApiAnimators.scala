package controllers

import javax.inject._

import models.Animator
import models.dao.AnimatorDao
import models.json.AnimatorJson.animatorWrites


class ApiAnimators @Inject()(animatorDao: AnimatorDao) extends GenericApiController[Animator] {
  override val dao = animatorDao
  override val jsonWrites = animatorWrites
}
