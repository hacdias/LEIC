/* global THREE, Mesh */
'use strict'

class Room extends THREE.Object3D {
  constructor ({ dimension, depth }) {
    super()

    const floor = this._buildWall(dimension, depth, 0xC19A6B)
    floor.receiveShadow = true
    floor.rotation.x = Math.PI / 2
    
    const top = this._buildWall(dimension, depth)
    top.receiveShadow = true
    top.position.z = -dimension / 2 + depth / 2
    top.position.y = dimension / 2 + depth / 2
    
    const bottom = this._buildWall(dimension, depth)
    bottom.receiveShadow = true
    bottom.rotation.y = Math.PI / 2
    bottom.position.x = -dimension / 2 + depth / 2
    bottom.position.y = dimension / 2 + depth / 2

    this.add(floor)
    this.add(top)
    this.add(bottom)
  }

  _buildWall (dimension, depth, color = 0xFFFFFF) {
    const geometry = new THREE.BoxGeometry(dimension, dimension, depth)

    return new Mesh(geometry, {
      color
    })
  }
}
