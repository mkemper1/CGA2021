package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.labyrinth.Labyrinth
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.*
import org.joml.Math.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    /** shader */
    private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram

    /** placeholder */
    private val phVector3f = Vector3f(0f, 0f, 0f)

    /** Renderables */
    private val objList = mutableListOf<Renderable>()
    private val walls = mutableListOf<Renderable>()
    private val wallsBig = mutableListOf<Renderable>()
    private val pillars = mutableListOf<Renderable>()
    private val pillarsBig = mutableListOf<Renderable>()
    private val buttons = mutableListOf<Renderable>()
    private val buttonBases = mutableListOf<Renderable>()
    private val gateDoors = mutableListOf<Renderable>()
    private val dungeonWalls = mutableListOf<Renderable>()
    private var skyBox : Renderable
    private var gate : Renderable
    private var doorCameraObject : Renderable
    private var floor : Renderable
    private var monster : Renderable
    private var spawn : Renderable
    private var player : Renderable
    private var portal : Renderable
    private var gameOverCameraObject : Renderable
    private var dungeonFloor : Renderable

    private val buttonPressed = mutableListOf(0, 0, 0)

    /** hitboxes */
    private val allHitboxes = mutableListOf<MutableList<Float>>()
    private val wallVerticalHitbox = mutableListOf( 0.5f, 4.0f )
    private val wallHorizontalHitbox = mutableListOf( 4f, 0.5f )
    private val pillarHitbox = mutableListOf( 1f, 1f )
    private val buttonHitbox = mutableListOf( 0.5f, 0.5f )
    private val gateDoorHorizontalHitbox = mutableListOf( 1.5f, 0.3f )
    private val gateDoorVerticalHitbox = mutableListOf( 0.3f, 1.5f )
    private val monsterHitbox = mutableListOf( 2f, 2f)
    private val playerHitbox = mutableListOf(1f, 1f)
    private val portalHorizontalHitbox = mutableListOf( 4f, 0.2f )
    private val portalVerticalHitbox = mutableListOf( 0.2f, 4f )

    /** lights */
    private val pointLight : PointLight
    private val spotLight: SpotLight
    private var notFirstFrame = false
    private var oldMousePosX = 0.0
    private var oldMousePosY = 0.0

    /** player */
    private var flashlight = false
    private var speedMul = 1.3f
    private var playerSpeed: Float = 0f
    private var playerNormalSpeed = 0.06f
    private var playerSprintSpeed = playerNormalSpeed * speedMul

    /** Monster */
    private var catchEm = false
    private var flyRange = mutableListOf<Float>()

    /** labyrinth */
    private val run = Labyrinth()
    private val rnd = (4..4).random()
    private var buttonSpawn = mutableListOf<Vector3f>()
    private var exitSpawn = mutableListOf<Vector3f>()

    /** camera */
    private val camera = TronCamera()
    private var cameraStatus = 0
    private var cameraPosition = mutableListOf(phVector3f, phVector3f, phVector3f, phVector3f, phVector3f)

    private var gameOverRotatePoint: Vector3f = phVector3f

    private val ghostWalkSpeed = 0.04f
    private val ghostCornerSpeed = 0.006f

    /** scene build */
    init {
        staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        glClearColor(0.0f, 0.0f, 1f, 1.0f); GLError.checkThrow()

        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        var x = 0

        /** walls */
        while (x < 196) {
            walls.add(ModelLoader.loadModel("assets/NewWall/NewNewWall/LabWall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (wall in walls) {
            wall.scaleLocal(Vector3f(0.5f))
            objList.add(wall)
        } //objList: 0 - 195

        /** wallsBig */
        x = 0
        while (x < 72) {
            wallsBig.add(ModelLoader.loadModel("assets/NewWall/NewNewWall/LabWallBig.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (wallBig in wallsBig) {
            wallBig.scaleLocal(Vector3f(0.5f))
            objList.add(wallBig)
        } //objList: 196 - 267

        /** pillars */
        x = 0
        while (x < 196) {
            pillars.add(ModelLoader.loadModel("assets/NewWall/pillar.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (pillar in pillars) {
            pillar.scaleLocal(Vector3f(0.58f))
            objList.add(pillar)
        } //objList: 268 - 463

        /** pillarsBig */
        x = 0
        while (x < 4) {
            pillarsBig.add(ModelLoader.loadModel("assets/NewWall/pillarBig.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (pillarBig in pillarsBig) {
            pillarBig.scaleLocal(Vector3f(0.58f))
            objList.add(pillarBig)
        } //objList: 464 - 467

        /** buttons */
        x = 0
        while (x < 3) {
            buttons.add(ModelLoader.loadModel("assets/Button/Button.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (button in buttons) {
            objList.add(button)
        } //objList: 468 - 470

        /** buttonBase */
        x = 0
        while (x < 3) {
            buttonBases.add(ModelLoader.loadModel("assets/Button/ButtonBase.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (buttonBase in buttonBases) {
            objList.add(buttonBase)
        } //objList: 471 - 473

        /** gateDoors */
        x = 0
        while (x < 2) {
            gateDoors.add(ModelLoader.loadModel("assets/Gate/GateDoor.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (gateDoor in gateDoors) {
            gateDoor.scaleLocal(Vector3f(0.5f))
            objList.add(gateDoor)
        } //objList: 474 - 475

        /** monster */
        monster = ModelLoader.loadModel("assets/Monster/OogieBoogie.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        monster.scaleLocal(Vector3f(0.015f))
        flyRange = run.flyRange(monster, 1.5f, 0.5f)
        objList.add(monster) //objList: 476

        /** player */
        player = ModelLoader.loadModel("assets/Player/playerObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        player.translateLocal(Vector3f(4f, 0.0f, 4.0f))
        player.rotateLocal(0f, toRadians(-90f), 0f)
        objList.add(player) //objList: 477

        /** gate */
        gate = ModelLoader.loadModel("assets/Gate/Gate.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        gate.scaleLocal(Vector3f(0.5f))
        objList.add(gate) //objList: 478

        /** floor */
        floor = ModelLoader.loadModel("assets/NewWall/NewFloor/floor.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        floor.scaleLocal(Vector3f(0.5f))
        floor.translateGlobal(Vector3f(60f, 0f, 60f))
        objList.add(floor) //objList: 479

        /** skyBox */
        skyBox = ModelLoader.loadModel("assets/SkyBox/skyBox(night)2.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        skyBox.translateGlobal(Vector3f(60f, 0f, 60f))
        skyBox.scaleLocal(Vector3f(13f))
        skyBox.rotateLocal(0f, 0f, toRadians(90f))
        objList.add(skyBox) //objList: 480

        /** spawn */
        spawn = ModelLoader.loadModel("assets/CubeObject/cubeObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        objList.add(spawn) //objList: 481

        /** portal */
        portal = ModelLoader.loadModel("assets/Portal/Portal.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        portal.scaleLocal(Vector3f(0.5f))
        objList.add(portal) //objList: 482

        /** gameOverCamera */
        gameOverCameraObject = ModelLoader.loadModel("assets/CubeObject/cubeObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        gameOverCameraObject.translateGlobal(Vector3f(60f, 40f, 60f))
        gameOverRotatePoint = gameOverCameraObject.getWorldPosition()
        gameOverCameraObject.translateGlobal(Vector3f(-79f, 0f, 0f))
        gameOverCameraObject.rotateLocal(0f, toRadians(-90f), 0f)
        objList.add(gameOverCameraObject) //objList: 483

        /** doorCamera */
        doorCameraObject = ModelLoader.loadModel("assets/CubeObject/cubeObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        doorCameraObject.translateLocal(Vector3f(0f, 6f, .0f))
        objList.add(doorCameraObject) //objList: 484

        /** dungeonWalls */
        x = 0
        while (x < 4) {
            dungeonWalls.add(ModelLoader.loadModel("assets/NewWall/NewNewWall/LabWallBig.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }
        for (dungeonWall in dungeonWalls) {
            dungeonWall.scaleLocal(Vector3f(0.5f))
            objList.add(dungeonWall)
        } //objList: 485 - 488

        /** dungeonFloor */
        dungeonFloor = ModelLoader.loadModel("assets/Dungeon/DungeonFloor.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        dungeonFloor.scaleLocal(Vector3f(0.5f))
        objList.add(dungeonFloor) //objList: 489

        /** camera */
        camera.translateGlobal(Vector3f(0f, 3f, 0f))
        cameraPosition[0] = camera.getPosition()
        camera.translateGlobal(Vector3f(0.2f, 0f, 0f))
        cameraPosition[1] = camera.getPosition()
        camera.translateGlobal(Vector3f(0.2f, 0f, 0f))
        cameraPosition[2] = camera.getPosition()
        camera.translateGlobal(Vector3f(-0.6f, 0f, 0f))
        cameraPosition[3] = camera.getPosition()
        camera.translateGlobal(Vector3f(-0.2f, 0f, 0f))
        cameraPosition[4] = camera.getPosition()
        camera.translateGlobal(Vector3f(0.4f, 0f, 0f))

        /** lights */
        pointLight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(0.5f, 0.2f, 0.1f), Vector3f(1f, 0.1f, 0.1f))
        spotLight = SpotLight(Vector3f(0.5f, -1f, -2f), Vector3f(0.0f,0.0f,0.0f), Vector3f(0.1f, 0.01f, 0.01f), Vector2f(toRadians(15f), toRadians(25f)))
        spotLight.rotateLocal(0f, 0.05f, 0f)
        spotLight.rotateLocal(toRadians(-5f), PI.toFloat(),0f)
        spotLight.parent = camera
        pointLight.parent = monster

        /** allHitboxes */
        allHitboxes.add(wallHorizontalHitbox)
        allHitboxes.add(wallVerticalHitbox)
        allHitboxes.add(pillarHitbox)
        allHitboxes.add(buttonHitbox)
        allHitboxes.add(gateDoorHorizontalHitbox)
        allHitboxes.add(gateDoorVerticalHitbox)
        allHitboxes.add(monsterHitbox)
        allHitboxes.add(playerHitbox)
        allHitboxes.add(portalHorizontalHitbox)
        allHitboxes.add(portalVerticalHitbox)

        /** essentials */
        run.labyrinthInformation(objList, allHitboxes, camera, ghostWalkSpeed, ghostCornerSpeed)

        /** random elements */
        buttonSpawn = run.buttonSpawn(rnd)
        exitSpawn = run.doorSpawn(rnd)
        run.wayOfDeathAndDecay(rnd)

        /** labyrinth */
        run.buildLabyrith(walls, wallsBig, pillars, pillarsBig)

    }

    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        tronShader.use()

        camera.bind(tronShader)
        spotLight.bind(tronShader, "spot", camera.getCalculateViewMatrix())
        pointLight.bind(tronShader, "point")

        tronShader.setUniform("farbe", Vector3f(0.15f,0.15f,0.15f))

        for (obj in objList) {
            if (obj != player && obj != spawn && obj != gameOverCameraObject && obj != doorCameraObject) obj.render(tronShader)
        }

    }

    fun update(dt: Float, t: Float) {

        when {
            /** Player-Movement */
            window.getKeyState(GLFW_KEY_D) -> {
                if(!strafe('D')) {
                    sprint()
                    playerMovement()
                    for (obj in objList) {
                        run.collision(player, obj, "solid", playerSpeed)
                        run.collision(portal, player, "gameOver", playerSpeed)
                    }
                    player.translateLocal(Vector3f(playerSpeed, 0.0f, 0.0f))
                }
            }

            window.getKeyState(GLFW_KEY_S) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {
                sprint()
                playerMovement()
                for (obj in objList) {
                    run.collision(player, obj, "solid", playerSpeed)
                    run.collision(portal, player, "gameOver", playerSpeed)
                }
                player.translateLocal(Vector3f(0.0f, 0.0f, playerSpeed))
            }

            window.getKeyState(GLFW_KEY_W) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {
                sprint()
                playerMovement()
                for (obj in objList) {
                    // run.collision(player, obj, "solid", playerSpeed)
                    run.collision(portal, player, "gameOver", playerSpeed)
                }
                player.translateLocal(Vector3f(0.0f, 0.0f, -playerSpeed))
            }

            window.getKeyState(GLFW_KEY_A) -> {
                if(!strafe('A')) {
                    sprint()
                    playerMovement()
                    for (obj in objList) {
                        run.collision(player, obj, "solid", playerSpeed)
                        run.collision(portal, player, "gameOver", playerSpeed)
                    }
                    player.translateLocal(Vector3f(-playerSpeed, 0.0f, 0.0f))
                }
            }

            !window.getKeyState(GLFW_KEY_W) && !window.getKeyState(GLFW_KEY_A) && !window.getKeyState(GLFW_KEY_S) && !window.getKeyState(GLFW_KEY_D) -> {
                if (camera.getPosition().x < cameraPosition[0].x - 0.001) camera.translateLocal(Vector3f(0.02f, 0f, 0f))
                if (camera.getPosition().x > cameraPosition[0].x + 0.001) camera.translateLocal(Vector3f(-0.02f, 0f, 0f))
                if (camera.getPosition().y < cameraPosition[0].y - 0.001) camera.translateLocal(Vector3f(0.0f, 0.02f, 0f))
                if (camera.getPosition().y > cameraPosition[0].y + 0.001) camera.translateLocal(Vector3f(0f, -0.02f, 0f))
            }

        }

        /** Button */
        run.buttonMovement(buttonPressed, buttonSpawn)

        /** Gate */
        if (run.buttonStatus(exitSpawn)) catchEm = true

        /** Monster */
        if(catchEm) run.monsterLetLoose(rnd)
        run.monsterMovement(monster, flyRange)
        if(flashlight) run.collision(monster, player, "dead", playerSpeed)

        gameOverCameraObject.rotateAroundPoint(0f, 0.001f, 0f, gameOverRotatePoint)

    }

    fun playerMovement() {

        if(!window.getKeyState(GLFW_KEY_LEFT_SHIFT)) {
            if (cameraStatus == 0 || cameraStatus == 1) {
                camera.rotateAroundPoint(0f, 0f, 0.065f, cameraPosition[1])
                camera.rotateAroundPoint(0f, 0f, -0.065f, camera.getPosition())
            }

            if (cameraStatus == 2 || cameraStatus == 3) {
                camera.rotateAroundPoint(0f, 0f, -0.065f, cameraPosition[3])
                camera.rotateAroundPoint(0f, 0f, 0.065f, camera.getPosition())
            }

            if (cameraStatus == 0 && camera.getPosition().y > cameraPosition[0].y - 0.001 && camera.getPosition().x > cameraPosition[1].x) cameraStatus = 1
            if (cameraStatus == 1 && camera.getPosition().y < cameraPosition[0].y + 0.001 && camera.getPosition().x < cameraPosition[1].x) cameraStatus = 2
            if (cameraStatus == 2 && camera.getPosition().y > cameraPosition[0].y - 0.001 && camera.getPosition().x < cameraPosition[3].x) cameraStatus = 3
            if (cameraStatus == 3 && camera.getPosition().y < cameraPosition[0].y + 0.001 && camera.getPosition().x > cameraPosition[3].x) cameraStatus = 0
        } else {
            if (cameraStatus == 0 || cameraStatus == 1) {
                camera.rotateAroundPoint(0f, 0f, 0.065f * speedMul, cameraPosition[1])
                camera.rotateAroundPoint(0f, 0f, -0.065f * speedMul, camera.getPosition())
            }

            if (cameraStatus == 2 || cameraStatus == 3) {
                camera.rotateAroundPoint(0f, 0f, -0.065f * speedMul, cameraPosition[3])
                camera.rotateAroundPoint(0f, 0f, 0.065f * speedMul, camera.getPosition())
            }

            if (cameraStatus == 0 && camera.getPosition().y > cameraPosition[0].y - 0.001 && camera.getPosition().x > cameraPosition[1].x) cameraStatus = 1
            if (cameraStatus == 1 && camera.getPosition().y < cameraPosition[0].y + 0.001 && camera.getPosition().x < cameraPosition[1].x) cameraStatus = 2
            if (cameraStatus == 2 && camera.getPosition().y > cameraPosition[0].y - 0.001 && camera.getPosition().x < cameraPosition[3].x) cameraStatus = 3
            if (cameraStatus == 3 && camera.getPosition().y < cameraPosition[0].y + 0.001 && camera.getPosition().x > cameraPosition[3].x) cameraStatus = 0
        }

    }

    fun sprint() { playerSpeed = if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) playerSprintSpeed else playerNormalSpeed }

    fun strafe(button: Char): Boolean {

        when(button) {
            'A' -> {
                if (window.getKeyState(GLFW_KEY_S)) {
                    playerMovement()
                    for (obj in objList) {
                        run.collision(player, obj, "solid", playerSpeed)
                        run.collision(player, obj, "solid", playerSpeed)
                    }
                    player.translateLocal(Vector3f(-playerSpeed, 0.0f, playerSpeed))

                } else if (window.getKeyState(GLFW_KEY_W)) {
                    playerMovement()
                    for (obj in objList) {
                        run.collision(player, obj, "solid", playerSpeed)
                        run.collision(player, obj, "solid", playerSpeed)
                    }
                    player.translateLocal(Vector3f(-playerSpeed, 0.0f, -playerSpeed))
                } else return false
            }
            'D' -> {
                if (window.getKeyState(GLFW_KEY_S)) {
                    playerMovement()
                    for (obj in objList) {
                        run.collision(player, obj, "solid", playerSpeed)
                        run.collision(player, obj, "solid", playerSpeed)
                    }
                    player.translateLocal(Vector3f(playerSpeed, 0.0f, playerSpeed))

                } else if (window.getKeyState(GLFW_KEY_W)) {
                    playerMovement()
                    for (obj in objList) {
                        run.collision(player, obj, "solid", playerSpeed)
                        run.collision(player, obj, "solid", playerSpeed)
                    }
                    player.translateLocal(Vector3f(playerSpeed, 0.0f, -playerSpeed))
                } else return false
            }
        }
        return true

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

        if(run.buttonPressRange(player, objList[468]) && window.getKeyState(GLFW_KEY_E) && buttonPressed[0] == 0) {
            buttonPressed[0] = 1
        }
        if(run.buttonPressRange(player, objList[469]) && window.getKeyState(GLFW_KEY_E) && buttonPressed[1] == 0) {
            buttonPressed[1] = 1
        }
        if(run.buttonPressRange(player, objList[470]) && window.getKeyState(GLFW_KEY_E) && buttonPressed[2] == 0) {
            buttonPressed[2] = 1
        }
        if(window.getKeyState(GLFW_KEY_L)) {
            catchEm = true
        }

        if (window.getKeyState(GLFW_KEY_F) && flashlight) {
            spotLight.lightColor = Vector3f(0f, 0f, 0f)
            flashlight = false
        } else if (window.getKeyState(GLFW_KEY_F) && !flashlight) {
            spotLight.lightColor = Vector3f(0.5f, 0.5f, 0.5f)
            flashlight = true
        }

    }

    fun onMouseMove(xpos: Double, ypos: Double) {

        val deltaX = xpos - oldMousePosX
        val deltaY = ypos - oldMousePosY
        oldMousePosX = xpos
        oldMousePosY = ypos

        /** Camera 1 */
        if(notFirstFrame) {
            /** left and right */
            player.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)
            /** up and down */
            camera.rotateLocal(toRadians(deltaY.toFloat() * -0.05f), 0f, 0f)
            player.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)

            /** y-range limitation */
            if(1.3f < (camera.getYAxis().angle(player.getYAxis()))) {
                camera.rotateLocal(toRadians(deltaY.toFloat() * 0.05f), 0f, 0f)
                player.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)
            }

        }
        notFirstFrame = true

    }

    fun cleanup() {}

}
