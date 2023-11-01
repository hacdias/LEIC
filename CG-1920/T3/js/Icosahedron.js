/* global THREE, Mesh */
'use strict'

const PHI = (1 + Math.sqrt(5)) / 2

class Icosahedron extends THREE.Object3D {
  constructor () {
    super()

    const geometry = new THREE.Geometry()

    geometry.vertices.push(new THREE.Vector3(-1.1, PHI, 0))
    geometry.vertices.push(new THREE.Vector3(0.9, PHI, 0))
    geometry.vertices.push(new THREE.Vector3(-1.05, -PHI, 0))
    geometry.vertices.push(new THREE.Vector3(0.85, -PHI, 0))

    geometry.vertices.push(new THREE.Vector3(0, -1, PHI + 0.21))
    geometry.vertices.push(new THREE.Vector3(0, 1, PHI - 0.23))
    geometry.vertices.push(new THREE.Vector3(0, -1, -PHI + 0.14))
    geometry.vertices.push(new THREE.Vector3(0, 1, -PHI + 0.1))

    geometry.vertices.push(new THREE.Vector3(PHI, 0, -1.5))
    geometry.vertices.push(new THREE.Vector3(PHI, 0.3, 1.14))
    geometry.vertices.push(new THREE.Vector3(-PHI, 0, -1.98))
    geometry.vertices.push(new THREE.Vector3(-PHI, 0, 2))

    geometry.faces.push(new THREE.Face3(0, 11, 5))
    geometry.faces.push(new THREE.Face3(0, 5, 1))
    geometry.faces.push(new THREE.Face3(0, 1, 7))
    geometry.faces.push(new THREE.Face3(0, 7, 10))
    geometry.faces.push(new THREE.Face3(0, 10, 11))

    geometry.faces.push(new THREE.Face3(1, 5, 9))
    geometry.faces.push(new THREE.Face3(5, 11, 4))
    geometry.faces.push(new THREE.Face3(11, 10, 2))
    geometry.faces.push(new THREE.Face3(10, 7, 6))
    geometry.faces.push(new THREE.Face3(7, 1, 8))

    geometry.faces.push(new THREE.Face3(3, 9, 4))
    geometry.faces.push(new THREE.Face3(3, 4, 2))
    geometry.faces.push(new THREE.Face3(3, 2, 6))
    geometry.faces.push(new THREE.Face3(3, 6, 8))
    geometry.faces.push(new THREE.Face3(3, 8, 9))

    geometry.faces.push(new THREE.Face3(4, 9, 5))
    geometry.faces.push(new THREE.Face3(2, 4, 11))
    geometry.faces.push(new THREE.Face3(6, 2, 10))
    geometry.faces.push(new THREE.Face3(8, 6, 7))
    geometry.faces.push(new THREE.Face3(9, 8, 1))

    geometry.computeFaceNormals()
    geometry.computeVertexNormals()

    this.add(new Mesh(geometry, { color: 0xFF0000, wireframe: true }))

    this.rotation.y = Math.PI / 4
    this.scale.set(5, 5, 5)
  }
}
