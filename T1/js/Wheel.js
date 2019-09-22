/* global THREE */
'use strict'

class Wheel extends THREE.Object3D {
  constructor (radius) {
    super()

    const material = new THREE.MeshBasicMaterial({
      color: 0xC0C0C0
    })

    const geometry = new THREE.SphereGeometry(radius, 32, 32)
    const mesh = new THREE.Mesh(geometry, material)
    this.add(mesh)
  }
}
