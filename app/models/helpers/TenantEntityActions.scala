package models.helpers

import io.strongtyped.active.slick.DBIOExtensions._
import io.strongtyped.active.slick._
import io.strongtyped.active.slick.exceptions.{NoRowsAffectedException, RowNotFoundException}
import slick.ast.BaseTypedType
import slick.dbio.{FailureAction, SuccessAction}

import scala.concurrent.ExecutionContext
import scala.language.{existentials, higherKinds, implicitConversions}
import scala.util.{Failure, Success}

abstract class TenantEntityActions[M <: BelongsToTenant[M], I] extends JdbcProfileProvider with PostgresProfileProvider {

  import jdbcProfile.api._

  /** The type of the entity being persisted */
  type Entity = M
  type Id = I
  type Model = M // TODO unify Model and Entity

  def baseTypedType: BaseTypedType[Id]

  protected implicit lazy val btt: BaseTypedType[Id] = baseTypedType

  type EntityTable <: Table[Entity] with BelongsToTenantTable

  def tableQuery: TableQuery[EntityTable]

  def $id(table: EntityTable): Rep[Id]

  def idLens: Lens[Entity, Option[Id]]

  def count(tenantCanonicalName: String): DBIO[Int] = tableQuery.filterByTenant(tenantCanonicalName).size.result

  def findById(tenantCanonicalName: String, id: Id): DBIO[Entity] = tableQuery
    .filterById(id)
    .filterByTenant(tenantCanonicalName)
    .result
    .head

  def findOptionById(tenantCanonicalName: String, id: Id): DBIO[Option[Entity]] = tableQuery
    .filterById(id)
    .filterByTenant(tenantCanonicalName)
    .result
    .headOption

  def save(tenantCanonicalName: String, entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    // The following line is a safeguard. Without it, someone could accidentally/purposely save an entity with a wrong
    // tenantCanonicalName
    // Obviously, it does not protect against a wrong implementation of the copy method
    val entityWithCanonicalName = entity.copyTenantCanonicalName(tenantCanonicalName)
    idLens.get(entityWithCanonicalName) match {
      // if has an Id, try to update it
      case Some(id) => update(tenantCanonicalName, entityWithCanonicalName)

      // if has no Id, try to add it
      case None => insert(tenantCanonicalName, entityWithCanonicalName).map { id =>
        idLens.set(entityWithCanonicalName, Option(id))
      }
    }
  }

  /**
    * Before insert interceptor method. This method is called just before record insertion.
    * The default implementation returns a successful DBIO wrapping the passed entity.
    *
    * The returned `DBIOAction` is combined with the final insert `DBIOAction`
    * and 'marked' to run on the same transaction.
    *
    * Override this method if you need to add extract validation or modify the entity before insert.
    *
    * See examples bellow:
    * {{{
    * // simple validation example
    * override def beforeInsert(foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
    *    if (foo.name.trim.isEmpty) {
    *      DBIO.failed(new RuntimeException("Name can't be empty!!!")
    *    } else {
    *      DBIO.successful(foo)
    *    }
    * }
    * }}}
    *
    * {{{
    * // simple audit example
    * override def beforeInsert(foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
    *    // ensure that created and lastUpdate fields are updated just before insert
    *    val audited = foo.copy(created = DateTime.now, lastUpdate = DateTime.now)
    *    DBIO.successful(audited)
    * }
    * }}}
    */
  def beforeInsert(entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    // default implementation does nothing
    DBIO.successful(entity)
  }


