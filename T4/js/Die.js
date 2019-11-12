class Die extends THREE.Object3D {
  constructor (dimensions = 10) {
    super()

    const geo = new THREE.BoxGeometry(dimensions, dimensions, dimensions)
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    const loader = new THREE.TextureLoader()
    const textures = [1, 2, 3, 4, 5, 6].map(i => loader.load(`./assets/dice-${i}.png`))

    const mesh = new Mesh(
      geo,
      textures.map(text => ({
        wireframe: false,
        map: text
      })),
      textures.map(text => ({
        wireframe: false,
        map: text,
        bumpMap: text,
        bumpScale: 1,
        shininess: 10, 
        specular: 0xffffff,
        color: 0xffffff
      }))
    )

    mesh.rotation.x = Math.PI / 4
    mesh.rotation.z = Math.PI / 4
    mesh.castShadow = true

    this.mesh = mesh
    this.add(mesh)
  }

  animate () {
    this.rotation.y += Math.PI / 360
  }
}