package com.goback.game

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import ktx.box2d.BodyDefinition
import ktx.collections.GdxArray

class TimingRequirements(
    val threeStarTime: Float, val twoStarTime: Float, val oneStarTime: Float, val totalTime: Float
) {
    fun getStarCount(timerValue: Float): Int {
        val remainingTime = totalTime - timerValue
        if (remainingTime > threeStarTime) return 3
        if (remainingTime > twoStarTime) {
            return 2
        }
        if (remainingTime > oneStarTime) {
            return 1
        }
        return 0
    }
}

abstract class Level {
    abstract fun resetLevel()
    abstract fun update()
    abstract fun draw(spriteBatch: PolygonSpriteBatch)
    abstract fun activateSwitch(switchNo: Int)
    abstract fun deactivateSwitch(switchNo : Int)

    abstract val timingRequirements: TimingRequirements
    abstract val startPosX: Float
    abstract val startPosY: Float
    abstract val realStartPosY: Float
    abstract val tutorialSwitchActivated: Boolean
}
