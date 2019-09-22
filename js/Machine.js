/* global THREE, Wheel, RoboticArm */
'use strict'

class Machine extends THREE.Object3D {
  constructor (width = 35, depth = 3) {
    super(...arguments)

    this._buildSurface({ width, depth })
    this._buildWheels({ width, depth })
    this._buildArticulation({ depth, radius: 7 })
    this._buildArm({ length: width * 0.7, depth: depth * 0.7 })
  }

  _buildSurface ({ width, depth }) {
    const geometry = new THREE.BoxGeometry(width, depth, width)
    const material = new THREE.MeshBasicMaterial({ color: 0xC0C0C0 })

    this.surface = new THREE.Mesh(geometry, material)
    this.surface.position.y = depth * 3 / 2
    this.add(this.surface)
  }

  _buildWheels ({ width, depth }) {
    this.wheelRadius = depth / 2

    this.wheels = [
      new Wheel(this.wheelRadius),
      new Wheel(this.wheelRadius),
      new Wheel(this.wheelRadius),
      new Wheel(this.wheelRadius)
    ]

    const relativePosition = width / 2 - width * 0.05

    this.wheels[0].position.x = -relativePosition
    this.wheels[0].position.z = -relativePosition

    this.wheels[1].position.x = relativePosition
    this.wheels[1].position.z = -relativePosition

    this.wheels[2].position.x = relativePosition
    this.wheels[2].position.z = relativePosition

    this.wheels[3].position.x = -relativePosition
    this.wheels[3].position.z = relativePosition

    this.wheels.forEach(wheel => {
      wheel.position.y = this.wheelRadius
      this.add(wheel)
    })
  }

  _buildArticulation ({ radius, depth }) {
    this.articulation = new Articulation(radius)
    this.articulation.position.y = depth * 2

    this.add(this.articulation)
  }

  _buildArm ({ length, depth }) {
    this.arm = new RoboticArm({ length, depth })
    this.arm.position.y = 20
    this.arm.position.z = -6
    this.arm.rotation.x = -Math.PI / 6

    this.add(this.arm)
  }

  rotateLeft () {
    this.rotation.y += Math.PI / 360
  }

  rotateRight () {
    this.rotation.y -= Math.PI / 360
  }

  moveArmFront () {
    console.warn('MOVE ARM FRONT')
    this.arm.rotation.x -= Math.PI / 360
  }

  moveArmBack () {
    console.warn('MOVE ARM BACK')

    this.arm.rotation.x += Math.PI / 360
  }

  translate (vector) {
    machine.translateOnAxis(new THREE.Vector3(...vector), 0.1)
  }
}
