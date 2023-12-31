/* global THREE */
'use strict'

class Die extends THREE.Object3D {
  constructor (dimensions = 10) {
    super()

    const geo = new THREE.BoxGeometry(dimensions, dimensions, dimensions)
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    const loader = new THREE.TextureLoader()
    const textures = [1, 2, 3, 4, 5, 6].map(i => ({
      map: loader.load(`./assets/dice-${i}.png`),
      bumpMap: loader.load(`./assets/dice-bump-${i}.png`),
      alphaMap: loader.load(`./assets/dice-alpha-${i}.png`)
    }))

    const mesh = new Mesh(
      geo,
      textures.map(({ map, alphaMap }) => ({
        wireframe: false,
        map,
        side: THREE.DoubleSide,
        transparent: true,
        alphaMap
      })),
      textures.map(({ map, bumpMap, alphaMap }) => ({
        wireframe: false,
        map,
        bumpMap,
        side: THREE.DoubleSide,
        transparent: true,
        alphaMap,
        bumpScale: 2,
        shininess: 10
      }))
    )

    mesh.rotation.x = Math.PI / 4
    mesh.rotation.z = Math.PI / 4
    mesh.position.y = dimensions - 1
    mesh.castShadow = true

    this.mesh = mesh
    this.add(mesh)
  }

  animate () {
   this.rotation.y += Math.PI / 360
  }
}