/* global THREE, Field, Ball, Cannon, getRandomInt */
'use strict'

const flags = {
  camera: 1,
  newCannon: 1,
  cannon: 0,
  shoot: false,
  visibleAxis: true,
  movingBallChanged: true,
  pressed: {}
}

const BALL_RADIUS = 5
const FIELD_WIDTH = 100
const WALLS_DEPTH = 2
const CANNON_LENGTH = BALL_RADIUS * 7

const ASPECT_RATIO = 2
const PLANE_H = 130
const DIAG_UNIT = Math.sqrt(0.5)

var scene, renderer, clock, field, balls = [], fallingBalls = []
var cameras = new Array(3)
var cannons = new Array(3)
var movingBall

function createScene () {
  scene = new THREE.Scene()
  scene.add(new THREE.AxesHelper(15))

  field = new Field({
    width: FIELD_WIDTH,
    height: BALL_RADIUS * 4,
    depth: WALLS_DEPTH
  })

  field.applyMatrix(makeTrans(-FIELD_WIDTH / 1.5, BALL_RADIUS * 2, 0))
  scene.add(field)

  generateCannons()
  generateBalls()
}

function generateCannons () {
  for (let i = 0; i < cannons.length; i++) {
    const cannon = new Cannon({ radius: BALL_RADIUS, length: CANNON_LENGTH })

    cannon.applyMatrix(makeTrans(1.5 * CANNON_LENGTH, BALL_RADIUS, 0))
    scene.add(cannon)
    cannons[i] = cannon
  }

  cannons[0].applyMatrix(makeTrans(0, 0, -FIELD_WIDTH / 2 + BALL_RADIUS))
  cannons[2].applyMatrix(makeTrans(0, 0, -cannons[0].position.z))
  cannons[0].increaseAngle(35)
  cannons[2].decreaseAngle(35)
}

function generateBalls () {
  let initialBalls = getRandomInt(3, 5)

  const MIN_X = -FIELD_WIDTH / 2 + field.position.x + WALLS_DEPTH + BALL_RADIUS
  const MAX_X = FIELD_WIDTH / 2 + field.position.x - BALL_RADIUS

  const MIN_Z = -FIELD_WIDTH / 2 + WALLS_DEPTH + BALL_RADIUS
  const MAX_Z = FIELD_WIDTH / 2 - WALLS_DEPTH - BALL_RADIUS

  const positions = []

  while (initialBalls !== 0) {
    const x = getRandomInt(MIN_X, MAX_X)
    const z = getRandomInt(MIN_Z, MAX_Z)

    const position = new THREE.Vector3(x, BALL_RADIUS, z)

    let collided = false

    for (const otherPosition of positions) {
      if (position.distanceToSquared(otherPosition) <= (BALL_RADIUS * 2) ** 2) {
        collided = true
      }
    }

    if (!collided) {
      initialBalls--
      positions.push(position)
    }
  }

  for (const position of positions) {
    const ball = new Ball({ radius: BALL_RADIUS, position })
    scene.add(ball)
    balls.push(ball)
  }

  movingBall = balls[0]
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
  cam.applyMatrix(makeTrans(...position))
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
  cam.applyMatrix(makeTrans(...position))
  cam.lookAt(new THREE.Vector3(...lookAt))
  return cam
}

function updatePerspectiveCamera (cam) {
  cam.aspect = window.innerWidth / window.innerHeight
  cam.updateProjectionMatrix()
}

function createCamera () {
  cameras[0] = createOrtographicCamera({ position: [-BALL_RADIUS * 3, 30, 0], lookAt: [-BALL_RADIUS * 3, 0, 0] })
  cameras[1] = createPerspectiveCamera({ position: [150, 60, 0], lookAt: [, 0, 0] })

  scene.add(cameras[0])
  scene.add(cameras[1])
}

function resizeCameras () {
  updateOrtographicCamera(cameras[0])
  updatePerspectiveCamera(cameras[1])

  if (flags.movingBallChanged) {
    flags.movingBallChanged = false
    cameras[2] = new THREE.PerspectiveCamera(90, window.innerWidth / window.innerHeight, 1, 1000)
    movingBall.add(cameras[2])
    cameras[2].applyMatrix(makeTrans(-10, 10, -10))
    cameras[2].lookAt(movingBall.position)
  }

  updateOrtographicCamera(cameras[2])
}

function animate () {
  requestAnimationFrame(animate)

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

    const ball = cannons[flags.cannon].shoot()
    if (ball) {
      movingBall = ball
      flags.movingBallChanged = true
      balls.push(ball)
      scene.add(ball)
    }
  }

  animateBalls()

  if (flags.resize || flags.movingBallChanged) {
    flags.resize = false
    renderer.setSize(window.innerWidth, window.innerHeight)
    resizeCameras()
  }

  renderer.render(scene, cameras[flags.camera - 1])
}

function animateBalls () {
  const delta = clock.getDelta()

  for (const ball of balls)
    ball.move(delta)

  for (let i = 0; i < balls.length; i++) {
    balls[i].collidesWith(field)

    for (let j = i + 1; j < balls.length; j++)
      if (balls[i].collidesWith(balls[j]))
        balls[i].solveCollision(balls[j])
  }

  for (const ball of balls) {
    ball.check()
    ball.showAxis(flags.visibleAxis)
  }

  // Remove balls that are falling for more than 5 seconds and balls
  // that were shoot but did not touch the ground for more than 5 seconds.
  balls = balls.filter(ball => {
    if ((ball.falling && Date.now() - ball.fallingTime >= 5 * 1000)) {
      scene.remove(ball)
      return false
    }

    return true
  })

  if (!balls.includes(movingBall)) {
    movingBall = balls[0]
    flags.movingBallChanged = true
  }
}

function init () {
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(window.innerWidth, window.innerHeight)

  document.body.append(renderer.domElement)

  createScene()
  createCamera()
  resizeCameras()

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
    case 'KeyR':
      flags.visibleAxis = !flags.visibleAxis
      break
    case 'Space':
      flags.shoot = true
  }

  flags.pressed[event.code] = false
})

window.addEventListener('resize', () => {
  flags.resize = true
})
