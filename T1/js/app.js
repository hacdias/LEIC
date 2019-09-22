/* global THREE, Machine  */
'use strict'

const flags = {
  showWireframe: true,
  camera: 1,
  pressed: {}
}

const ASPECT_RATIO = 16 / 9
const PLANE_H = 100

var scene; var renderer; var machine; var cameras = new Array(3)

function createScene () {
  scene = new THREE.Scene()
  scene.add(new THREE.AxesHelper(10))

  machine = new Machine()
  machine.position.z = -25

  const target = new Target()
  target.position.z = 25

  scene.add(machine)
  scene.add(target)
}

function calcCameraSize () {
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
  var { width, height } = calcCameraSize()

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
  const width = window.innerWidth
  const height = window.innerHeight

  cameras[0] = createOrtographicCamera({ position: [0, 10, 0], lookAt: [0, 0, 0] })
  cameras[1] = createOrtographicCamera({ position: [30, 20, 0], lookAt: [0, 20, 0] })
  cameras[2] = createOrtographicCamera({ position: [0, 20, -30], lookAt: [0, 20, 0] })

  cameras.forEach(cam => scene.add(cam))
}

function animate () {
  requestAnimationFrame(animate)

  scene.traverse(child => {
    if (child instanceof THREE.Mesh) {
      child.material.wireframe = flags.showWireframe
    }
  })

  if (flags.resize) {
    flags.resize = false
    renderer.setSize(window.innerWidth, window.innerHeight)

    // TODO: FIX THIS
    if (window.innerHeight > 0 && window.innerWidth > 0) {
      cameras.forEach(camera => {
        camera.aspect = window.innerWidth / window.innerHeight
        camera.updateProjectionMatrix()
      })
    }
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

  const transVector = [0, 0, 0]

  if (flags.pressed.ArrowRight) {
    transVector[0] = 1
  }

  if (flags.pressed.ArrowLeft) {
    transVector[0] = -1
  }

  if (flags.pressed.ArrowUp) {
    transVector[2] = 1
  }

  if (flags.pressed.ArrowDown) {
    transVector[2] = -1
  }

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
      flags.showWireframe = !flags.showWireframe
  }

  flags.pressed[event.code] = false
})

window.addEventListener('resize', () => {
  flags.resize = true
})
