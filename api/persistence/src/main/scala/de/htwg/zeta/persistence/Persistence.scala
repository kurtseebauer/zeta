package de.htwg.zeta.persistence

import java.util.UUID

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.transient.TransientLoginInfoPersistence
import de.htwg.zeta.persistence.transient.TransientRepository
import de.htwg.zeta.persistence.transient.TransientTokenCache


/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  lazy val service: Repository = new TransientRepository

  /** The current implementation of the PersistenceService with a overlaying Access-Restriction Layer.
   *
   * @param ownerID The id of the assigned user to the restriction
   * @return PersistenceService
   */
  def restrictedRepository(ownerID: UUID): Repository = {
    AccessRestrictedRepository(ownerID, service)
  }

  /** The current implementation of TokenCache. */
  lazy val tokenCache: TokenCache = new TransientTokenCache

  /** The current implementation of LoginInfoPersistence. */
  lazy val loginInfoPersistence: LoginInfoPersistence = new TransientLoginInfoPersistence

}
