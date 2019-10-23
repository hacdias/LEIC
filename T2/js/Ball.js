/* global THREE, Field, getRandomInt */
'use strict'

const calcAngleToRotate = (vector) => {
  if (vector.x == 0 && vector.z == 0) {
    return 0
  } else if ((vector.x >= 0 && vector.z >= 0) || (vector.x <= 0 && vector.z >= 0)) {
    return Math.PI * 2 - vector.angleTo(X_AXIS)
  } else {
    return vector.angleTo(X_AXIS)
  }
}

const X_AXIS = new THREE.Vector3(1, 0, 0)
const Y_AXIS = new THREE.Vector3(0, 1, 0)
const Z_AXIS = new THREE.Vector3(0, 0, 1)

class Ball extends THREE.Object3D {
  constructor ({ radius, direction, position, speed }) {
    super()

    this.collided = false
    this.falling = false
    this.creationTime = Date.now()
    this.radius = radius
    this.speed = speed || 0
    this.direction = direction || new THREE.Vector3(0, 0, 0)

    const geometry = new THREE.SphereGeometry(radius, 15, 15)
    const material = new THREE.MeshBasicMaterial({
      color: 0xffffff,
      wireframe: true
    })

    this.ball = new THREE.Mesh(geometry, material)
    this.axis = new THREE.AxesHelper(radius * 1.5 )

    this.add(this.ball)
    this.add(this.axis)
    this.applyMatrix(makeTrans(position.x, position.y, position.z))
    this.oldPosition = position.clone()
  }

  move (delta) {
    if (this.falling) {
      this.speed += 1 * delta
      const gForce = new THREE.Vector3(0, -0.98, 0).multiplyScalar(delta)
      this.direction = this.direction.clone().add(gForce)
    } else if (this.speed > 0) {
      this.speed = Math.max(0, this.speed - 5 * delta)
    }

    if (this.speed === 0) {
      this.direction = new THREE.Vector3(0, 0, 0)
      return
    }

    this.position.add(this.direction.clone().multiplyScalar(this.speed * delta))
    this.setRotationFromAxisAngle(Y_AXIS, calcAngleToRotate(this.direction) + Math.PI / 2)
    this.ball.rotateX(delta * this.speed / this.radius)
  }

  collidesWith (obj) {
    const thisPos = new THREE.Vector3()
    this.ball.getWorldPosition(thisPos)

    if (obj instanceof Ball) {
      const otherPos = new THREE.Vector3()
      obj.ball.getWorldPosition(otherPos)

      return thisPos.distanceToSquared(otherPos) <= (BALL_RADIUS * 2) ** 2
    }

    if (obj instanceof Field) {
      if (!detectCollisionCubes(obj, this) && Date.now() - this.creationTime >= 1500) {
        if (!this.falling) {
          this.direction.setComponent(1, -1)
          this.falling = true
          this.fallingTime = Date.now()
        }

        return false
      }

      for (const { wall, normal } of obj.walls) {
        if (detectCollisionCubes(this, wall)) {
          this.direction.reflect(normal)
          this.collided = true
          return true
        }
      }
    }

    return false
  }

  solveCollision (ball) {
    [this.direction, ball.direction] = [ball.direction, this.direction];
    [this.speed, ball.speed] = [ball.speed, this.speed];
    this.collided = true
    ball.collided = true
  }

  check () {
    if (this.collided) {
      this.position.set(this.oldPosition.x, this.oldPosition.y, this.oldPosition.z)
    } else {
      this.oldPosition = this.position.clone()
    }

    this.collided = false
  }

  showAxis (visible) {
    this.axis.visible = visible
  }
}
