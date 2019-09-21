/* global THREE, Arm, Joint */
'use strict'

class RoboticArm extends THREE.Object3D {
  constructor ({
    armLength = 28,
    armDepth = 2.25
  } = {}) {
    super()

    const armJoint = new Joint()
    armJoint.position.y = armLength / 2
    armJoint.position.z = armLength

    const forearmJoint = new Joint()
    forearmJoint.position.y = armLength / 2

    const forearm = new Arm({ length: armLength, depth: armDepth })

    const arm = new Arm({ length: armLength, depth: armDepth })
    arm.rotation.x = Math.PI / 2
    arm.position.y = armLength / 2
    arm.position.z = armLength / 2

    this.add(arm)
    this.add(forearm)
    this.add(armJoint)
    this.add(forearmJoint)
  }
}
