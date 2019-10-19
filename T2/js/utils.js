function getRandomInt (min, max) {
  min = Math.ceil(min)
  max = Math.floor(max)
  return Math.floor(Math.random() * (max - min)) + min
}

function makeRotY (angle) {
  const m = new THREE.Matrix4()
  return m.set(Math.cos(angle), 0, Math.sin(angle), 0,
    0, 1, 0, 0,
    -Math.sin(angle), 0, Math.cos(angle), 0,
    0, 0, 0, 1)
}

function makeRotX (angle) {
  const m = new THREE.Matrix4()
  return m.set(1, 0, 0, 0,
    0, Math.cos(angle), -Math.sin(angle), 0,
    0, Math.sin(angle), Math.cos(angle), 0,
    0, 0, 0, 1)
}

function makeRotZ (angle) {
  const m = new THREE.Matrix4()
  return m.set(Math.cos(angle), -Math.sin(angle), 0, 0,
    Math.sin(angle), Math.cos(angle), 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1)
}

function makeTrans (x, y, z) {
  const m = new THREE.Matrix4()
  return m.set(1, 0, 0, x,
    0, 1, 0, y,
    0, 0, 1, z,
    0, 0, 0, 1)
}

function detectCollisionCubes(object1, object2){
  // object1.updateMatrixWorld();
  // object2.updateMatrixWorld();

  var box1 = new THREE.Box3().setFromObject(object1)
  // box1.applyMatrix4(object1.matrixWorld);

  var box2 = new THREE.Box3().setFromObject(object2)
  // box2.applyMatrix4(object2.matrixWorld);

  return box1.intersectsBox(box2);
}
