package com.project.sendit

import android.graphics.Color
import android.graphics.RectF
import java.util.*

class SquareSprite {

    var x = 0f
    var y = 0f

    var detectCollision = RectF()

    var radius = 60.0f

    var detection = 0f

    var color = Color.GREEN


    constructor(squareSize : Float) {

        radius = squareSize / 2 - 10
        detection = radius + radius/5

        RectF()
        detectCollision = RectF(
            x - detection,
            y - detection,
            x + detection,
            y + detection)
    }


    fun update(){
        detectCollision.left = x - detection
        detectCollision.top = y - detection
        detectCollision.right = x + detection
        detectCollision.bottom = y + detection

    }

}