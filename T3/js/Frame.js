class Frame extends THREE.Object3D {
  constructor ({ width, height, depth }) {
    super()

    const painting = new Painting({
      width: width - 2 * depth,
      height: height - 2 * depth
    })

    painting.position.x = 2

    const geometry = new THREE.BoxGeometry(depth, height, width)
    const material = new THREE.MeshBasicMaterial({
      color: 0xd4af37
    })

    const frame = new THREE.Mesh(geometry, material)


    this.add(frame)
    this.add(painting)
  }

  _makeBox (width, height, depth) {
    const geometry = new THREE.BoxGeometry(width, height, depth)
    const material = new THREE.MeshBasicMaterial({
      color: 0xd4af37
    })

    return new THREE.Mesh(geometry, material)
  }
}