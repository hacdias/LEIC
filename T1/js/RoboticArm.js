/* global THREE, Arm, Joint */
'use strict'

class Hand extends THREE.Mesh {
  constructor ({ radius, height }) {
    const geometry = new THREE.CylinderGeometry(radius, radius, height, 32)
    const material = new THREE.MeshBasicMaterial({ color: 0xffff00 })

    super(geometry, material)

    this.rotation.x = Math.PI / 2

    const fingerLeft = this._buildFinger()
    const fingerRight = this._buildFinger()
    fingerLeft.position.x = 2
    fingerLeft.position.y = 4 - height
    fingerLeft.position.z = 0

    fingerRight.position.x = -2
    fingerRight.position.y = 4 - height
    fingerRight.position.z = 0
    this.add(fingerLeft)
    this.add(fingerRight)
  }

  _buildFinger () {
    const material = new THREE.MeshBasicMaterial({ color: 0xffff00 })
    const geometry = new THREE.BoxGeometry(1, 5, 1)
    return new THREE.Mesh(geometry, material)
  }
}

class RoboticArm extends THREE.Object3D {
  constructor ({ length, depth, forearmLength }) {
    super()

    const jointRadius = depth + 1
    const handRadius = jointRadius
    const handHeight = depth - 1

    const armJoint = new Joint(jointRadius)
    armJoint.position.y = forearmLength / 2
    armJoint.position.z = length

    const forearmJoint = new Joint(jointRadius)
    forearmJoint.position.y = forearmLength / 2

    const forearm = new Arm({ length: forearmLength, depth })

    const arm = new Arm({ length, depth })
    arm.rotation.x = Math.PI / 2
    arm.position.y = forearmLength / 2
    arm.position.z = length / 2

    const hand = new Hand({
      radius: handRadius,
      height: handHeight
    })

    hand.position.y = armJoint.position.y
    hand.position.z = armJoint.position.z + jointRadius + handHeight / 2

    this.add(arm)
    this.add(forearm)
    this.add(armJoint)
    this.add(forearmJoint)
    this.add(hand)
  }
}
