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
  constructor ({ radius, direction, speed }) {
    super()
    const geometry = new THREE.SphereGeometry(radius, 15, 15)
    const material = new THREE.MeshBasicMaterial({
      color: 0xffffff,
      wireframe: true
    })

    this.falling = false
    this.radius = radius
    this.diameterSquared = Math.pow(radius * 2, 2)

    this.ball = new THREE.Mesh(geometry, material)

    this.add(this.ball)

    this.add(new THREE.AxesHelper(5))

    this.speed = speed || getRandomInt(4, 10)
    this.direction = direction || new THREE.Vector3(
      Math.random() * 2 - 1,
      0,
      Math.random() * 2 - 1
    ).normalize()

    setInterval(this.updateSpeed.bind(this), 1000)

    // this.rotation.x = -Math.PI / 2
  }

  animate (delta) {
    if (this.speed === 0) {
      return
    }

    this.position.add(this.direction.clone().multiplyScalar(this.speed * delta))
    this.setRotationFromAxisAngle(Y_AXIS, calcAngleToRotate(this.direction) + Math.PI / 2)
    this.ball.rotateX(delta * this.speed / this.radius)
  }

  collidesWith (obj) {
    if (obj instanceof Ball) {
      return this.position.distanceToSquared(obj.position) <= this.diameterSquared
    }

    if (obj instanceof Field) {
      for (const { wall, normal } of obj.walls) {
        const u = new THREE.Vector3()
        const union = this.getWorldPosition(u).clone().sub(wall.getWorldPosition(u))
        const copy = normal.clone()
        const dst = union.projectOnVector(copy)

        if (dst.dot(copy) < 0 /* || dst.lengthSq() <= (this.radius + wall.depth / 2) ** 2 */) {
          // console.log('p')
          this.direction.reflect(copy)
        }
      }
    }

    return false
  }

  resolveCollision(obj) {
    [this.direction, obj.direction] = [obj.direction, this.direction];
    [this.velocity, obj.velocity] = [obj.velocity, this.velocity];
    this.collided = true;
    obj.collided = true;
  }

  updateSpeed () {
    if (this.falling) {
      this.speed += 1
    } else if (this.speed > 0) {
      this.speed = Math.max(0, this.speed - 0.25)
    }
  }

  fallToInfinity () {
    this.direction.setComponent(1, -1)
    this.falling = true
  }
}
