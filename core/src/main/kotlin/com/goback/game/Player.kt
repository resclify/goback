package com.goback.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import ktx.box2d.body
import ktx.box2d.box
import kotlin.math.abs

class Player(world: World) : Group() {

    var body = world.body(type = BodyDef.BodyType.DynamicBody) {
        position.set(5f, 10f)

        box(2.8f, 4.85f) {
            friction = 0f
            userData = this@Player
        }
        box(2.75f, 0.1f, Vector2(0f, -4.85f/2f+0.05f))
        {
            friction = 0f
            filter.categoryBits = CategoryBits.PLAYER_BOTTOM
            userData = this@Player
        }
        box(2.75f, 0.1f, Vector2(0f, 4.85f/2f-0.05f))
        {
            friction = 0f
            filter.categoryBits = CategoryBits.PLAYER_TOP
            userData = this@Player
        }
    }

    val sprite = Image(Texture("sprites/player2.png")).apply {
        setSize(2.85f, 4.85f)
        setOrigin(Align.center)
        addActor(this)
    }

    var flipped = false
    private var groundContacts = 0

    override fun act(delta: Float) {
        updatePosition()
        //playerSprite.setSize(2.85f, 4.85f)
        //playerSprite.setOriginCenter()
        //playerSprite.transformFromBody(body)
        //playerSprite.draw(spriteBatch)
        //
        super.act(delta)
    }

    private fun updatePosition() {
        if (flipped) {
            sprite.setPosition(body.position.x + 2.85f * 0.5f, body.position.y - 4.85f * 0.5f)
        } else {
            sprite.setPosition(body.position.x - 2.85f * 0.5f, body.position.y - 4.85f * 0.5f)
        }
    }

    fun updateInput(left: Boolean, right: Boolean, up: Boolean, down: Boolean) {
        if (up && touchesGround()) {
            body.applyLinearImpulse(0f, 25f, 0f, 0f, true)
        }
        if (right && body.linearVelocity.x <= 8f) {
            body.applyForceToCenter(75f, 0f, true)
        }
        if (left && body.linearVelocity.x >= -8f) {
            body.applyForceToCenter(-75f, 0f, true)
        }
        if (!right && !left) {
            body.applyForceToCenter(-6 * body.linearVelocity.x, 0f, false)
        }

        if (left) {
            sprite.setFlippedX(true)
        }
        if (right) {
            sprite.setFlippedX(false)
        }
        updatePosition()
    }

    fun touchGround() {
        groundContacts++
        ktx.log.debug { "Player Touched Ground" }
    }

    fun leftGround() {
        groundContacts--
        groundContacts = groundContacts.coerceAtLeast(0)
        ktx.log.debug { "Player Left Ground" }
    }

    private fun touchesGround() = groundContacts > 0

    fun draw(spriteBatch: PolygonSpriteBatch) {

    }

    private fun Image.setFlippedX(flip: Boolean) {
        if (flip && !flipped) {
            this.width = (-abs(this.width.toDouble())).toFloat()
            flipped = true
        } else if (!flip && flipped) {
            this.width = abs(this.width.toDouble()).toFloat()
            flipped = false
        }
    }
}


fun Image.setFlippedY(flipped: Boolean) {
    if (flipped) {
        this.height = (-abs(this.height.toDouble())).toFloat()
        this.y = abs(this.y.toDouble()).toFloat()
    } else {
        this.height = abs(this.height.toDouble()).toFloat()
        this.y = (-abs(this.y.toDouble())).toFloat()
    }
    //this.flipped = flipped
}

fun Sprite.transformFromBody(body: Body) {
    setOriginBasedPosition(body.position.x, body.position.y)
    rotation = body.angle * MathUtils.radiansToDegrees
}
