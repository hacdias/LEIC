/* global THREE, Wall */
'use strict'

class Field extends THREE.Object3D {
  constructor ({ width, height, depth }) {
    super()

    this.depth = depth

    this.top = new Wall({ width, height, depth })
    this.left = new Wall({ width, height, depth })
    this.bottom = new Wall({ width, height, depth })
    this.base = new Wall({ width: width, height: width, depth })

    this.top.applyMatrix(makeTrans(0, 0, -width / 2 - depth /2))
    this.bottom.applyMatrix(makeTrans(0, 0, -this.top.position.z))
    this.left.applyMatrix(makeRotY(Math.PI / 2))
    this.left.applyMatrix(makeTrans(this.top.position.z + depth, 0, 0))
    this.base.applyMatrix(makeRotX(Math.PI / 2))
    this.base.applyMatrix(makeTrans(0, -height / 2 - depth / 2, 0))

    this.add(this.top)
    this.add(this.left)
    this.add(this.bottom)
    this.add(this.base)

    this.walls = [
      {
        wall: this.top,
        normal: new THREE.Vector3(0, 0, -1).normalize()
      },
      {
        wall: this.bottom,
        normal: new THREE.Vector3(0, 0, 1).normalize()
      },
      {
        wall: this.left,
        normal: new THREE.Vector3(1, 0, 0).normalize()
      }
    ]

    this.boundingBox = new THREE.Box3().setFromObject(this)
  }
}
