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
    private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram

    private val objList = mutableListOf<Renderable>()
    private val walls = mutableListOf<Renderable>()
    private val wallsBig = mutableListOf<Renderable>()
    private val pillars = mutableListOf<Renderable>()
    private val pillarsBig = mutableListOf<Renderable>()
    private val buttons = mutableListOf<Renderable>()
    private val buttonBases = mutableListOf<Renderable>()
    private val gateDoors = mutableListOf<Renderable>()

    private val buttonStatus = mutableListOf(false, false, false)
    private val buttonPressed = mutableListOf(0, 0, 0)

    /** 0 = Horizontal, 1 = Vertikal */
    var buttonOrientation : Int = 0
    var gateOrientation : Int = 0

    /** Renderables */
    var skyBox : Renderable
    var gate : Renderable
    var firstCameraPosition : Renderable
    var floor : Renderable
    var monster : Renderable
    var spawn : Renderable

    var player : Renderable
    var playerSpeed: Float = 0f
    var playerNormalSpeed = 0.08f
    var playerSprintSpeed = playerNormalSpeed * 2f

    val camera = TronCamera()

    /** hitboxes */
    val allHitboxes = mutableListOf<MutableList<Float>>()
    val wallVerticalHitbox = mutableListOf( 0.5f, 4.0f )
    val wallHorizontalHitbox = mutableListOf( 4f, 0.5f )
    val pillarHitbox = mutableListOf( 1f, 1f )
    val buttonHitbox = mutableListOf( 0.5f, 0.5f )
    val gateDoorHorizontalHitbox = mutableListOf( 1.5f, 0.3f )
    val gateDoorVerticalHitbox = mutableListOf( 0.3f, 1.5f )
    val monsterHitbox = mutableListOf( 2f, 2f)
    val playerHitbox = mutableListOf(1f, 1f)

    /** Var´s */
    val pointLight : PointLight
    val spotLight: SpotLight
    var notFirstFrame = false
    var oldMousePosX = 0.0
    var oldMousePosY = 0.0

    var catchEm = false
    val rnd = (1..1).random()
    var wallDespawn = 0

    val phVector3f = Vector3f(0f, 0f, 0f)
    val routePosition = mutableListOf<Vector3f>()
    val routeRotatePosition = mutableListOf<Vector3f>()

    val run = Labyrinth()

    var buttonSpawn = mutableListOf<Vector3f>()
    var doorSpawn = mutableListOf<Vector3f>()
    var flyRange = mutableListOf<Float>()

    /** Scene Build */
    init {
        staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        glClearColor(0.0f, 0.0f, 1f, 1.0f); GLError.checkThrow()

        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        for (x in 0..30) {
            routePosition.add(phVector3f)
            routeRotatePosition.add(phVector3f)
        }

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
        camera.parent = player
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
        skyBox = ModelLoader.loadModel("assets/SkyBox/skybox.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        skyBox.translateGlobal(Vector3f(60f, 0f, 60f))
        skyBox.scaleLocal(Vector3f(13f))
        objList.add(skyBox) //objList: 480

        /** camera */
        camera.translateLocal(Vector3f(0f, 3f, .0f))

        /** firstCameraPosition */
        firstCameraPosition = ModelLoader.loadModel("assets/CubeObject/cubeObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        firstCameraPosition.translateLocal(Vector3f(0f, 3f, .0f))

        /** spawn */
        spawn = ModelLoader.loadModel("assets/CubeObject/cubeObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        spawn.translateGlobal(Vector3f(12f, 0f, 4f))
        objList.add(spawn) //objList: 481

        /** lights */
        pointLight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(1f, 1f, 0f), Vector3f(1f, 0.5f, 0.1f))
        spotLight = SpotLight(Vector3f(0f, 0f, -2f), Vector3f(1f,0.8f,0.8f), Vector3f(0.1f, 0.05f, 0.05f), Vector2f(toRadians(20f), toRadians(25f)))
        spotLight.rotateLocal(toRadians(-5f), PI.toFloat(),0f)
        spotLight.parent = camera

        /** allHitboxes */
        allHitboxes.add(wallHorizontalHitbox)
        allHitboxes.add(wallVerticalHitbox)
        allHitboxes.add(pillarHitbox)
        allHitboxes.add(buttonHitbox)
        allHitboxes.add(gateDoorHorizontalHitbox)
        allHitboxes.add(gateDoorVerticalHitbox)
        allHitboxes.add(monsterHitbox)
        allHitboxes.add(playerHitbox)

        /** random elements */
        buttonSpawn = run.buttonSpawn(buttons, buttonBases, rnd)
        doorSpawn = run.doorSpawn(gate, gateDoors, firstCameraPosition, rnd)
        run.wayOfDeathAndDecay(monster, routePosition, routeRotatePosition, rnd)

        when (rnd) {
            1 -> {
                wallDespawn = 7
            }
            2 -> {
                wallDespawn = 22
            }
            3 -> {
                wallDespawn = 37
                gateOrientation = 1
                buttonOrientation = 1
            }
            4 -> {
                wallDespawn = 52
                gateOrientation = 1
                buttonOrientation = 1
            }
        }

        /** labyrinth */
        run.buildLabyrith(walls, wallsBig, pillars, pillarsBig, wallDespawn)

    }

    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        tronShader.use()
        staticShader.use()

        camera.bind(tronShader)
        spotLight.bind(staticShader, "spot", camera.getCalculateViewMatrix())
        pointLight.bind(tronShader, "point")

        tronShader.setUniform("farbe", Vector3f(0.08f,0.08f,0.08f))

        for (obj in objList) {
            if (obj != player && obj != spawn) obj.render(tronShader)
        }

    }

    fun update(dt: Float, t: Float) {

        when {
            /** Player-Movement */
            window.getKeyState(GLFW_KEY_D) -> {
                if(!strafe('D')) {
                    sprint()
                    for (obj in objList) {
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                    }
                    player.translateLocal(Vector3f(playerSpeed, 0.0f, 0.0f))
                }
            }

            window.getKeyState(GLFW_KEY_S) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {
                sprint()
                for (obj in objList) {
                    run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                }
                player.translateLocal(Vector3f(0.0f, 0.0f, playerSpeed))
            }

            window.getKeyState(GLFW_KEY_W) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {
                sprint()
                for (obj in objList) {
                    run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                }
                player.translateLocal(Vector3f(0.0f, 0.0f, -playerSpeed))
            }

            window.getKeyState(GLFW_KEY_A) -> {
                if(!strafe('A')) {
                    sprint()
                    for (obj in objList) {
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                    }
                    player.translateLocal(Vector3f(-playerSpeed, 0.0f, 0.0f))
                }
            }
        }

        /** Button */
        run.buttonMovement(buttonPressed, buttonStatus, buttonSpawn, buttonOrientation, objList)

        /** Gate */
        if (run.buttonStatus(buttonStatus, objList, doorSpawn, gateOrientation, camera, player, firstCameraPosition)) catchEm = true

        /** Monster */
        if(catchEm) run.monsterLetLoose(monster, routePosition, routeRotatePosition, rnd)
        run.monsterMovement(monster, flyRange)
        run.collision(monster, player, allHitboxes, "dead", objList, gateOrientation, playerSpeed)

    }

    fun sprint() { playerSpeed = if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) playerSprintSpeed else playerNormalSpeed }

    fun strafe(button: Char): Boolean {

        when(button) {
            'A' -> {
                if (window.getKeyState(GLFW_KEY_S)) {
                    for (obj in objList) {
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                    }
                    player.translateLocal(Vector3f(-playerSpeed, 0.0f, playerSpeed))

                } else if (window.getKeyState(GLFW_KEY_W)) {
                    for (obj in objList) {
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                    }
                    player.translateLocal(Vector3f(-playerSpeed, 0.0f, -playerSpeed))
                } else return false
            }
            'D' -> {
                if (window.getKeyState(GLFW_KEY_S)) {
                    for (obj in objList) {
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                    }
                    player.translateLocal(Vector3f(playerSpeed, 0.0f, playerSpeed))

                } else if (window.getKeyState(GLFW_KEY_W)) {
                    for (obj in objList) {
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                        run.collision(player, obj, allHitboxes, "solid", objList, gateOrientation, playerSpeed)
                    }
                    player.translateLocal(Vector3f(playerSpeed, 0.0f, -playerSpeed))
                } else return false
            }
        }
        return true

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

        if(run.buttonPressRange(player, objList[468], buttonOrientation, buttonHitbox) && window.getKeyState(GLFW_KEY_E) && buttonPressed[0] == 0) {
            buttonPressed[0] = 1
        }
        if(run.buttonPressRange(player, objList[469], buttonOrientation, buttonHitbox) && window.getKeyState(GLFW_KEY_E) && buttonPressed[1] == 0) {
            buttonPressed[1] = 1
        }
        if(run.buttonPressRange(player, objList[470], buttonOrientation, buttonHitbox) && window.getKeyState(GLFW_KEY_E) && buttonPressed[2] == 0) {
            buttonPressed[2] = 1
        }
        if(window.getKeyState(GLFW_KEY_L)) catchEm = true

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
