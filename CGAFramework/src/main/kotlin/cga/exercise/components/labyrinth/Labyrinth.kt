package cga.exercise.components.labyrinth

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Renderable
import org.joml.Math
import org.joml.Vector3f

var step = 0
var tap = 0

class Labyrinth {

    fun monsterMovement(monster: Renderable, flyRange: MutableList<Float>) {
        when {
            tap == 0 -> {
                if (monster.getWorldPosition().y < flyRange[0]) {
                    monster.translateGlobal(Vector3f(0f, 0.01f, 0f))
                } else tap = 1
            }
            tap == 1 -> {
                if (monster.getWorldPosition().y > flyRange[0]) {
                    monster.translateGlobal(Vector3f(0f, -0.01f, 0f))
                } else tap = 2
            }
            tap == 2 -> {
                if (monster.getWorldPosition().y > flyRange[1]) {
                    monster.translateGlobal(Vector3f(0f, -0.01f, 0f))
                } else tap = 3
            }
            tap == 3 -> {
                if (monster.getWorldPosition().y < flyRange[1]) {
                    monster.translateGlobal(Vector3f(0f, 0.01f, 0f))
                } else tap = 0
            }
        }
    }

    fun flyRange(mesh: Renderable, upRange: Float, downRange: Float): MutableList<Float> {

        val monsterMovement = mutableListOf<Float>()

        if(mesh.getWorldPosition().y != 0f) mesh.translateGlobal(Vector3f(0f, (mesh.getWorldPosition().y * -1), 0f))
        mesh.translateGlobal(Vector3f(0f, downRange, 0f))
        val monsterDown = mesh.getWorldPosition().y
        mesh.translateGlobal(Vector3f(0f, downRange * -1, 0f))
        mesh.translateGlobal(Vector3f(0f, upRange, 0f))
        val monsterUp = mesh.getWorldPosition().y
        mesh.translateGlobal(Vector3f(0f, upRange * -1, 0f))
        mesh.translateGlobal(Vector3f(0f, (upRange + downRange) / 2, 0f))

        monsterMovement.add(monsterUp)
        monsterMovement.add(monsterDown)

        return monsterMovement

    }

