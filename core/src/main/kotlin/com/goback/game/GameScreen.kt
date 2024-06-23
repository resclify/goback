package com.goback.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.begin


enum class GameState {
    INIT, RECORDING, WAIT_FOR_PLAYBACK, TRAVELBACK_ANIMATION, PLAYBACK, GAME_OVER, LEVEL_COMPLETED
}

class GameScreen(private val game: GoBack, private val currentLevel: Int, private val currentScore: Int) : KtxScreen {

    private var currentState = GameState.INIT

    private val worldWidth = 100f
    private var world = World(Vector2(0f, 4 * -9.81f), false)
    private val worldStage = Stage(ExtendViewport(worldWidth, worldWidth * 1080f / 1920f))
    private val uiStage = Stage(ExtendViewport(1920f, 1080f))

    private val spriteBatch = PolygonSpriteBatch()

    private val camera: Camera get() = worldStage.camera
    private var enableDebugRenderer = false
    private val debugRenderer = Box2DDebugRenderer()
    private val gameFont = BitmapFont(Gdx.files.internal("fonts/m5x7_100.fnt"))

    private val labelStyle = LabelStyle(gameFont, Color.WHITE)

    private val helpers = PlayerHelpers().apply {
        uiStage.addActor(this)
    }


    private val retryButtonTexture = TextureRegionDrawable(Texture("sprites/retryButton1.png")).apply{
        this.setMinSize(180f, 180f)
    }
    private val buttonRetry = ImageButton(retryButtonTexture, retryButtonTexture).apply {
        this.setPosition(120f, 1080f - 250f)
        this.setSize(180f, 180f)
        this.onClick {
            resetLevel()
        }
        uiStage.addActor(this)
    }

    private val clockHand = Image(Texture("sprites/clockHand1.png")).apply {
        this.setOrigin(5f, 0f)
        this.setPosition(86f, 87f)
    }
    private val clockButton = Group().apply {
        val clockButtonTexture = TextureRegionDrawable(Texture("sprites/clock.png"))
        val buttonPlayback = ImageButton(clockButtonTexture, clockButtonTexture).apply {
            this.setSize(180f, 180f)
            this.onClick { startTravelBackAnimation() }
        }
        this.onClick { startTravelBackAnimation() }
        this.addActor(buttonPlayback)
        this.addActor(clockHand)
        this.setOrigin(85f, 85f)
        this.setPosition(1920f - 250f, 1080f - 250f)
        uiStage.addActor(this)
    }

    private val recorder = GameRecorder()
    private var recordTimer = 0f

    private val labelLevel = Label("Level: $currentLevel", labelStyle).apply {
        this.setPosition(1920f * 0.5f - 210f, 1080f - 50f, Align.center)
        uiStage.addActor((this))
    }

    private val labelScore = Label("Score: $currentScore", labelStyle).apply {
        this.setPosition(1920f * 0.5f + 210f, 1080f - 50f, Align.center)
        uiStage.addActor((this))
    }

    var level: Level

    private val playerPortal = Image(Texture("sprites/portal1.png")).apply {
        this.isVisible = false
        worldStage.addActor(this)
    }

    private val player = Player(world).apply { worldStage.addActor(this) }
    private val playbackPlayer = Player(world).apply { worldStage.addActor(this) }

    private val onScreenControls = OnScreenControls().apply {
        uiStage.addActor(this)
    }

    val gameOverDialog = GameOverDialog().apply {
        this.isVisible = false
        this.buttonRetry.onClick {
            resetLevel()
            this@apply.isVisible = false
        }
        uiStage.addActor(this)
    }

    val levelCompleteDialog = LevelCompleteDialog().apply {
        this.isVisible = false

        this.buttonRetry.onClick {
            this@apply.hide()
            resetLevel()
        }
        this.buttonForward.onClick {
            this@apply.hide()
            startNextLevel()
        }
        uiStage.addActor(this)
    }
    val gameCompleteDialog = GameCompleteDialog().apply {
        this.isVisible = false
        this.buttonRetry.onClick {
            game.nextLevel(1, 0)
            this@apply.isVisible = false
        }
        uiStage.addActor(this)
    }

    init {
        input.inputProcessor = InputMultiplexer(uiStage, worldStage)

        world.setContactListener(WorldContactListener(this))
        level = when (currentLevel) {
            1 -> Level1(world)
            2 -> Level2(world)
            3 -> Level3(world)
            4 -> Level4(world)
            5 -> Level5(world)
            else -> Level6(world)
        }
        resetLevel()
        resetLevel()
    }


