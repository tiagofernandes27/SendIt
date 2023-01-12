package com.project.sendit

import android.graphics.Color
import android.graphics.RectF
import java.util.*

class SquareSprite {

    var x = 0f
    var y = 0f

    var detectCollision = RectF()

    var radius = 60.0f

    var detection = radius + 12

    var color = Color.GREEN


    constructor() {

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