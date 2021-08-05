package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
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

    private val checkList = mutableListOf<Boolean>()

    /** Renderables */
    var mapcameraobjekt: Renderable
    var player: Renderable
    var lantern : Renderable
    var mac : Renderable
    var skyBox : Renderable
    var gate : Renderable
    var cubeObject : Renderable
    var floor : Renderable

    var moveablewall : Renderable

    val camera = TronCamera()

    /** Hitboxen */
    val wallVerticalHitbox = mutableListOf( 0.5f, 4.0f )
    val wallHorizontalHitbox = mutableListOf( 4f, 0.5f )
    val pillarHitbox = mutableListOf( 1f, 1f )
    val buttonHorizontalHitbox = mutableListOf( 0.2f, 2f )
    val buttonVerticalHitbox = mutableListOf( 2f, 0.2f )
    val doorHitbox = mutableListOf( 1.5f, 0.3f )

    var buttonPressed = false

    /** Var´s */
    val pointLight : PointLight
    val spotLight: SpotLight
    var notFirstFrame = false
    var oldMousePosX = 0.0
    var oldMousePosY = 0.0
    var cameracheck1 = true
    var cameracheck2 = false
    var cameracheck3 = false
    var cameracheck4 = false

    val rnd = (1..4).random()
    var doorDespawn = 0
    var skip = false
    var buttonVert = false
    var buttonHor = false
    lateinit var firstButtonFP: Vector3f
    lateinit var secoundButtonFP: Vector3f
    lateinit var thirdButtonFP: Vector3f
    lateinit var firstButtonBP : Vector3f
    lateinit var secoundButtonBP : Vector3f
    lateinit var thirdButtonBP : Vector3f

    lateinit var gateDoorLO : Vector3f
    lateinit var gateDoorLC : Vector3f
    lateinit var gateDoorRO : Vector3f
    lateinit var gateDoorRC : Vector3f

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

        /** Modelloader */
        player = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mapcameraobjekt = ModelLoader.loadModel("assets/among_us_obj/among us.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        lantern = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(-0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mac = ModelLoader.loadModel("assets/models/mac10.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        skyBox = ModelLoader.loadModel("assets/SkyBox/skybox.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")

        moveablewall = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        gate = ModelLoader.loadModel("assets/Gate/Gate.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        cubeObject = ModelLoader.loadModel("assets/CubeObject/cubeObject.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        floor = ModelLoader.loadModel("assets/NewWall/NewFloor/floor.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")

        var a = 0
        while (a < 196) {
            walls.add(ModelLoader.loadModel("assets/NewWall/NewNewWall/LabWall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            pillars.add(ModelLoader.loadModel("assets/NewWall/pillar.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            a++
        }

        a = 0
        while (a < 60) {
            wallsBig.add(ModelLoader.loadModel("assets/NewWall/NewNewWall/LabWallBig.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            a++
        }

        a = 0
        while (a < 4) {
            pillarsBig.add(ModelLoader.loadModel("assets/NewWall/pillarBig.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            a++
        }

        a = 0
        while (a < 3) {
            buttons.add(ModelLoader.loadModel("assets/Button/Button.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            buttonBases.add(ModelLoader.loadModel("assets/Button/ButtonBase.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            a++
        }

        a = 0
        while (a < 2) {
            gateDoors.add(ModelLoader.loadModel("assets/Gate/GateDoor.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            a++
        }

        /** Random elements */
        rndButtonSpawn()
        rndDoorSpawn()

        /** Labyrinth */
        buildLabyrith()

        skyBox.scaleLocal(Vector3f(5f))
        skyBox.translateGlobal(Vector3f(-5f, 0f, -5f))

        /** Camerastart Position */
        camera.translateLocal(Vector3f(0f, 10f, .0f))

        /** Playerstart Position */
        player.translateLocal(Vector3f(-35f, 2f, -5f))
        player.rotateLocal(0f, toRadians(180f), 0f)

        /** Orbitalecamera Position */
        mapcameraobjekt.scaleLocal(Vector3f(0.008f))
        mapcameraobjekt.translateLocal(Vector3f(10f, 3000f, 0f))

        /** Pistole */
        mac.scaleLocal(Vector3f(0.008f))
        mac.translateLocal(Vector3f(20f, 150f, -35f))

        /** Objektplatzierung */
        lantern.translateLocal(Vector3f(5f, 1f, 0f))

        /** Lichter */
        pointLight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(1f, 1f, 0f), Vector3f(1f, 0.5f, 0.1f))
        spotLight = SpotLight(Vector3f(0f, 1f, -1f), Vector3f(2f,2f,0.1f), Vector3f(0.1f, 0.01f, 0.01f), Vector2f(toRadians(150f), toRadians(30f)))
        spotLight.rotateLocal(toRadians(-10f), PI.toFloat(),0f)
        pointLight.parent = lantern
        spotLight.parent = mapcameraobjekt

        /** Mauern */
        camera.parent = moveablewall
        moveablewall.scaleLocal(Vector3f(0.3f))
        moveablewall.translateLocal(Vector3f(8f, 0.0f, 8.0f))

        /** CubeObject */
        cubeObject.translateGlobal(Vector3f(0.0f, -8.0f, -6.0f))
        cubeObject.rotateLocal(0.0f, toRadians(180f), 0.0f)

        floor.scaleLocal(Vector3f(0.5f))
        floor.translateGlobal(Vector3f(60f, 0f, 60f))

        /** ObjList: 0 - 195 */
        var f = 0
        while (f < walls.size) {
            walls[f].scaleLocal(Vector3f(0.5f))
            objList.add(walls[f])
            f++
        }

        /** ObjList: 196 - 255 */
        var u = 0
        while (u < wallsBig.size) {
            wallsBig[u].scaleLocal(Vector3f(0.5f))
            objList.add(wallsBig[u])
            u++
        }

        /** ObjList: 256 - 451 */
        var g = 0
        while (g < pillars.size) {
            pillars[g].scaleLocal(Vector3f(0.58f))
            objList.add(pillars[g])
            g++
        }

        /** ObjList: 452 - 455 */
        var h = 0
        while (h < pillarsBig.size) {
            pillarsBig[h].scaleLocal(Vector3f(0.58f))
            objList.add(pillarsBig[h])
            h++
        }

        /** ObjList: 456 - 458 */
        var e = 0
        while (e < buttons.size) {
            objList.add(buttons[e])
            e++
        }

        /** ObjList: 459 - 461 */
        var s = 0
        while (s < buttonBases.size) {
            objList.add(buttonBases[s])
            s++
        }

        /** ObjList: 462*/
        objList.add(gate)

        /** ObjList: 463 - 464 */
        var b = 0
        while (b < gateDoors.size) {
            gateDoors[b].scaleLocal(Vector3f(0.5f))
            objList.add(gateDoors[b])
            b++
        }

        gate.scaleLocal(Vector3f(0.5f))

        /** Button */





        //gate.scaleLocal(Vector3f(0.5f))
        //gate.translateGlobal(Vector3f(0.0f, 0.0f, 0.0f))
//
        //leftDoor.scaleLocal(Vector3f(0.5f))
        //leftDoor.translateGlobal(Vector3f(2.0f, 0.0f, 0.0f))
        //rightDoor.scaleLocal(Vector3f(0.5f))
        //rightDoor.translateGlobal(Vector3f(-2.0f, 0.0f, 0.0f))
//
        //dd1.scaleLocal(Vector3f(0.5f))
        //dd1.translateGlobal(Vector3f(4.0f, 0.0f, 0.0f))
        //dd2.scaleLocal(Vector3f(0.5f))
        //dd2.translateGlobal(Vector3f(-4.0f, 0.0f, 0.0f))


        /** Steuerung Info an Console*/
        println("Steuerung:")                                                                       
        println("Spielfigurbewegen: W,A,S,D und Maus:")                                             
        println("Spielfigurbewegen: F für Fliegen und Shift für Sprinten:")                         
        println("Moveable Mauer: T,F,G,H   wo bei T die Mauer auf der X Achse liegt")               
        println("Altes Labyrint sichtbar / unsichtbar machen mit C")
        println("")                                                                                 
        println("Kameras")                                                                          
        println("V für Spielfigurkamera")                                                           
        println("B für Bewegende Wand Kamera")                                                      
        println("N für die kleine Laterne auf dem Boden")                                           
        println("M für die Orbitalekamera")                                                         

    }

    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        tronShader.use()

        camera.bind(tronShader)
        spotLight.bind(tronShader, "spot", camera.getCalculateViewMatrix())
        pointLight.bind(tronShader, "point")

        // tronShader.setUniform("farbe", Vector3f(abs(sin(t)), abs(sin(t/2f)), abs(sin(t/3f))))

        tronShader.setUniform("farbe", Vector3f(1f,0.2f,0f))
        tronShader.setUniform("farbe", Vector3f(0.6f,0.6f,0.6f))
        //mapcameraobjekt.render(tronShader)
        // lantern.render(tronShader)
        //mac.render(tronShader)
        // buttonBase.render(tronShader)
        skyBox.render(tronShader)
        // gate.render(tronShader)
        // cubeObject.render(tronShader)
        floor.render(tronShader)

        var z = 0
        while (z < objList.size) {
            objList[z].render(tronShader)
            z++
        }

    }

    var z = objList[81].getPosition().x
    var z2 = objList[82].getPosition().x



    var t = camera.rotateLocal(toRadians(1.0f), 0f, 0f)

    fun update(dt: Float, t: Float) {

        // pointLight.lightColor = Vector3f(abs(sin(t/3f)), abs(sin(t/4f)), abs(sin(t/2)))

        var x = 0

        checkList.add(true)
        checkList.add(true)
        checkList.add(true)
        checkList.add(true)

        when {
            /** wall Bewegung */

            window.getKeyState(GLFW_KEY_D) -> {

                if(window.getKeyState(GLFW_KEY_S)) {
                    while (x < objList.size) {
                        collision(moveablewall, objList[x])
                        collision(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(0.2f, 0.0f, 0.2f))

                }
                else if(window.getKeyState(GLFW_KEY_W)) {
                    while (x < objList.size) {
                        collision(moveablewall, objList[x])
                        collision(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(0.2f, 0.0f, -0.2f))

                }
                else {
                    while (x < objList.size) {
                        collision(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(0.2f, 0.0f, 0.0f))
                }

            }

            window.getKeyState(GLFW_KEY_S) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {

                while (x < objList.size) {
                    collision(moveablewall, objList[x])
                    x++
                }
                moveablewall.translateLocal(Vector3f(0.0f, 0.0f, 0.2f))

            }

            window.getKeyState(GLFW_KEY_W) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {

                while (x < objList.size) {
                    collision(moveablewall, objList[x])
                    x++
                }
                moveablewall.translateLocal(Vector3f(0.0f, 0.0f, -0.2f))

            }

            window.getKeyState(GLFW_KEY_A) -> {

                if(window.getKeyState(GLFW_KEY_S)) {
                    while (x < objList.size) {
                        collision(moveablewall, objList[x])
                        collision(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(-0.2f, 0.0f, 0.2f))

                }
                else if (window.getKeyState(GLFW_KEY_W)) {
                    while (x < objList.size) {
                        collision(moveablewall, objList[x])
                        collision(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(-0.2f, 0.0f, -0.2f))
                }
                else {
                    while (x < objList.size) {
                        collision(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(-0.2f, 0.0f, 0.0f))
                }

            }

            /** Cameraview */
            /** 1st Person */
            window.getKeyState(GLFW_KEY_V) -> {
                cameracheck1 = true
                cameracheck2 = false
                cameracheck3 = false
                cameracheck4 = false
                camera.parent = player
            }
            /** Camera moveablewall */
            window.getKeyState(GLFW_KEY_B) -> {
                cameracheck1 = false
                cameracheck2 = false
                cameracheck2 = false
                cameracheck4 = true
                camera.parent = moveablewall
            }
            /** Camera 3 */
            window.getKeyState(GLFW_KEY_N) -> {
                cameracheck1 = false
                cameracheck2 = false
                cameracheck3 = true
                cameracheck4 = false
                player.parent = lantern
            }
            /** Camera 4 */
            window.getKeyState(GLFW_KEY_M) -> {
                cameracheck1 = false
                cameracheck2 = false
                cameracheck3 = false
                cameracheck4 = true
                camera.parent = mapcameraobjekt
            }

        }
        when {
            buttonPressed -> {
                if (objList[463].getWorldPosition().x > gateDoorLO.x || objList[464].getWorldPosition().x < gateDoorRO.x) {
                    objList[463].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    objList[464].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    camera.parent = cubeObject
                } else {
                    camera.parent = moveablewall
                }
                if (objList[456].getWorldPosition().x > firstButtonBP.x) {
                    objList[456].translateGlobal(Vector3f(-0.0f, 0.0f, 0.0f))
                }
            }
            !buttonPressed -> {
                if (objList[463].getWorldPosition().x < gateDoorLC.x || objList[464].getWorldPosition().x < gateDoorRC.x) {
                    objList[463].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    objList[464].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                }
                if (objList[456].getWorldPosition().x < firstButtonFP.x) {
                    objList[456].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                }
            }
        }

    }

    fun collision(firstMesh: Renderable, secoundMesh: Renderable) {

        val t: Boolean
        val g: Boolean
        val h: Boolean
        val f: Boolean

        var minusX = 0.0f
        var plusX = 0.0f
        var minusZ = 0.0f
        var plusZ = 0.0f

        var count = 0

        for (x in objList) {
            ///** Horizontale Wände */
            //if (x == secoundMesh && (count in 0..94 || count in 196..225)) {
            //    minusX = secoundMesh.getWorldPosition().x - wallHorizontalHitbox[0]
            //    plusX = secoundMesh.getWorldPosition().x + wallHorizontalHitbox[0]
            //    minusZ = secoundMesh.getWorldPosition().z - wallHorizontalHitbox[1]
            //    plusZ = secoundMesh.getWorldPosition().z + wallHorizontalHitbox[1]
            //}
            ///** Vertikale Wände */
            //if (x == secoundMesh && (count in 95..195 || count in 226..255)) {
            //    minusX = secoundMesh.getWorldPosition().x - wallVerticalHitbox[0]
            //    plusX = secoundMesh.getWorldPosition().x + wallVerticalHitbox[0]
            //    minusZ = secoundMesh.getWorldPosition().z - wallVerticalHitbox[1]
            //    plusZ = secoundMesh.getWorldPosition().z + wallVerticalHitbox[1]
            //}
            ///** Pillar */
            //if (x == secoundMesh && count in 256..464) {
            //    minusX = secoundMesh.getWorldPosition().x - pillarHitbox[0]
            //    plusX = secoundMesh.getWorldPosition().x + pillarHitbox[0]
            //    minusZ = secoundMesh.getWorldPosition().z - pillarHitbox[1]
            //    plusZ = secoundMesh.getWorldPosition().z + pillarHitbox[1]
            //}
            /** Button */
            if (x == secoundMesh && count in 468..470) {

                if (buttonVert) {
                    minusX = secoundMesh.getWorldPosition().x - buttonVerticalHitbox[0]
                    plusX = secoundMesh.getWorldPosition().x + buttonVerticalHitbox[0]
                    minusZ = secoundMesh.getWorldPosition().z - buttonVerticalHitbox[1]
                    plusZ = secoundMesh.getWorldPosition().z + buttonVerticalHitbox[1]
                }
                if (buttonHor) {
                    minusX = secoundMesh.getWorldPosition().x - buttonHorizontalHitbox[0]
                    plusX = secoundMesh.getWorldPosition().x + buttonHorizontalHitbox[0]
                    minusZ = secoundMesh.getWorldPosition().z - buttonHorizontalHitbox[1]
                    plusZ = secoundMesh.getWorldPosition().z + buttonHorizontalHitbox[1]
                }

            }
            /** Gate */
            if (x == secoundMesh && count == 462) {
                skip = true
            }
            /** Linkes und rechtes Tor */
            if (x == secoundMesh && count in 463..464) {
                minusX = secoundMesh.getWorldPosition().x - doorHitbox[0]
                plusX = secoundMesh.getWorldPosition().x + doorHitbox[0]
                minusZ = secoundMesh.getWorldPosition().z - doorHitbox[1]
                plusZ = secoundMesh.getWorldPosition().z + doorHitbox[1]
            }
            count++
        }

        if (!skip) {
            t = !(firstMesh.getWorldPosition().x + 1 > minusX && firstMesh.getWorldPosition().x - 1 < minusX)
                    || firstMesh.getWorldPosition().z - 0.8 > plusZ
                    || firstMesh.getWorldPosition().z + 0.8 < minusZ
            g = !(firstMesh.getWorldPosition().x - 1 < plusX && firstMesh.getWorldPosition().x + 1 > plusX)
                    || firstMesh.getWorldPosition().z - 0.8 > plusZ
                    || firstMesh.getWorldPosition().z + 0.8 < minusZ
            h = !(firstMesh.getWorldPosition().z + 1 > minusZ && firstMesh.getWorldPosition().z - 1 < minusZ)
                    || firstMesh.getWorldPosition().x - 0.8 > plusX
                    || firstMesh.getWorldPosition().x + 0.8 < minusX
            f = !(firstMesh.getWorldPosition().z - 1 < plusZ && firstMesh.getWorldPosition().z + 1 > plusZ)
                    || firstMesh.getWorldPosition().x - 0.8 > plusX
                    || firstMesh.getWorldPosition().x + 0.8 < minusX
            if (!t) {
                moveablewall.translateGlobal(Vector3f(-0.06f, 0.0f, 0.0f))
            }
            if (!g) {
                moveablewall.translateGlobal(Vector3f(0.06f, 0.0f, 0.0f))
            }
            if (!h) {
                moveablewall.translateGlobal(Vector3f(0.0f, 0.0f, -0.06f))
            }
            if (!f) {
                moveablewall.translateGlobal(Vector3f(0.0f, 0.0f, 0.06f))
            }
        } else {
            skip = false
        }

    }

    fun buttonPressRange(firstMesh: Renderable, secoundMesh: Renderable): Boolean {

        val h: Boolean
        val t: Boolean
        var bool = true

        var turn = true

        when {

            !turn -> {

                val minusX: Float = secoundMesh.getWorldPosition().x - buttonHorizontalHitbox[0]
                val plusX: Float = secoundMesh.getWorldPosition().x + buttonHorizontalHitbox[0]
                val minusZ: Float = secoundMesh.getWorldPosition().z - 2

                h = !(firstMesh.getWorldPosition().z + 1 > minusZ && firstMesh.getWorldPosition().z - 1 < minusZ)
                        || firstMesh.getWorldPosition().x - 0.8 > plusX
                        || firstMesh.getWorldPosition().x + 0.8 < minusX

                if (!h) {
                    bool = false
                }

            }

            turn -> {

                val minusX: Float = secoundMesh.getWorldPosition().x - buttonHorizontalHitbox[0]
                val plusZ: Float = secoundMesh.getWorldPosition().z + buttonHorizontalHitbox[0]
                val minusZ: Float = secoundMesh.getWorldPosition().z - 2

                t = !(firstMesh.getWorldPosition().x + 1 > minusX && firstMesh.getWorldPosition().x - 1 < minusX)
                        || firstMesh.getWorldPosition().z - 0.8 > plusZ
                        || firstMesh.getWorldPosition().z + 0.8 < minusZ

                if (!t) {
                    bool = false
                }

            }

        }

        return bool

    }

    fun rndButtonSpawn() {
        when(rnd) {
            1 -> {

                buttonVert = true

                buttons[0].rotateLocal(0f, toRadians(90f), 0f)
                buttons[0].translateGlobal(Vector3f(1f, 0f, 4f))
                buttonBases[0].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[0].translateGlobal(Vector3f(1f, 0f, 4f))

                buttons[1].rotateLocal(0f, toRadians(90f), 0f)
                buttons[1].translateGlobal(Vector3f(1f, 0f, 12f))
                buttonBases[1].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[1].translateGlobal(Vector3f(1f, 0f, 12f))

                buttons[2].rotateLocal(0f, toRadians(900f), 0f)
                buttons[2].translateGlobal(Vector3f(1f, 0f, 20f))
                buttonBases[2].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[2].translateGlobal(Vector3f(1f, 0f, 20f))

                firstButtonFP = buttons[0].getWorldPosition()
                secoundButtonFP = buttons[1].getWorldPosition()
                thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                firstButtonBP = buttons[0].getWorldPosition()
                secoundButtonBP = buttons[1].getWorldPosition()
                thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(0.2f, 0f, 0f))
            }
            2 -> {

                buttonVert = true

                buttons[0].rotateLocal(0f, toRadians(90f), 0f)
                buttons[0].translateGlobal(Vector3f(1f, 0f, 4f))
                buttonBases[0].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[0].translateGlobal(Vector3f(1f, 0f, 4f))

                buttons[1].rotateLocal(0f, toRadians(90f), 0f)
                buttons[1].translateGlobal(Vector3f(1f, 0f, 12f))
                buttonBases[1].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[1].translateGlobal(Vector3f(1f, 0f, 12f))

                buttons[2].rotateLocal(0f, toRadians(90f), 0f)
                buttons[2].translateGlobal(Vector3f(1f, 0f, 20f))
                buttonBases[2].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[2].translateGlobal(Vector3f(1f, 0f, 20f))

                firstButtonFP = buttons[0].getWorldPosition()
                secoundButtonFP = buttons[1].getWorldPosition()
                thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                firstButtonBP = buttons[0].getWorldPosition()
                secoundButtonBP = buttons[1].getWorldPosition()
                thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(0.2f, 0f, 0f))

                // buttonBase.translateGlobal(Vector3f(-15.0f, -2f, -1.0f))
                // buttonBase.rotateLocal(0.0f, toRadians(90f), 0f)
                // buttonFrontPosition = buttonBase.getWorldPosition()
                // buttonBase.translateGlobal((Vector3f(0.0f, 0.0f, 0.2f)))
                // buttonBackPosition = buttonBase.getWorldPosition()
                // buttonBase.translateGlobal((Vector3f(0.0f, 0.0f, -0.2f)))
            }
            3 -> {

                buttonVert = true

                buttons[0].rotateLocal(0f, toRadians(90f), 0f)
                buttons[0].translateGlobal(Vector3f(1f, 0f, 4f))
                buttonBases[0].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[0].translateGlobal(Vector3f(1f, 0f, 4f))

                buttons[1].rotateLocal(0f, toRadians(90f), 0f)
                buttons[1].translateGlobal(Vector3f(1f, 0f, 12f))
                buttonBases[1].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[1].translateGlobal(Vector3f(1f, 0f, 12f))

                buttons[2].rotateLocal(0f, toRadians(90f), 0f)
                buttons[2].translateGlobal(Vector3f(1f, 0f, 20f))
                buttonBases[2].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[2].translateGlobal(Vector3f(1f, 0f, 20f))

                firstButtonFP = buttons[0].getWorldPosition()
                secoundButtonFP = buttons[1].getWorldPosition()
                thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                firstButtonBP = buttons[0].getWorldPosition()
                secoundButtonBP = buttons[1].getWorldPosition()
                thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(0.2f, 0f, 0f))

                // buttonBase.translateGlobal(Vector3f(-20.0f, -2f, -1.0f))
                // buttonBase.rotateLocal(0.0f, toRadians(90f), 0f)
                // buttonFrontPosition = buttonBase.getWorldPosition()
                // buttonBase.translateGlobal((Vector3f(0.0f, 0.0f, 0.2f)))
                // buttonBackPosition = buttonBase.getWorldPosition()
                // buttonBase.translateGlobal((Vector3f(0.0f, 0.0f, -0.2f)))
            }
            4 -> {

                buttonVert = true

                buttons[0].rotateLocal(0f, toRadians(90f), 0f)
                buttons[0].translateGlobal(Vector3f(1f, 0f, 4f))
                buttonBases[0].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[0].translateGlobal(Vector3f(1f, 0f, 4f))

                buttons[1].rotateLocal(0f, toRadians(90f), 0f)
                buttons[1].translateGlobal(Vector3f(1f, 0f, 12f))
                buttonBases[1].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[1].translateGlobal(Vector3f(1f, 0f, 12f))

                buttons[2].rotateLocal(0f, toRadians(90f), 0f)
                buttons[2].translateGlobal(Vector3f(1f, 0f, 20f))
                buttonBases[2].rotateLocal(0f, toRadians(90f), 0f)
                buttonBases[2].translateGlobal(Vector3f(1f, 0f, 20f))

                firstButtonFP = buttons[0].getWorldPosition()
                secoundButtonFP = buttons[1].getWorldPosition()
                thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                firstButtonBP = buttons[0].getWorldPosition()
                secoundButtonBP = buttons[1].getWorldPosition()
                thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(0.2f, 0f, 0f))

            }
        }
    }

    fun rndDoorSpawn() {
        when(rnd) {
            1 -> {
                doorDespawn = 7

                gate.translateGlobal(Vector3f(60f, 0f, 0f))

                gateDoors[0].translateGlobal(Vector3f(58f, 0f, 0f))
                gateDoorLC = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(62f, 0f, 0f))
                gateDoorRC = gateDoors[1].getWorldPosition()

                gateDoors[0].translateGlobal(Vector3f(-2f, 0f, 0f))
                gateDoorLO = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(2f, 0f, 0f))
                gateDoorRO = gateDoors[1].getWorldPosition()
            }
            2 -> {
                doorDespawn = 7

                gate.translateGlobal(Vector3f(60f, 0f, 0f))

                gateDoors[0].translateGlobal(Vector3f(58f, 0f, 0f))
                gateDoorLC = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(62f, 0f, 0f))
                gateDoorRC = gateDoors[1].getWorldPosition()

                gateDoors[0].translateGlobal(Vector3f(-2f, 0f, 0f))
                gateDoorLO = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(2f, 0f, 0f))
                gateDoorRO = gateDoors[1].getWorldPosition()

                //gate.translateGlobal(Vector3f(60f, 0f, 120f))
                //doorDespawn = 22

            }
            3 -> {

                doorDespawn = 7

                gate.translateGlobal(Vector3f(60f, 0f, 0f))

                gateDoors[0].translateGlobal(Vector3f(58f, 0f, 0f))
                gateDoorLC = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(62f, 0f, 0f))
                gateDoorRC = gateDoors[1].getWorldPosition()

                gateDoors[0].translateGlobal(Vector3f(-2f, 0f, 0f))
                gateDoorLO = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(2f, 0f, 0f))
                gateDoorRO = gateDoors[1].getWorldPosition()

                //gate.translateGlobal(Vector3f(0f, 0f, 60f))
                //gate.rotateLocal(0f, toRadians(90f), 0f)
                //doorDespawn = 37

            }
            4 -> {

                doorDespawn = 7

                gate.translateGlobal(Vector3f(60f, 0f, 0f))

                gateDoors[0].translateGlobal(Vector3f(58f, 0f, 0f))
                gateDoorLC = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(62f, 0f, 0f))
                gateDoorRC = gateDoors[1].getWorldPosition()

                gateDoors[0].translateGlobal(Vector3f(-2f, 0f, 0f))
                gateDoorLO = gateDoors[0].getWorldPosition()
                gateDoors[1].translateGlobal(Vector3f(2f, 0f, 0f))
                gateDoorRO = gateDoors[1].getWorldPosition()

                //gate.translateGlobal(Vector3f(120f, 0f, 60f))
                //gate.rotateLocal(0f, toRadians(90f), 0f)
                //doorDespawn = 52

            }
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

        if(!buttonPressRange(moveablewall, objList[80])) {

            if(window.getKeyState(GLFW_KEY_E) && !buttonPressed) buttonPressed = true
            else if(window.getKeyState(GLFW_KEY_E) && buttonPressed) buttonPressed = false

        }

    }

    fun onMouseMove(xpos: Double, ypos: Double) {
        val deltaX = xpos - oldMousePosX
        val deltaY = ypos - oldMousePosY
        oldMousePosX = xpos
        oldMousePosY = ypos

        /** Camera 1 */
        if(notFirstFrame && cameracheck1) {
            /** links-rechts */
            player.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)
            /** hoch-runter */
            camera.rotateLocal(toRadians(deltaY.toFloat() * -0.05f), 0f, 0f)
            moveablewall.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)

            if(1.3f < (camera.getYAxis().angle(moveablewall.getYAxis()))) {
                camera.rotateLocal(toRadians(deltaY.toFloat() * 0.05f), 0f, 0f)
                moveablewall.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)
            }

        }
        notFirstFrame = true

        /** Camera 4 */
        if(notFirstFrame && cameracheck4) {
            /** links-rechts */
            camera.rotateAroundPoint(0f, toRadians(deltaX.toFloat() * -0.03f), 0f, Vector3f(0f))
            /** hoch-runter */
            camera.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.05f), 0f, 0f)
        }
        notFirstFrame = true

    }

    fun cleanup() {}

    fun buildLabyrith() {

        wallsBig[0].translateGlobal(Vector3f(4f, 0f, 0f))
        wallsBig[1].translateGlobal(Vector3f(12f, 0f, 0f))
        wallsBig[2].translateGlobal(Vector3f(20f, 0f, 0f))
        wallsBig[3].translateGlobal(Vector3f(28f, 0f, 0f))
        wallsBig[4].translateGlobal(Vector3f(36f, 0f, 0f))
        wallsBig[5].translateGlobal(Vector3f(44f, 0f, 0f))
        wallsBig[6].translateGlobal(Vector3f(52f, 0f, 0f))
        if (doorDespawn != 7) wallsBig[7].translateGlobal(Vector3f(60f, 0f, 0f)) else wallsBig[7].translateGlobal(Vector3f(0f, -10f, 0f))
        wallsBig[8].translateGlobal(Vector3f(68f, 0f, 0f))
        wallsBig[9].translateGlobal(Vector3f(76f, 0f, 0f))
        wallsBig[10].translateGlobal(Vector3f(84f, 0f, 0f))
        wallsBig[11].translateGlobal(Vector3f(92f, 0f, 0f))
        wallsBig[12].translateGlobal(Vector3f(100f, 0f, 0f))
        wallsBig[13].translateGlobal(Vector3f(108f, 0f, 0f))
        wallsBig[14].translateGlobal(Vector3f(116f, 0f, 0f))

        walls[0].translateGlobal(Vector3f(20f, 0f, 8f))
        walls[1].translateGlobal(Vector3f(36f, 0f, 8f))
        walls[2].translateGlobal(Vector3f(44f, 0f, 8f))
        walls[3].translateGlobal(Vector3f(52f, 0f, 8f))
        walls[4].translateGlobal(Vector3f(68f, 0f, 8f))
        walls[5].translateGlobal(Vector3f(84f, 0f, 8f))
        walls[6].translateGlobal(Vector3f(92f, 0f, 8f))
        walls[7].translateGlobal(Vector3f(108f, 0f, 8f))
        walls[8].translateGlobal(Vector3f(116f, 0f, 8f))

        walls[9].translateGlobal(Vector3f(12f, 0f, 16f))
        walls[10].translateGlobal(Vector3f(20f, 0f, 16f))
        walls[11].translateGlobal(Vector3f(28f, 0f, 16f))
        walls[12].translateGlobal(Vector3f(36f, 0f, 16f))
        walls[13].translateGlobal(Vector3f(44f, 0f, 16f))
        walls[14].translateGlobal(Vector3f(52f, 0f, 16f))
        walls[15].translateGlobal(Vector3f(60f, 0f, 16f))
        walls[16].translateGlobal(Vector3f(76f, 0f, 16f))
        walls[17].translateGlobal(Vector3f(100f, 0f, 16f))
        walls[18].translateGlobal(Vector3f(108f, 0f, 16f))

        walls[19].translateGlobal(Vector3f(20f, 0f, 24f))
        walls[20].translateGlobal(Vector3f(28f, 0f, 24f))
        walls[21].translateGlobal(Vector3f(36f, 0f, 24f))
        walls[22].translateGlobal(Vector3f(60f, 0f, 24f))
        walls[23].translateGlobal(Vector3f(68f, 0f, 24f))

        walls[24].translateGlobal(Vector3f(20f, 0f, 32f))
        walls[25].translateGlobal(Vector3f(28f, 0f, 32f))
        walls[26].translateGlobal(Vector3f(36f, 0f, 32f))
        walls[27].translateGlobal(Vector3f(44f, 0f, 32f))
        walls[28].translateGlobal(Vector3f(60f, 0f, 32f))
        walls[29].translateGlobal(Vector3f(76f, 0f, 32f))
        walls[30].translateGlobal(Vector3f(84f, 0f, 32f))
        walls[31].translateGlobal(Vector3f(92f, 0f, 32f))
        walls[32].translateGlobal(Vector3f(100f, 0f, 32f))

        walls[33].translateGlobal(Vector3f(12f, 0f, 40f))
        walls[34].translateGlobal(Vector3f(20f, 0f, 40f))
        walls[35].translateGlobal(Vector3f(44f, 0f, 40f))
        walls[36].translateGlobal(Vector3f(52f, 0f, 40f))
        walls[37].translateGlobal(Vector3f(68f, 0f, 40f))
        walls[38].translateGlobal(Vector3f(84f, 0f, 40f))
        walls[39].translateGlobal(Vector3f(92f, 0f, 40f))
        walls[40].translateGlobal(Vector3f(100f, 0f, 40f))
        walls[41].translateGlobal(Vector3f(108f, 0f, 40f))
        walls[42].translateGlobal(Vector3f(116f, 0f, 40f))

        walls[43].translateGlobal(Vector3f(12f, 0f, 48f))
        walls[44].translateGlobal(Vector3f(28f, 0f, 48f))
        walls[45].translateGlobal(Vector3f(52f, 0f, 48f))
        walls[46].translateGlobal(Vector3f(92f, 0f, 48f))
        walls[47].translateGlobal(Vector3f(100f, 0f, 48f))
        walls[48].translateGlobal(Vector3f(108f, 0f, 48f))

        walls[49].translateGlobal(Vector3f(4f, 0f, 56f))
        walls[50].translateGlobal(Vector3f(20f, 0f, 56f))
        walls[51].translateGlobal(Vector3f(44f, 0f, 56f))
        walls[52].translateGlobal(Vector3f(60f, 0f, 56f))
        walls[53].translateGlobal(Vector3f(68f, 0f, 56f))
        walls[54].translateGlobal(Vector3f(84f, 0f, 56f))
        walls[55].translateGlobal(Vector3f(100f, 0f, 56f))

        walls[56].translateGlobal(Vector3f(4f, 0f, 64f))
        walls[57].translateGlobal(Vector3f(12f, 0f, 64f))
        walls[58].translateGlobal(Vector3f(28f, 0f, 64f))
        walls[59].translateGlobal(Vector3f(36f, 0f, 64f))
        walls[60].translateGlobal(Vector3f(92f, 0f, 64f))
        walls[61].translateGlobal(Vector3f(108f, 0f, 64f))
        walls[62].translateGlobal(Vector3f(116f, 0f, 64f))

        walls[63].translateGlobal(Vector3f(12f, 0f, 72f))
        walls[64].translateGlobal(Vector3f(20f, 0f, 72f))
        walls[65].translateGlobal(Vector3f(76f, 0f, 72f))
        walls[66].translateGlobal(Vector3f(84f, 0f, 72f))
        walls[67].translateGlobal(Vector3f(100f, 0f, 72f))
        walls[68].translateGlobal(Vector3f(108f, 0f, 72f))

        walls[69].translateGlobal(Vector3f(20f, 0f, 80f))
        walls[70].translateGlobal(Vector3f(108f, 0f, 80f))
        walls[71].translateGlobal(Vector3f(116f, 0f, 80f))

        walls[72].translateGlobal(Vector3f(4f, 0f, 88f))
        walls[73].translateGlobal(Vector3f(12f, 0f, 88f))
        walls[74].translateGlobal(Vector3f(28f, 0f, 88f))
        walls[75].translateGlobal(Vector3f(84f, 0f, 88f))
        walls[76].translateGlobal(Vector3f(92f, 0f, 88f))
        walls[77].translateGlobal(Vector3f(100f, 0f, 88f))

        walls[78].translateGlobal(Vector3f(20f, 0f, 96f))
        walls[79].translateGlobal(Vector3f(44f, 0f, 96f))
        walls[80].translateGlobal(Vector3f(52f, 0f, 96f))
        walls[81].translateGlobal(Vector3f(68f, 0f, 96f))
        walls[82].translateGlobal(Vector3f(76f, 0f, 96f))
        walls[83].translateGlobal(Vector3f(92f, 0f, 96f))
        walls[84].translateGlobal(Vector3f(100f, 0f, 96f))
        walls[85].translateGlobal(Vector3f(108f, 0f, 96f))

        walls[86].translateGlobal(Vector3f(28f, 0f, 104f))
        walls[87].translateGlobal(Vector3f(36f, 0f, 104f))
        walls[88].translateGlobal(Vector3f(52f, 0f, 104f))
        walls[89].translateGlobal(Vector3f(60f, 0f, 104f))
        walls[90].translateGlobal(Vector3f(116f, 0f, 104f))

        walls[91].translateGlobal(Vector3f(12f, 0f, 112f))
        walls[92].translateGlobal(Vector3f(20f, 0f, 112f))
        walls[93].translateGlobal(Vector3f(28f, 0f, 112f))
        walls[94].translateGlobal(Vector3f(108f, 0f, 112f))

        wallsBig[15].translateGlobal(Vector3f(4f, 0f, 120f))
        wallsBig[16].translateGlobal(Vector3f(12f, 0f, 120f))
        wallsBig[17].translateGlobal(Vector3f(20f, 0f, 120f))
        wallsBig[18].translateGlobal(Vector3f(28f, 0f, 120f))
        wallsBig[19].translateGlobal(Vector3f(36f, 0f, 120f))
        wallsBig[20].translateGlobal(Vector3f(44f, 0f, 120f))
        wallsBig[21].translateGlobal(Vector3f(52f, 0f, 120f))
        if (doorDespawn != 22) wallsBig[22].translateGlobal(Vector3f(60f, 0f, 120f)) else wallsBig[22].translateGlobal(Vector3f(0f, -10f, 0f))
        wallsBig[23].translateGlobal(Vector3f(68f, 0f, 120f))
        wallsBig[24].translateGlobal(Vector3f(76f, 0f, 120f))
        wallsBig[25].translateGlobal(Vector3f(84f, 0f, 120f))
        wallsBig[26].translateGlobal(Vector3f(92f, 0f, 120f))
        wallsBig[27].translateGlobal(Vector3f(100f, 0f, 120f))
        wallsBig[28].translateGlobal(Vector3f(108f, 0f, 120f))
        wallsBig[29].translateGlobal(Vector3f(116f, 0f, 120f))

        var q = 95
        while (q < 196) {
            walls[q].rotateLocal(0f, toRadians(90f), 0f)
            q++
        }

        var o = 30
        while (o < 60) {
            wallsBig[o].rotateLocal(0f, toRadians(90f), 0f)
            o++
        }

        wallsBig[30].translateGlobal(Vector3f(0f, 0f, 4f))
        wallsBig[31].translateGlobal(Vector3f(0f, 0f, 12f))
        wallsBig[32].translateGlobal(Vector3f(0f, 0f, 20f))
        wallsBig[33].translateGlobal(Vector3f(0f, 0f, 28f))
        wallsBig[34].translateGlobal(Vector3f(0f, 0f, 36f))
        wallsBig[35].translateGlobal(Vector3f(0f, 0f, 44f))
        wallsBig[36].translateGlobal(Vector3f(0f, 0f, 52f))
        if (doorDespawn != 37) wallsBig[37].translateGlobal(Vector3f(0f, 0f, 60f)) else wallsBig[37].translateGlobal(Vector3f(0f, -10f, 0f))
        wallsBig[38].translateGlobal(Vector3f(0f, 0f, 68f))
        wallsBig[39].translateGlobal(Vector3f(0f, 0f, 76f))
        wallsBig[40].translateGlobal(Vector3f(0f, 0f, 84f))
        wallsBig[41].translateGlobal(Vector3f(0f, 0f, 92f))
        wallsBig[42].translateGlobal(Vector3f(0f, 0f, 100f))
        wallsBig[43].translateGlobal(Vector3f(0f, 0f, 108f))
        wallsBig[44].translateGlobal(Vector3f(0f, 0f, 116f))

        walls[95].translateGlobal(Vector3f(8f, 0f, 12f))
        walls[96].translateGlobal(Vector3f(8f, 0f, 20f))
        walls[97].translateGlobal(Vector3f(8f, 0f, 28f))
        walls[98].translateGlobal(Vector3f(8f, 0f, 36f))
        walls[99].translateGlobal(Vector3f(8f, 0f, 76f))
        walls[100].translateGlobal(Vector3f(8f, 0f, 92f))
        walls[101].translateGlobal(Vector3f(8f, 0f, 100f))

        walls[102].translateGlobal(Vector3f(16f, 0f, 4f))
        walls[103].translateGlobal(Vector3f(16f, 0f, 28f))
        walls[104].translateGlobal(Vector3f(16f, 0f, 52f))
        walls[105].translateGlobal(Vector3f(16f, 0f, 60f))
        walls[106].translateGlobal(Vector3f(16f, 0f, 84f))
        walls[107].translateGlobal(Vector3f(16f, 0f, 100f))
        walls[108].translateGlobal(Vector3f(16f, 0f, 108f))

        walls[109].translateGlobal(Vector3f(24f, 0f, 44f))
        walls[110].translateGlobal(Vector3f(24f, 0f, 52f))
        walls[111].translateGlobal(Vector3f(24f, 0f, 68f))
        walls[112].translateGlobal(Vector3f(24f, 0f, 92f))

        walls[113].translateGlobal(Vector3f(32f, 0f, 36f))
        walls[114].translateGlobal(Vector3f(32f, 0f, 60f))
        walls[115].translateGlobal(Vector3f(32f, 0f, 76f))
        walls[116].translateGlobal(Vector3f(32f, 0f, 84f))
        walls[117].translateGlobal(Vector3f(32f, 0f, 92f))
        walls[118].translateGlobal(Vector3f(32f, 0f, 100f))

        walls[119].translateGlobal(Vector3f(40f, 0f, 44f))
        walls[120].translateGlobal(Vector3f(40f, 0f, 52f))
        walls[121].translateGlobal(Vector3f(40f, 0f, 68f))
        walls[122].translateGlobal(Vector3f(40f, 0f, 76f))
        walls[123].translateGlobal(Vector3f(40f, 0f, 84f))
        walls[124].translateGlobal(Vector3f(40f, 0f, 92f))
        walls[125].translateGlobal(Vector3f(40f, 0f, 108f))
        walls[126].translateGlobal(Vector3f(40f, 0f, 116f))

        walls[127].translateGlobal(Vector3f(48f, 0f, 20f))
        walls[128].translateGlobal(Vector3f(48f, 0f, 28f))
        walls[129].translateGlobal(Vector3f(48f, 0f, 60f))
        walls[130].translateGlobal(Vector3f(48f, 0f, 68f))
        walls[131].translateGlobal(Vector3f(48f, 0f, 76f))
        walls[132].translateGlobal(Vector3f(48f, 0f, 84f))
        walls[133].translateGlobal(Vector3f(48f, 0f, 100f))
        walls[134].translateGlobal(Vector3f(48f, 0f, 108f))

        walls[135].translateGlobal(Vector3f(56f, 0f, 12f))
        walls[136].translateGlobal(Vector3f(56f, 0f, 28f))
        walls[137].translateGlobal(Vector3f(56f, 0f, 36f))
        walls[138].translateGlobal(Vector3f(56f, 0f, 52f))
        walls[139].translateGlobal(Vector3f(56f, 0f, 60f))
        walls[140].translateGlobal(Vector3f(56f, 0f, 68f))
        walls[141].translateGlobal(Vector3f(56f, 0f, 76f))
        walls[142].translateGlobal(Vector3f(56f, 0f, 84f))
        walls[143].translateGlobal(Vector3f(56f, 0f, 92f))
        walls[144].translateGlobal(Vector3f(56f, 0f, 116f))

        walls[145].translateGlobal(Vector3f(64f, 0f, 4f))
        walls[146].translateGlobal(Vector3f(64f, 0f, 44f))
        walls[147].translateGlobal(Vector3f(64f, 0f, 68f))
        walls[148].translateGlobal(Vector3f(64f, 0f, 76f))
        walls[149].translateGlobal(Vector3f(64f, 0f, 84f))
        walls[150].translateGlobal(Vector3f(64f, 0f, 92f))
        walls[151].translateGlobal(Vector3f(64f, 0f, 100f))
        walls[152].translateGlobal(Vector3f(64f, 0f, 108f))
        walls[153].translateGlobal(Vector3f(64f, 0f, 116f))

        walls[154].translateGlobal(Vector3f(72f, 0f, 12f))
        walls[155].translateGlobal(Vector3f(72f, 0f, 20f))
        walls[156].translateGlobal(Vector3f(72f, 0f, 28f))
        walls[157].translateGlobal(Vector3f(72f, 0f, 36f))
        walls[158].translateGlobal(Vector3f(72f, 0f, 52f))
        walls[159].translateGlobal(Vector3f(72f, 0f, 60f))
        walls[160].translateGlobal(Vector3f(72f, 0f, 68f))
        walls[161].translateGlobal(Vector3f(72f, 0f, 76f))
        walls[162].translateGlobal(Vector3f(72f, 0f, 84f))
        walls[163].translateGlobal(Vector3f(72f, 0f, 108f))
        walls[164].translateGlobal(Vector3f(72f, 0f, 116f))

        walls[165].translateGlobal(Vector3f(80f, 0f, 20f))
        walls[166].translateGlobal(Vector3f(80f, 0f, 44f))
        walls[167].translateGlobal(Vector3f(80f, 0f, 52f))
        walls[168].translateGlobal(Vector3f(80f, 0f, 68f))
        walls[169].translateGlobal(Vector3f(80f, 0f, 84f))
        walls[170].translateGlobal(Vector3f(80f, 0f, 92f))
        walls[171].translateGlobal(Vector3f(80f, 0f, 100f))
        walls[172].translateGlobal(Vector3f(80f, 0f, 108f))

        walls[173].translateGlobal(Vector3f(88f, 0f, 12f))
        walls[174].translateGlobal(Vector3f(88f, 0f, 20f))
        walls[175].translateGlobal(Vector3f(88f, 0f, 28f))
        walls[176].translateGlobal(Vector3f(88f, 0f, 52f))
        walls[177].translateGlobal(Vector3f(88f, 0f, 76f))
        walls[178].translateGlobal(Vector3f(88f, 0f, 108f))
        walls[179].translateGlobal(Vector3f(88f, 0f, 116f))

        walls[180].translateGlobal(Vector3f(96f, 0f, 12f))
        walls[181].translateGlobal(Vector3f(96f, 0f, 20f))
        walls[182].translateGlobal(Vector3f(96f, 0f, 60f))
        walls[183].translateGlobal(Vector3f(96f, 0f, 68f))
        walls[184].translateGlobal(Vector3f(96f, 0f, 76f))
        walls[185].translateGlobal(Vector3f(96f, 0f, 84f))
        walls[186].translateGlobal(Vector3f(96f, 0f, 100f))
        walls[187].translateGlobal(Vector3f(96f, 0f, 108f))

        walls[188].translateGlobal(Vector3f(104f, 0f, 28f))
        walls[189].translateGlobal(Vector3f(104f, 0f, 92f))
        walls[190].translateGlobal(Vector3f(104f, 0f, 108f))
        walls[191].translateGlobal(Vector3f(104f, 0f, 116f))

        walls[192].translateGlobal(Vector3f(112f, 0f, 28f))
        walls[193].translateGlobal(Vector3f(112f, 0f, 36f))
        walls[194].translateGlobal(Vector3f(112f, 0f, 52f))
        walls[195].translateGlobal(Vector3f(112f, 0f, 84f))

        wallsBig[45].translateGlobal(Vector3f(120f, 0f, 4f))
        wallsBig[46].translateGlobal(Vector3f(120f, 0f, 12f))
        wallsBig[47].translateGlobal(Vector3f(120f, 0f, 20f))
        wallsBig[48].translateGlobal(Vector3f(120f, 0f, 28f))
        wallsBig[49].translateGlobal(Vector3f(120f, 0f, 36f))
        wallsBig[50].translateGlobal(Vector3f(120f, 0f, 44f))
        wallsBig[51].translateGlobal(Vector3f(120f, 0f, 52f))
        if (doorDespawn != 52) wallsBig[52].translateGlobal(Vector3f(120f, 0f, 60f)) else wallsBig[52].translateGlobal(Vector3f(0f, -10f, 0f))
        wallsBig[53].translateGlobal(Vector3f(120f, 0f, 68f))
        wallsBig[54].translateGlobal(Vector3f(120f, 0f, 76f))
        wallsBig[55].translateGlobal(Vector3f(120f, 0f, 84f))
        wallsBig[56].translateGlobal(Vector3f(120f, 0f, 92f))
        wallsBig[57].translateGlobal(Vector3f(120f, 0f, 100f))
        wallsBig[58].translateGlobal(Vector3f(120f, 0f, 108f))
        wallsBig[59].translateGlobal(Vector3f(120f, 0f, 116f))

        pillarsBig[0].translateGlobal(Vector3f(0f, 100f, 0f))
        pillarsBig[1].translateGlobal(Vector3f(120f, 0f, 0f))
        pillarsBig[2].translateGlobal(Vector3f(0f, 0f, 120f))
        pillarsBig[3].translateGlobal(Vector3f(120f, 0f, 120f))

        var x = 8f
        var y = 8f
        var i = 0
        while (i < 196) {

                if(x < 120f ) {
                    pillars[i].translateGlobal(Vector3f(x, 0f, y))
                    x += 8f
                    i++
                }
                else if (x > 119f && y < 120f) {
                    y += 8f
                    x = 8f

                }

        }


    }
}
