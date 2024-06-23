package com.goback.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body

class BodySprite(val body: Body, sizeX: Float, sizeY: Float, texture: Texture) : DrawableGameObject {

    private val sprite = Sprite(texture).apply {
        this.setSize(sizeX, sizeY)
        this.setOriginCenter()
    }

    override fun draw(batch: PolygonSpriteBatch) {
        sprite.transformFromBody(body)
        sprite.draw(batch)
    }
}
