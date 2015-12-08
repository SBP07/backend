package models.helpers

import slick.lifted.Rep

/**
  * @tparam T: this.tye, e.g. Child extends BelongsToTenant[Child]. This is needed due to constraints of this.type
  */
trait BelongsToTenant[T <: BelongsToTenant[T]] {
  val tenantCanonicalName: String

  /**
    * This method returns a new object with tenantCanonicalName set to the provided parameter
    * @param tenantCanonicalName What to set the tenantCanonicalName of the returned object to
    */
  def copyTenantCanonicalName(tenantCanonicalName: String): T
}

trait BelongsToTenantTable {
  def tenantCanonicalName: Rep[String]
}
