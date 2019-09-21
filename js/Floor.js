/* global THREE */
'use strict'

class Floor extends THREE.Mesh {
  constructor () {
    const geometry = new THREE.BoxGeometry(1000, 1, 1000)
    const material = new THREE.MeshStandardMaterial({
      color: 0x87CEFA,
      roughness: 1,
      metalness: 0
    })

    super(geometry, material)
  }
}
