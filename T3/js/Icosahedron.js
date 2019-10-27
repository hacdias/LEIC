/* global THREE, Mesh */
'use strict'

const PHI = (1 + Math.sqrt(5)) / 2

class Icosahedron extends THREE.Object3D {
  constructor () {
    super()
    console.log(PHI)

    const values = [0, -1, +1, -PHI, +PHI]
    const points = []

    for (const v1 of values) {
      for (const v2 of values) {
        if (Math.abs(v1) === Math.abs(v2)
          || Math.abs(v1) === 0 && Math.abs(v2) === PHI
          || Math.abs(v1) === 1 && Math.abs(v2) === 0
          || Math.abs(v1) === PHI && Math.abs(v2) === 1) {
          continue
        }

        for (const v3 of values) {
          if (Math.abs(v1) === Math.abs(v3) || Math.abs(v2) === Math.abs(v3)) {
            continue
          }

          points.push(new THREE.Vector3(v1, v2, v3))
        }
      }
    }

    var geometry = new THREE.ConvexGeometry(points);
    var mesh = new Mesh( geometry, {color: 0x00ff00} );
    this.add(mesh)
  }
}
