package com.goback.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import ktx.actors.alpha
import ktx.actors.onClick

class GameOverDialog : Group() {

    private val dialogWidth = 600f
    private val dialogHeight = 600f

    private val gameFont = BitmapFont(Gdx.files.internal("fonts/m5x7_100.fnt"))
    private val labelStyle = LabelStyle(gameFont, Color.WHITE)

    private val background = TextureRegionDrawable(Texture("sprites/dialog.png"))
    private val windowStyle = WindowStyle(gameFont, Color.WHITE, background)


    private val dialog = Dialog("", windowStyle).apply {
        this.setSize(dialogWidth, dialogHeight)
        this.setOrigin(Align.center)

        this.setPosition(1920f / 2f, 1080f / 2f, Align.center)
        isModal = true
        this@GameOverDialog.addActor(this)
    }

    private val gameOverLabel = Label("Time's up...\nTry again?", labelStyle).apply {
        this.setPosition(dialogWidth * 0.5f, 450f, Align.center)
        dialog.addActor(this)
    }

    private val retryButtonTexture = TextureRegionDrawable(Texture("sprites/retryButtonBorder.png")).apply {
        this.setMinSize(180f, 180f)
    }
    private val retryButtonClickedTexture =
        TextureRegionDrawable(Texture("sprites/retryButtonBorderClicked.png")).apply {
            this.setMinSize(170f, 170f)
        }
    val buttonRetry = ImageButton(retryButtonTexture, retryButtonClickedTexture).apply {
        this.setPosition(dialogWidth / 2f, 150f, Align.center)
        this.onClick {}
        dialog.addActor(this)
    }

    fun startAnimation()
    {
        this.isVisible = true
        dialog.alpha = 0f
        dialog.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f)
            )
        )
    }

}


