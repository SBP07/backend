package models.helpers

import io.strongtyped.active.slick.{JdbcProfileProvider, EntityActions}

// TODO
// this class subclasses ActiveSlick's EntityActions but only saves/updates/... entities belonging to the current tenant

abstract class TenantEntityActions extends EntityActions with JdbcProfileProvider {

}
