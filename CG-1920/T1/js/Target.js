/* global THREE, Machine  */
'use strict'

class Target extends WireframeToggler {
  constructor () {
    super(...arguments)
    this._buildCylinder()
    this._buildTorus()
  }

  _buildCylinder () {
    const geometry = new THREE.CylinderGeometry(7, 7, 30, 32)
    const material = new THREE.MeshBasicMaterial({ color: 0xffff00 })
    this.cylinder = new THREE.Mesh(geometry, material)
    this.cylinder.position.y = 15
    this.add(this.cylinder)
  }

  _buildTorus () {
    const geometry = new THREE.TorusGeometry(5, 2, 16, 100)
    const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 })
    this.torus = new THREE.Mesh(geometry, material)
    this.torus.position.y = 37
    this.add(this.torus)
  }
}
