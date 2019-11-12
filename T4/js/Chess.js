class Chess extends THREE.Object3D {
  constructor (dimension = 75) {
    super()

    // TODO: bump map

    const texture = new THREE.TextureLoader().load('./assets/chess.jpg')
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping
    texture.repeat.set(2, 2)

    const geo = new THREE.BoxGeometry(dimension, 0.01, dimension, 10, 1, 10)
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    const mesh = new Mesh(geo, {
      wireframe: false,
      map: texture
    }, {
      shininess: 10
    })

    mesh.receiveShadow = true;
    this.add(mesh)
  }
}