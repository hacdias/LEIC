/* global THREE, Wall */
'use strict'

class Field extends THREE.Object3D {
  constructor ({ width, height, depth }) {
    super()

    this.top = new Wall({ width, height, depth })
    this.left = new Wall({ width, height, depth })
    this.bottom = new Wall({ width, height, depth })

    this.top.position.z = -width / 2 - depth / 2
    this.bottom.position.z = -this.top.position.z
    this.left.position.x = this.top.position.z + depth
    this.left.rotation.y = Math.PI / 2

    this.add(this.top)
    this.add(this.left)
    this.add(this.bottom)

    this.walls = [
      {
        wall: this.top,
        normal: new THREE.Vector3(0, 0, -1)
      },
      {
        wall: this.bottom,
        normal: new THREE.Vector3(0, 0, 1)
      },
      {
        wall: this.left,
        normal: new THREE.Vector3(1, 0, 0)
      }
    ]
  }
}
