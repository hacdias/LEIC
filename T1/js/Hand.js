/* global THREE */
'use strict'

class Hand extends THREE.Mesh {
  constructor ({ radius, height }) {
    const geometry = new THREE.CylinderGeometry(radius, radius, height, 32)
    const material = new THREE.MeshBasicMaterial({ color: 0xffff00 })

    super(geometry, material)

    this.rotation.x = Math.PI / 2

    const fingerLeft = this._buildFinger()
    const fingerRight = this._buildFinger()
    fingerLeft.position.x = 2
    fingerLeft.position.y = 4 - height
    fingerLeft.position.z = 0

    fingerRight.position.x = -2
    fingerRight.position.y = 4 - height
    fingerRight.position.z = 0
    this.add(fingerLeft)
    this.add(fingerRight)
  }

  _buildFinger () {
    const material = new THREE.MeshBasicMaterial({ color: 0xffff00 })
    const geometry = new THREE.BoxGeometry(1, 5, 1)
    return new THREE.Mesh(geometry, material)
  }
}