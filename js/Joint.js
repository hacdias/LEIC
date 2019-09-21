/* global THREE */
'use strict'

class Joint extends THREE.Mesh {
  constructor (radius = 3) {
    const material = new THREE.MeshStandardMaterial({
      color: 0xC0C0C0
    })

    const geometry = new THREE.SphereGeometry(radius, 32, 32)
    super(geometry, material)
  }
}
