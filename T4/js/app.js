/* global THREE, Scene */
'use strict'

let flags = {
  camera: 0,
  toggleLight: false,
  toggleDirectionalLight: false,
  togglePointLight: false,
  toggleWireframe: false,
  toggleBall: false,
  togglePause: false,
  reset: false
}

var scene, renderer

function animate () {
  requestAnimationFrame(animate)

  if (flags.resize) {
    renderer.setSize(window.innerWidth, window.innerHeight)
    scene.resize()
  }

  scene.animate()
  if (flags.toggleLight) scene.toggleLight()
  if (flags.toggleDirectionalLight) scene.toggleDirectionalLight()
  if (flags.togglePointLight) scene.togglePointLight()
  if (flags.toggleWireframe) scene.toggleWireframe()
  if (flags.toggleBall) scene.toggleBall()
  if (flags.togglePause) scene.togglePause()
  if (flags.reset) scene.reset()

  flags = {
    ...flags,
    toggleLight: false,
    toggleDirectionalLight: false,
    togglePointLight: false,
    toggleWireframe: false,
    toggleBall: false,
    togglePause: false,
    reset: false
  }

  renderer.render(scene, scene.cameras[flags.camera])
}

function init () {
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.shadowMap.enabled = true
  renderer.setSize(window.innerWidth, window.innerHeight)

  document.body.append(renderer.domElement)

  scene = new Scene()
  animate()
}

document.addEventListener('DOMContentLoaded', () => {
  init()
})

document.addEventListener('keyup', event => {
  switch (event.key) {
    case '1':
    case '2':
      flags.camera = parseInt(event.key) - 1
      return
  }

  switch (event.code) {
    case 'KeyL':
      flags.toggleLight = true
      return
    case 'KeyD':
      flags.toggleDirectionalLight = true
      return
    case 'KeyP':
      flags.togglePointLight = true
      return
    case 'KeyW':
      flags.toggleWireframe = true
      return
    case 'KeyB':
      flags.toggleBall = true
      return
    case 'KeyS':
      flags.togglePause = true
      return
    case 'KeyR':
      flags.reset = true
  }
})

window.addEventListener('resize', () => {
  flags.resize = true
})
