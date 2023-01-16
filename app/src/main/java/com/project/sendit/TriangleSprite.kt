package com.project.sendit

import android.graphics.RectF
import java.util.*

class TriangleSprite {
    var x = 0f
    var y = 0f

    var vx = 0f
    var vy = 0f

    var speed = 10f

    var detectCollision = RectF()

    var radius = 35.0f

    var hide = false

    constructor(squareSize : Float) {

        radius = squareSize / 4

        RectF()
        detectCollision = RectF(
            x - radius,
            y - radius,
            x + radius,
            y + radius)

    }


    fun update(){
        x += vx
        y += vy

        detectCollision.left = x - radius
        detectCollision.top = y - radius
        detectCollision.right = x + radius
        detectCollision.bottom = y + radius

    }
}