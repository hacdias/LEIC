const LIMIT_VEL = Math.PI

class Ball extends THREE.Object3D {
  constructor (radius = 5, distanceFromCenter = 30) {
    super()
    this.radius = radius
    this.distanceFromCenter = distanceFromCenter

    const geo = new THREE.SphereGeometry(radius, 32, 32)
    geo.computeFaceNormals()
    geo.computeVertexNormals()

    const loader = new THREE.TextureLoader()
    const texture = loader.load('./assets/mona.jpg')
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping

    this.acceleration = 1
    this.angle = 0
    this.velocity = 0

    const mesh = new Mesh(
      geo,
      {
        wireframe: false,
        map: texture
      },
      {
        shininess: 5,
        specular: 0xffffff,
        color: 0xffffff,
        wireframe: false,
        map: texture
      }
    )

    mesh.position.y = radius
    mesh.castShadow = true

    this.mesh = mesh
    this.add(mesh)
  }

  animate (delta) {
    this.velocity += this.acceleration * delta

    if (this.velocity > LIMIT_VEL) {
      this.velocity = LIMIT_VEL
    } else if (this.velocity < 0) {
      this.velocity = 0
    }

    const angleDiff = this.velocity * delta
    this.angle += angleDiff;
    this.angle %= 2 * Math.PI

    this.rotateY(-1 * angleDiff);
    this.mesh.rotateX(angleDiff * 5 / this.radius)

    this.position.x = this.distanceFromCenter * Math.cos(this.angle)
    this.position.z = this.distanceFromCenter * Math.sin(this.angle)
  }

  toggleAcceleration () {
    this.acceleration *= -1
  }
}