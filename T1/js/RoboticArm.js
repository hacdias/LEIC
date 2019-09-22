/* global THREE, Arm, Joint */
'use strict'

class Hand extends THREE.Mesh {
  constructor ({ radius, height }) {
    const geometry = new THREE.CylinderGeometry(radius, radius, height, 32)
    const material = new THREE.MeshBasicMaterial({ color: 0xffff00 })

    super(geometry, material)

    this.rotation.x = Math.PI / 2
  }
}

class RoboticArm extends THREE.Object3D {
  constructor ({ length, depth }) {
    super()

    const jointRadius = depth + 1
    const handRadius = jointRadius + 1
    const handHeight = depth

    const armJoint = new Joint(jointRadius)
    armJoint.position.y = length / 2
    armJoint.position.z = length

    const forearmJoint = new Joint(jointRadius)
    forearmJoint.position.y = length / 2

    const forearm = new Arm({ length, depth })

    const arm = new Arm({ length, depth })
    arm.rotation.x = Math.PI / 2
    arm.position.y = length / 2
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
