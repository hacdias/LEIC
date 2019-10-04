/* global THREE, Arm, Joint, Hand */
'use strict'

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
