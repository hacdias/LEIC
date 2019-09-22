/* global THREE, Arm, Joint */
'use strict'

class RoboticArm extends THREE.Object3D {
  constructor ({ length, depth }) {
    super()

    const armJoint = new Joint(depth + 1)
    armJoint.position.y = length / 2
    armJoint.position.z = length

    const forearmJoint = new Joint(depth + 1)
    forearmJoint.position.y = length / 2

    const forearm = new Arm({ length, depth })

    const arm = new Arm({ length, depth })
    arm.rotation.x = Math.PI / 2
    arm.position.y = length / 2
    arm.position.z = length / 2

    this.add(arm)
    this.add(forearm)
    this.add(armJoint)
    this.add(forearmJoint)
  }
}
