class Chess extends THREE.Object3D {
  constructor (dimension = 100) {
    super()

    const texture = new THREE.TextureLoader().load('./assets/chess.png')
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping
    texture.repeat.set(2, 2)

    const bumps = new THREE.TextureLoader().load('./assets/wood-bump.png')
    bumps.wrapS = bumps.wrapT = THREE.RepeatWrapping
    bumps.repeat.set(2, 2)

    const geo = new THREE.BoxGeometry(dimension, 0.01, dimension, 10, 1, 10)
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    this.mesh = new Mesh(geo, {
      wireframe: false,
      map: texture
    }, {
      shininess: 10,
      bumpMap: bumps,
      bumpScale: 0.15
    })

    this.mesh.receiveShadow = true
    this.add(this.mesh)
  }
}