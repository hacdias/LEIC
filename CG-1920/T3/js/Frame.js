/* global THREE, Painting, Mesh */
'use strict'

class Frame extends THREE.Object3D {
  constructor ({ width, height, depth }) {
    super()

    const painting = new Painting({
      width: width - 2 * depth,
      height: height - 2 * depth
    })

    painting.position.x = 2

    const geometry = new THREE.BoxGeometry(depth, height, width)
    const frame = new Mesh(geometry, {
      color: 0xd4af37
    })

    this.add(frame)
    this.add(painting)
  }
}
