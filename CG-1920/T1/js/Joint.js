/* global THREE */
'use strict'

class Joint extends THREE.Mesh {
  constructor (radius) {
    const material = new THREE.MeshBasicMaterial({
      color: 0xF0F0F0
    })

    const geometry = new THREE.SphereGeometry(radius, 32, 32)
    super(geometry, material)
  }
}
