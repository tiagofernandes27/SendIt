package com.project.sendit

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


class GameView : SurfaceView, Runnable {
    var playing = false
    lateinit var gameThread : Thread
    var canvas : Canvas? = null
    lateinit var paint: Paint

    lateinit var paintCol: Paint
    var screenWidth : Int = 0
    var screenHeight : Int = 0

    private lateinit var mDbRef : FirebaseDatabase

    lateinit var chatActivity : ChatActivity

    private var xCoordinate = 0f
    private var yCoordinate = 0f

    var circleSprite = CircleSprite()

    var circlePrevX = 0f
    var circlePrevY = 0f

    var squareSprites = arrayListOf<SquareSprite>()

    var triangleSprites = arrayListOf<TriangleSprite>()

    var starSprite = StarSprite()

    // the size of each square in the grid
    val squareSize = 150

    // the number of rows and columns in the grid
    val rows = 7
    val cols = 7

    var gridWidth = cols * squareSize
    var gridHeight = rows * squareSize

    // the position of the grid on the canvas
    var gridX = 0f
    var gridY = 0f

    var gridSquares = ArrayList<Square>()

    var currentGridSquare = Square(0f,0f)

    var fileName = "Levels"

    var levels = ArrayList<ArrayList<Int>>()
    var currentLevel = 1

    var totalScore = 0

    class Square(val x: Float, val y: Float)

