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

    this.radius = radius
    this.speed = speed || 0
    this.direction = direction || new THREE.Vector3(0, 0, 0)

    const geometry = new THREE.SphereGeometry(radius, 15, 15)
    const material = new THREE.MeshBasicMaterial({
      color: 0xffffff,
      wireframe: true
    })

    this.ball = new THREE.Mesh(geometry, material)
    this.ball.add(new THREE.AxesHelper(radius + 3))

    this.add(this.ball)
    this.applyMatrix(makeTrans(position.x, position.y, position.z))
    this.oldPosition = position.clone()

    this.collided = false
    this.falling = false
  }

  move (delta) {
    if (this.speed === 0) return

    this.updateSpeed(delta)

    if (!this.atTheBase) {
      const gForce = new THREE.Vector3(0, -0.98, 0).multiplyScalar(delta)
      this.direction = this.direction.clone().add(gForce)
    }

    this.position.add(this.direction.clone().multiplyScalar(this.speed * delta))
    this.setRotationFromAxisAngle(Y_AXIS, calcAngleToRotate(this.direction) + Math.PI / 2)
    this.ball.rotateX(delta * this.speed / this.radius)
  }

  collidesWith (obj) {
    if (obj instanceof Ball) {
      //console.log(this.position.distanceToSquared(obj.position), (this.radius * 2) ** 2)
      return detectCollisionCubes(this, obj)
    }

    if (obj instanceof Field) {
      if (!this.atTheBase && detectCollisionCubes(this, obj.base)) {
        this.atTheBase = true
        this.direction.multiply(new THREE.Vector3(1, 0, 1))
      }

      if (this.atTheBase && !detectCollisionCubes(obj, this)) {
        if (!this.falling) {
          this.direction.setComponent(1, -1)
          this.falling = true
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
    console.log(ball)
    const bDir = ball.direction.clone()
    const mDir = this.direction.clone()

    ball.direction = mDir
    this.direction = bDir

    // TODO: IF one of the directions is 0,0,0

    let bSpeed = ball.speed
    let mSpeed = this.speed

    if (bSpeed === 0) {
      bSpeed = mSpeed / 2
    } else if (mSpeed === 0) {
      mSpeed = bSpeed / 2
    }

    this.speed = bSpeed
    ball.speed = mSpeed
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

  updateSpeed (delta) {
    if (this.falling) {
      this.speed += 1 * delta
    } else if (this.speed > 0) {
      this.speed = Math.max(0, this.speed - 5 * delta)
    }
  }
}
