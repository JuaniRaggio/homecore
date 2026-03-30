import * as THREE from 'three'

export function createFloorMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0x1a1a24,
    roughness: 0.7,
    metalness: 0.05
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
  return new THREE.MeshStandardMaterial({
    color: color,
    emissive: color,
    emissiveIntensity: 1.5,
    transparent: true,
    opacity: 0.9,
    roughness: 0.3,
    metalness: 0.0
  })
}

/**
 * Semi-transparent glow sphere material for lamp halos.
 * Uses additive blending for a bloom-like effect.
 */
export function createLampGlowMaterial(hexColor, brightness) {
  const color = new THREE.Color(hexColor)
  const opacity = 0.15 + (brightness / 100) * 0.15
  return new THREE.MeshBasicMaterial({
    color: color,
    transparent: true,
    opacity: opacity,
    blending: THREE.AdditiveBlending,
    side: THREE.BackSide,
    depthWrite: false
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

/**
 * Glow ring material for lock indicator.
 */
export function createLockGlowMaterial(locked) {
  return new THREE.MeshBasicMaterial({
    color: locked ? 0xef4444 : 0x22c55e,
    transparent: true,
    opacity: 0.12,
    blending: THREE.AdditiveBlending,
    side: THREE.BackSide,
    depthWrite: false
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

/**
 * Alarm tube material with emissive glow for pulsing effect.
 */
export function createAlarmTubeMaterial() {
  return new THREE.MeshStandardMaterial({
    color: 0xef4444,
    emissive: new THREE.Color(0xef4444),
    emissiveIntensity: 1.0,
    transparent: true,
    opacity: 1.0,
    roughness: 0.4,
    metalness: 0.1
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
