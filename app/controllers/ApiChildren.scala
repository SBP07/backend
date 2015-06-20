package controllers

import models.dao.ChildDao
import play.api.mvc.Action

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.Child
import models.json.ChildJson._

class ApiChildren @Inject() (childDao: ChildDao) extends GenericApiController[Child] {
  override val dao = childDao
  override val jsonWrites = childWrites

  def update(id: Long): Action[Child] = Action.async(parse.json(childReads)) { req =>
    childDao.update(req.body).map(numUpdated => if(numUpdated == 0) BadRequest else Ok)
  }

  def newChild: Action[Child] = Action.async(parse.json(childReads)) { implicit req =>
    childDao.insert(req.body).map(numCreated => Created)
  }
}
