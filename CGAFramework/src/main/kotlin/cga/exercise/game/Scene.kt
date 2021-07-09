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
import kotlin.concurrent.thread


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram
    private val meshListSphere = mutableListOf<Mesh>()
    private val meshListGround = mutableListOf<Mesh>()

    val bodenmatrix: Matrix4f = Matrix4f()
    val kugelMatrix: Matrix4f = Matrix4f()


    /** Renderables */
    val ground: Renderable
    val sphere: Renderable
    var mapcameraobjekt: Renderable
    var player: Renderable
    var lantern : Renderable
    var cycle : Renderable
    var mac : Renderable

    /** Labyrint */
    private val meshListMazeWalls = mutableListOf<Mesh>()
    private val meshListMazeFloor = mutableListOf<Mesh>()
    private val meshListMazeTop = mutableListOf<Mesh>()
    private val meshListCube = mutableListOf<Mesh>()


    val mazeWallsMatrix: Matrix4f = Matrix4f()
    val mazeFloorMatrix: Matrix4f = Matrix4f()
    val mazeTopMatrix: Matrix4f = Matrix4f()
    val cubeMatrix: Matrix4f = Matrix4f()


    val mazeWalls : Renderable
    val mazeFloor : Renderable
    val mazeTop : Renderable

    var moveablewall :Renderable


    var wall : Renderable
    var wall1 : Renderable
    var wall2 : Renderable
    var wall3 : Renderable
    var wall4 : Renderable
    var wall5 : Renderable
    var wall6 : Renderable
    var wall7 : Renderable


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
        val objResMazeWalls : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeWall.obj")
        val objMeshListMazeWalls : MutableList<OBJLoader.OBJMesh> = objResMazeWalls.objects[0].meshes

        val objResMazeFloor : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeTop.obj")
        val objMeshListMazeFloor : MutableList<OBJLoader.OBJMesh> = objResMazeFloor.objects[0].meshes

        val objResMazeTop : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeTop.obj")
        val objMeshListMazeTop : MutableList<OBJLoader.OBJMesh> = objResMazeTop.objects[0].meshes

        val objResCube : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/Cube.obj")
        val objMeshListCube : MutableList<OBJLoader.OBJMesh> = objResCube.objects[0].meshes



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
        val mazeWallsTexture = Texture2D("assets/textures/brick_shit.png", true)
        val mazeFloorTexture = Texture2D("assets/textures/pexels_shit.png", true)
        val mazeTopTexture = Texture2D("assets/textures/pexels_shit.png", true)

        val cubeTexture = Texture2D("assets/textures/brick_shit.png", true)

        mazeWallsTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        mazeFloorTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        mazeTopTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        cubeTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundShininess = 60f
        val groundTCMultiplier = Vector2f(64f)

        /** Labyrint */
        val mazeWallMaterial = Material(groundDiffTexture, mazeWallsTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeFloorMaterial = Material(groundDiffTexture, mazeFloorTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeTopMaterial = Material(groundDiffTexture, mazeTopTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)

        val cubeMaterial = Material(groundDiffTexture, cubeTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)




        /** Labyrint mesh */
        for (mesh in objMeshListMazeWalls) {
            meshListMazeWalls.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeWallMaterial))
        }
        for (mesh in objMeshListMazeFloor) {
            meshListMazeFloor.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeFloorMaterial))
        }
        for (mesh in objMeshListMazeTop) {
            meshListMazeTop.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeTopMaterial))
        }

        for (mesh in objMeshListCube) {
            meshListCube.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, cubeMaterial))
        }

        bodenmatrix.scale(1f)
        bodenmatrix.rotateX(90f)

        mazeWallsMatrix.scale(1f)
        mazeWallsMatrix.translateLocal(Vector3f(0.0f, 0.0f, 0.0f))

        mazeFloorMatrix.scale(1f)
        mazeFloorMatrix.translateLocal(Vector3f(0.0f, 0.0f, 0.0f))

        cubeMatrix.scale(10f)
        cubeMatrix.translateLocal(Vector3f(0.0f, 0.0f, 0.0f))

        mazeTopMatrix.scale(1f)
        mazeTopMatrix.translateLocal(Vector3f(0.0f, 0.0f, 0.0f))



         /** Labyrint Positionen und Render */

        ground = Renderable(meshListGround)
        sphere = Renderable(meshListSphere)
        mazeWalls = Renderable(meshListMazeWalls)
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

        wall = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall1 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall2 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall3 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall4 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall5 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall6 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")
        wall7 = ModelLoader.loadModel("assets/models/wall.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")


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



        wall.scaleLocal(Vector3f(0.5f))
        wall1.scaleLocal(Vector3f(0.5f))
        wall2.scaleLocal(Vector3f(0.5f))
        wall3.scaleLocal(Vector3f(0.5f))
        wall4.scaleLocal(Vector3f(0.5f))
        wall5.scaleLocal(Vector3f(0.5f))
        wall6.scaleLocal(Vector3f(0.5f))
        wall7.scaleLocal(Vector3f(0.5f))


        objList.add(wall1)
        objList.add(wall2)
        objList.add(wall3)
        objList.add(wall4)
        objList.add(wall5)
        objList.add(wall6)
        objList.add(wall7)

        wall.translateLocal(Vector3f(0f, 0.0f, 0f))
        wall1.translateLocal(Vector3f(20f, 0.0f, 0f))
        wall2.translateLocal(Vector3f(40f, 0.0f, 0f))
        wall3.translateLocal(Vector3f(60f, 0.0f, 0f))

        wall4.translateLocal(Vector3f(20f, 0.0f, -20f))
        wall5.translateLocal(Vector3f(40f, 0.0f, -20f))
        wall6.translateLocal(Vector3f(60f, 0.0f, -20f))

        wall7.translateLocal(Vector3f(70f, 0.0f, -10f))
        /** translate weicht um 10f ab weil er sich in der mitte dreht */
        wall7.rotateLocal(0f, toRadians(90f), 0f)


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
        mazeWalls.render(tronShader)
        mazeFloor.render(tronShader)
        mazeTop.render(tronShader)
        mapcameraobjekt.render(tronShader)
        lantern.render(tronShader)
        mac.render(tronShader)
        player.render(tronShader)

        moveablewall.render(tronShader)

        wall.render(tronShader)
        wall1.render(tronShader)
        wall2.render(tronShader)
        wall3.render(tronShader)
        wall4.render(tronShader)
        wall5.render(tronShader)
        wall6.render(tronShader)
        wall7.render(tronShader)
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

            window.getKeyState(GLFW_KEY_C) -> {

                if (alteslabyrintcheck == true) {
                    mazeWalls.translateLocal(Vector3f(0f, -100f, 0f))
                    alteslabyrintcheck = false
                }
                else{
                    mazeWalls.translateLocal(Vector3f(0f, 100f, 0f))
                    alteslabyrintcheck = true
                }

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
