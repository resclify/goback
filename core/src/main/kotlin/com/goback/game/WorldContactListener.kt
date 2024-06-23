package com.goback.game

import com.badlogic.gdx.physics.box2d.*
import kotlin.experimental.and
import kotlin.experimental.or

class WorldContactListener(private val gameScreen: GameScreen) : ContactListener {
    override fun beginContact(contact: Contact?) {
        if (contact == null) return

        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB

        val collisionDef = fixtureA.filterData.categoryBits or fixtureB.filterData.categoryBits

        when (collisionDef) {
            CategoryBits.PLAYER_BOTTOM or CategoryBits.PLAYER_TOP  -> {
                val player = resolveUserData(fixtureA, fixtureB, CategoryBits.PLAYER_BOTTOM)
                if (player is Player) {
                    player.touchGround()
                }
            }

            CategoryBits.GROUND or CategoryBits.PLAYER_BOTTOM -> {
                val player = resolveUserData(fixtureA, fixtureB, CategoryBits.PLAYER_BOTTOM)
                if (player is Player) {
                    player.touchGround()
                }
            }

            CategoryBits.SWITCH or CategoryBits.PLAYER_BOTTOM -> {
                val switchNo = resolveUserData(fixtureA, fixtureB, CategoryBits.SWITCH)
                if (switchNo is Int) {
                    gameScreen.level.activateSwitch(switchNo)
                }
            }

            CategoryBits.END_ZONE or CategoryBits.PLAYER_BOTTOM -> {
                gameScreen.levelFinished()
            }
        }
    }

    private fun resolveUserData(fixtureA: Fixture, fixtureB: Fixture, category: Short): Any {
        return if ((fixtureA.filterData.categoryBits and category) != 0.toShort()) {
            fixtureA.userData
        } else {
            fixtureB.userData
        }
    }


    override fun endContact(contact: Contact?) {
        if (contact == null) return

        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB

        val collisionDef = fixtureA.filterData.categoryBits or fixtureB.filterData.categoryBits

        when (collisionDef) {
            CategoryBits.PLAYER_BOTTOM or CategoryBits.PLAYER_TOP -> {
                val player = resolveUserData(fixtureA, fixtureB, CategoryBits.PLAYER_BOTTOM)
                if (player is Player) {
                    player.leftGround()
                }
            }

            CategoryBits.GROUND or CategoryBits.PLAYER_BOTTOM -> {
                val player = resolveUserData(fixtureA, fixtureB, CategoryBits.PLAYER_BOTTOM)
                if (player is Player) {
                    player.leftGround()
                }
            }

            CategoryBits.START_ZONE or CategoryBits.PLAYER_BOTTOM -> {
                gameScreen.playerLeavesStart()
            }

            CategoryBits.SWITCH or CategoryBits.PLAYER_BOTTOM -> {
                val switchNo = resolveUserData(fixtureA, fixtureB, CategoryBits.SWITCH)
                if (switchNo is Int) {
                    gameScreen.level.deactivateSwitch(switchNo)
                }
            }
        }
    }

    override fun preSolve(contact: Contact?, p1: Manifold?) {
    }

    override fun postSolve(contact: Contact?, p1: ContactImpulse?) {
    }
}
