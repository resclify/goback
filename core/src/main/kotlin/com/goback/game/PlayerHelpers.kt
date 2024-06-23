package com.goback.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle

class PlayerHelpers : Group() {

    private val gameFont = BitmapFont(Gdx.files.internal("fonts/m5x7_100.fnt"))
    private val labelStyle = LabelStyle(gameFont, Color.WHITE)

    private val labelControlText = if (Gdx.app.type == Application.ApplicationType.Android) {
        "Press Arrows to move"
    } else {
        "Press W/A/D to move"
    }

    val helperControlsLabel = Label(labelControlText, labelStyle).apply {
        this.setPosition(100f, 600f)
        this@PlayerHelpers.addActor(this)
    }

    val helperInterfereLabel = Label("Don't interfere with yourself", labelStyle).apply {
        this.setPosition(100f, 600f)
        this@PlayerHelpers.addActor(this)
    }
    val helperTravelBack = Group().apply {
        val helperTravelBackLabel = Label("Click on the clock \nto travel back in time", labelStyle)
        val helperArrow = Image(Texture("sprites/helper_arrow1.png")).apply {
            setPosition(500f, -120f)
        }
        this.addActor(helperTravelBackLabel)
        this.addActor(helperArrow)
        this.setPosition(1920 / 2f + 10f, 800f)
        this@PlayerHelpers.addActor(this)
    }

    val helperTimesUp = Label("Time's up...", labelStyle).apply {
        this.setPosition(1920f - 350f, 1080f - 420f)
        this@PlayerHelpers.addActor(this)
    }

    fun hideAll() {
        helperControlsLabel.hide()
        helperInterfereLabel.hide()
        helperTimesUp.hide()
        helperTravelBack.hide()
    }

}

fun Actor.hide() {
    this.isVisible = false
}

fun Actor.show() {
    this.isVisible = true
}

