class Chess extends THREE.Object3D {
  constructor (dimension = 100) {
    super()

    const texture = new THREE.TextureLoader().load('./assets/chess.jpg')
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping
    texture.repeat.set(2, 2)
    const bumps = new THREE.TextureLoader().load('./assets/wood-bump.jpg')
    bumps.wrapS = bumps.wrapT = THREE.RepeatWrapping
    bumps.repeat.set(2, 2)

    const geo = new THREE.BoxGeometry(dimension, 0.01, dimension, 10, 1, 10)
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    const mesh = new Mesh(geo, {
      wireframe: false,
      map: texture
    }, {
      shininess: 10,
      bumpMap: bumps,
      bumpScale: 0.15
    })

    mesh.receiveShadow = true;
    this.add(mesh)
  }
}