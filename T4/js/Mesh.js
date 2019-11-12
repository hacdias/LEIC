/* global THREE */
'use strict'

class Mesh extends THREE.Mesh {
  constructor (geometry, options, phongOpts) {
    const materials = {
      basic: Array.isArray(options)
        ? options.map(opts => new THREE.MeshBasicMaterial(opts))
        : new THREE.MeshBasicMaterial(options),
      phong: Array.isArray(phongOpts)
        ? phongOpts.map(opts => new THREE.MeshPhongMaterial(opts))
        : new THREE.MeshPhongMaterial(Object.assign({}, options, phongOpts))
    }

    super(geometry, materials.phong)
    this.materials = materials
    return this
  }

  toggleLight () {
    if (this.material === this.materials.basic) {
      this.material = this.materials.phong
    } else {
      this.material = this.materials.basic
    }
  }

  toggleWireframe () {
    for (const key in this.materials) {
      if (Array.isArray(this.materials[key])) {
        this.materials[key].forEach(m => {
          m.wireframe = !m.wireframe
        })
      } else {
        this.materials[key].wireframe = !this.materials[key].wireframe
      }
    }
  }
}
