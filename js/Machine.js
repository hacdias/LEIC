/* global THREE, Wheel, RoboticArm */
'use strict'

class Machine extends THREE.Object3D {
  constructor (width = 35, depth = 3) {
    super(...arguments)

    const geometry = new THREE.BoxGeometry(width, depth, width)
    const material = new THREE.MeshStandardMaterial({
      color: 0xC0C0C0
    })

    const table = new THREE.Mesh(geometry, material)

    table.position.y = depth * 3 / 2

    const wheelRadius = depth / 2

    const wheels = [
      new Wheel(wheelRadius),
      new Wheel(wheelRadius),
      new Wheel(wheelRadius),
      new Wheel(wheelRadius)
    ]

    const relativePosition = width / 2 - width * 0.05

    wheels[0].position.x = -relativePosition
    wheels[0].position.z = -relativePosition

    wheels[1].position.x = relativePosition
    wheels[1].position.z = -relativePosition

    wheels[2].position.x = relativePosition
    wheels[2].position.z = relativePosition

    wheels[3].position.x = -relativePosition
    wheels[3].position.z = relativePosition

    const articulationRadius = 7
    const articulation = new Articulation(articulationRadius)
    articulation.position.y = depth * 2

    this.add(table)
    this.add(articulation)

    const arm = new RoboticArm()
    arm.position.y = 20
    arm.rotation.x = -Math.PI / 6
    arm.position.z = -6

    this.add(arm)

    wheels.forEach(wheel => {
      wheel.position.y = wheelRadius
      this.add(wheel)
    })
  }
}
