/* global THREE, Machine, Target  */
'use strict'

const flags = {
  toggleWireframe: true,
  camera: 1,
  pressed: {}
}

const ASPECT_RATIO = 16 / 9
const PLANE_H = 100
const DIAG_UNIT = Math.sqrt(0.5)

var scene, renderer, machine, target
var cameras = new Array(3)

function createScene () {
  scene = new THREE.Scene()
  scene.add(new THREE.AxesHelper(10))

  machine = new Machine()
  machine.position.z = -25

  target = new Target()
  target.position.z = 25

  scene.add(machine)
  scene.add(target)
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

function createCamera () {
  cameras[0] = createOrtographicCamera({ position: [0, 10, 0], lookAt: [0, 0, 0] })
  cameras[1] = createOrtographicCamera({ position: [30, 20, 0], lookAt: [0, 20, 0] })
  cameras[2] = createOrtographicCamera({ position: [0, 20, 30], lookAt: [0, 20, 0] })

  cameras.forEach(cam => scene.add(cam))
}

function resizeCameras () {
  const { width, height } = getCameraSizes()

  for (const cam of cameras) {
    cam.left = -width / 2
    cam.right = width / 2
    cam.top = height / 2
    cam.bottom = -height / 2
    cam.updateProjectionMatrix()
  }
}

function animate () {
  requestAnimationFrame(animate)

  if (flags.toggleWireframe) {
    flags.toggleWireframe = false
    machine.toggleWireframe()
    target.toggleWireframe()
  }

  if (flags.resize) {
    flags.resize = false
    renderer.setSize(window.innerWidth, window.innerHeight)
    resizeCameras()
  }

  if (flags.pressed.KeyA) {
    machine.rotateLeft()
  }

  if (flags.pressed.KeyS) {
    machine.rotateRight()
  }

  if (flags.pressed.KeyW) {
    machine.moveArmFront()
  }

  if (flags.pressed.KeyQ) {
    machine.moveArmBack()
  }

  let transVector = [
    flags.pressed.ArrowRight ? 1 : flags.pressed.ArrowLeft ? -1 : 0,
    0,
    flags.pressed.ArrowUp ? 1 : flags.pressed.ArrowDown ? -1 : 0
  ]

  const isDiagonal = transVector.filter(a => a !== 0).length > 1
  if (isDiagonal) transVector = transVector.map(a => a * DIAG_UNIT)

  machine.translate(transVector)
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

document.addEventListener('keydown', event => {
  flags.pressed[event.code] = true
})

document.addEventListener('keyup', event => {
  switch (event.key) {
    case '1':
    case '2':
    case '3':
      flags.camera = parseInt(event.key)
      break
    case '4':
      flags.toggleWireframe = true
  }

  flags.pressed[event.code] = false
})

window.addEventListener('resize', () => {
  flags.resize = true
})
