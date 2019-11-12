/* global THREE */
'use strict'

const ASPECT_RATIO = 16 / 9
const PLANE_H = 100

function getCameraSizes () {
  const scale = window.innerWidth / window.innerHeight
  let width, height

  if (scale > ASPECT_RATIO) {
    width = scale * PLANE_H
    height = PLANE_H
  } else {
    width = ASPECT_RATIO * PLANE_H
    height = width / scale
  }

  return { width, height }
}

function createOrtographicCamera ({ position, lookAt }) {
  var { width, height } = getCameraSizes()

  const cam = new THREE.OrthographicCamera(
    -width / 2,
    width / 2,
    height / 2,
    -height / 2,
    -1000,
    1000
  )
  cam.position.set(...position)
  cam.lookAt(new THREE.Vector3(...lookAt))
  return cam
}

function updateOrtographicCamera (cam) {
  const { width, height } = getCameraSizes()
  cam.left = -width / 2
  cam.right = width / 2
  cam.top = height / 2
  cam.bottom = -height / 2
  cam.updateProjectionMatrix()
}

function createPerspectiveCamera ({ position, lookAt }) {
  const cam = new THREE.PerspectiveCamera(90, window.innerWidth / window.innerHeight, 1, 1000)
  cam.position.set(...position)
  cam.lookAt(new THREE.Vector3(...lookAt))
  return cam
}

function updatePerspectiveCamera (cam) {
  cam.aspect = window.innerWidth / window.innerHeight
  cam.updateProjectionMatrix()
}

class Scene extends THREE.Scene {
  constructor () {
    super()

    this.clock = new THREE.Clock()
    this._makeObjects()
    this._makeDirectionalLight()
    this._makePointLight()
    this._makeCameras()
  }

  _makeObjects () {
    this.chess = new Chess()
    this.add(this.chess)

    this.die = new Die()
    this.add(this.die)

    this.ball = new Ball()
    this.add(this.ball)
  }

  _makeDirectionalLight () {
    this.directionalLight = new THREE.DirectionalLight(0x3f9cff, 0.9)
    this.directionalLight.position.set(5, 20, 10)
    this.directionalLight.castShadow = true

    this.directionalLight.shadow.camera.near = -60
    this.directionalLight.shadow.camera.far = 90
    this.directionalLight.shadow.camera.left = -90
    this.directionalLight.shadow.camera.right = 90
    this.directionalLight.shadow.camera.top = 90
    this.directionalLight.shadow.camera.bottom = -90

    this.directionalLight.shadow.mapSize.width = 4096
    this.directionalLight.shadow.mapSize.height = 4096

    this.add(this.directionalLight)
  }

  _makePointLight () {
    this.pointLight = new THREE.PointLight(0xff653f, 2, 60, 2)
    this.pointLight.position.set(-10, 15, 0)
    this.pointLight.castShadow = true
    this.add(this.pointLight)
  }

  _makeCameras () {
    this.cameras = new Array(2)
    this.cameras[0] = createPerspectiveCamera({ position: [0, 50, 50], lookAt: [0, 0, 0] })
    this.cameras[1] = createOrtographicCamera({ position: [0, 50, 0], lookAt: [0, 0, 0] })

    this.controls = new THREE.OrbitControls (this.cameras[0], renderer.domElement)

    this.add(this.cameras[0])
    this.add(this.cameras[1])
  }

  resize () {
    updatePerspectiveCamera(this.cameras[0])
    updateOrtographicCamera(this.cameras[1])
  }

  animate () {
    const delta = this.clock.getDelta()

    this.die.animate(delta)
    this.ball.animate(delta)
    this.controls.update()
  }

  toggleLight () {
    this.traverse(node => {
      if (node instanceof Mesh) node.toggleLight()
    })
  }

  toggleDirectionalLight () {
    this.directionalLight.visible = !this.directionalLight.visible
  }

  togglePointLight () {
    this.pointLight.visible = !this.pointLight.visible
  }

  toggleWireframe () {
    this.traverse(node => {
      if (node instanceof Mesh) node.toggleWireframe()
    })
  }

  toggleBall () {
    // TODO
  }

  togglePause () {
    // TODO
  }

  reset () {
    // TODO: only if paused
  }
}
