/* global THREE */
'use strict'

class Articulation extends THREE.Mesh {
  constructor (radius = 7) {
    const material = new THREE.MeshBasicMaterial({
      color: 0xF0F0F0
    })

    const geometry = new THREE.SphereGeometry(radius, 32, 32, 0, 2 * Math.PI, 0, Math.PI / 2)
    super(geometry, material)
  }
}
