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

    this._makeObjects()
    this._makeSpotLights()
    this._makeDirectionalLight()
    this._makeCameras()
  }

  _makeObjects () {
    this.room = new Room({ dimension: 100, depth: 2 })
    this.add(this.room)

    this.pedestal = new Pedestal({ height: 40, depth: 15, step: 2 })
    this.pedestal.position.set(30, 2, -30)
    this.add(this.pedestal)

    this.icosahedron = new Icosahedron()
    this.icosahedron.position.set(this.pedestal.position.x, 49, this.pedestal.position.z)
    this.add(this.icosahedron)

    this.frame = new Frame({ width: 40, height: 46.5, depth: 2 })
    this.frame.position.set(-48, 50, 20)
    this.add(this.frame)
  }

  _makeSpotLights () {
    const spotlights = new Array(4)

    for (let i = 0; i < 4; i++) {
      spotlights[i] = new Spotlight()
      this.add(spotlights[i])
    }

    spotlights[0].position.set(0, 50, 100)
    spotlights[1].position.set(100, 50, -20)
    spotlights[2].position.set(0, 150, 0)
    spotlights[3].position.set(50, 50, 50)

    spotlights.forEach(s => s.lookAt(0, 0, 0))
    spotlights[1].lookAt(-50, 50, 50)

    this.spotlights = spotlights
  }

  _makeDirectionalLight () {
    this.globalLight = new THREE.DirectionalLight(0xFFFFFF, 0.5)
    this.globalLight.position.set(100, 50, 100)
    this.globalLight.lookAt(0, 0, 0)

    this.globalLight.castShadow = true

    this.globalLight.shadow.mapSize.width = 1024
    this.globalLight.shadow.mapSize.height = 1024

    this.globalLight.shadow.camera.near = 500
    this.globalLight.shadow.camera.far = 4000
    this.globalLight.shadow.camera.fov = 30

    this.add(this.globalLight)
  }

  _makeCameras () {
    this.cameras = new Array(2)
    this.cameras[0] = createPerspectiveCamera({ position: [100, 100, 100], lookAt: [0, 50, 0] })
    this.cameras[1] = createOrtographicCamera({ position: [0, 50, 20], lookAt: [-48, 50, 20] })

    this.add(this.cameras[0])
    this.add(this.cameras[1])
  }

  resize () {
    updatePerspectiveCamera(this.cameras[0])
    updateOrtographicCamera(this.cameras[1])
  }

  toggleLight () {
    this.traverse(node => {
      if (node instanceof Mesh) node.toggleLight()
    })
  }

  toggleLightMaterial () {
    this.traverse(node => {
      if (node instanceof Mesh) node.toggleLightMaterial()
    })
  }

  toggleGlobalLight () {
    this.globalLight.visible = !this.globalLight.visible
  }

  toggleSpotlight (i) {
    this.spotlights[i].toggle()
  }
}
