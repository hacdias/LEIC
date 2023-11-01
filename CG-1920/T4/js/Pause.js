/* global THREE */
'use strict'

class PauseScene extends THREE.Scene {
  constructor() {
    super()

    this._buildPause()
    this._makeCamera()
  }

  _buildPause () {
    const texture = new THREE.TextureLoader().load('./assets/pause.png')
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping
    texture.repeat.set(1,1)

    const geo = new THREE.BoxGeometry(PLANE_H * 2, 2, PLANE_H, 10, 1, 10);
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    var mesh = new THREE.Mesh(geo, new THREE.MeshBasicMaterial({
        color: 0xffffff,
        wireframe: false,
        map: texture
      })
    )

    this.add(mesh)
  }

  _makeCamera () {
    this.camera= createOrtographicCamera({ position: [0, 50, 0], lookAt: [0, 0, 0] })
    this.add(this.camera)
  }

  resize () {
    updateOrtographicCamera(this.camera)
  }
}
