/* global THREE */
'use strict'

class Wall extends THREE.Mesh {
  constructor ({ width, height, depth }) {
    const geometry = new THREE.BoxGeometry(width, height, depth)
    const material = new THREE.MeshBasicMaterial({
      color: 0xC0C0C0,
      wireframe: true
    })

    super(geometry, material)
    this.depth = depth
    this.width = width
    this.height = height
  }
}
