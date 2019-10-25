/* global THREE, Room */
'use strict'

const flags = {
  camera: 1,
  spotlights: []
}

const ASPECT_RATIO = 16 / 9
const PLANE_H = 150
const DIAG_UNIT = Math.sqrt(0.5)

var scene, renderer
var spotlights = new Array(4)
var cameras = new Array(2)

function createScene () {
  scene = new THREE.Scene()
  scene.add(new THREE.AxesHelper(10))

  const room = new Room({ dimension: 100, depth: 2 })
  scene.add(room)

  const pedestal = new Pedestal({ height: 40, depth: 15, step: 2})
  pedestal.position.y = 2
  pedestal.position.x = 30

  scene.add(pedestal)
}

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

function createCamera () {
  cameras[0] = createPerspectiveCamera({ position: [100, 50, 100], lookAt: [0, 50, 0] })
  cameras[1] = createOrtographicCamera({ position: [150, 150, 150], lookAt: [0, 50, 0] })

  scene.add(cameras[0])
  scene.add(cameras[1])
}

function resizeCameras () {
  updatePerspectiveCamera(cameras[0])
  updateOrtographicCamera(cameras[1])
}

function animate () {
  requestAnimationFrame(animate)

  if (flags.resize) {
    flags.resize = false
    renderer.setSize(window.innerWidth, window.innerHeight)
    resizeCameras()
  }

  flags.spotlights.forEach(i => spotlights[i].toggle())
  flags.spotlights = []

  renderer.render(scene, cameras[flags.camera - 1])
}

function init () {
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(window.innerWidth, window.innerHeight)

  document.body.append(renderer.domElement)

  createScene()
  createCamera()

  animate()
}

document.addEventListener('DOMContentLoaded', () => {
  init()
})

document.addEventListener('keyup', event => {
  switch (event.key) {
    case '1':
    case '2':
    case '3':
    case '4':
      flags.spotlights.push(parseInt(event.key))
      return
    case '5':
    case '6':
      flags.camera = parseInt(event.key) - 4
      return
  }

  switch (event.code) {
    case 'KeyQ':
      console.warn('Toggle light')
      return
    case 'KeyW':
      console.warn('Toggle ilumination calculus')
      return
    case 'KeyE':
      console.warn('Toggle shadding')
  }
})

window.addEventListener('resize', () => {
  flags.resize = true
})