  /**
    * Before update interceptor method. This method is called just before record update.
    * The default implementation returns a successful DBIO wrapping the passed entity.
    *
    * The returned `DBIOAction` is combined with the final update `DBIOAction`
    * and 'marked' to run on the same transaction.
    *
    * Override this method if you need to add extract validation or modify the entity before update.
    *
    * See examples bellow:
    *
    * {{{
    * // simple validation example
    * override def beforeUpdate(id: Int, foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
    *    findById(id).flatMap { oldFoo =>
    *      if (oldFoo.name != foo.name) {
    *        DBIO.failed(new RuntimeException("Can't modify name!!!")
    *      } else {
    *        DBIO.successful(foo)
    *      }
    *    }
    * }
    * }}}
    *
    * {{{
    * // simple audit example
    * override def beforeUpdate(id: Int, foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
    *    // ensure that lastUpdate fields are updated just before update
    *    val audited = foo.copy(lastUpdate = DateTime.now)
    *    DBIO.successful(audited)
    * }
    * }}}
    */
  def beforeUpdate(id: Id, entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    // default implementation does nothing
    DBIO.successful(entity)
  }

  def insert(tenantCanonicalName: String, entity: Entity)(implicit exc: ExecutionContext): DBIO[Id] = {
    val action = beforeInsert(entity.copyTenantCanonicalName(tenantCanonicalName)).flatMap { preparedModel =>
      tableQuery.returning(tableQuery.map($id)) += preparedModel
    }
    // beforeInsert and '+=' must run on same tx
    action.transactionally
  }

  def fetchAll(tenantCanonicalName: String, fetchSize: Int /* = 100 commented out due to compiler restrictions TODO once this class is no longer a subclass, uncomment this */)(implicit exc: ExecutionContext): StreamingDBIO[Seq[Entity], Entity] = {
    tableQuery
      .filterByTenant(tenantCanonicalName)
      .result
      .transactionally
      .withStatementParameters(fetchSize = fetchSize)
  }

  def update(tenantCanonicalName: String, entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    val action =
      for {
        id <- tryExtractId(entity.copyTenantCanonicalName(tenantCanonicalName))
        preparedModel <- beforeUpdate(id, entity.copyTenantCanonicalName(tenantCanonicalName))
        updatedModel <- update(tenantCanonicalName, id, preparedModel)
      } yield updatedModel

    // beforeUpdate and update must run on same tx
    action.transactionally
  }


  protected def update(tenantCanonicalName: String, id: Id, entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {

    val triedUpdate = tableQuery.filterById(id)
      .filterByTenant(tenantCanonicalName)
      .update(entity)
      .mustAffectOneSingleRow
      .asTry

    triedUpdate.flatMap {
      case Success(_) => DBIO.successful(entity)
      case Failure(NoRowsAffectedException) => DBIO.failed(new RowNotFoundException(entity))
      case Failure(ex) => DBIO.failed(ex)
    }

  }

  def delete(tenantCanonicalName: String, entity: Entity)(implicit exc: ExecutionContext): DBIO[Int] = {
    tryExtractId(entity).flatMap { id =>
      tableQuery.deleteById(tenantCanonicalName, id)
    }
  }

  def deleteById(tenantCanonicalName: String, id: Id)(implicit exc: ExecutionContext): DBIO[Int] = {
    tableQuery.deleteById(tenantCanonicalName, id)
  }

  private def tryExtractId(entity: Entity): DBIO[Id] = {
    idLens.get(entity) match {
      case Some(id) => SuccessAction(id)
      case None => FailureAction(new RowNotFoundException(entity))
    }
  }

  class PimpedEntityQuery(query: Query[EntityTable, Entity, Seq]) {
    def filterByTenant(tenantCanonicalName: String): Query[EntityTable, Entity, Seq] = query.filter(_.tenantCanonicalName === tenantCanonicalName)

    def deleteById(tenantCanonicalName: String, id: Id)(implicit exc: ExecutionContext): DBIO[Int] = {
      filterById(id).filterByTenant(tenantCanonicalName).delete.mustAffectOneSingleRow
    }

    def filterById(id: Id) = query.filter($id(_) === id)
  }

  implicit def pimpEntityQuery(query: Query[EntityTable, Entity, Seq]): PimpedEntityQuery = new PimpedEntityQuery(query)

}
