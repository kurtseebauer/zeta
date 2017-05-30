package de.htwg.zeta.server.model.modelValidator.validator

import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, ElementsRule}
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent.MetaModelIndependent
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks.NullChecksResult
import models.modelDefinitions.model.Model

trait ModelValidator {

  val metaModelId: String
  val metaModelRevision: String
  val metaModelDependentRules: Seq[ElementsRule]

  def validate(model: Model): Seq[ModelValidationResult] = NullChecks.check(model) match {
    case NullChecksResult(false, Some(rule)) => Seq(ModelValidationResult(rule, valid = false))
    case _ => (MetaModelIndependent.rules ++ metaModelDependentRules).flatMap(_.check(model.elements.values.toSeq))
  }

  override def toString: String =
    s"""Validator for: $metaModelId
       |Database revision: $metaModelRevision
       |
       |${metaModelDependentRules.collect { case r: DslRule => r.dslStatement }.mkString("\n")}"""
      .stripMargin
}