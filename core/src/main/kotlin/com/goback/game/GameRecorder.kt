package com.goback.game

import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxArray

class Record(val left: Boolean, val right: Boolean, val up: Boolean, val down: Boolean)

class GameRecorder {
    val playerStartPosition = Vector2()
    val playerStartVel = Vector2()

    private val MAXRUNTIME = 10 * 60
    private var position = 0

    private val records = GdxArray<Record>(MAXRUNTIME)

    fun reset() {
        records.clear()
        position = 0
        playerStartVel.set(0f, 0f)
        playerStartPosition.set(0f, 0f)
    }

    fun record(left: Boolean, right: Boolean, up: Boolean, down: Boolean) {
        records.add(Record(left, right, up, down))
    }

    fun getCurrentRecord(): Record? {
        val currentPos = position
        position++
        return if (currentPos < records.size) {
            records.get(currentPos)
        } else {
            null
        }
    }
}
