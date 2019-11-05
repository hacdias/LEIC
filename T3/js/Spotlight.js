/* global THREE, Mesh */
'use strict'

class Spotlight extends THREE.Object3D {
  constructor () {
    super()

    this.light = new THREE.SpotLight(0xffffff, 1, 150, Math.PI, 0, 1)
    this.light.castShadow = true

    this.light.shadow.mapSize.width = 1024
    this.light.shadow.mapSize.height = 1024

    this.light.shadow.camera.near = 500
    this.light.shadow.camera.far = 4000
    this.light.shadow.camera.fov = 30
    this.obj = this._makeObject()

    this.add(this.obj)
    this.add(this.light)
  }

  _makeObject () {
    const group = new THREE.Group()
    const sphere = new Mesh(new THREE.SphereGeometry(5, 32, 32), { color: 0xFFFFFF })
    const cone = new Mesh(new THREE.ConeGeometry(5, 15, 32), { color: 0xFFFFFF })

    sphere.position.y = 5

    group.add(sphere)
    group.add(cone)
    group.rotation.x = -Math.PI / 2
    return group
  }

  toggle () {
    this.light.visible = !this.light.visible
  }
}