    private fun resetLevel() {
        ktx.log.debug { "Reset Level" }
        recordTimer = 0f;
        recorder.reset()

        resetClockButton()

        helpers.hideAll()
        if (currentLevel == 1) {
            helpers.helperControlsLabel.show()
            helpers.helperControlsLabel.clearActions()
            helpers.helperControlsLabel.alpha = 1.0f
        }
        helpers.helperInterfereLabel.clearActions()
        helpers.helperInterfereLabel.alpha = 1.0f

        player.clearActions()
        player.alpha = 1.0f
        playbackPlayer.alpha = 0.0f
        playerPortal.clearActions()
        playerPortal.alpha = 0f

        //Workaround for unwanted momentum save of player figure when resetting
        world.step(1 / 60f, 8, 3)

        player.body.setTransform(level.startPosX, level.startPosY, 0f)
        player.body.setLinearVelocity(0f, 0f)
        playbackPlayer.body.setTransform(1000f, 0f, 0f)
        playbackPlayer.body.setLinearVelocity(0f, 0f)
        level.resetLevel()

        //Workaround for unwanted momentum save of player figure when resetting
        world.step(1 / 60f, 8, 3)

        currentState = GameState.INIT
    }


    override fun dispose() {
        super.dispose()
    }

    override fun hide() {
        super.hide()
    }

    override fun pause() {
        super.pause()
    }


    private fun updateCamera() {
        camera.position.x = player.body.position.x + 10f
        camera.position.y = 20f
        camera.update(true)
    }


    private fun updateInput() {
        val left =
            input.isKeyPressed(Input.Keys.LEFT) || input.isKeyPressed(Input.Keys.A) || onScreenControls.buttonLeft.isPressed
        val right =
            input.isKeyPressed(Input.Keys.RIGHT) || input.isKeyPressed(Input.Keys.D) || onScreenControls.buttonRight.isPressed
        val up =
            input.isKeyJustPressed(Input.Keys.SPACE) || input.isKeyJustPressed(Input.Keys.UP) || input.isKeyJustPressed(
                Input.Keys.W
            ) || onScreenControls.buttonUp.isJustPressed


        player.updateInput(left, right, up, false)

        if (currentState == GameState.RECORDING) {
            recorder.record(left, right, up, false)
        } else if (currentState == GameState.PLAYBACK) {
            val record = recorder.getCurrentRecord()
            if (record != null) {
                playbackPlayer.updateInput(record.left, record.right, record.up, record.down)
            } else {
                playbackPlayer.updateInput(left = false, right = false, up = false, down = false)
            }
        }
    }

    fun updateDebugging() {
        if (input.isKeyJustPressed(Input.Keys.F11)) {
            enableDebugRenderer = !enableDebugRenderer
        }
        if (input.isKeyJustPressed(Input.Keys.F12)) {
            game.nextLevel(currentLevel + 1, currentScore + 3)
        }
        if (input.isKeyJustPressed(Input.Keys.F10)) {
            levelFinished()
        }
        if (input.isKeyJustPressed(Input.Keys.F9)) {
            gameOver()
        }
        if (input.isKeyJustPressed(Input.Keys.F8)) {
            recordTimer--
        }
    }

    private fun update() {
        worldStage.act()
        uiStage.act()

        //updateDebugging()

        if (currentState == GameState.RECORDING || currentState == GameState.PLAYBACK) {
            recordTimer += Gdx.graphics.deltaTime
        }
        if (recordTimer > level.timingRequirements.totalTime && currentState == GameState.RECORDING) {
            waitForPlayback()
        }
        if (currentState == GameState.PLAYBACK && recordTimer > level.timingRequirements.totalTime) {
            gameOver()
        }

        if (currentState == GameState.RECORDING && currentLevel == 1) {
            helpers.helperTravelBack.isVisible = level.tutorialSwitchActivated
        }

        if (currentState != GameState.TRAVELBACK_ANIMATION) {
            clockHand.rotation = -recordTimer / level.timingRequirements.totalTime * 360f
        }
        level.update()

        updateCamera()
        if (currentState == GameState.INIT || currentState == GameState.RECORDING || currentState == GameState.PLAYBACK) {
            updateInput()
            world.step(1 / 60f, 8, 3)
        }
    }

