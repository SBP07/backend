package controllers

import javax.inject._

import models.Animator
import models.dao.AnimatorDao
import models.json.AnimatorJson._


class ApiAnimators @Inject()(animatorDao: AnimatorDao)
  extends GenericApiControllerFullCrud[Animator]
{
  override val dao = animatorDao
  override val jsonWrites = animatorWrites
  override val jsonReads = animatorReads
}
