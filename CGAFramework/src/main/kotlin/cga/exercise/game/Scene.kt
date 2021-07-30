package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.*
import org.joml.Math.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.EnumSet.range
import kotlin.math.roundToLong


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram
    private val walls = mutableListOf<Renderable>()
    private val checkList = mutableListOf<Boolean>()

    /** Renderables */
    var mapcameraobjekt: Renderable
    var player: Renderable
    var lantern : Renderable
    var mac : Renderable
    var buttonBase : Renderable
    var skyBox : Renderable
    var gate : Renderable
    var leftDoor : Renderable
    var rightDoor : Renderable
    var dd1 : Renderable
    var dd2 : Renderable

    /** Labyrint */
    private val meshListMazeFloor = mutableListOf<Mesh>()
    private val meshListMazeTop = mutableListOf<Mesh>()

    val mazeFloor : Renderable
    val mazeTop : Renderable

    var moveablewall :Renderable

    val camera = TronCamera()

    val objList = mutableListOf<Renderable>()
    val wallVerticalHitbox = mutableListOf( 0.5f, 5.0f )
    val wallHorizontalHitbox = mutableListOf( 5.0f, 0.5f )
    val buttonHitbox = mutableListOf( 0.2f, 0.5f )
    val doorHitbox = mutableListOf( 1.5f, 0.3f )

    var test = false

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

        /** Labyrint */

        val objResMazeFloor : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeTop.obj")
        val objMeshListMazeFloor : MutableList<OBJLoader.OBJMesh> = objResMazeFloor.objects[0].meshes

        val objResMazeTop : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeTop.obj")
        val objMeshListMazeTop : MutableList<OBJLoader.OBJMesh> = objResMazeTop.objects[0].meshes

        val stride = 8 * 4
        val attrPos = VertexAttribute(3, GL_FLOAT, stride, 0)
        val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3 * 4)
        val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5 * 4)

        val vertexAttributes = arrayOf(attrPos,attrTC, attrNorm)

        val groundEmitTexture = Texture2D("assets/textures/brick_shit.png", true)
        val groundDiffTexture = Texture2D("assets/textures/ground_diff.png", true)
        val groundSpecTexture = Texture2D("assets/textures/ground_spec.png", true)

        groundEmitTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        groundDiffTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        groundSpecTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        /** Labyrint */
        val mazeFloorTexture = Texture2D("assets/textures/pexels_shit.png", true)
        val mazeTopTexture = Texture2D("assets/textures/pexels_shit.png", true)

        mazeFloorTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        mazeTopTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundShininess = 60f
        val groundTCMultiplier = Vector2f(64f)

        /** Labyrint */
        val mazeFloorMaterial = Material(groundDiffTexture, mazeFloorTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeTopMaterial = Material(groundDiffTexture, mazeTopTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)

        /** Labyrint mesh */
        for (mesh in objMeshListMazeFloor) {
            meshListMazeFloor.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeFloorMaterial))
        }
        for (mesh in objMeshListMazeTop) {
            meshListMazeTop.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeTopMaterial))
        }

         /** Labyrint Positionen und Render */

        mazeFloor = Renderable(meshListMazeFloor)
        mazeTop = Renderable(meshListMazeTop)
        
        mazeFloor.scaleLocal(Vector3f(3f))
        mazeFloor.translateLocal(Vector3f(0f,-4.5f,0f))

        mazeTop.translateLocal(Vector3f(0f,1f,0f))

        /** Modelloader */
        player = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mapcameraobjekt = ModelLoader.loadModel("assets/among_us_obj/among us.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        lantern = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(-0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mac = ModelLoader.loadModel("assets/models/mac10.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        buttonBase = ModelLoader.loadModel("assets/ButtonBase/Buttonbase.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        skyBox = ModelLoader.loadModel("assets/SkyBox/skybox.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")

        moveablewall = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        gate = ModelLoader.loadModel("assets/Gate/Gate.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        leftDoor = ModelLoader.loadModel("assets/Gate/Door.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        rightDoor = ModelLoader.loadModel("assets/Gate/Door.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")

        dd1 = ModelLoader.loadModel("assets/Gate/Door.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        dd2 = ModelLoader.loadModel("assets/Gate/Door.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")



        var x = 0
        while (x < 80) {
            walls.add(ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }

        skyBox.scaleLocal(Vector3f(5f))
        skyBox.translateGlobal(Vector3f(-5f, 0f, -5f))

        /** Camerastart Position */
        // camera.parent = player
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

        /** Button */
        buttonBase.translateGlobal(Vector3f(-15.0f, -2f, -1.0f))
        buttonBase.rotateLocal(0.0f, toRadians(90f), 0f)

        /** Mauern */
        camera.parent = moveablewall
        moveablewall.scaleLocal(Vector3f(0.3f))
        moveablewall.translateLocal(Vector3f(-30f, 0.0f, -9.0f))

        var f = 0
        while (f < 80) {
            walls[f].scaleLocal(Vector3f(0.5f))
            walls[f].translateLocal(Vector3f(0f, 0f, 0f))
            objList.add(walls[f])
            f++
        }

        objList.add(buttonBase)
        //objList.add(gate)
        objList.add(leftDoor)
        objList.add(rightDoor)

        /** Horizontale Wände */
        walls[0].translateLocal(Vector3f(30f, 0.0f, 0f))
        walls[1].translateLocal(Vector3f(-20f, 0.0f, 0f))
        walls[2].translateLocal(Vector3f(-40f, 0.0f, 0f))
        walls[3].translateLocal(Vector3f(-60f, 0.0f, 0f))
        walls[4].translateLocal(Vector3f(-80f, 0.0f, 0f))
        walls[5].translateLocal(Vector3f(-100f, 0.0f, 0f))
        walls[6].translateLocal(Vector3f(-120f, 0.0f, 0f))
        walls[7].translateLocal(Vector3f(-140f, 0.0f, 0f))

        walls[8].translateLocal(Vector3f(-40f, 0.0f, -20f))
        walls[9].translateLocal(Vector3f(-80f, 0.0f, -20f))

        walls[10].translateLocal(Vector3f(-20f, 0.0f, -40f))
        walls[11].translateLocal(Vector3f(-40f, 0.0f, -40f))
        walls[12].translateLocal(Vector3f(-80f, 0.0f, -40f))
        walls[13].translateLocal(Vector3f(-120f, 0.0f, -40f))

        walls[14].translateLocal(Vector3f( 0f, 0.0f, -60f))
        walls[15].translateLocal(Vector3f(-40f, 0.0f, -60f))
        walls[16].translateLocal(Vector3f(-60f, 0.0f, -60f))
        walls[17].translateLocal(Vector3f(-100f, 0.0f, -60f))
        walls[18].translateLocal(Vector3f(-140f, 0.0f, -60f))

        walls[19].translateLocal(Vector3f( -20f, 0.0f, -80f))
        walls[20].translateLocal(Vector3f(-40f, 0.0f, -80f))
        walls[21].translateLocal(Vector3f(-60f, 0.0f, -80f))
        walls[22].translateLocal(Vector3f(-80f, 0.0f, -80f))

        walls[23].translateLocal(Vector3f( -20f, 0.0f, -100f))
        walls[24].translateLocal(Vector3f(-40f, 0.0f, -100f))
        walls[25].translateLocal(Vector3f(-60f, 0.0f, -100f))
        walls[26].translateLocal(Vector3f(-100f, 0.0f, -100f))
        walls[27].translateLocal(Vector3f(-120f, 0.0f, -100f))

        walls[28].translateLocal(Vector3f( 0f, 0.0f, -120f))
        walls[29].translateLocal(Vector3f(-80f, 0.0f, -120f))
        walls[30].translateLocal(Vector3f(-100f, 0.0f, -120f))
        walls[31].translateLocal(Vector3f(-120f, 0.0f, -120f))

        walls[32].translateLocal(Vector3f( -40f, 0.0f, -140f))
        walls[33].translateLocal(Vector3f(-80f, 0.0f, -140f))
        walls[34].translateLocal(Vector3f(-100f, 0.0f, -140f))
        walls[35].translateLocal(Vector3f(-120f, 0.0f, -140f))

        walls[36].translateLocal(Vector3f( 0f, 0.0f, -160f))
        walls[37].translateLocal(Vector3f(-20f, 0.0f, -160f))
        walls[38].translateLocal(Vector3f(-40f, 0.0f, -160f))
        walls[39].translateLocal(Vector3f(-60f, 0.0f, -160f))
        walls[40].translateLocal(Vector3f(-80f, 0.0f, -160f))
        walls[41].translateLocal(Vector3f(-100f, 0.0f, -160f))
        walls[42].translateLocal(Vector3f(-120f, 0.0f, -160f))

        walls[43].translateLocal(Vector3f(10f, 0f, -10f))
        walls[43].rotateLocal(0f, toRadians(90f), 0f)
        walls[44].translateLocal(Vector3f(-10f, 0f, -10f))
        walls[44].rotateLocal(0f, toRadians(90f), 0f)
        walls[45].translateLocal(Vector3f(-90f, 0f, -10f))
        walls[45].rotateLocal(0f, toRadians(90f), 0f)
        walls[46].translateLocal(Vector3f(-110f, 0f, -10f))
        walls[46].rotateLocal(0f, toRadians(90f), 0f)
        walls[47].translateLocal(Vector3f(-150f, 0f, -10f))
        walls[47].rotateLocal(0f, toRadians(90f), 0f)

        walls[48].translateLocal(Vector3f(10f, 0f, -30f))
        walls[48].rotateLocal(0f, toRadians(90f), 0f)
        walls[49].translateLocal(Vector3f(-30f, 0f, -30f))
        walls[49].rotateLocal(0f, toRadians(90f), 0f)
        walls[50].translateLocal(Vector3f(-70f, 0f, -30f))
        walls[50].rotateLocal(0f, toRadians(90f), 0f)
        walls[51].translateLocal(Vector3f(-110f, 0f, -30f))
        walls[51].rotateLocal(0f, toRadians(90f), 0f)
        walls[52].translateLocal(Vector3f(-130f, 0f, -30f))
        walls[52].rotateLocal(0f, toRadians(90f), 0f)
        walls[53].translateLocal(Vector3f(-150f, 0f, -30f))
        walls[53].rotateLocal(0f, toRadians(90f), 0f)

        walls[54].translateLocal(Vector3f(10f, 0f, -50f))
        walls[54].rotateLocal(0f, toRadians(90f), 0f)
        walls[55].translateLocal(Vector3f(-10f, 0f, -50f))
        walls[55].rotateLocal(0f, toRadians(90f), 0f)
        walls[56].translateLocal(Vector3f(-50f, 0f, -50f))
        walls[56].rotateLocal(0f, toRadians(90f), 0f)
        walls[57].translateLocal(Vector3f(-90f, 0f, -50f))
        walls[57].rotateLocal(0f, toRadians(90f), 0f)
        walls[58].translateLocal(Vector3f(-150f, 0f, -50f))
        walls[58].rotateLocal(0f, toRadians(90f), 0f)

        walls[59].translateLocal(Vector3f(10f, 0f, -70f))
        walls[59].rotateLocal(0f, toRadians(90f), 0f)
        walls[60].translateLocal(Vector3f(-10f, 0f, -70f))
        walls[60].rotateLocal(0f, toRadians(90f), 0f)
        walls[61].translateLocal(Vector3f(-110f, 0f, -70f))
        walls[61].rotateLocal(0f, toRadians(90f), 0f)
        walls[62].translateLocal(Vector3f(-130f, 0f, -70f))
        walls[62].rotateLocal(0f, toRadians(90f), 0f)
        walls[63].translateLocal(Vector3f(-150f, 0f, -70f))
        walls[63].rotateLocal(0f, toRadians(90f), 0f)

        walls[64].translateLocal(Vector3f(10f, 0f, -90f))
        walls[64].rotateLocal(0f, toRadians(90f), 0f)
        walls[65].translateLocal(Vector3f(-130f, 0f, -90f))
        walls[65].rotateLocal(0f, toRadians(90f), 0f)
        walls[66].translateLocal(Vector3f(-150f, 0f, -90f))
        walls[66].rotateLocal(0f, toRadians(90f), 0f)

        walls[67].translateLocal(Vector3f(10f, 0f, -110f))
        walls[67].rotateLocal(0f, toRadians(90f), 0f)
        walls[68].translateLocal(Vector3f(-10f, 0f, -110f))
        walls[68].rotateLocal(0f, toRadians(90f), 0f)
        walls[69].translateLocal(Vector3f(-30f, 0f, -110f))
        walls[69].rotateLocal(0f, toRadians(90f), 0f)
        walls[70].translateLocal(Vector3f(-70f, 0f, -110f))
        walls[70].rotateLocal(0f, toRadians(90f), 0f)
        walls[71].translateLocal(Vector3f(-150f, 0f, -110f))
        walls[71].rotateLocal(0f, toRadians(90f), 0f)

        walls[72].translateLocal(Vector3f(10f, 0f, -130f))
        walls[72].rotateLocal(0f, toRadians(90f), 0f)
        walls[73].translateLocal(Vector3f(-10f, 0f, -130f))
        walls[73].rotateLocal(0f, toRadians(90f), 0f)
        walls[74].translateLocal(Vector3f(-30f, 0f, -130f))
        walls[74].rotateLocal(0f, toRadians(90f), 0f)
        walls[75].translateLocal(Vector3f(-50f, 0f, -130f))
        walls[75].rotateLocal(0f, toRadians(90f), 0f)
        walls[76].translateLocal(Vector3f(-70f, 0f, -130f))
        walls[76].rotateLocal(0f, toRadians(90f), 0f)
        walls[77].translateLocal(Vector3f(-150f, 0f, -130f))
        walls[77].rotateLocal(0f, toRadians(90f), 0f)

        walls[78].translateLocal(Vector3f(10f, 0f, -150f))
        walls[78].rotateLocal(0f, toRadians(90f), 0f)
        walls[79].translateLocal(Vector3f(-150f, 0f, -150f))
        walls[79].rotateLocal(0f, toRadians(90f), 0f)

        gate.scaleLocal(Vector3f(0.5f))
        gate.translateGlobal(Vector3f(0.0f, 0.0f, 0.0f))

        leftDoor.scaleLocal(Vector3f(0.5f))
        leftDoor.translateGlobal(Vector3f(2.0f, 0.0f, 0.0f))
        rightDoor.scaleLocal(Vector3f(0.5f))
        rightDoor.translateGlobal(Vector3f(-2.0f, 0.0f, 0.0f))

        dd1.scaleLocal(Vector3f(0.5f))
        dd1.translateGlobal(Vector3f(4.0f, 0.0f, 0.0f))
        dd2.scaleLocal(Vector3f(0.5f))
        dd2.translateGlobal(Vector3f(-4.0f, 0.0f, 0.0f))


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
        mazeFloor.render(tronShader)
        mapcameraobjekt.render(tronShader)
        lantern.render(tronShader)
        //mac.render(tronShader)
        buttonBase.render(tronShader)
        skyBox.render(tronShader)
        gate.render(tronShader)
        leftDoor.render(tronShader)
        rightDoor.render(tronShader)

        var z = 0
        while (z < 80) {
            walls[z].render(tronShader)
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
                        collisionTest2(moveablewall, objList[x])
                        collisionTest2(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(0.2f, 0.0f, 0.2f))

                }
                else if(window.getKeyState(GLFW_KEY_W)) {
                    while (x < objList.size) {
                        collisionTest2(moveablewall, objList[x])
                        collisionTest2(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(0.2f, 0.0f, -0.2f))

                }
                else {
                    while (x < objList.size) {
                        collisionTest2(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(0.2f, 0.0f, 0.0f))
                }

            }

            window.getKeyState(GLFW_KEY_S) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {

                while (x < objList.size) {
                    collisionTest2(moveablewall, objList[x])
                    x++
                }
                moveablewall.translateLocal(Vector3f(0.0f, 0.0f, 0.2f))

            }

            window.getKeyState(GLFW_KEY_W) && !window.getKeyState(GLFW_KEY_D) && !window.getKeyState(GLFW_KEY_A) -> {

                while (x < objList.size) {
                    collisionTest2(moveablewall, objList[x])
                    x++
                }
                moveablewall.translateLocal(Vector3f(0.0f, 0.0f, -0.2f))

            }

            window.getKeyState(GLFW_KEY_A) -> {

                if(window.getKeyState(GLFW_KEY_S)) {
                    while (x < objList.size) {
                        collisionTest2(moveablewall, objList[x])
                        collisionTest2(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(-0.2f, 0.0f, 0.2f))

                }
                else if (window.getKeyState(GLFW_KEY_W)) {
                    while (x < objList.size) {
                        collisionTest2(moveablewall, objList[x])
                        collisionTest2(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(-0.2f, 0.0f, -0.2f))
                }
                else {
                    while (x < objList.size) {
                        collisionTest2(moveablewall, objList[x])
                        x++
                    }
                    moveablewall.translateLocal(Vector3f(-0.2f, 0.0f, 0.0f))
                }

            }

            test -> {
                if (objList[81].getWorldPosition().x < dd1.getWorldPosition().x || objList[82].getWorldPosition().x > dd2.getWorldPosition().x) {
                    objList[81].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    objList[82].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                }
            }

            !test -> {
                if (objList[81].getWorldPosition().x > z || objList[82].getWorldPosition().x < z2) {
                    objList[81].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    objList[82].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
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
                mazeTop.scaleLocal(Vector3f(0.000005f))
                camera.parent = mapcameraobjekt
            }

        }

    }

    fun collisionTest2(firstMesh: Renderable, secoundMesh: Renderable) {

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
            /** Horizontale Wände */
            if (x == secoundMesh && count < 43) {
                minusX = secoundMesh.getWorldPosition().x - wallHorizontalHitbox[0]
                plusX = secoundMesh.getWorldPosition().x + wallHorizontalHitbox[0]
                minusZ = secoundMesh.getWorldPosition().z - wallHorizontalHitbox[1]
                plusZ = secoundMesh.getWorldPosition().z + wallHorizontalHitbox[1]
            }
            /** Vertikale Wände */
            if (x == secoundMesh && count > 42 && count < 80) {
                minusX = secoundMesh.getWorldPosition().x - wallVerticalHitbox[0]
                plusX = secoundMesh.getWorldPosition().x + wallVerticalHitbox[0]
                minusZ = secoundMesh.getWorldPosition().z - wallVerticalHitbox[1]
                plusZ = secoundMesh.getWorldPosition().z + wallVerticalHitbox[1]
            }
            /** Button */
            if (x == secoundMesh && count == 80) {
                minusX = secoundMesh.getWorldPosition().x - buttonHitbox[0]
                plusX = secoundMesh.getWorldPosition().x + buttonHitbox[0]
                minusZ = secoundMesh.getWorldPosition().z - buttonHitbox[1]
                plusZ = secoundMesh.getWorldPosition().z + buttonHitbox[1]
            }
            /** Linkes und rechtes Tor */
            if (x == secoundMesh && count > 80) {
                minusX = secoundMesh.getWorldPosition().x - doorHitbox[0]
                plusX = secoundMesh.getWorldPosition().x + doorHitbox[0]
                minusZ = secoundMesh.getWorldPosition().z - doorHitbox[1]
                plusZ = secoundMesh.getWorldPosition().z + doorHitbox[1]
            }
            count++
        }

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

    }

    fun buttonPress(firstMesh: Renderable, secoundMesh: Renderable): Boolean {

        val minusX: Float
        val plusX: Float
        val minusZ: Float

        val h: Boolean


        var bool = true

        minusX = secoundMesh.getWorldPosition().x - buttonHitbox[0]
        plusX = secoundMesh.getWorldPosition().x + buttonHitbox[0]
        minusZ = secoundMesh.getWorldPosition().z - 2

        h = !(firstMesh.getWorldPosition().z + 1 > minusZ && firstMesh.getWorldPosition().z - 1 < minusZ)
                || firstMesh.getWorldPosition().x - 0.8 > plusX
                || firstMesh.getWorldPosition().x + 0.8 < minusX

        if (!h) {
            bool = false
        }

        return bool

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

        if(!buttonPress(moveablewall, objList[80])) {

            if(window.getKeyState(GLFW_KEY_E) && !test) test = true
            else if(window.getKeyState(GLFW_KEY_E) && test) test = false

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
            // moveablewall.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.05f), 0f)
            /** hoch-runter */
            camera.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.05f), 0f, 0f)
        }
        notFirstFrame = true

    }

    fun cleanup() {}
}
