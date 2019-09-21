/* global THREE, Machine, Floor  */
'use strict'

class Target extends THREE.Object3D {
  constructor () {
    super(...arguments)

    var geometry = new THREE.CylinderGeometry(7, 7, 30, 32)
    var material = new THREE.MeshStandardMaterial({ color: 0xffff00 })
    var cylinder = new THREE.Mesh(geometry, material)
    this.add(cylinder)

    cylinder.position.y = 15

    var geometry = new THREE.TorusGeometry(5, 2, 16, 100)
    var material = new THREE.MeshStandardMaterial({ color: 0x00ff00 })
    var torus = new THREE.Mesh(geometry, material)
    torus.position.y = 37
    this.add(torus)
  }
}
