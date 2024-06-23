package com.goback.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class OnScreenControls : Group(), Resizeable {

    private val rightButtonTexture = TextureRegionDrawable(Texture("sprites/controlArrowRight.png")).apply {
        setMinSize(200f, 200f)
    }
    val buttonRight = ImageButton(rightButtonTexture, rightButtonTexture).apply {
        setPosition(350f, 0f)
        setSize(200f, 200f)
        this@OnScreenControls.addActor(this)
    }

    private val leftButtonTexture = TextureRegionDrawable(Texture("sprites/controlArrowLeft.png")).apply {
        setMinSize(200f, 200f)
    }
    val buttonLeft = ImageButton(leftButtonTexture, leftButtonTexture).apply {
        setPosition(50f, 0f)
        setSize(200f, 200f)
        this@OnScreenControls.addActor(this)
    }

    private val upButtonTexture = TextureRegionDrawable(Texture("sprites/controlArrowUp.png")).apply {
        setMinSize(200f, 200f)
    }

    val buttonUp = JustPressedButton(upButtonTexture, upButtonTexture).apply {
        setPosition(1600f, 0f)
        setSize(300f, 200f)
        this@OnScreenControls.addActor(this)
    }

    override fun resize(width: Int, height: Int) {
        buttonUp.setRelativePosition(1f, 0.0f)
    }
}

fun Actor.setRelativePosition(factorX: Float = 0.5f, factorY: Float = 0.5f) {
    setPosition((stage.width - this.width) * factorX, (stage.height - this.height) * factorY)
}

class JustPressedButton(imageUp: Drawable, imageDown: Drawable) : ImageButton(imageUp, imageDown) {
    private var lastPressed = false
    private var lastPressed2 = false

    val isJustPressed get() = isPressed && !lastPressed2

    override fun act(delta: Float) {
        lastPressed2 = lastPressed
        lastPressed = isPressed
        super.act(delta)
    }
}

