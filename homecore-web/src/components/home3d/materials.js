import * as THREE from 'three'

export function createFloorMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x1a1a24,
    roughness: 0.9,
    metalness: 0.0
  })
}

export function createWallMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x252532,
    transparent: true,
    opacity: 0.6,
    roughness: 0.8,
    metalness: 0.0,
    side: THREE.DoubleSide
  })
}

export function createEdgeMaterial() {
  return new THREE.LineBasicMaterial({
    color: 0x2e2e3e
  })
}

export function createHoverHighlightMaterial() {
  return new THREE.MeshBasicMaterial({
    color: 0x6366f1,
    transparent: true,
    opacity: 0.08
  })
}

export function createLampOffMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x64748b,
    roughness: 0.6,
    metalness: 0.2
  })
}

export function createLampOnMaterial(hexColor) {
  const color = new THREE.Color(hexColor)
  return new THREE.MeshBasicMaterial({
    color: color,
    transparent: true,
    opacity: 0.9
  })
}

export function createDoorMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x8b7355,
    roughness: 0.7,
    metalness: 0.1
  })
}

export function createLockMaterial(locked) {
  return new THREE.MeshBasicMaterial({
    color: locked ? 0xef4444 : 0x22c55e
  })
}

export function createBlindsMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x94a3b8,
    roughness: 0.5,
    metalness: 0.1,
    side: THREE.DoubleSide
  })
}

export function createWindowMaterial() {
  return new THREE.MeshBasicMaterial({
    color: 0x334155,
    transparent: true,
    opacity: 0.3,
    side: THREE.DoubleSide
  })
}

export function createAlarmLineMaterial() {
  return new THREE.LineBasicMaterial({
    color: 0xef4444,
    transparent: true,
    opacity: 1.0
  })
}

export function createFaucetMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x94a3b8,
    roughness: 0.3,
    metalness: 0.6
  })
}

export function createWaterDropMaterial() {
  return new THREE.MeshBasicMaterial({
    color: 0x38bdf8,
    transparent: true,
    opacity: 0.7
  })
}
