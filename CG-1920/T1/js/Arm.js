/* global THREE */
'use strict'

class Arm extends THREE.Mesh {
  constructor ({ length, depth }) {
    const geometry = new THREE.BoxGeometry(depth, length, depth)
    const material = new THREE.MeshBasicMaterial({
      color: 0xC0C0C0
    })

    super(geometry, material)
  }
}
