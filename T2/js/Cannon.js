/* global THREE */
'use strict'

function refBall () {
  const geometry = new THREE.SphereGeometry(BALL_RADIUS, 15, 15)
  const material = new THREE.MeshBasicMaterial({
    color: 0xffffff,
    visible: false,
    wireframe: true
  })
  return new THREE.Mesh(geometry, material)
}

class Cannon extends THREE.Object3D {
  constructor ({ radius, length }) {
    super()

    const geometry = new THREE.CylinderGeometry(radius, radius + 2, length, 32)
    const material = new THREE.MeshBasicMaterial({
      color: 0xFF0000,
      wireframe: true
    })

    this.length = length
    this.lastShoot = null
    this.refBall = refBall()
    this.botBall = refBall()
    this.mesh = new THREE.Mesh(geometry, material)

    this.mesh.applyMatrix(makeRotZ(Math.PI / 2))
    this.refBall.applyMatrix(makeTrans(-length / 2, 0, 0))
    this.botBall.applyMatrix(makeTrans(length / 2, 0, 0))
    this.add(this.mesh)
    this.add(this.refBall)
    this.add(this.botBall)
  }

  select () {
    this.mesh.material.color.setHex(0x0000FF)
  }

  deselect () {
    this.mesh.material.color.setHex(0xFF0000)
  }

  increaseAngle (mul = 1) {
    this.mesh.applyMatrix(makeRotY(Math.PI / 360 * mul))
    this.refBall.applyMatrix(makeRotY(Math.PI / 360 * mul))
    this.botBall.applyMatrix(makeRotY(Math.PI / 360 * mul))
  }

  decreaseAngle (mul = 1) {
    this.mesh.applyMatrix(makeRotY(-Math.PI / 360 * mul))
    this.refBall.applyMatrix(makeRotY(-Math.PI / 360 * mul))
    this.botBall.applyMatrix(makeRotY(-Math.PI / 360 * mul))
  }

  shoot () {
    // a cannon can only shoot a ball every 1 second
    if (this.lastShoot && Date.now() - this.lastShoot < 1 * 1000) {
      return
    }

    const bot = new THREE.Vector3()
    this.botBall.getWorldPosition(bot)

    const pos = new THREE.Vector3()
    this.refBall.getWorldPosition(pos)

    const direction = new THREE.Vector3()
    direction.subVectors(pos, bot).normalize()

    const ball = new Ball({
      radius: BALL_RADIUS,
      position: pos,
      direction: direction,
      speed: getRandomInt(50, 100)
    })

    this.lastShoot = Date.now()
    return ball
  }
}
