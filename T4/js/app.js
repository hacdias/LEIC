/* global THREE, Scene */
'use strict'

let flags = {
  toggleLight: false,
  toggleDirectionalLight: false,
  togglePointLight: false,
  toggleWireframe: false,
  toggleBall: false,
  togglePause: false,
  reset: false
}

var scene, pauseScene, renderer, activeScene

function animate () {
  requestAnimationFrame(animate)

  if (flags.resize) {
    renderer.setSize(window.innerWidth, window.innerHeight)
    scene.resize()
    pauseScene.resize()
  }

  scene.animate()
  if (flags.toggleLight) scene.toggleLight()
  if (flags.toggleDirectionalLight) scene.toggleDirectionalLight()
  if (flags.togglePointLight) scene.togglePointLight()
  if (flags.toggleWireframe) scene.toggleWireframe()
  if (flags.toggleBall) scene.toggleBall()

  if (flags.togglePause) {
    if (activeScene === scene) {
      activeScene = pauseScene
    } else {
      activeScene = scene
    }
    scene.togglePause()
  }

  if (flags.reset && activeScene === pauseScene) {
    scene = new Scene()
    activeScene = scene
  }

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

  renderer.render(activeScene, activeScene.camera)
}

function init () {
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.shadowMap.enabled = true
  renderer.setSize(window.innerWidth, window.innerHeight)

  document.body.append(renderer.domElement)

  scene = new Scene()
  pauseScene = new PauseScene()

  activeScene = scene
  animate()
}

document.addEventListener('DOMContentLoaded', () => {
  init()
})

document.addEventListener('keyup', event => {
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
