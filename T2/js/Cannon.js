/* global THREE */
'use strict'

class Cannon extends THREE.Mesh {
  constructor ({ radius, length }) {
    const geometry = new THREE.CylinderGeometry(radius, radius + 2, length, 32)
    const material = new THREE.MeshBasicMaterial({
      color: 0xFF0000,
      wireframe: true
    })

    super(geometry, material)

    this.rotation.x = -Math.PI / 2
    this.rotation.z = Math.PI / 2
  }

  select () {
    this.material.color.setHex(0x0000FF)
  }

  deselect () {
    this.material.color.setHex(0xFF0000)
  }

  increaseAngle () {
    this.rotation.z += Math.PI / 360
  }

  decreaseAngle () {
    this.rotation.z -= Math.PI / 360
  }
}
