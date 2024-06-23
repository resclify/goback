package com.goback.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import ktx.actors.alpha
import ktx.actors.onClick

class LevelCompleteDialog : Group() {

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
        this@LevelCompleteDialog.addActor(this)
    }

    private val levelCompleteLabel = Label("Level Completed", labelStyle).apply {
        this.setPosition(dialogWidth / 2f, 650f, Align.center)
        dialog.addActor(this)
    }


    private val starBackground1 = Image(Texture("sprites/starBlank.png")).apply {
        setSize(173f, 173f)
        this.setPosition(dialogWidth / 2 - 250f, 450f, Align.center)
        dialog.addActor(this)
    }
    private val starBackground2 = Image(Texture("sprites/starBlank.png")).apply {
        setSize(173f, 173f)
        this.setPosition(dialogWidth / 2, 450f, Align.center)
        dialog.addActor(this)
    }
    private val starBackground3 = Image(Texture("sprites/starBlank.png")).apply {
        setSize(173f, 173f)
        this.setPosition(dialogWidth / 2 + 250f, 450f, Align.center)
        dialog.addActor(this)
    }

    private val star1 = Image(Texture("sprites/star1.png")).apply {
        setSize(173f, 173f)
        this.setPosition(dialogWidth / 2 - 250f, 450f, Align.center)
        this.setOrigin(Align.center)
        dialog.addActor(this)

    }

    private val star2 = Image(Texture("sprites/star1.png")).apply {
        setSize(173f, 173f)
        this.setPosition(dialogWidth / 2, 450f, Align.center)
        this.setOrigin(Align.center)
        dialog.addActor(this)

    }
    private val star3 = Image(Texture("sprites/star1.png")).apply {
        setSize(173f, 173f)
        this.setPosition(dialogWidth / 2 + 250f, 450f, Align.center)
        this.setOrigin(Align.center)
        dialog.addActor(this)

    }

    private val timeLeftLabel = Label("Time left: 10.3s", labelStyle).apply {
        this.setPosition(150f, 250f)
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
        this.setPosition(180f, 130f, Align.center)
        //this.setSize(140f, 140f)
        dialog.addActor(this)
    }

    private val forwardButtonTexture = TextureRegionDrawable(Texture("sprites/rightArrowBorder.png")).apply {
        this.setMinSize(180f, 180f)
    }
    private val forwardButtonClickedTexture =
        TextureRegionDrawable(Texture("sprites/rightArrowBorderClicked.png")).apply {
            this.setMinSize(180f, 180f)
        }
    val buttonForward = ImageButton(forwardButtonTexture, forwardButtonClickedTexture).apply {
        this.setPosition(dialogWidth * 0.5f, 130f, Align.center)
        //this.setSize(140f, 140f)
        this.setOrigin(Align.center)
        dialog.addActor(this)
    }


    fun startAnimation(timeLeft: Float, stars: Int) {
        this.isVisible = true
        dialog.alpha = 0.0f
        dialog.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f)
            )
        )
        buttonForward.isVisible = false
        buttonRetry.isVisible = false
        star1.isVisible = false
        star2.isVisible = false
        star3.isVisible = false
        timeLeftLabel.setText("Time left: %.1fs".format(timeLeft))
        if (stars >= 1) {
            star1.addAction(
                Actions.sequence(
                    Actions.delay(1f),
                    Actions.scaleTo(0f, 0f),
                    Actions.visible(true),
                    Actions.scaleTo(1f, 1f, 0.3f, Interpolation.swingOut)
                )
            )
        }
        if (stars >= 2) {
            star2.addAction(
                Actions.sequence(
                    Actions.delay(1.3f),
                    Actions.scaleTo(0f, 0f),
                    Actions.visible(true),
                    Actions.scaleTo(1f, 1f, 0.3f, Interpolation.swingOut)
                )
            )
        }
        if (stars == 3) {
            star3.addAction(
                Actions.sequence(
                    Actions.delay(1.6f),
                    Actions.scaleTo(0f, 0f),
                    Actions.visible(true),
                    Actions.scaleTo(1f, 1f, 0.3f, Interpolation.swingOut)
                )
            )
        }

        buttonForward.addAction(
            Actions.sequence(
                Actions.delay(2f),
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.alpha(1f, 0.3f, Interpolation.swingOut)
            )
        )
        buttonRetry.addAction(
            Actions.sequence(
                Actions.delay(2.3f),
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.alpha(1f, 0.3f, Interpolation.swingOut)
            )
        )
    }


}
