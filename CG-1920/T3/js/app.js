/* global THREE, Scene */
'use strict'

let flags = {
  camera: 0,
  toggleLight: false,
  toggleLightMaterial: false,
  spotlights: []
}

var scene, renderer

function animate () {
  requestAnimationFrame(animate)

  if (flags.resize) {
    renderer.setSize(window.innerWidth, window.innerHeight)
    scene.resize()
  }

  if (flags.toggleLight) scene.toggleLight()
  if (flags.toggleLightMaterial) scene.toggleLightMaterial()
  if (flags.toggleGlobalLight) scene.toggleGlobalLight()

  flags.spotlights.forEach(i => scene.toggleSpotlight(i))

  flags = {
    ...flags,
    resize: false,
    toggleLight: false,
    toggleLightMaterial: false,
    toggleGlobalLight: false,
    spotlights: []
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
    case '3':
    case '4':
      flags.spotlights.push(parseInt(event.key) - 1)
      return
    case '5':
    case '6':
      flags.camera = parseInt(event.key) - 5
      return
  }

  switch (event.code) {
    case 'KeyQ':
      flags.toggleGlobalLight = true
      return
    case 'KeyW':
      flags.toggleLight = true
      return
    case 'KeyE':
      flags.toggleLightMaterial = true
  }
})

window.addEventListener('resize', () => {
  flags.resize = true
})
