/* global THREE, Machine, Floor  */
'use strict'

const flags = {
  showWireframe: false,
  camera: 1,
  pressed: {}
}

window.DEBUG = false

var camera, scene, renderer, controls

function createScene () {
  scene = new THREE.Scene()
  // scene.add(new THREE.AxesHelper(100))

  const floor = new Floor()
  floor.position.y = -1

  const machine = new Machine()
  machine.position.z = -25

  const target = new Target()
  target.position.z = 25

  scene.add(machine)
  scene.add(floor)
  scene.add(target)
}

function createCamera () {
  camera = new THREE.PerspectiveCamera(
    50,
    window.innerWidth / window.innerHeight,
    1,
    1000
  )

  camera.lookAt(scene.position)
}

function createSpotlight () {
  const spotlights = [
    new THREE.SpotLight(0xffffff, 0.5, 0, Math.PI / 3, 1),
    new THREE.SpotLight(0xffffff, 0.2, 0, Math.PI / 3, 1),
    new THREE.SpotLight(0xffffff, 0.2, 0, Math.PI / 3, 1),
    new THREE.SpotLight(0xffffff, 0.2, 0, Math.PI / 3, 1),
    new THREE.SpotLight(0xffffff, 0.2, 0, Math.PI / 3, 1)
  ]

  spotlights[0].position.set(0, 150, 0)
  spotlights[1].position.set(300, 100, 0)
  spotlights[2].position.set(0, 100, 300)
  spotlights[3].position.set(-300, 100, 0)
  spotlights[4].position.set(0, 100, -300)

  spotlights.forEach(spotLight => {
    scene.add(spotLight)

    if (window.DEBUG) {
      scene.add(new THREE.SpotLightHelper(spotLight))
    }
  })
}

function animate () {
  requestAnimationFrame(animate)
  controls.update()

  scene.traverse(child => {
    if (child instanceof THREE.Mesh) {
      child.material.wireframe = flags.showWireframe
    }
  })

  if (flags.resize) {
    flags.resize = false
    renderer.setSize(window.innerWidth, window.innerHeight)

    if (window.innerHeight > 0 && window.innerWidth > 0) {
      camera.aspect = window.innerWidth / window.innerHeight
      camera.updateProjectionMatrix()
    }
  }

  // if (flags.pressed.ArrowRight) --> do sth

  renderer.render(scene, camera)
}

function init () {
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(window.innerWidth, window.innerHeight)

  document.body.append(renderer.domElement)

  createScene()
  createCamera()
  createSpotlight()

  controls = new THREE.OrbitControls(camera, renderer.domElement)
  camera.position.set(100, 20, 90)
  controls.update()

  animate()
}

document.addEventListener('DOMContentLoaded', () => {
  init()
})

document.addEventListener('keydown', event => {
  flags.pressed[event.key] = true
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

  flags.pressed[event.key] = false
})

window.addEventListener('resize', () => {
  flags.resize = true
})