package com.project.sendit

import android.graphics.RectF
import java.util.*

class CircleSprite {
    var x = 0f
    var y = 0f

    var vx = 0f
    var vy = 0f

    var detectCollision = RectF()

    var radius = 60.0f
    var direction = "none" // variable to store the current direction of movement

    var speed = 25f

    var score = 0

    var isMoving = false

    constructor() {

        RectF()
        detectCollision = RectF(
            x - radius,
            y - radius,
            x + radius,
            y + radius)
    }

    fun setDirectionCircle(direction : String) {
        this.direction = direction
    }

    fun updateDirection(){
        vx = 0f
        vy = 0f

        when (direction) {
            "right" -> vx = speed
            "left" -> vx = -speed
            "up" -> vy = -speed
            "down" -> vy = speed
            else -> { // default case: stop movement
                vx = 0f
                vy = 0f
            }
        }
    }

    fun update(){
        updateDirection()

        x += vx
        y += vy

        detectCollision.left = x - radius
        detectCollision.top = y - radius
        detectCollision.right = x + radius
        detectCollision.bottom = y + radius

    }
}