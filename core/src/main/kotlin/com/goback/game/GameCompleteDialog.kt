package com.goback.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
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

class GameCompleteDialog : Group() {

    private val dialogWidth = 800f
    private val dialogHeight = 800f

    private val gameFont = BitmapFont(Gdx.files.internal("fonts/m5x7_100.fnt"))
    private val labelStyle = LabelStyle(gameFont, Color.WHITE)

    private val background = TextureRegionDrawable(Texture("sprites/dialog.png"))
    private val windowStyle = WindowStyle(gameFont, Color.WHITE, background)


    private val dialog = Dialog("", windowStyle).apply {
        this.setSize(dialogWidth, dialogHeight)
        this.setPosition(1920f / 2f, 1080f / 2f, Align.center)
        isModal = true
        this@GameCompleteDialog.addActor(this)
    }

    private val gameCompletedLabel = Label("Game Completed", labelStyle).apply {
        this.setPosition(dialogWidth / 2f, 670f, Align.center)
        dialog.addActor(this)
    }
    private val congratulationsLabel = Label("Congratulations you \nfinished the game!", labelStyle).apply {
        this.setPosition(dialogWidth / 2f, 500f, Align.center)
        dialog.addActor(this)
    }


    private val totalScoreLabel = Label("Your total score\nis: 50!", labelStyle).apply {
        this.setPosition(150f, 200f)
        dialog.addActor(this)
    }

    private val retryButtonTexture = TextureRegionDrawable(Texture("sprites/retryButtonBorder.png")).apply {
        this.setMinSize(100f, 100f)
    }
    private val retryButtonClickedTexture =
        TextureRegionDrawable(Texture("sprites/retryButtonBorderClicked.png")).apply {
            this.setMinSize(100f, 100f)
        }
    val buttonRetry = ImageButton(retryButtonTexture, retryButtonClickedTexture).apply {
        this.setPosition(dialogWidth*0.5f, 100f, Align.center)
        //this.setSize(140f, 140f)
        dialog.addActor(this)
    }


    fun startAnimation(totalScore: Int) {
        this.isVisible = true
        dialog.alpha = 0.0f
        dialog.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f)
            )
        )
        totalScoreLabel.setText("Your final\nscore is: $totalScore")
    }


}

