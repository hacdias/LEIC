/* global THREE */
'use strict'

class WireframeToggler extends THREE.Object3D {
  toggleWireframe () {
    this.traverse(child => {
      if (child instanceof THREE.Mesh) {
        child.material.wireframe = !child.material.wireframe
      }
    })
  }
}
