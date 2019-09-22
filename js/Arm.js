/* global THREE */
'use strict'

class Arm extends THREE.Object3D {
  constructor ({ length, depth }) {
    super(...arguments)

    const geometry = new THREE.BoxGeometry(depth, length, depth)
    const material = new THREE.MeshBasicMaterial({
      color: 0xC0C0C0
    })

    const arm = new THREE.Mesh(geometry, material)
    this.add(arm)
  }
}
