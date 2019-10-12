/* global THREE, Field, Ball, Cannon  */
'use strict'

const flags = {
  camera: 1,
  newCannon: 1,
  cannon: 0,
  shoot: false,
  pressed: {}
}

const BALL_RADIUS = 5
const FIELD_WIDTH = 100
const WALLS_DEPTH = 2
const CANNON_LENGTH = BALL_RADIUS * 7

const ASPECT_RATIO = 2
const PLANE_H = 130
const DIAG_UNIT = Math.sqrt(0.5)

var scene, renderer, clock, field, balls = []
var cameras = new Array(3)
var cannons = new Array(3)

function getRandomInt (min, max) {
  min = Math.ceil(min)
  max = Math.floor(max)
  return Math.floor(Math.random() * (max - min)) + min
}

function createScene () {
  scene = new THREE.Scene()
  scene.add(new THREE.AxesHelper(10))

  field = new Field({
    width: FIELD_WIDTH,
    height: BALL_RADIUS * 2,
    depth: WALLS_DEPTH
  })

  field.position.x = -FIELD_WIDTH / 2 - 2 * BALL_RADIUS + CANNON_LENGTH
  scene.add(field)

  generateCannons()
  generateBalls()
}

function generateCannons () {
  for (let i = 0; i < cannons.length; i++) {
    const cannon = new Cannon({ radius: BALL_RADIUS, length: CANNON_LENGTH })
    cannon.position.x = 2 * CANNON_LENGTH + BALL_RADIUS
    scene.add(cannon)
    cannons[i] = cannon
  }

  cannons[0].position.z = -FIELD_WIDTH / 2 + BALL_RADIUS
  cannons[2].position.z = -cannons[0].position.z

  cannons[0].rotation.z += Math.PI / 10
  cannons[2].rotation.z -= Math.PI / 10
}

function generateBalls () {
  let initialBalls = getRandomInt(3, 8)

  const MIN_X = -FIELD_WIDTH / 2 + field.position.x + WALLS_DEPTH + BALL_RADIUS
  const MAX_X = FIELD_WIDTH / 2 + field.position.x - BALL_RADIUS

  const MIN_Z = -FIELD_WIDTH / 2 + WALLS_DEPTH + BALL_RADIUS
  const MAX_Z = FIELD_WIDTH / 2 - WALLS_DEPTH - BALL_RADIUS

  while (initialBalls !== 0) {
    const x = getRandomInt(MIN_X, MAX_X)
    const z = getRandomInt(MIN_Z, MAX_Z)

    const ball = new Ball({ radius: BALL_RADIUS })
    ball.position.x = x
    ball.position.z = z

    let collided = false

    for (const theOtherBall of balls) {
      if (theOtherBall.collidesWith(ball)) {
        collided = true
        break
      }
    }

    if (!collided) {
      scene.add(ball)
      balls.push(ball)
      initialBalls--
    }
  }
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
  const { width, height } = getCameraSizes()

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
  cameras[0] = createOrtographicCamera({ position: [0, 30, 0], lookAt: [0, 0, 0] })

  // TODO: perspective cameras
  cameras[1] = createPerspectiveCamera({ position: [150, 60, 0], lookAt: [0, 0, 0] })
  cameras[2] = createOrtographicCamera({ position: [0, 20, 30], lookAt: [0, 20, 0] })

  cameras.forEach(cam => scene.add(cam))
}

function resizeCameras () {
  updateOrtographicCamera(cameras[0])
  updatePerspectiveCamera(cameras[1])
  updateOrtographicCamera(cameras[2])
}

function animate () {
  requestAnimationFrame(animate)

  if (flags.resize) {
    flags.resize = false
    renderer.setSize(window.innerWidth, window.innerHeight)
    resizeCameras()
  }

  if (flags.newCannon !== flags.cannon) {
    for (let i = 0; i < cannons.length; i++) {
      if (i === flags.newCannon) {
        cannons[i].select()
      } else {
        cannons[i].deselect()
      }
    }

    flags.cannon = flags.newCannon
  }

  if (flags.pressed.ArrowRight) {
    cannons[flags.cannon].decreaseAngle()
  } else if (flags.pressed.ArrowLeft) {
    cannons[flags.cannon].increaseAngle()
  }

  if (flags.shoot) {
    flags.shoot = false
    console.warn('Shoot')
  }

  const delta = clock.getDelta()

  for (const ball of balls) {
    ball.animate(delta)

    if (ball.position.x > field.position.x + FIELD_WIDTH / 2 + BALL_RADIUS) {
      ball.fallToInfinity()
    }
  }

  for (let i = 0; i < balls.length; i++) {
    balls[i].collidesWith(field)

    for (let j = i + 1; j < balls.length; j++) {
      if (balls[i].collidesWith(balls[j])) {
        balls[i].resolveCollision(balls[j])
        console.log('collided')
      }
    }
  }

  renderer.render(scene, cameras[flags.camera - 1])
}

function init () {
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(window.innerWidth, window.innerHeight)

  document.body.append(renderer.domElement)

  createScene()
  createCamera()

  clock = new THREE.Clock()

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
  }

  switch (event.code) {
    case 'KeyQ':
      flags.newCannon = 0
      break
    case 'KeyW':
      flags.newCannon = 1
      break
    case 'KeyE':
      flags.newCannon = 2
      break
    case 'Space':
      flags.shoot = true
  }

  flags.pressed[event.code] = false
})

window.addEventListener('resize', () => {
  flags.resize = true
})
