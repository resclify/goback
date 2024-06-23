package com.goback.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.box2d.box
import ktx.collections.gdxArrayOf

class Level1(private val world: World) : Level() {

    override val timingRequirements = TimingRequirements(10f, 7.5f, 5f, 20f)

    override val startPosX = 5f
    override val startPosY = 10f
    override val realStartPosY = 5.45f
    override var tutorialSwitchActivated = false

    private val platformTexture = Texture("sprites/platform1.png")
    private val platformHorizontalTexture = Texture("sprites/platform1_horizontal.png")
    private val switchTexture = Texture("sprites/switch1.png")
    private val houseTexture = Texture("sprites/house2.png")
    private val doorTexture = Texture("sprites/door1.png")
    private val starTexture = Texture("sprites/star1.png")
    private val flagTexture = Texture("sprites/flag1.png")

    private val groundTexture = TextureRegion(Texture("sprites/groundTexture3.png").apply {
        setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })

    private val groundSprite = Sprite(groundTexture)


    private val renderObjects = gdxArrayOf<DrawableGameObject>()

    private val cageDoor = world.body(BodyDef.BodyType.KinematicBody) {
        position.set(60f, 7f)
        box(1f, 10f)
    }

    val ground = world.body {
        position.set(5f, 3f)
        box(500f, 0f) {
            friction = 0.1f

            filter.categoryBits = CategoryBits.GROUND
            //density = 10.0f
        }
    }

    init {
        addPlatform(25f, 10f)
        addPlatform(40f, 17f)
        addPlatform(55f, 24f)
        addPlatform(35f, 30f)
        addPlatform(35f, 40f)

        val horizontalPlatform = world.body {
            position.set(30f, 35f)
            box(1f, 10f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }
        renderObjects.add(BodySprite(horizontalPlatform, 1f, 10f, platformHorizontalTexture))

        val switch = world.body {
            position.set(35f, 31f)
            box(3f, 1f) {
                isSensor = true
                filter.categoryBits = CategoryBits.SWITCH
                userData = 1
            }
        }
        renderObjects.add(BodySprite(switch, 3f, 1f, switchTexture))
        renderObjects.add(BodySprite(cageDoor, 1f, 10f, doorTexture))

        val endCage = world.body {
            position.set(70f, 8f)
            box(25f, 3f, Vector2(0f, 3.5f))
            box(5f, 10f, Vector2(10f, -3f))
        }
        renderObjects.add(BodySprite(endCage, 25f, 10f, houseTexture))

        val startingZone = world.body {
            position.set(startPosX, realStartPosY)
            box(4f, 15f) {
                friction = 0.1f
                isSensor = true
                filter.categoryBits = CategoryBits.START_ZONE
            }
        }

        val finishZone = world.body {
            position.set(70f, 5f)
            box(2.0f, 4.5f) {
                friction = 0.1f
                isSensor = true
                filter.categoryBits = CategoryBits.END_ZONE
            }
        }
        renderObjects.add(BodySprite(finishZone, 4.57f, 4.57f, starTexture))
        renderObjects.add(BodySprite(startingZone, 20f / 7f, 64f / 7f, flagTexture))
    }

    private fun addPlatform(posX: Float, posY: Float) {
        val body = world.body {
            position.set(posX, posY)
            box(10f, 1f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }

        renderObjects.add(BodySprite(body, 10f, 1f, platformTexture))
    }


    override fun activateSwitch(switchNo: Int) {
        ktx.log.debug { "Switch $switchNo activated" }
        tutorialSwitchActivated = true
        cageDoor.linearVelocity = Vector2(0f, -10f)
    }

    override fun deactivateSwitch(switchNo: Int) {
        ktx.log.debug { "Switch $switchNo deactivated" }
        tutorialSwitchActivated = false
        cageDoor.linearVelocity = Vector2(0f, 10f)
    }

    override fun update() {
        if (cageDoor.position.y > 7f) {
            cageDoor.setTransform(cageDoor.position.x, 7f, 0f)
            cageDoor.linearVelocity = Vector2(0f, 0f)
        }
        if (cageDoor.position.y < -2f) {
            cageDoor.setTransform(cageDoor.position.x, -2f, 0f)
            cageDoor.linearVelocity = Vector2(0f, 0f)
        }
    }

    override fun resetLevel() {
        cageDoor.setTransform(60f, 7f, 0f)
        cageDoor.setLinearVelocity(0f, 0f)
    }

    override fun draw(spriteBatch: PolygonSpriteBatch) {
        for (levelObject in renderObjects) {
            levelObject.draw(spriteBatch)
        }

        groundSprite.setOriginCenter()
        groundSprite.setSize(100f, 100f)
        groundSprite.setPosition(-100f, -97f)
        groundSprite.draw(spriteBatch)
        groundSprite.setPosition(0f, -97f)
        groundSprite.draw(spriteBatch)
        groundSprite.setPosition(100f, -97f)
        groundSprite.draw(spriteBatch)
    }
}
