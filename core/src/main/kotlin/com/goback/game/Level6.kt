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

class Level6(private val world: World) : Level() {

    override val timingRequirements = TimingRequirements(20f, 15f, 10f, 35f)

    override val startPosX = 5f
    override val startPosY = 10f
    override val realStartPosY = 5.45f
    override var tutorialSwitchActivated = false

    private val platformTexture = Texture("sprites/platform1.png")
    private val platformHorizontalTexture = Texture("sprites/platform1_horizontal.png")
    private val switchTexture = Texture("sprites/switch1.png")
    private val houseTexture = Texture("sprites/house2.png")
    private val doorTexture = Texture("sprites/door1.png")
    private val doorTexture2 = Texture("sprites/door2.png")
    private val starTexture = Texture("sprites/star1.png")
    private val flagTexture = Texture("sprites/flag1.png")

    private val groundTexture = TextureRegion(Texture("sprites/groundTexture3.png").apply {
        setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })

    private val groundSprite = Sprite(groundTexture)


    private val renderObjects = gdxArrayOf<DrawableGameObject>()

    private val door1 = world.body(BodyDef.BodyType.KinematicBody) {
        box(1f, 15f)
        {
            filter.categoryBits = CategoryBits.GROUND
        }
    }.apply { renderObjects.add(BodySprite(this, 1f, 15f, doorTexture)) }

    private val door2 = world.body(BodyDef.BodyType.KinematicBody) {
        box(10f, 1f)
        {
            filter.categoryBits = CategoryBits.GROUND
        }
    }.apply { renderObjects.add(BodySprite(this, 10f, 1f, doorTexture2)) }

    val ground = world.body {
        position.set(5f, 3f)
        box(500f, 0f) {
            friction = 0.1f

            filter.categoryBits = CategoryBits.GROUND
            //density = 10.0f
        }
    }

    val switch1 = world.body {
        position.set(85f, 16f)
        box(3f, 1f) {
            isSensor = true
            filter.categoryBits = CategoryBits.SWITCH
            userData = 1
        }
    }.apply { renderObjects.add(BodySprite(this, 3f, 1f, switchTexture)) }

    val switch2 = world.body {
        position.set(35f, 3.5f)
        box(3f, 1f) {
            isSensor = true
            filter.categoryBits = CategoryBits.SWITCH
            userData = 2
        }
    }.apply { renderObjects.add(BodySprite(this, 3f, 1f, switchTexture)) }

    init {
        addPlatform(25f, 10f)
        addPlatform(35f, 15f)
        addPlatform(50f, 42f)
        //addPlatform(40f, 15f)
        //addPlatform(55f, 25f)
        addPlatform(75f, 15f)
        addPlatform(85f, 15f)
        addPlatform(95f, 15f)
        addPlatform(105f, 15f)
        addPlatform(115f, 8f)
        addPlatform(15f, 20f)
        addPlatform(5f, 25f)
        addPlatform(-10f, 20f)

        val horizontalPlatform = world.body {
            position.set(30f, 12.5f)
            box(1f, 5f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }.apply { renderObjects.add(BodySprite(this, 1f, 5f, platformHorizontalTexture)) }

        val horizontalPlatform2 = world.body {
            position.set(50f, 12.5f)
            box(1f, 25f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }.apply { renderObjects.add(BodySprite(this, 1f, 25f, platformHorizontalTexture)) }

        val horizontalPlatform3 = world.body {
            position.set(-5f, 25f)
            box(1f, 10f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }.apply { renderObjects.add(BodySprite(this, 1f, 10f, platformHorizontalTexture)) }

        val horizontalPlatform4 = world.body {
            position.set(-15f, 25f)
            box(1f, 10f) {
                friction = 0.1f
                filter.categoryBits = CategoryBits.GROUND
            }
        }.apply { renderObjects.add(BodySprite(this, 1f, 10f, platformHorizontalTexture)) }



        val startingZone = world.body {
            position.set(startPosX, realStartPosY)
            box(4f, 15f) {
                friction = 0.1f
                isSensor = true
                filter.categoryBits = CategoryBits.START_ZONE
            }
        }

        val finishZone = world.body {
            position.set(-10f, 22.5f)
            box(10.0f, 4.5f) {
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
        ktx.log.debug { "Activate Switch $switchNo" }

        if (switchNo == 1) {
            door2.linearVelocity = Vector2(-15f, 0f)
        } else {
            door1.linearVelocity = Vector2(0f, -15f)
        }


    }

    override fun deactivateSwitch(switchNo: Int) {
        ktx.log.debug { "Deactiave Switch $switchNo" }
        if (switchNo == 2) {
            door1.linearVelocity = Vector2(0f, 15f)
        }
    }

    override fun update() {
        if (door1.position.y > 22f) {
            door1.setTransform(door1.position.x, 22f, 0f)
            door1.linearVelocity = Vector2(0f, 0f)
        }
        if (door1.position.y < 22f - 15f) {
            door1.setTransform(door1.position.x, 22f - 15f, 0f)
            door1.linearVelocity = Vector2(0f, 0f)
        }
        if (door2.position.x < -20f) {
            door2.setTransform(-20f, door2.position.y, 0f)
            door2.linearVelocity = Vector2(0f, 0f)
        }
        //    door2.setTransform(door2.position.x, 22f, 0f)
        //    door2.linearVelocity = Vector2(0f, 0f)
        //}
        //if (door2.position.y < 22f - 15f) {
        //    door2.setTransform(door2.position.x, 22f - 15f, 0f)
        //    door2.linearVelocity = Vector2(0f, 0f)
        //}
    }

    override fun resetLevel() {
        door1.setTransform(40f, 22f, 0f)
        door1.setLinearVelocity(0f, 0f)

        door2.setTransform(-10f, 25f, 0f)
        door2.setLinearVelocity(0f, 0f)
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
