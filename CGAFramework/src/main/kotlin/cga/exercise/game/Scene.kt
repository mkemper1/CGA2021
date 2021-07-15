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
import org.lwjgl.system.CallbackI
import java.util.*
import kotlin.concurrent.thread


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram
    private val walls = mutableListOf<Renderable>()

    /** Renderables */
    var mapcameraobjekt: Renderable
    var player: Renderable
    var lantern : Renderable
    var cycle : Renderable
    var mac : Renderable

    /** Labyrint */
    private val meshListMazeFloor = mutableListOf<Mesh>()
    private val meshListMazeTop = mutableListOf<Mesh>()

    val mazeFloor : Renderable
    val mazeTop : Renderable

    var moveablewall :Renderable


    val camera = TronCamera()

    val objList = mutableListOf<Renderable>()
    val wallHitbox = mutableListOf<Float>()

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
    var alteslabyrintcheck = true

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
        cycle = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        player = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mapcameraobjekt = ModelLoader.loadModel("assets/among_us_obj/among us.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        lantern = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(-0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mac = ModelLoader.loadModel("assets/models/mac10.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")

        moveablewall = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")


        var x = 0

        while (x < 8) {
            walls.add(ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!"))
            x++
        }




        /** Camerastart Position */
        camera.parent = player
        camera.translateLocal(Vector3f(0f, 1.4f, 0f))
        

        /** Playerstart Position */ 0 
        player.translateLocal(Vector3f(0f, 0f, -25f))
        player.rotateLocal(0f, toRadians(180f), 0f)

        /** Orbitalecamera Position */
        mapcameraobjekt.scaleLocal(Vector3f(0.008f))
        mapcameraobjekt.translateLocal(Vector3f(10f, 3000f, 0f))

        /** Pistole */
        mac.scaleLocal(Vector3f(0.008f))
        mac.translateLocal(Vector3f(20f, 150f, -35f))
        mac.parent = player

        /** Objektplatzierung */
        cycle.scaleLocal(Vector3f(1.8f))
        lantern.translateLocal(Vector3f(5f, 1f, 0f))


        /** Lichter */
        pointLight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(1f, 1f, 0f), Vector3f(1f, 0.5f, 0.1f))
        spotLight = SpotLight(Vector3f(0f, 1f, -1f), Vector3f(2f,2f,0.1f), Vector3f(0.1f, 0.01f, 0.01f), Vector2f(toRadians(150f), toRadians(30f)))
        spotLight.rotateLocal(toRadians(-10f), PI.toFloat(),0f)
        pointLight.parent = mapcameraobjekt
        spotLight.parent = mapcameraobjekt








        /** Mauern */

        moveablewall.scaleLocal(Vector3f(0.5f))
        moveablewall.translateLocal(Vector3f(-35f, 0.0f, 0f))



        var u = 0

        while (u < 8) {
            walls[u].scaleLocal(Vector3f(0.5f))
            objList.add(walls[u])
            u++
        }


        walls[0].translateLocal(Vector3f(0f, 0.0f, 0f))
        walls[1].translateLocal(Vector3f(20f, 0.0f, 0f))
        walls[2].translateLocal(Vector3f(40f, 0.0f, 0f))
        walls[3].translateLocal(Vector3f(60f, 0.0f, 0f))

        walls[4].translateLocal(Vector3f(20f, 0.0f, -20f))
        walls[5].translateLocal(Vector3f(40f, 0.0f, -20f))
        walls[6].translateLocal(Vector3f(60f, 0.0f, -20f))

        walls[7].translateLocal(Vector3f(70f, 0.0f, -10f))
        walls[7].rotateLocal(0f, toRadians(90f), 0f)



        /** Mauern Hitbox */

        wallHitbox.add(moveablewall.getPosition().x)
        wallHitbox.add(moveablewall.getPosition().z)

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

        tronShader.setUniform("farbe", Vector3f(abs(sin(t)), abs(sin(t/2f)), abs(sin(t/3f))))
        cycle.render(tronShader)

        tronShader.setUniform("farbe", Vector3f(0f,1f,0f))
        tronShader.setUniform("farbe", Vector3f(1f,1f,1f))
        mazeFloor.render(tronShader)
        mazeTop.render(tronShader)
        mapcameraobjekt.render(tronShader)
        lantern.render(tronShader)
        mac.render(tronShader)
        player.render(tronShader)

        moveablewall.render(tronShader)


        var z = 0

        while (z < 8) {
            walls[z].render(tronShader)
            z++
        }

    }







    fun update(dt: Float, t: Float) {

        pointLight.lightColor = Vector3f(abs(sin(t/3f)), abs(sin(t/4f)), abs(sin(t/2)))















        
        when {
            /** Movement */
            window.getKeyState(GLFW_KEY_W) -> {
                if (window.getKeyState(GLFW_KEY_A)) {
                    player.translateLocal(Vector3f(2f * -dt, 0f, 0f))
                }
                if (window.getKeyState(GLFW_KEY_D)) {
                    player.translateLocal(Vector3f(2f * dt, 0f, 0f))
                }
                if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) {
                    player.translateLocal(Vector3f(0f, 0f, 4 * -dt))
                }
                player.translateLocal(Vector3f(0f, 0f, 2 * -dt))
            }
            window.getKeyState(GLFW_KEY_S) -> {
                if (window.getKeyState(GLFW_KEY_A)) {
                    player.rotateLocal(0f, 2f * dt, 0f)
                }
                if (window.getKeyState(GLFW_KEY_D)) {
                    player.rotateLocal(0f, 2 * -dt, 0f)
                }
                player.translateLocal(Vector3f(0f, 0f, 2f * dt))
            }
            window.getKeyState(GLFW_KEY_A) -> {
                player.translateLocal(Vector3f(2f * -dt, 0f, 0f))
            }
            window.getKeyState(GLFW_KEY_D) -> {
                player.translateLocal(Vector3f(2f * dt, 0f, 0f))
            }

            /** wall Bewegung */

            window.getKeyState(GLFW_KEY_T) -> {
                for (obj in objList) {
                    if (collisionTest(moveablewall, obj, 'T')) moveablewall.translateLocal(
                        Vector3f(
                            0.05f,
                            0.0f,
                            0.0f
                        )
                    ) else moveablewall.translateLocal(Vector3f(-0.05f, 0.0f, 0.0f))
                }
            }

            window.getKeyState(GLFW_KEY_H) -> {
                for (obj in objList) {
                    if (collisionTest(moveablewall, obj, 'H')) moveablewall.translateLocal(
                        Vector3f(
                            0.0f,
                            0.0f,
                            0.05f
                        )
                    ) else moveablewall.translateLocal(Vector3f(0.0f, 0.0f, -0.05f))
                }
            }

            window.getKeyState(GLFW_KEY_F) -> {
                for (obj in objList) {
                    if (collisionTest(moveablewall, obj, 'F')) moveablewall.translateLocal(
                        Vector3f(
                            0.0f,
                            0.0f,
                            -0.05f
                        )
                    ) else moveablewall.translateLocal(Vector3f(0.0f, 0.0f, 0.05f))
                }

            }

            window.getKeyState(GLFW_KEY_G) -> {
                for (obj in objList) {
                    if (collisionTest(moveablewall, obj, 'G')) moveablewall.translateLocal(
                        Vector3f(
                            -0.05f,
                            0.0f,
                            0.0f
                        )
                    ) else moveablewall.translateLocal(Vector3f(0.05f, 0.0f, 0.0f))
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

    fun collisionTest(firstMesh: Renderable, secoundMesh: Renderable, key: Char): Boolean {

        var move = false

        when {

            key == 'T' -> {
                if (firstMesh.getPosition().x + 1 > secoundMesh.getPosition().x - 1 && firstMesh.getPosition().x - 1 < secoundMesh.getPosition().x - 1) {
                    if (firstMesh.getPosition().z - 1 > secoundMesh.getPosition().z + 0.9) move = true
                    if (firstMesh.getPosition().z + 1 < secoundMesh.getPosition().z - 0.9) move = true
                } else move = true
            }

            key == 'H' -> {
                if(firstMesh.getWorldPosition().z + 1 > secoundMesh.getPosition().z - 1 && firstMesh.getWorldPosition().z - 1 < secoundMesh.getWorldPosition().z - 1) {
                    if (firstMesh.getWorldPosition().x - 1 > secoundMesh.getPosition().x + 0.9) move = true
                    if (firstMesh.getWorldPosition().x + 1 < secoundMesh.getPosition().x - 0.9) move = true
                } else move = true
            }

            key == 'F' -> {
                if(firstMesh.getPosition().z - 1 < secoundMesh.getPosition().z + 1 && firstMesh.getPosition().z + 1 > secoundMesh.getPosition().z + 1) {
                    if (firstMesh.getPosition().x - 1 > secoundMesh.getPosition().x + 0.9) move = true
                    if (firstMesh.getPosition().x + 1 < secoundMesh.getPosition().x - 0.9) move = true
                } else move = true
            }

            key == 'G' -> {
                if (firstMesh.getPosition().x - 1 < secoundMesh.getPosition().x + 1 && firstMesh.getPosition().x + 1 > secoundMesh.getPosition().x + 1) {
                    if (firstMesh.getPosition().z - 1 > secoundMesh.getPosition().z + 0.9) move = true
                    if (firstMesh.getPosition().z + 1 < secoundMesh.getPosition().z - 0.9) move = true
                } else move = true
            }
        }

        return move

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
    }

    fun onMouseMove(xpos: Double, ypos: Double) {
        val deltaX = xpos - oldMousePosX
        var deltaY = ypos - oldMousePosY
        oldMousePosX = xpos
        oldMousePosY = ypos


        /** Camera 1 */
        if(notFirstFrame && cameracheck1 == true) {
            /** links-rechts */
            player.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.06f), 0f)
            /** hoch-runter */
            camera.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.05f), 0f, 0f)
            /** Fliegen mit Taste F */
            when {
                window.getKeyState(GLFW_KEY_F) -> {
                player.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.1f), 0f, 0f)
                    player.rotateLocal(0f, toRadians(deltaX.toFloat() * -0.06f), 0f)
                }
            }
        }
        notFirstFrame = true

        /** Camera 4 */
        if(notFirstFrame && cameracheck4 == true) {
            /** links-rechts */
            camera.rotateAroundPoint(0f, toRadians(deltaX.toFloat() * -0.03f), 0f, Vector3f(0f))
            /** hoch-runter */
            camera.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.05f), 0f, 0f)
        }
        notFirstFrame = true
    }




    fun cleanup() {}
}