    fun monsterLetLoose(monster: Renderable, routePosition: MutableList<Vector3f>, routeRotatePosition: MutableList<Vector3f>, rnd: Int) {
        when(rnd) {
            1 -> {
                if (monster.getWorldPosition().z < routePosition[1].z && step == 0) monster.translateGlobal(Vector3f(0f, 0f, 0.02f)) else if (monster.getWorldPosition().z > routePosition[1].z && step == 0) step = 1
                if (monster.getWorldPosition().x < routePosition[2].x && step == 1) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[0]) else if (monster.getWorldPosition().x > routePosition[2].x && step == 1) step = 2
                if (monster.getWorldPosition().z < routePosition[3].z && step == 2) monster.rotateAroundPoint(0f, -0.006f, 0f, routeRotatePosition[1]) else if (monster.getWorldPosition().z > routePosition[3].z && step == 2) step = 3
                if (monster.getWorldPosition().x > routePosition[4].x && step == 3) monster.translateGlobal(Vector3f(-0.02f, 0f, 0f)) else if (monster.getWorldPosition().x < routePosition[4].x && step == 3) step = 4
                if (monster.getWorldPosition().z < routePosition[5].z && step == 4) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[2]) else if (monster.getWorldPosition().z > routePosition[5].z && step == 4) step = 5
                if (monster.getWorldPosition().z < routePosition[6].z && step == 5) monster.translateGlobal(Vector3f(0f, 0f, 0.02f)) else if (monster.getWorldPosition().z > routePosition[6].z && step == 5) step = 6
                if (monster.getWorldPosition().x > routePosition[7].x && step == 6) monster.rotateAroundPoint(0f, -0.006f, 0f, routeRotatePosition[3]) else if (monster.getWorldPosition().x < routePosition[7].x && step == 6) step = 7
                if (monster.getWorldPosition().x > routePosition[8].x && step == 7) monster.translateGlobal(Vector3f(-0.02f, 0f, 0f)) else if (monster.getWorldPosition().x < routePosition[8].x && step == 7) step = 8
                if (monster.getWorldPosition().z < routePosition[9].z && step == 8) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[4]) else if (monster.getWorldPosition().z > routePosition[9].z && step == 8) step = 9
                if (monster.getWorldPosition().z < routePosition[10].z && step == 9) monster.translateGlobal(Vector3f(0f, 0f, 0.02f)) else if (monster.getWorldPosition().z > routePosition[10].z && step == 9) step = 10
                if (monster.getWorldPosition().x < routePosition[11].x && step == 10) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[5]) else if (monster.getWorldPosition().x > routePosition[11].x && step == 10) step = 11
                if (monster.getWorldPosition().x < routePosition[12].x && step == 11) monster.translateGlobal(Vector3f(0.02f, 0f, 0f)) else if (monster.getWorldPosition().x > routePosition[12].x && step == 11) step = 12
                if (monster.getWorldPosition().z > routePosition[13].z && step == 12) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[6]) else if (monster.getWorldPosition().z < routePosition[13].z && step == 12) step = 13
                if (monster.getWorldPosition().z > routePosition[14].z && step == 13) monster.translateGlobal(Vector3f(0f, 0f, -0.02f)) else if (monster.getWorldPosition().z < routePosition[14].z && step == 13) step = 14
                if (monster.getWorldPosition().x < routePosition[15].x && step == 14) monster.rotateAroundPoint(0f, -0.006f, 0f, routeRotatePosition[7]) else if (monster.getWorldPosition().x > routePosition[15].x && step == 14) step = 15
                if (monster.getWorldPosition().z > routePosition[16].z && step == 15) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[8]) else if (monster.getWorldPosition().z < routePosition[16].z && step == 15) step = 16
                if (monster.getWorldPosition().x < routePosition[17].x && step == 16) monster.rotateAroundPoint(0f, -0.006f, 0f, routeRotatePosition[9]) else if (monster.getWorldPosition().x > routePosition[17].x && step == 16) step = 17
                if (monster.getWorldPosition().z > routePosition[18].z && step == 17) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[10]) else if (monster.getWorldPosition().z < routePosition[18].z && step == 17) step = 18
                if (monster.getWorldPosition().x < routePosition[19].x && step == 18) monster.rotateAroundPoint(0f, -0.006f, 0f, routeRotatePosition[11]) else if (monster.getWorldPosition().x > routePosition[19].x && step == 18) step = 19
                if (monster.getWorldPosition().x < routePosition[20].x && step == 19) monster.translateGlobal(Vector3f(0.02f, 0f, 0f)) else if (monster.getWorldPosition().x > routePosition[20].x && step == 19) step = 20
                if (monster.getWorldPosition().z > routePosition[21].z && step == 20) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[12]) else if (monster.getWorldPosition().z < routePosition[21].z && step == 20) step = 21
                if (monster.getWorldPosition().z > routePosition[22].z && step == 21) monster.translateGlobal(Vector3f(0f, 0f, -0.02f)) else if (monster.getWorldPosition().z < routePosition[22].z && step == 21) step = 22
                if (monster.getWorldPosition().x > routePosition[23].x && step == 22) monster.rotateAroundPoint(0f, 0.006f, 0f, routeRotatePosition[13]) else if (monster.getWorldPosition().x < routePosition[23].x && step == 22) step = 3
            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
        }
    }

    fun buttonPressRange(firstMesh: Renderable, secoundMesh: Renderable, buttonOrientation: Int, buttonHitbox: MutableList<Float>): Boolean {

        val minusX: Float
        val minusZ: Float
        val plusX: Float
        val plusZ: Float

        val f: Boolean
        val g: Boolean
        var bool = false

        when (buttonOrientation) {
            0 -> {
                minusX = secoundMesh.getWorldPosition().x - buttonHitbox[0]
                plusX = secoundMesh.getWorldPosition().x + buttonHitbox[0]
                plusZ = secoundMesh.getWorldPosition().z + 2

                f = !(firstMesh.getWorldPosition().z - 1 < plusZ && firstMesh.getWorldPosition().z + 1 > plusZ)
                        || firstMesh.getWorldPosition().x - 0.8 > plusX
                        || firstMesh.getWorldPosition().x + 0.8 < minusX

                if (!f) {
                    bool = true
                }
            }
            1 -> {
                plusX = secoundMesh.getWorldPosition().x + 2
                plusZ = secoundMesh.getWorldPosition().z + buttonHitbox[1]
                minusZ = secoundMesh.getWorldPosition().z - buttonHitbox[1]

                g = !(firstMesh.getWorldPosition().x - 1 < plusX && firstMesh.getWorldPosition().x + 1 > plusX)
                        || firstMesh.getWorldPosition().z - 0.8 > plusZ
                        || firstMesh.getWorldPosition().z + 0.8 < minusZ

                if (!g) {
                    bool = true
                }
            }
        }

        return bool

    }

    fun buttonMovement(buttonPressed: MutableList<Int>, buttonStatus: MutableList<Boolean>, buttonSpawn: MutableList<Vector3f>, buttonOrientation: Int, objList: MutableList<Renderable>) {

        when {
            buttonPressed[0] == 1 || buttonPressed[1] == 1 || buttonPressed[2] == 1 -> {
                if (buttonPressed[0] == 1) {
                    if (buttonOrientation == 0 && objList[468].getWorldPosition().z > buttonSpawn[3].z) {
                        objList[468].translateGlobal(Vector3f(0f, 0.0f, -0.01f))
                    } else if (buttonOrientation == 1 && objList[468].getWorldPosition().x > buttonSpawn[3].x) {
                        objList[468].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    } else {
                        buttonStatus[0] = !buttonStatus[0]
                        println(buttonStatus[0])
                        buttonPressed[0] = -1
                    }
                }
                if (buttonPressed[1] == 1) {
                    if (buttonOrientation == 0 && objList[469].getWorldPosition().z > buttonSpawn[4].z) {
                        objList[469].translateGlobal(Vector3f(0f, 0.0f, -0.01f))
                    } else if (buttonOrientation == 1 && objList[469].getWorldPosition().x > buttonSpawn[4].x) {
                        objList[469].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    } else {
                        buttonStatus[1] = !buttonStatus[1]
                        println(buttonStatus[1])
                        buttonPressed[1] = -1
                    }
                }
                if (buttonPressed[2] == 1) {
                    if (buttonOrientation == 0 && objList[470].getWorldPosition().z > buttonSpawn[5].z) {
                        objList[470].translateGlobal(Vector3f(0f, 0.0f, -0.01f))
                    } else if (buttonOrientation == 1 && objList[470].getWorldPosition().x > buttonSpawn[5].x) {
                        objList[470].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    } else {
                        buttonStatus[2] = !buttonStatus[2]
                        println(buttonStatus[2])
                        buttonPressed[2] = -1
                    }
                }
            }
            buttonPressed[0] == -1 || buttonPressed[1] == -1 || buttonPressed[2] == -1 -> {
                if (buttonPressed[0] == -1) {
                    if (buttonOrientation == 0 && objList[468].getWorldPosition().z < buttonSpawn[0].z) {
                        objList[468].translateGlobal(Vector3f(0.0f, 0.0f, 0.01f))
                    } else if (buttonOrientation == 1 && objList[468].getWorldPosition().x < buttonSpawn[0].x) {
                        objList[468].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    } else {
                        buttonPressed[0] = 0
                    }
                }
                if (buttonPressed[1] == -1) {
                    if (buttonOrientation == 0 && objList[469].getWorldPosition().z < buttonSpawn[1].z) {
                        objList[469].translateGlobal(Vector3f(0.0f, 0.0f, 0.01f))
                    } else if (buttonOrientation == 1 && objList[469].getWorldPosition().x < buttonSpawn[1].x) {
                        objList[469].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    } else {
                        buttonPressed[1] = 0
                    }
                }
                if (buttonPressed[2] == -1) {
                    if (buttonOrientation == 0 && objList[470].getWorldPosition().z < buttonSpawn[2].z) {
                        objList[470].translateGlobal(Vector3f(0.0f, 0.0f, 0.01f))
                    } else if (buttonOrientation == 1 && objList[470].getWorldPosition().x < buttonSpawn[2].x) {
                        objList[470].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    } else {
                        buttonPressed[2] = 0
                    }
                }
            }
        }
    }

    fun buttonStatus(buttonStatus: MutableList<Boolean>, objList: MutableList<Renderable>, doorSpawn: MutableList<Vector3f>, gateOrientation: Int, camera: TronCamera, player: Renderable, firstCameraPosition: Renderable): Boolean {
        when {
            buttonStatus[0] && buttonStatus[1] && buttonStatus[2] -> {
                if (gateOrientation == 0 && (objList[474].getWorldPosition().x > doorSpawn[2].x || objList[475].getWorldPosition().x < doorSpawn[3].x)) {
                    objList[474].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    objList[475].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    camera.parent = firstCameraPosition
                    return true
                } else if (gateOrientation == 1 && (objList[474].getWorldPosition().z > doorSpawn[2].z || objList[475].getWorldPosition().z < doorSpawn[3].z)) {
                    objList[474].translateGlobal(Vector3f(0.0f, 0.0f, -0.01f))
                    objList[475].translateGlobal(Vector3f(0.0f, 0.0f, 0.01f))
                    camera.parent = firstCameraPosition
                    return true
                } else {
                    camera.parent = player
                }
            }
            !buttonStatus[0] && !buttonStatus[1] && !buttonStatus[2] -> {
                if (gateOrientation == 0 && (objList[474].getWorldPosition().x < doorSpawn[0].x || objList[475].getWorldPosition().x > doorSpawn[1].x)) {
                    objList[474].translateGlobal(Vector3f(0.01f, 0.0f, 0.0f))
                    objList[475].translateGlobal(Vector3f(-0.01f, 0.0f, 0.0f))
                    camera.parent = firstCameraPosition
                    return true
                } else if (gateOrientation == 1 && (objList[474].getWorldPosition().z < doorSpawn[0].z || objList[475].getWorldPosition().z < doorSpawn[1].z)) {
                    objList[474].translateGlobal(Vector3f(0.0f, 0.0f, 0.01f))
                    objList[475].translateGlobal(Vector3f(0.0f, 0.0f, -0.01f))
                    camera.parent = firstCameraPosition
                    return true
                } else {
                    camera.parent = player
                }
            }
        }
        return false
    }

    fun portTo(firstObject: Renderable, secoundObject: Renderable, orientation: String) {

        val rangeXUp = secoundObject.getWorldPosition().x + 0.1f
        val rangeXDown = secoundObject.getWorldPosition().x - 0.1f
        val rangeZUp = secoundObject.getWorldPosition().z + 0.1f
        val rangeZDown = secoundObject.getWorldPosition().z - 0.1f

        while (firstObject.getWorldPosition().x !in rangeXDown..rangeXUp)
            if(firstObject.getWorldPosition().x < secoundObject.getWorldPosition().x)
                firstObject.translateGlobal(Vector3f(0.1f, 0f, 0f)) else firstObject.translateGlobal(Vector3f(-0.1f, 0f, 0f))
        while (firstObject.getWorldPosition().z !in rangeZDown..rangeZUp)
            if(firstObject.getWorldPosition().z < secoundObject.getWorldPosition().z)
                firstObject.translateGlobal(Vector3f(0f, 0f, 0.1f)) else firstObject.translateGlobal(Vector3f(0f, 0f, -0.1f))

        when(orientation) {
            "x+" -> while (firstObject.getZAxis().angle(secoundObject.getXAxis()) < 3.10f) firstObject.rotateLocal(0f,
                Math.toRadians(-0.01f), 0f)
            "x-" -> while (firstObject.getZAxis().angle(secoundObject.getXAxis()) > 0.01f) firstObject.rotateLocal(0f,
                Math.toRadians(-0.01f), 0f)
            "z+" -> while (firstObject.getZAxis().angle(secoundObject.getZAxis()) < 3.01f) firstObject.rotateLocal(0f,
                Math.toRadians(-0.01f), 0f)
            "z-" -> while (firstObject.getZAxis().angle(secoundObject.getZAxis()) > 0.01f) firstObject.rotateLocal(0f,
                Math.toRadians(-0.01f), 0f)
        }

    }

    fun collision(firstMesh: Renderable, secoundMesh: Renderable, allHitboxes: MutableList<MutableList<Float>>, op: String, objList: MutableList<Renderable>, gateOrientation: Int, playerSpeed: Float) {

        val xHitbox = mutableListOf(0f, 0f)
        val zHitbox = mutableListOf(0f, 0f)
        var count = 0
        var count2 = 0
        var skip = false

        for (x in objList) {
            /** horizontal Walls (walls - wallsBig) */
            if (x == firstMesh && (count in 0..94 || count in 196..225)) {
                xHitbox[0] = allHitboxes[0][0]
                zHitbox[0] = allHitboxes[0][1]
            }
            if (x == secoundMesh && (count2 in 0..94 || count2 in 196..225) && op == "solid") {
                xHitbox[1] = allHitboxes[0][0]
                zHitbox[1] = allHitboxes[0][1]
            }
            /** vertical walls (walls - wallsBig) */
            if (x == firstMesh && (count in 95..195 || count in 226..267)) {
                xHitbox[0] = allHitboxes[1][0]
                zHitbox[0] = allHitboxes[1][1]
            }
            if (x == secoundMesh && (count2 in 95..195 || count2 in 226..267) && op == "solid") {
                xHitbox[1] = allHitboxes[1][0]
                zHitbox[1] = allHitboxes[1][1]
            }
            /** pillars and pillarsBig */
            if (x == firstMesh && count in 268..467) {
                xHitbox[0] = allHitboxes[2][0]
                zHitbox[0] = allHitboxes[2][1]
            }
            if (x == secoundMesh && count2 in 268..467 && op == "solid") {
                xHitbox[1] = allHitboxes[2][0]
                zHitbox[1] = allHitboxes[2][1]
            }
            /** buttons */
            if (x == firstMesh && count in 468..473) {
                xHitbox[0] = allHitboxes[3][0]
                zHitbox[0] = allHitboxes[3][1]
            }
            if (x == secoundMesh && count2 in 468..473 && op == "solid") {
                xHitbox[1] = allHitboxes[3][0]
                zHitbox[1] = allHitboxes[3][1]
            }
            /** doors */
            if (x == firstMesh && count in 474..475) {
                if (gateOrientation == 0) {
                    xHitbox[0] = allHitboxes[4][0]
                    zHitbox[0] = allHitboxes[4][1]
                } else {
                    xHitbox[0] = allHitboxes[5][0]
                    zHitbox[0] = allHitboxes[5][1]
                }
            }
            if (x == secoundMesh && count2 in 474..475 && op == "solid") {
                if (gateOrientation == 0) {
                    xHitbox[1] = allHitboxes[4][0]
                    zHitbox[1] = allHitboxes[4][1]
                } else {
                    xHitbox[1] = allHitboxes[5][0]
                    zHitbox[1] = allHitboxes[5][1]
                }
            }
            /** monster */
            if (x == firstMesh && count == 476) {
                xHitbox[0] = allHitboxes[6][0]
                zHitbox[0] = allHitboxes[6][1]
            }
            if (x == secoundMesh && count2 == 476 && op == "dead") {
                xHitbox[1] = allHitboxes[6][0]
                zHitbox[1] = allHitboxes[6][1]
            }
            /** player */
            if (x == firstMesh && count == 477) {
                xHitbox[0] = allHitboxes[7][0]
                zHitbox[0] = allHitboxes[7][1]
            }
            /** objects without hitbox: player, gate, floor, skybox */
            if (x == secoundMesh && count2 in 478..481) {
                skip = true
            }
            count++
            count2++
        }

        /** Collision-Test */
        if (!skip) {
            val worldXPLus =
                !(firstMesh.getWorldPosition().x + xHitbox[0] > secoundMesh.getWorldPosition().x - xHitbox[1]
                        && firstMesh.getWorldPosition().x - xHitbox[0] < secoundMesh.getWorldPosition().x - xHitbox[1])
                        || firstMesh.getWorldPosition().z - zHitbox[0] + 0.2 > secoundMesh.getWorldPosition().z + zHitbox[1]
                        || firstMesh.getWorldPosition().z + zHitbox[0] - 0.2 < secoundMesh.getWorldPosition().z - zHitbox[1]
            val worldXMinus =
                !(firstMesh.getWorldPosition().x - xHitbox[0] < secoundMesh.getWorldPosition().x + xHitbox[1]
                        && firstMesh.getWorldPosition().x + xHitbox[0] > secoundMesh.getWorldPosition().x + xHitbox[1])
                        || firstMesh.getWorldPosition().z - zHitbox[0] + 0.2 > secoundMesh.getWorldPosition().z + zHitbox[1]
                        || firstMesh.getWorldPosition().z + zHitbox[0] - 0.2 < secoundMesh.getWorldPosition().z - zHitbox[1]
            val worldZPlus =
                !(firstMesh.getWorldPosition().z + zHitbox[0] > secoundMesh.getWorldPosition().z - zHitbox[1]
                        && firstMesh.getWorldPosition().z - zHitbox[0] < secoundMesh.getWorldPosition().z - zHitbox[1])
                        || firstMesh.getWorldPosition().x - xHitbox[0] + 0.2 > secoundMesh.getWorldPosition().x + xHitbox[1]
                        || firstMesh.getWorldPosition().x + xHitbox[0] - 0.2 < secoundMesh.getWorldPosition().x - xHitbox[1]
            val worldZMinus =
                !(firstMesh.getWorldPosition().z - zHitbox[0] < secoundMesh.getWorldPosition().z + zHitbox[1]
                        && firstMesh.getWorldPosition().z + zHitbox[0] > secoundMesh.getWorldPosition().z + zHitbox[1])
                        || firstMesh.getWorldPosition().x - xHitbox[0] + 0.2 > secoundMesh.getWorldPosition().x + xHitbox[1]
                        || firstMesh.getWorldPosition().x + xHitbox[0] - 0.2 < secoundMesh.getWorldPosition().x - xHitbox[1]

            if (!worldXPLus) {
                when(op) {
                    "solid" -> firstMesh.translateGlobal(Vector3f(-playerSpeed, 0.0f, 0.0f))
                    "dead" -> portTo(objList[477], objList[481], "z+")
                }
            }
            if (!worldXMinus) {
                when(op) {
                    "solid" -> firstMesh.translateGlobal(Vector3f(playerSpeed, 0.0f, 0.0f))
                    "dead" -> portTo(objList[477], objList[481], "z+")
                }
            }
            if (!worldZPlus) {
                when(op) {
                    "solid" -> firstMesh.translateGlobal(Vector3f(0.0f, 0.0f, -playerSpeed))
                    "dead" -> portTo(objList[477], objList[481], "z+")
                }
            }
            if (!worldZMinus) {
                when(op) {
                    "solid" -> firstMesh.translateGlobal(Vector3f(0.0f, 0.0f, playerSpeed))
                    "dead" -> portTo(objList[477], objList[481], "z+")
                }
            }
        }
    }

    fun wayOfDeathAndDecay(monster: Renderable, routePosition: MutableList<Vector3f>, routeRotatePosition: MutableList<Vector3f>, rnd: Int) {

        when (rnd) {
            1 -> {
                monster.translateGlobal(Vector3f(60f, 0f, -4f))
                routePosition[0] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(0f, 0f, 12f))
                routePosition[1] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routeRotatePosition[0] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routePosition[2] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routeRotatePosition[1] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routePosition[3] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(-8f, 0f, 0f))
                routePosition[4] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routeRotatePosition[2] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(-4f, 0f, 0f))
                routePosition[5] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(0f, 0f, 8f))
                routePosition[6] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(-4f, 0f, 0f))
                routeRotatePosition[3] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routePosition[7] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(-8f, 0f, 0f))
                routePosition[8] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routeRotatePosition[4] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(-4f, 0f, 0f))
                routePosition[9] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(0f, 0f, 40f))
                routePosition[10] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routeRotatePosition[5] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, 4f))
                routePosition[11] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(16f, 0f, 0f))
                routePosition[12] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routeRotatePosition[6] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routePosition[13] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(0f, 0f, -24f))
                routePosition[14] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routeRotatePosition[7] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routePosition[15] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routeRotatePosition[8] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routePosition[16] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routeRotatePosition[9] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routePosition[17] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routeRotatePosition[10] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routePosition[18] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routeRotatePosition[11] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routePosition[19] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(24f, 0f, 0f))
                routePosition[20] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routeRotatePosition[12] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(4f, 0f, 0f))
                routePosition[21] = monster.getWorldPosition()
                monster.translateGlobal(Vector3f(0f, 0f, -8f))
                routePosition[22] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(-4f, 0f, 0f))
                routeRotatePosition[13] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(0f, 0f, -4f))
                routePosition[23] = monster.getWorldPosition()

                monster.translateGlobal(Vector3f(-44f, 0f, -24f))
            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
        }
    }

    fun buttonSpawn(buttons: MutableList<Renderable>, buttonBases: MutableList<Renderable>, rnd: Int): MutableList<Vector3f> {

        val buttonPositions = mutableListOf<Vector3f>()

        when(rnd) {
            1 -> {
                buttons[0].translateGlobal(Vector3f(84f, 0f, 9f))
                buttonBases[0].translateGlobal(Vector3f(84f, 0f, 9f))

                buttons[1].translateGlobal(Vector3f(116f, 0f, 81f))
                buttonBases[1].translateGlobal(Vector3f(116f, 0f, 81f))

                buttons[2].translateGlobal(Vector3f(4f, 0f, 89f))
                buttonBases[2].translateGlobal(Vector3f(4f, 0f, 89f))

                val firstButtonFP = buttons[0].getWorldPosition()
                val secoundButtonFP = buttons[1].getWorldPosition()
                val thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0f, 0f, -0.2f))
                buttons[1].translateGlobal(Vector3f(0f, 0f, -0.2f))
                buttons[2].translateGlobal(Vector3f(0f, 0f, -0.2f))
                val firstButtonBP = buttons[0].getWorldPosition()
                val secoundButtonBP = buttons[1].getWorldPosition()
                val thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0f, 0f, 0.2f))
                buttons[1].translateGlobal(Vector3f(0f, 0f, 0.2f))
                buttons[2].translateGlobal(Vector3f(0f, 0f, 0.2f))

                buttonPositions.add(firstButtonFP)
                buttonPositions.add(secoundButtonFP)
                buttonPositions.add(thirdButtonFP)
                buttonPositions.add(firstButtonBP)
                buttonPositions.add(secoundButtonBP)
                buttonPositions.add(thirdButtonBP)
            }
            2 -> {
                buttons[0].translateGlobal(Vector3f(20f, 0f, 41f))
                buttonBases[0].translateGlobal(Vector3f(20f, 0f, 41f))

                buttons[1].translateGlobal(Vector3f(114f, 0f, 1f))
                buttonBases[1].translateGlobal(Vector3f(114f, 0f, 1f))

                buttons[2].translateGlobal(Vector3f(68f, 0f, 97f))
                buttonBases[2].translateGlobal(Vector3f(68f, 0f, 97f))

                val firstButtonFP = buttons[0].getWorldPosition()
                val secoundButtonFP = buttons[1].getWorldPosition()
                val thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0f, 0f, -0.2f))
                buttons[1].translateGlobal(Vector3f(0f, 0f, -0.2f))
                buttons[2].translateGlobal(Vector3f(0f, 0f, -0.2f))
                val firstButtonBP = buttons[0].getWorldPosition()
                val secoundButtonBP = buttons[1].getWorldPosition()
                val thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0f, 0f, 0.2f))
                buttons[1].translateGlobal(Vector3f(0f, 0f, 0.2f))
                buttons[2].translateGlobal(Vector3f(0f, 0f, 0.2f))

                buttonPositions.add(firstButtonFP)
                buttonPositions.add(secoundButtonFP)
                buttonPositions.add(thirdButtonFP)
                buttonPositions.add(firstButtonBP)
                buttonPositions.add(secoundButtonBP)
                buttonPositions.add(thirdButtonBP)
            }
            3 -> {
                buttons[0].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttons[0].translateGlobal(Vector3f(65f, 0f, 4f))
                buttonBases[0].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttonBases[0].translateGlobal(Vector3f(65f, 0f, 4f))

                buttons[1].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttons[1].translateGlobal(Vector3f(17f, 0f, 28f))
                buttonBases[1].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttonBases[1].translateGlobal(Vector3f(17f, 0f, 28f))

                buttons[2].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttons[2].translateGlobal(Vector3f(81f, 0f, 84f))
                buttonBases[2].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttonBases[2].translateGlobal(Vector3f(81f, 0f, 84f))

                val firstButtonFP = buttons[0].getWorldPosition()
                val secoundButtonFP = buttons[1].getWorldPosition()
                val thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                val firstButtonBP = buttons[0].getWorldPosition()
                val secoundButtonBP = buttons[1].getWorldPosition()
                val thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(0.2f, 0f, 0f))

                buttonPositions.add(firstButtonFP)
                buttonPositions.add(secoundButtonFP)
                buttonPositions.add(thirdButtonFP)
                buttonPositions.add(firstButtonBP)
                buttonPositions.add(secoundButtonBP)
                buttonPositions.add(thirdButtonBP)
            }
            4 -> {
                buttons[0].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttons[0].translateGlobal(Vector3f(97f, 0f, 12f))
                buttonBases[0].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttonBases[0].translateGlobal(Vector3f(97f, 0f, 12f))

                buttons[1].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttons[1].translateGlobal(Vector3f(9f, 0f, 76f))
                buttonBases[1].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttonBases[1].translateGlobal(Vector3f(9f, 0f, 76f))

                buttons[2].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttons[2].translateGlobal(Vector3f(105f, 0f, 116f))
                buttonBases[2].rotateLocal(0f, Math.toRadians(90f), 0f)
                buttonBases[2].translateGlobal(Vector3f(105f, 0f, 116f))

                val firstButtonFP = buttons[0].getWorldPosition()
                val secoundButtonFP = buttons[1].getWorldPosition()
                val thirdButtonFP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(-0.2f, 0f, 0f))
                val firstButtonBP = buttons[0].getWorldPosition()
                val secoundButtonBP = buttons[1].getWorldPosition()
                val thirdButtonBP = buttons[2].getWorldPosition()
                buttons[0].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[1].translateGlobal(Vector3f(0.2f, 0f, 0f))
                buttons[2].translateGlobal(Vector3f(0.2f, 0f, 0f))

                buttonPositions.add(firstButtonFP)
                buttonPositions.add(secoundButtonFP)
                buttonPositions.add(thirdButtonFP)
                buttonPositions.add(firstButtonBP)
                buttonPositions.add(secoundButtonBP)
                buttonPositions.add(thirdButtonBP)
            }
        }
        return buttonPositions
    }

    fun doorSpawn (gate: Renderable, gateDoors: MutableList<Renderable>, firstCameraPosition: Renderable, rnd: Int): MutableList<Vector3f> {

        val returnValues = mutableListOf<Vector3f>()

        when(rnd) {
            1 -> {
                gate.translateGlobal(Vector3f(60f, 0f, 0f))
                firstCameraPosition.rotateLocal(Math.toRadians(-15f), 0f, 0f)
                firstCameraPosition.translateGlobal(Vector3f(60f, -4f, 10f))

                gateDoors[0].translateGlobal(Vector3f(58f, 0f, 0f))
                gateDoors[1].translateGlobal(Vector3f(62f, 0f, 0f))

                val gateDoorLC = gateDoors[0].getWorldPosition()
                val gateDoorRC = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(-2f, 0f, 0f))
                gateDoors[1].translateGlobal(Vector3f(2f, 0f, 0f))
                val gateDoorLO = gateDoors[0].getWorldPosition()
                val gateDoorRO = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(2f, 0f, 0f))
                gateDoors[1].translateGlobal(Vector3f(-2f, 0f, 0f))

                returnValues.add(gateDoorLC)
                returnValues.add(gateDoorRC)
                returnValues.add(gateDoorLO)
                returnValues.add(gateDoorRO)
            }
            2 -> {
                gate.translateGlobal(Vector3f(60f, 0f, 120f))
                firstCameraPosition.rotateLocal(0f, Math.toRadians(180f), 0f)
                firstCameraPosition.rotateLocal(Math.toRadians(-15f), 0f, 0f)
                firstCameraPosition.translateGlobal(Vector3f(60f, -4f, 106f))

                gateDoors[0].translateGlobal(Vector3f(58f, 0f, 120f))
                gateDoors[1].translateGlobal(Vector3f(62f, 0f, 120f))

                val gateDoorLC = gateDoors[0].getWorldPosition()
                val gateDoorRC = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(-2f, 0f, 0f))
                gateDoors[1].translateGlobal(Vector3f(2f, 0f, 0f))
                val gateDoorLO = gateDoors[0].getWorldPosition()
                val gateDoorRO = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(2f, 0f, 0f))
                gateDoors[1].translateGlobal(Vector3f(-2f, 0f, 0f))

                returnValues.add(gateDoorLC)
                returnValues.add(gateDoorRC)
                returnValues.add(gateDoorLO)
                returnValues.add(gateDoorRO)
            }
            3 -> {
                gate.rotateLocal(0f, Math.toRadians(90f), 0f)
                gate.translateGlobal(Vector3f(0f, 0f, 60f))
                firstCameraPosition.rotateLocal(0f, Math.toRadians(90f), 0f)
                firstCameraPosition.rotateLocal(Math.toRadians(-15f), 0f, 0f)
                firstCameraPosition.translateGlobal(Vector3f(10f, -4f, 60f))

                gateDoors[0].rotateLocal(0f, Math.toRadians(90f), 0f)
                gateDoors[0].translateGlobal(Vector3f(0f, 0f, 58f))
                gateDoors[1].rotateLocal(0f, Math.toRadians(90f), 0f)
                gateDoors[1].translateGlobal(Vector3f(0f, 0f, 62f))

                val gateDoorLC = gateDoors[0].getWorldPosition()
                val gateDoorRC = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(0f, 0f, -2f))
                gateDoors[1].translateGlobal(Vector3f(0f, 0f, 2f))
                val gateDoorLO = gateDoors[0].getWorldPosition()
                val gateDoorRO = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(0f, 0f, 2f))
                gateDoors[1].translateGlobal(Vector3f(0f, 0f, -2f))

                returnValues.add(gateDoorLC)
                returnValues.add(gateDoorRC)
                returnValues.add(gateDoorLO)
                returnValues.add(gateDoorRO)
            }
            4 -> {
                gate.rotateLocal(0f, Math.toRadians(90f), 0f)
                gate.translateGlobal(Vector3f(120f, 0f, 60f))
                firstCameraPosition.rotateLocal(0f, Math.toRadians(-90f), 0f)
                firstCameraPosition.rotateLocal(Math.toRadians(-15f), 0f, 0f)
                firstCameraPosition.translateGlobal(Vector3f(106f, -4f, 60f))

                gateDoors[0].rotateLocal(0f, Math.toRadians(90f), 0f)
                gateDoors[0].translateGlobal(Vector3f(120f, 0f, 58f))
                gateDoors[1].rotateLocal(0f, Math.toRadians(90f), 0f)
                gateDoors[1].translateGlobal(Vector3f(120f, 0f, 62f))

                val gateDoorLC = gateDoors[0].getWorldPosition()
                val gateDoorRC = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(0f, 0f, -2f))
                gateDoors[1].translateGlobal(Vector3f(0f, 0f, 2f))
                val gateDoorLO = gateDoors[0].getWorldPosition()
                val gateDoorRO = gateDoors[1].getWorldPosition()
                gateDoors[0].translateGlobal(Vector3f(0f, 0f, 2f))
                gateDoors[1].translateGlobal(Vector3f(0f, 0f, -2f))

                returnValues.add(gateDoorLC)
                returnValues.add(gateDoorRC)
                returnValues.add(gateDoorLO)
                returnValues.add(gateDoorRO)
            }
        }
        return returnValues
    }

    fun buildLabyrith(walls: MutableList<Renderable>, wallsBig: MutableList<Renderable>, pillars: MutableList<Renderable>, pillarsBig: MutableList<Renderable>, wallDespawn: Int) {


        wallsBig[0].translateGlobal(Vector3f(4f, 0f, 0f))
        wallsBig[1].translateGlobal(Vector3f(12f, 0f, 0f))
        wallsBig[2].translateGlobal(Vector3f(20f, 0f, 0f))
        wallsBig[3].translateGlobal(Vector3f(28f, 0f, 0f))
        wallsBig[4].translateGlobal(Vector3f(36f, 0f, 0f))
        wallsBig[5].translateGlobal(Vector3f(44f, 0f, 0f))
        wallsBig[6].translateGlobal(Vector3f(52f, 0f, 0f))
        if (wallDespawn != 7) wallsBig[7].translateGlobal(Vector3f(60f, 0f, 0f)) else wallsBig[7].translateGlobal(Vector3f(0f, -10f, 0f))
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
        if (wallDespawn != 22) wallsBig[22].translateGlobal(Vector3f(60f, 0f, 120f)) else wallsBig[22].translateGlobal(Vector3f(0f, -10f, 0f))
        wallsBig[23].translateGlobal(Vector3f(68f, 0f, 120f))
        wallsBig[24].translateGlobal(Vector3f(76f, 0f, 120f))
        wallsBig[25].translateGlobal(Vector3f(84f, 0f, 120f))
        wallsBig[26].translateGlobal(Vector3f(92f, 0f, 120f))
        wallsBig[27].translateGlobal(Vector3f(100f, 0f, 120f))
        wallsBig[28].translateGlobal(Vector3f(108f, 0f, 120f))
        wallsBig[29].translateGlobal(Vector3f(116f, 0f, 120f))

        var q = 95
        while (q < 196) {
            walls[q].rotateLocal(0f, Math.toRadians(90f), 0f)
            q++
        }

        var o = 30
        while (o < 60) {
            wallsBig[o].rotateLocal(0f, Math.toRadians(90f), 0f)
            o++
        }

        wallsBig[30].translateGlobal(Vector3f(0f, 0f, 4f))
        wallsBig[31].translateGlobal(Vector3f(0f, 0f, 12f))
        wallsBig[32].translateGlobal(Vector3f(0f, 0f, 20f))
        wallsBig[33].translateGlobal(Vector3f(0f, 0f, 28f))
        wallsBig[34].translateGlobal(Vector3f(0f, 0f, 36f))
        wallsBig[35].translateGlobal(Vector3f(0f, 0f, 44f))
        wallsBig[36].translateGlobal(Vector3f(0f, 0f, 52f))
        if (wallDespawn != 37) wallsBig[37].translateGlobal(Vector3f(0f, 0f, 60f)) else wallsBig[37].translateGlobal(Vector3f(0f, -10f, 0f))
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
        if (wallDespawn != 52) wallsBig[52].translateGlobal(Vector3f(120f, 0f, 60f)) else wallsBig[52].translateGlobal(Vector3f(0f, -10f, 0f))
        wallsBig[53].translateGlobal(Vector3f(120f, 0f, 68f))
        wallsBig[54].translateGlobal(Vector3f(120f, 0f, 76f))
        wallsBig[55].translateGlobal(Vector3f(120f, 0f, 84f))
        wallsBig[56].translateGlobal(Vector3f(120f, 0f, 92f))
        wallsBig[57].translateGlobal(Vector3f(120f, 0f, 100f))
        wallsBig[58].translateGlobal(Vector3f(120f, 0f, 108f))
        wallsBig[59].translateGlobal(Vector3f(120f, 0f, 116f))

        pillarsBig[0].translateGlobal(Vector3f(0f, 0f, 0f))
        pillarsBig[1].translateGlobal(Vector3f(120f, 0f, 0f))
        pillarsBig[2].translateGlobal(Vector3f(0f, 0f, 120f))
        pillarsBig[3].translateGlobal(Vector3f(120f, 0f, 120f))

        wallsBig[60].rotateLocal(0f, Math.toRadians(90f), 0f)
        wallsBig[60].translateGlobal(Vector3f(56f, 0f, -4f))
        wallsBig[61].translateGlobal(Vector3f(60f, 0f, -8f))
        wallsBig[62].rotateLocal(0f, Math.toRadians(90f), 0f)
        wallsBig[62].translateGlobal(Vector3f(64f, 0f, -4f))

        wallsBig[63].rotateLocal(0f, Math.toRadians(90f), 0f)
        wallsBig[63].translateGlobal(Vector3f(56f, 0f, 124f))
        wallsBig[64].translateGlobal(Vector3f(60f, 0f, 128f))
        wallsBig[65].rotateLocal(0f, Math.toRadians(90f), 0f)
        wallsBig[65].translateGlobal(Vector3f(64f, 0f, 124f))

        wallsBig[66].translateGlobal(Vector3f(-4f, 0f, 56f))
        wallsBig[67].rotateLocal(0f, Math.toRadians(90f), 0f)
        wallsBig[67].translateGlobal(Vector3f(-8f, 0f, 60f))
        wallsBig[68].translateGlobal(Vector3f(-4f, 0f, 64f))

        wallsBig[69].translateGlobal(Vector3f(124f, 0f, 56f))
        wallsBig[70].rotateLocal(0f, Math.toRadians(90f), 0f)
        wallsBig[70].translateGlobal(Vector3f(128f, 0f, 60f))
        wallsBig[71].translateGlobal(Vector3f(124f, 0f, 64f))

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