package CombatSim.Attack

/**
 * Created by IntelliJ IDEA.
 * User: mononofu
 * Date: 9/19/11
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */

import CombatSim.Fighter._
import CombatSim.HitLocation._

case class AttackModifiers(hitMod: Int = 0, dmgMod: Int = 0)

class BasicAttack(mods: AttackModifiers = AttackModifiers(), hitloc: HitLocation = new Untargeted, defendMod: Int = 0) {
  def attack(attacker: Fighter, defender: Fighter) = {
    import CombatSim.Tools._
    attacker.attack(mods.hitMod) match {
      case CriticalSuccess =>
        // no defense possible
        // TODO: critical hit table here
        val dmg = attacker.doDamage(mods.dmgMod)
        defender.receiveDamage(hitloc.calcDamage(dmg))

      case Success => defender.maneuver.defend(attacker, defender, defendMod) match {
        case CriticalFailure | Failure =>
          val dmg = attacker.doDamage(mods.dmgMod)
          defender.receiveDamage(hitloc.calcDamage(dmg))
        case _ =>
      }

      case CriticalFailure =>
        // TODO: put critical miss table here

      case Failure =>
        // miss, nothing happens
    }
  }
}

class DeceptiveAttack(level: Int, mods: AttackModifiers = AttackModifiers(), hitloc: HitLocation = new Untargeted)
  extends BasicAttack(AttackModifiers(mods.hitMod -2 *level, mods.dmgMod), hitloc, -level) {}

class TelegraphicAttack(mods: AttackModifiers = AttackModifiers(), hitloc: HitLocation = new Untargeted)
  extends BasicAttack(AttackModifiers(mods.hitMod + 4, mods.dmgMod), hitloc, +2) {}


class Feint(mods: AttackModifiers = AttackModifiers(), hitloc: HitLocation = new Untargeted) {
  def attack(attacker: Fighter, defender: Fighter) = {

    import CombatSim.Tools._
    import CombatSim.CharacterSheet._
    val attackerMargin = attacker.charsheet(WeaponSkill) - DefaultDice.roll()
    val defenderMargin = defender.charsheet(WeaponSkill) - DefaultDice.roll()

    if (attackerMargin > 0) {
      if (defenderMargin < 0)
        defender.temporaryModifiers.put(FeintPenalty, List(Modifier(Parry, - attackerMargin, 2), Modifier(Dodge, -attackerMargin, 2)))
      else if (attackerMargin > defenderMargin)
        defender.temporaryModifiers.put(FeintPenalty, List(Modifier(Parry, - (attackerMargin - defenderMargin), 2), Modifier(Dodge, - (attackerMargin - defenderMargin), 2)))
    }

  }
}


// for dual-weapon attack and rapid-strike, see the "chooseAttacks" method in AI

// TODO: add FP based combat options