/* global THREE */
'use strict'

class Mesh extends THREE.Mesh {
  constructor (geometry, options) {
    const materials = {
      phong: new THREE.MeshPhongMaterial(options),
      lambert: new THREE.MeshLambertMaterial(options),
      basic: new THREE.MeshBasicMaterial(options)
    }

    super(geometry, materials.phong)
    this.materials = materials
    this.lastLightMaterial = materials.phong
    return this
  }

  toggleLightMaterial () {
    const updateMaterial = this.material !== this.materials.basic

    if (this.material === this.materials.phong) {
      if (updateMaterial) this.material = this.materials.lambert
      this.lastLightMaterial = this.materials.lambert
    } else {
      if (updateMaterial) this.material = this.materials.phong
      this.lastLightMaterial = this.materials.phong
    }
  }

  toggleLight () {
    if (this.material === this.materials.basic) {
      this.material = this.lastLightMaterial
    } else {
      this.material = this.materials.basic
    }
  }
}