    constructor(context: Context?, screenWidth : Int, screenHeight:Int) : super(context) {
        init( context, screenWidth, screenHeight )
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init (context : Context?, screenWidth : Int, screenHeight:Int ) {
        paint = Paint()
        paintCol = Paint()
        paintCol.style = Paint.Style.STROKE
        paintCol.strokeWidth = 4.0f
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        mDbRef = Firebase.database("https://chat-b06e6-default-rtdb.europe-west1.firebasedatabase.app/")

        chatActivity = ChatActivity()

        currentLevel = 1

        squareSprites.clear()
        triangleSprites.clear()
        starSprite.hide = true
        circleSprite.direction = "none"
        circleSprite.isMoving = false
        circleSprite.score = 0

        grid()
        levelManager()
        changeLevel()
    }

    fun levelManager(){
        val file = context.assets.open(fileName)
        val fileReader = BufferedReader(InputStreamReader(file))
        var level = ArrayList<Int>()
        var line:String? = fileReader.readLine()
        while(line != null){
            if(line == "{"){
                level = ArrayList<Int>()
            }
            else if(line == "}"){
                levels.add(level)
            }else {
                var rowElem = line.split(";")
                for(elem in rowElem){
                    level.add(elem.toInt())
                }
            }
            line = fileReader.readLine()
        }
        fileReader.close()
        println(levels)
    }

    fun changeLevel(){
        totalScore += circleSprite.score

        mDbRef.getReference("scores").child(chatActivity.senderUid!!).child("score").push()
            .setValue(totalScore)

        squareSprites.clear()
        triangleSprites.clear()
        starSprite.hide = true
        circleSprite.direction = "none"
        circleSprite.isMoving = false
        circleSprite.score = 0

        spawnSquare()
        spawnCircle()
        spawnTriangle()
        spawnStar()
    }


    // Function that returns true if there is a square sprite in the square
    fun squareSpriteInSquare(square: Square) : Boolean {
        for (s in squareSprites) {
            if (s.x in square.x..square.x + squareSize && s.y in square.y..square.y + squareSize) {
                return true
            }
        }
        return false
    }

    // Function that spawns the squares sprites
    fun spawnSquare() {
        var i = 0

        for (l in levels[currentLevel - 1]) {
            if (l == 2){
                val squareSprite = SquareSprite()

                squareSprite.x = gridSquares[i].x + squareSize/2
                squareSprite.y = gridSquares[i].y  + squareSize/2

                squareSprites.add(squareSprite)
            }

            i++
        }
    }

    // Function that stores the squares of the grid
    fun grid() {
        gridX = (screenWidth / 2) - (cols * squareSize / 2f)
        gridY = (screenHeight / 2) - (rows * squareSize / 2f)

        // Create and store the squares of the grid
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = gridX + col * squareSize
                val y = gridY + row * squareSize
                val square = Square(x, y)
                gridSquares.add(square)
            }
        }
    }

    // Function that spawns the circle sprite
    fun spawnCircle() {
        var i = 0

        for (l in levels[currentLevel - 1]) {
            if (l == 1){
                circleSprite.x = gridSquares[i].x + squareSize/2
                circleSprite.y = gridSquares[i].y + squareSize/2
            }

            i++
        }
    }

    // Function that spawns the triangles sprites
    fun spawnTriangle() {
        var i = 0

        for (l in levels[currentLevel - 1]) {
            if (l == 3 || l == 31 || l == 32 || l == 33 || l == 34){
                val triangleSprite = TriangleSprite()

                triangleSprite.x = gridSquares[i].x + squareSize/2
                triangleSprite.y = gridSquares[i].y + squareSize/2

                if (l == 31)
                    triangleSprite.vx = triangleSprite.speed
                if (l == 32)
                    triangleSprite.vx = -triangleSprite.speed
                if (l == 33)
                    triangleSprite.vy = triangleSprite.speed
                if (l == 34)
                    triangleSprite.vy = -triangleSprite.speed

                triangleSprites.add(triangleSprite)
            }

            i++
        }
    }

    // Function that spawns the star sprite
    fun spawnStar() {
        var i = 0

        for (l in levels[currentLevel - 1]) {
            if (l == 4){
                starSprite.x = gridSquares[i].x + squareSize/2
                starSprite.y = gridSquares[i].y + squareSize/2
            }

            i++
        }
    }

    override fun run() {
        while (playing){
            update()
            draw()
            control()
        }
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread.start()
    }

    fun pause() {
        playing = false
        gameThread.join()
    }

    fun update() {
        squareUpdate()
        circleSprite.update()
        gridCollision()
        triangleUpdate()
        starUpdate()
    }

    fun squareUpdate(){
        for (s in squareSprites) {
            s.update()
            if (s.detectCollision.intersect(circleSprite.detectCollision)) {
                val relativeX = circleSprite.x - s.x
                val relativeY = circleSprite.y - s.y
                if (abs(relativeX) > abs(relativeY)) {
                    // collision from left or right
                    if (relativeX < 0) {
                        currentGridSquare = findSquare(s.x - squareSize/2, s.y)
                    } else {
                        currentGridSquare = findSquare(s.x + squareSize, s.y)
                    }
                } else {
                    // collision from top or bottom
                    if (relativeY < 0) {
                        currentGridSquare = findSquare(s.x, s.y - squareSize/2)
                    } else {
                        currentGridSquare = findSquare(s.x, s.y + squareSize)
                    }
                }
                circlePrevX = currentGridSquare.x + squareSize/2
                circlePrevY = currentGridSquare.y + squareSize/2
                circleSprite.x = circlePrevX
                circleSprite.y = circlePrevY
                circleSprite.setDirectionCircle("none")
                circleSprite.isMoving = false

                break
            }
            for (t in triangleSprites){
                if (s.detectCollision.intersect(t.detectCollision)) {
                    val relativeX = t.x - s.x
                    val relativeY = t.y - s.y
                    if (abs(relativeX) > abs(relativeY)) {
                        // collision from left or right
                        t.vx *= -1
                    } else {
                        t.vy *= -1
                    }

                    break
                }
            }
        }
    }

    fun gridCollision(){
        var nextSquare = Square(0f,0f)
        if (circleSprite.x > gridX + gridWidth) {
            nextSquare = findSquare(gridX, circleSprite.y)
            if(!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize }){
                circleSprite.x = gridX
            }else{
                circleSprite.x = gridX + gridWidth - squareSize/2
                circleSprite.direction = "none"
                circleSprite.isMoving = false
            }
        } else if (circleSprite.x < gridX) {
            nextSquare = findSquare(gridX + gridWidth, circleSprite.y)
            if(!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize  }){
                circleSprite.x = gridX + gridWidth
            }else{
                circleSprite.x = gridX + squareSize/2
                circleSprite.direction = "none"
                circleSprite.isMoving = false
            }
        }
        if (circleSprite.y > gridY + gridHeight) {
            nextSquare = findSquare(circleSprite.x, gridY)
            if (!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize  }){
                circleSprite.y = gridY
            }else{
                circleSprite.y = gridY + gridHeight - squareSize/2
                circleSprite.direction = "none"
                circleSprite.isMoving = false
            }
        } else if (circleSprite.y < gridY) {
            nextSquare = findSquare(circleSprite.x, gridY + gridHeight)
            if(!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize  }){
                circleSprite.y = gridY + gridHeight
            }else{
                circleSprite.y = gridY + squareSize/2
                circleSprite.direction = "none"
                circleSprite.isMoving = false
            }
        }

        for (t in triangleSprites){
            if (t.x > gridX + gridWidth) {
                nextSquare = findSquare(gridX, t.y)
                if(!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize }){
                    t.x = gridX
                }else{
                    t.vx *= -1
                }
            } else if (t.x < gridX) {
                nextSquare = findSquare(gridX + gridWidth, t.y)
                if(!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize  }){
                    t.x = gridX + gridWidth
                }else{
                    t.vx *= -1
                }
            }
            if (t.y > gridY + gridHeight) {
                nextSquare = findSquare(t.x, gridY)
                if (!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize  }){
                    t.y = gridY
                }else{
                    t.vy *= -1
                }
            } else if (t.y < gridY) {
                nextSquare = findSquare(t.x, gridY + gridHeight)
                if(!squareSprites.any { it.x in nextSquare.x..nextSquare.x + squareSize && it.y in nextSquare.y..nextSquare.y + squareSize  }){
                    t.y = gridY + gridHeight
                }else{
                    t.vy *= -1
                }
            }
        }
    }

    fun triangleUpdate(){
        for (t in triangleSprites) {
            if (!t.hide){
                t.update()

                if (t.detectCollision.intersect(circleSprite.detectCollision))
                {
                    t.hide = true
                    circleSprite.score++
                }
            }
        }
    }

    fun starUpdate(){
        if (circleSprite.score == triangleSprites.size)
            starSprite.hide = false

        if (!starSprite.hide){
            starSprite.update()

            if (starSprite.detectCollision.intersect(circleSprite.detectCollision)) {
                circleSprite.score += 2

                if (currentLevel < levels.size) {
                    currentLevel++
                    changeLevel()
                }else{
                    currentLevel = 1
                    changeLevel()
                }
            }
        }
    }

    private fun findSquare(x: Float, y: Float): Square {
        for (square in gridSquares) {
            if (x in square.x..square.x + squareSize && y in square.y..square.y + squareSize) {
                return square
            }
        }
        return Square(0f, 0f)
    }


    fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas?.drawColor(Color.BLACK)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4.0f

            squareDraw()
            circleDraw()

            //Debug Purpose
            //gridDraw()

            triangleDraw()

            starDraw()

            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun squareDraw(){
        for (s in squareSprites){
            paint.color = s.color

            val left = s.x - s.radius
            val top = s.y - s.radius
            val right = s.x + s.radius
            val bottom = s.y + s.radius
            canvas?.drawRect(left, top, right, bottom, paint)
        }
    }

    fun circleDraw(){
        paint.color = Color.BLUE
        canvas?.drawCircle(circleSprite.x, circleSprite.y, circleSprite.radius, paint) //draw the CircleSprite

    }

    //To be removed or commented
    fun gridDraw(){
        paint.color = Color.RED
        // Draw the squares of the grid using the stored data (To be removed)
        for (square in gridSquares) {
            canvas?.drawRect(square.x, square.y, square.x + squareSize, square.y + squareSize, paint)
        }
    }

    fun triangleDraw(){
        paint.color = Color.RED
        for (t in triangleSprites){
            if (!t.hide){
                val path = Path()

                path.moveTo(t.x - t.radius, t.y + t.radius) // move to top
                path.lineTo(t.x + t.radius, t.y + t.radius) // bottom right
                path.lineTo(t.x, t.y - t.radius) // bottom left
                path.lineTo(t.x - t.radius, t.y + t.radius) // back to top
                canvas?.drawPath(path, paint)
            }
        }
    }

    fun starDraw(){
        paint.color = Color.WHITE
        if (!starSprite.hide){
            val path = Path()
            var angle = starSprite.angle
            val ratio = starSprite.ratio
            var outerRadius = starSprite.outerRadius
            val innerRadius = outerRadius / ratio

            var x = starSprite.x
            var y = starSprite.y

            path.moveTo(
                (x + innerRadius * cos(0.0)).toFloat(),
                (y + innerRadius * sin(0.0)).toFloat()
            )
            for (i in 0 until 5) {
                path.lineTo(
                    (x + outerRadius * cos(angle * (i * 2 + 1))).toFloat(),
                    (y + outerRadius * sin(angle * (i * 2 + 1))).toFloat()
                )
                path.lineTo(
                    (x + innerRadius * cos(angle * (i * 2 + 2))).toFloat(),
                    (y + innerRadius * sin(angle * (i * 2 + 2))).toFloat()
                )
            }
            path.close()
            canvas?.drawPath(path, paint)

            // Add this block to draw pentagon inside star
            val pentagonPath = Path()
            pentagonPath.moveTo(
                (x + innerRadius * cos(0.0)).toFloat(),
                (y + innerRadius * sin(0.0)).toFloat()
            )
            for (i in 0 until 5) {
                pentagonPath.lineTo(
                    (x + innerRadius * cos(angle * (i * 2 + 2))).toFloat(),
                    (y + innerRadius * sin(angle * (i * 2 + 2))).toFloat()
                )
            }
            pentagonPath.close()
            canvas?.drawPath(pentagonPath, paint)
        }
    }

    fun control() {
        Thread.sleep(17)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when(it.action.and(MotionEvent.ACTION_MASK)){
                MotionEvent.ACTION_UP ->{
                    if (!circleSprite.isMoving){
                        val dx = event.x - xCoordinate
                        val dy = event.y - yCoordinate
                        if (abs(dx) > abs(dy)) {
                            // Horizontal swipe
                            if (dx > 0) {
                                // Swipe to the right
                                circleSprite.setDirectionCircle("right")
                            } else {
                                // Swipe to the left
                                circleSprite.setDirectionCircle("left")
                            }
                        } else {
                            // Vertical swipe
                            if (dy > 0) {
                                // Swipe down
                                circleSprite.setDirectionCircle("down")
                            } else {
                                // Swipe up
                                circleSprite.setDirectionCircle("up")
                            }
                        }

                        circleSprite.isMoving = true
                    }

                }
                MotionEvent.ACTION_DOWN ->{
                    xCoordinate = event.x
                    yCoordinate = event.y
                }
            }
        }
        return true
    }
}