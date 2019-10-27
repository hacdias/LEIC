/* global THREE, Mesh */
'use strict'

class Pedestal extends THREE.Object3D {
  constructor ({ height, depth, step }) {
    super()

    const bottomL1 = this._buildPart(depth, step, depth)
    const bottomL2 = this._buildPart(depth - step, step, depth - step)
    const bottomL3 = this._buildPart(depth - 2 * step, step, depth - 2 * step)

    bottomL2.position.y = step
    bottomL3.position.y = step * 2

    const pillar = this._buildPart(depth - 3 * step, height - 6 * step, depth - 3 * step)
    pillar.position.y = height / 2 - step / 2

    const topL1 = this._buildPart(depth - 2 * step, step, depth - 2 * step)
    const topL2 = this._buildPart(depth - step, step, depth - step)
    const topL3 = this._buildPart(depth, step, depth)

    topL1.position.y = height - step * 3
    topL2.position.y = height - step * 2
    topL3.position.y = height - step

    this.add(bottomL1)
    this.add(bottomL2)
    this.add(bottomL3)
    this.add(pillar)
    this.add(topL1)
    this.add(topL2)
    this.add(topL3)
  }

  _buildPart (height, width, depth) {
    const geometry = new THREE.BoxGeometry(height, width, depth)
    return new Mesh(geometry, {
      color: 0xBABABA
    })
  }
}
