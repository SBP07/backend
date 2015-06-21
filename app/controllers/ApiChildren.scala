package controllers

import models.dao.ChildDao
import javax.inject.Inject

import models.Child
import models.json.ChildJson._

class ApiChildren @Inject() (childDao: ChildDao)
  extends GenericApiControllerFullCrud[Child]
{
  override val dao = childDao
  override val jsonWrites = childWrites
  override val jsonReads = childReads
}
