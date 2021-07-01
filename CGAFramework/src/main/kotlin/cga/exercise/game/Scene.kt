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
import org.joml.Math
import org.joml.Math.*
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*


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

    val mazeWallsMatrix: Matrix4f = Matrix4f()
    val mazeFloorMatrix: Matrix4f = Matrix4f()
    val mazeTopMatrix: Matrix4f = Matrix4f()

    val mazeWalls : Renderable
    val mazeFloor : Renderable
    val mazeTop : Renderable

    val camera = TronCamera()

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
        val objResMazeWalls : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeWall.obj")
        val objMeshListMazeWalls : MutableList<OBJLoader.OBJMesh> = objResMazeWalls.objects[0].meshes

        val objResMazeFloor : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/MazeFloor.obj")
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
        val mazeWallsTexture = Texture2D("assets/textures/brick_shit.png", true)
        val mazeFloorTexture = Texture2D("assets/textures/pexels_shit.png", true)
        val mazeTopTexture = Texture2D("assets/textures/pexels_shit.png", true)

        mazeWallsTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        mazeFloorTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        mazeTopTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundShininess = 60f
        val groundTCMultiplier = Vector2f(64f)

        /** Labyrint */
        val mazeWallMaterial = Material(groundDiffTexture, mazeWallsTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeFloorMaterial = Material(groundDiffTexture, mazeFloorTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeTopMaterial = Material(groundDiffTexture, mazeTopTexture, groundSpecTexture, groundShininess,
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

        /** Labyrint Positionen und Render */
        bodenmatrix.scale(0.03f)
        bodenmatrix.rotateX(90f)
        mazeWallsMatrix.scale(0.02f)
        mazeWallsMatrix.scale(0.001f)
        mazeWallsMatrix.translateLocal(Vector3f(0.0f, 0.0f, 10.0f))

        mazeFloorMatrix.scale(0.001f)
        mazeFloorMatrix.translateLocal(Vector3f(0.0f, 0.0f, 10.0f))

        mazeTopMatrix.scale(0.001f)
        mazeTopMatrix.translateLocal(Vector3f(0.0f, 0.0f, 10.0f))

        ground = Renderable(meshListGround)
        sphere = Renderable(meshListSphere)
        mazeWalls = Renderable(meshListMazeWalls)
        mazeFloor = Renderable(meshListMazeFloor)
        mazeTop = Renderable(meshListMazeTop)


        /** Modelloader */
        cycle = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        player = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mapcameraobjekt = ModelLoader.loadModel("assets/among_us_obj/among us.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        lantern = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(-0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        mac = ModelLoader.loadModel("assets/models/mac10.obj", toRadians(0f), toRadians(180f), 0f)?: throw Exception("Renderable can't be NULL!")

        /** Camerastart Position */
        camera.translateLocal(Vector3f(0f, 1.4f, 0f))
        camera.parent = player

        /** Orbitalecamera Position */
        mapcameraobjekt.scaleLocal(Vector3f(0.008f))
        mapcameraobjekt.translateLocal(Vector3f(10f, 3000f, 0f))

        /** Pistole */
        mac.scaleLocal(Vector3f(0.008f))
        mac.translateLocal(Vector3f(20f, 150f, -35f))
        mac.parent = player

        /** Objektplatzierung */
        cycle.scaleLocal(Vector3f(1.8f))
        lantern.translateLocal(Vector3f(2.1f, 1f, -3.2f))


        /** Lichter */
        pointLight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(1f, 1f, 0f), Vector3f(1f, 0.5f, 0.1f))

        spotLight = SpotLight(Vector3f(0f, 1f, -1f), Vector3f(2f,2f,0.1f), Vector3f(0.1f, 0.01f, 0.01f), Vector2f(toRadians(150f), toRadians(30f)))

        spotLight.rotateLocal(toRadians(-10f), PI.toFloat(),0f)
        pointLight.parent = lantern
        spotLight.parent = lantern

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
                    player.rotateLocal(0f,2f * dt,0f)
                }
                if (window.getKeyState(GLFW_KEY_D)) {
                    player.rotateLocal(0f, 2 * -dt,0f)
                }
                player.translateLocal(Vector3f(0f, 0f, 2f * dt))
            }
            window.getKeyState(GLFW_KEY_A) -> {
                player.translateLocal(Vector3f(2f * -dt, 0f, 0f))
            }
            window.getKeyState(GLFW_KEY_D) -> {
                player.translateLocal(Vector3f(2f * dt, 0f, 0f))
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
            /** Camera 2 */
            window.getKeyState(GLFW_KEY_B) -> {
                cameracheck1 = false
                cameracheck2 = true
                cameracheck2 = false
                cameracheck4 = false
                camera.parent = cycle
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

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

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
