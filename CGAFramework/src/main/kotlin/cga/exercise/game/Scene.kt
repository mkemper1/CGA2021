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

    val ground: Renderable
    val sphere: Renderable
    var player: Renderable
    var lantern : Renderable
    var cycle : Renderable

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

    val pointLight : PointLight
    val spotLight: SpotLight
    //MouseParam
    var notFirstFrame = false
    var oldMousePosX = 0.0
    var oldMousePosY = 0.0

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        //initial opengl state

        glClearColor(0.0f, 0.0f, 1f, 1.0f); GLError.checkThrow()

        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        //val objResSphere : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/sphere.obj")
        //val objMeshListSphere : MutableList<OBJLoader.OBJMesh> = objResSphere.objects[0].meshes
//
        //val objResGround : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
        //val objMeshListGround : MutableList<OBJLoader.OBJMesh> = objResGround.objects[0].meshes

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

        // val groundMaterial = Material(groundDiffTexture, groundEmitTexture, groundSpecTexture, groundShininess,
        //         groundTCMultiplier)

        /** Labyrint */
        val mazeWallMaterial = Material(groundDiffTexture, mazeWallsTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeFloorMaterial = Material(groundDiffTexture, mazeFloorTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)
        val mazeTopMaterial = Material(groundDiffTexture, mazeTopTexture, groundSpecTexture, groundShininess,
            groundTCMultiplier)

        // for (mesh in objMeshListSphere) {
        //     meshListSphere.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes))
        // }
//
        // for (mesh in objMeshListGround) {
        //     meshListGround.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, groundMaterial))
        // }

        /** Labyrint */
        for (mesh in objMeshListMazeWalls) {
            meshListMazeWalls.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeWallMaterial))
        }
        for (mesh in objMeshListMazeFloor) {
            meshListMazeFloor.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeFloorMaterial))
        }
        for (mesh in objMeshListMazeTop) {
            meshListMazeTop.add(Mesh(mesh.vertexData, mesh.indexData, vertexAttributes, mazeTopMaterial))
        }

        bodenmatrix.scale(0.03f)
        bodenmatrix.rotateX(90f)

        mazeWallsMatrix.scale(0.02f)

        /** Labyrint */
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

        camera.rotateLocal(Math.toRadians(0f),0f, 0f)
        camera.translateLocal(Vector3f(0f, 0.8f, 0f))


        cycle = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", toRadians(-0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        player = ModelLoader.loadModel("assets/among_us_obj/among us.obj", toRadians(0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")
        lantern = ModelLoader.loadModel("assets/SA_LD_Medieval_Horn_Lantern_OBJ/SA_LD_Medieval_Horn_Lantern.obj", toRadians(-0f), toRadians(0f), 0f)?: throw Exception("Renderable can't be NULL!")



        cycle.scaleLocal(Vector3f(1.8f))
        lantern.translateLocal(Vector3f(2.1f, 1f, -3.2f))
        player.scaleLocal(Vector3f(0.008f))
        player.translateLocal(Vector3f(10f, 3000f, 0f))



        camera.parent = cycle

        pointLight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(1f, 1f, 0f),
                Vector3f(1f, 0.5f, 0.1f))

        spotLight = SpotLight(Vector3f(0f, 1f, -2f), Vector3f(1f,1f,0.6f),
                Vector3f(0.5f, 0.05f, 0.01f), Vector2f(toRadians(15f), toRadians(30f)))

        spotLight.rotateLocal(toRadians(-10f), PI.toFloat(),0f)

        pointLight.parent = cycle
        spotLight.parent = cycle

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
        // ground.render(tronShader)
        tronShader.setUniform("farbe", Vector3f(1f,1f,1f))
        mazeWalls.render(tronShader)
        mazeFloor.render(tronShader)
        mazeTop.render(tronShader)
        player.render(tronShader)
        lantern.render(tronShader)
    }

    fun update(dt: Float, t: Float) {

        pointLight.lightColor = Vector3f(abs(sin(t/3f)), abs(sin(t/4f)), abs(sin(t/2)))
        //pointLight.lightColor = Vector3f(0.5f * sin(t) + 0.5f,0.5f * sin(t - 2/3 * PI.toFloat()) + 0.5f, 0.5f * sin(t - 5/3 * PI.toFloat()) + 0.5f)
        when {
            window.getKeyState(GLFW_KEY_W) -> {
                if (window.getKeyState(GLFW_KEY_A)) {
                    cycle.rotateLocal(0f,1.5f * dt,0f)
                }
                if (window.getKeyState(GLFW_KEY_D)) {
                    cycle.rotateLocal(0f, 1.5f * -dt,0f)
                }
                if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)) {
                    cycle.translateLocal(Vector3f(0f, 0f, 4 * -dt))
                }
                cycle.translateLocal(Vector3f(0f, 0f, 2 * -dt))
            }
            window.getKeyState(GLFW_KEY_S) -> {
                if (window.getKeyState(GLFW_KEY_A)) {
                    cycle.rotateLocal(0f,1.5f * dt,0f)
                }
                if (window.getKeyState(GLFW_KEY_D)) {
                    cycle.rotateLocal(0f, 1.5f * -dt,0f)
                }
                cycle.translateLocal(Vector3f(0f, 0f, 2f * dt))
            }

            window.getKeyState(GLFW_KEY_V) -> {
                camera.parent = lantern
            }
            window.getKeyState(GLFW_KEY_B) -> {
                camera.parent = cycle
            }
            window.getKeyState(GLFW_KEY_N) -> {
                mazeTop.scaleLocal(Vector3f(0.000005f))
                camera.parent = player
            }



        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val deltaX = xpos - oldMousePosX
        var deltaY = ypos - oldMousePosY
        oldMousePosX = xpos
        oldMousePosY = ypos

        if(notFirstFrame) {
            /** links-rechts */
            camera.rotateAroundPoint(0f, toRadians(deltaX.toFloat() * -0.03f), 0f, Vector3f(0f))

            /** hoch-runter */
            camera.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.05f),0f, 0f)


            /** Fliegen mit Taste F */
            when {
                window.getKeyState(GLFW_KEY_F) -> {
                    cycle.rotateLocal(Math.toRadians(deltaY.toFloat() * -0.1f), 0f, 0f)
                    cycle.rotateAroundPoint(0f, toRadians(deltaX.toFloat() * -0.03f), 0f, Vector3f(0f))
                }
            }
        }
        notFirstFrame = true
    }

    fun cleanup() {}
}
