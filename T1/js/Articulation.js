/* global THREE */
'use strict'

class Articulation extends THREE.Object3D {
  constructor (radius = 7) {
    super()

    const material = new THREE.MeshBasicMaterial({
      color: 0xF0F0F0
    })

    const geometry = new THREE.SphereGeometry(radius, 32, 32, 0, 2 * Math.PI, 0, Math.PI / 2)
    const mesh = new THREE.Mesh(geometry, material)
    this.add(mesh)
  }
}
