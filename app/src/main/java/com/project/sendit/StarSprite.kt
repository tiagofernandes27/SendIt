package com.project.sendit

import android.graphics.RectF

class StarSprite {
    var x = 0f
    var y = 0f

    var vx = 0f
    var vy = 0f

    var detectCollision = RectF()

    var angle = Math.PI / 5f
    var ratio = 5 / 2
    var outerRadius = 35f
    var initialOuterRadius = outerRadius
    var radiusSpeed = 1f

    var squareSize = 0f

    var isRadiusTotal = false

    var hide = true

    constructor(squareSize : Float) {

        this.squareSize = squareSize

        outerRadius = squareSize / 4 - 10
        initialOuterRadius = outerRadius

        RectF()
        detectCollision = RectF(
            x - outerRadius,
            y - outerRadius,
            x + outerRadius,
            y + outerRadius)

    }


    fun update(){
        if (outerRadius == squareSize/2)
            isRadiusTotal = true

        if (outerRadius == initialOuterRadius)
            isRadiusTotal = false

        if (isRadiusTotal)
        {
            outerRadius -= radiusSpeed
        } else
        {
            outerRadius += radiusSpeed
        }

        detectCollision.left = x - outerRadius
        detectCollision.top = y - outerRadius
        detectCollision.right = x + outerRadius
        detectCollision.bottom = y + outerRadius

    }
}