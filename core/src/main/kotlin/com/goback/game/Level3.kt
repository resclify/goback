package com.goback.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.body
import ktx.box2d.box
import ktx.collections.gdxArrayOf

class Level3(private val world: World) : Level() {

    override val timingRequirements = TimingRequirements(11f, 8f, 5f, 25f)

    override val startPosX = 5f
    override val startPosY = 10f
    override val realStartPosY = 5.45f
    override var tutorialSwitchActivated = false

    private val platformTexture = Texture("sprites/platform1.png")
    private val platformHorizontalTexture = Texture("sprites/platform1_horizontal.png")
    private val switchTexture = Texture("sprites/switch2.png")
    private val houseTexture = Texture("sprites/house2.png")
    private val doorTexture = Texture("sprites/door1.png")
    private val starTexture = Texture("sprites/star1.png")
    private val flagTexture = Texture("sprites/flag1.png")
    private val crateTexture = Texture("sprites/crate.png")

    private val groundTexture = TextureRegion(Texture("sprites/groundTexture3.png").apply {
        setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })

    private val groundSprite = Sprite(groundTexture)


    private val renderObjects = gdxArrayOf<DrawableGameObject>()

    private val crate1 = world.body(BodyDef.BodyType.DynamicBody) {
        position.set(25f, 15f)
        box(2.5f, 2.5f) {
            filter.categoryBits = CategoryBits.GROUND
        }
    }.apply { renderObjects.add(BodySprite(this, 2.5f, 2.5f, crateTexture)) }
    private val crate2 = world.body(BodyDef.BodyType.DynamicBody) {
        position.set(25f, 20f)
        box(2.5f, 2.5f) {
            filter.categoryBits = CategoryBits.GROUND
        }
    }.apply { renderObjects.add(BodySprite(this, 2.5f, 2.5f, crateTexture)) }

    val ground = world.body {
        position.set(5f, 3f)
        box(500f, 0f) {
            friction = 0.1f

            filter.categoryBits = CategoryBits.GROUND
            //density = 10.0f
        }
    }

    init {
        addPlatform(25f, 7.43f)
        addPlatform(35f, 7.42f)
        addPlatform(48.1f, 7.35f)
        addPlatform(58.1f, 7.35f)
        addPlatform(70f, 7.35f+5f+7f)
        addPlatform(80f, 7.35f+5f+7f)
        addPlatform(90f, 7.35f+5f+7f)

        val horizontalPlatform1 = world.body {
            position.set(20f, 5f)
            box(1f, 10f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }.apply { renderObjects.add(BodySprite(this, 1f, 10f, platformHorizontalTexture)) }
        val horizontalPlatform2 = world.body {
            position.set(20f, 5f)
            box(1f, 10f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }.apply { renderObjects.add(BodySprite(this, 1f, 10f, platformHorizontalTexture)) }

        val endCage = world.body {
            position.set(85f, 19.35f+5f)
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
            position.set(85f, 19f+3f)
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

    }

    override fun deactivateSwitch(switchNo: Int) {
    }

    override fun update() {

    }

    override fun resetLevel() {
        crate1.setTransform(25f, 15f, 0f)
        crate1.setLinearVelocity(0f, 0f)

        crate2.setTransform(25f, 20f, 0f)
        crate2.setLinearVelocity(0f, 0f)
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
