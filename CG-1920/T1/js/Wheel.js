/* global THREE */
'use strict'

class Wheel extends THREE.Mesh {
  constructor (radius) {
    const material = new THREE.MeshBasicMaterial({
      color: 0xFF0000
    })

    const geometry = new THREE.SphereGeometry(radius, 32, 32)
    super(geometry, material)
  }
}
