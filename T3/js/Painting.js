
const SQUARE_SIZE = 5

class Painting extends THREE.Object3D {
  constructor ({ width, height }) {
    super()

    const widthSquares = Math.floor(width / SQUARE_SIZE) - 1
    const heightSquares = Math.floor(height / SQUARE_SIZE) - 1
    const space = (width - widthSquares * SQUARE_SIZE) / (widthSquares - 1)

    this.width = width
    this.height = height

    const geometry = new THREE.BoxGeometry(1, height, width)
    const material = new THREE.MeshBasicMaterial({
      color: 0x999999
    })

    const bg = new THREE.Mesh(geometry, material)
    this.add(bg)

    for (let i = -width / 2 + SQUARE_SIZE / 2; i <= width / 2 - SQUARE_SIZE / 2; i += SQUARE_SIZE + space) {
      for (let j = -height / 2 + SQUARE_SIZE / 2; j <= height / 2 - SQUARE_SIZE / 2; j += SQUARE_SIZE + space) {
        const square = this._makeBlackSquare(SQUARE_SIZE)
        square.position.z = i
        square.position.y = j
        this.add(square)
      }
    }

    for (let i = 1; i < widthSquares; i++) {
      for (let j = 1; j < heightSquares; j++) {
        const circle = this._makeWhiteCircle(Math.sqrt(2 * (space / 2) ** 2))
    
        circle.position.z = (-width / 2 - space / 2) + i * (SQUARE_SIZE + space)
        circle.position.y = (-height / 2 - space / 2) + j * (SQUARE_SIZE + space)
    
        this.add(circle)
      }
    }
  }

  _makeBlackSquare (dim) {
    const geometry = new THREE.BoxGeometry(1, dim, dim)
    const material = new THREE.MeshBasicMaterial({
      color: 0x000000
    })

    return new THREE.Mesh(geometry, material)
  }
 
  _makeWhiteCircle (rad) {
    const geometry = new THREE.CylinderGeometry( rad, rad, 1, 32 );
    const material = new THREE.MeshBasicMaterial( { color: 0xffffff } );
    const mesh = new THREE.Mesh( geometry, material )
    mesh.rotation.z = Math.PI / 2
    return mesh
  }
}