    private fun waitForPlayback() {
        currentState = GameState.WAIT_FOR_PLAYBACK
        helpers.helperTimesUp.show()
        clockButton.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.2f, 1.2f, 0.5f, Interpolation.linear),
                    Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.linear)
                )
            )
        )
    }

    private fun gameOver() {
        currentState = GameState.GAME_OVER
        gameOverDialog.startAnimation()
    }

    fun playerLeavesStart() {
        if (currentState == GameState.INIT) {
            ktx.log.debug { "Start recording" }
            recordTimer = 0f
            recorder.playerStartPosition.set(player.body.position)
            recorder.playerStartVel.set(player.body.linearVelocity)
            currentState = GameState.RECORDING
            helpers.helperControlsLabel.addAction(Actions.alpha(0f, 1.0f))
        }
        if (currentState == GameState.PLAYBACK) {
            helpers.helperInterfereLabel.addAction(Actions.alpha(0f, 1.0f))
        }
    }

    private fun startTravelBackAnimation() {
        if (currentState == GameState.RECORDING || currentState == GameState.WAIT_FOR_PLAYBACK) {
            resetClockButton()

            helpers.helperTravelBack.hide()
            currentState = GameState.TRAVELBACK_ANIMATION

            playerPortal.isVisible = true
            playerPortal.setSize(2.85f, 4.85f)
            playerPortal.setOrigin(Align.center)
            if (player.flipped) {
                playerPortal.setPosition(
                    player.body.position.x - 2.85f * 0.5f - 1f, player.body.position.y - 4.85f * 0.5f + 0.3f
                )
            } else {
                playerPortal.setPosition(
                    player.body.position.x - 2.85f * 0.5f + 1f, player.body.position.y - 4.85f * 0.5f + 0.3f
                )
            }
            playerPortal.addAction(
                Actions.sequence(Actions.alpha(1f, 0.3f),
                    Actions.delay(0.7f),
                    Actions.alpha(0f),
                    Actions.moveTo(level.startPosX - 2.85f * 0.5f - 1f, level.realStartPosY - 4.85f * 0.5f + 0.3f),
                    Actions.alpha(1.0f, 0.3f),
                    Actions.run { clockHand.addAction(Actions.rotateTo(0.0f, 0.3f)) },
                    Actions.delay(1.0f),
                    Actions.alpha(0.0f, 0.3f),
                    Actions.run { startPlayback() })
            )
            player.addAction(
                Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.alpha(0.0f, 0.5f),
                    Actions.delay(0.3f),
                    Actions.run { player.body.setTransform(level.startPosX, level.realStartPosY, 0f) },
                    Actions.run { level.resetLevel() },
                    Actions.alpha(1.0f, 0.4f)
                )
            )
            playbackPlayer.body.setTransform(recorder.playerStartPosition, 0f)
            playbackPlayer.addAction(Actions.sequence(Actions.delay(1f), Actions.alpha(1.0f, 0f)))

            if (currentLevel == 1) {
                helpers.helperInterfereLabel.addAction(Actions.sequence(Actions.delay(1.3f), Actions.show()))
            }
        }
    }

    private fun startPlayback() {
        if (currentState == GameState.TRAVELBACK_ANIMATION) {
            ktx.log.debug { "Start playback" }
            currentState = GameState.PLAYBACK

            recordTimer = 0f
            helpers.helperTravelBack.hide()

            //Workaround for unwanted momentum save of player figure when resetting
            world.step(1 / 60f, 8, 3)
            world.step(1 / 60f, 8, 3)
            world.step(1 / 60f, 8, 3)

            playbackPlayer.body.linearVelocity = recorder.playerStartVel
            playbackPlayer.body.setTransform(recorder.playerStartPosition, 0f)

            player.body.setLinearVelocity(0f, 0f)
            player.body.setTransform(level.startPosX, level.realStartPosY, 0f)
        }
    }

    override fun render(delta: Float) {
        update()
        clearScreen(13f / 255f, 76 / 255f, 200f / 255f)

        worldStage.draw()

        spriteBatch.begin(camera)
        level.draw(spriteBatch)
        player.draw(spriteBatch)
        playbackPlayer.draw(spriteBatch)

        spriteBatch.end()

        if (enableDebugRenderer) {
            debugRenderer.render(world, camera.combined)
        }
        uiStage.draw()
        super.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
        worldStage.viewport.update(width, height, false)

        uiStage.resizeAll(width, height)
        super.resize(width, height)
    }

    override fun resume() {
        super.resume()
    }

    override fun show() {
        super.show()
    }

    fun levelFinished() {
        currentState = GameState.LEVEL_COMPLETED
        val stars = level.timingRequirements.getStarCount(recordTimer)
        levelCompleteDialog.startAnimation(level.timingRequirements.totalTime - recordTimer, stars)

        ktx.log.debug { "Level finished in $recordTimer" }
    }

    private fun resetClockButton() {
        helpers.helperTimesUp.hide()
        clockButton.clearActions()
        clockButton.setScale(1.0f, 1.0f)
    }

    private fun startNextLevel() {
        val score = level.timingRequirements.getStarCount(recordTimer)

        if (currentLevel != 6) {
            game.nextLevel(currentLevel + 1, currentScore + score)
        } else {
            gameCompleteDialog.startAnimation(currentScore + score)
        }

    }
}

private fun Stage.resizeAll(width: Int, height: Int) {
    this.actors.filterIsInstance<Resizeable>().forEach { it.resize(width, height) }
}
