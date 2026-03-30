import * as THREE from 'three'
import TWEEN from '@tweenjs/tween.js'
import {
  createLampOffMaterial,
  createLampOnMaterial,
  createLampGlowMaterial,
  createDoorMaterial,
  createLockMaterial,
  createLockGlowMaterial,
  createBlindsMaterial,
  createWindowMaterial,
  createAlarmTubeMaterial,
  createFaucetMaterial,
  createWaterDropMaterial
} from './materials.js'
import { getPlanBounds } from './floorPlans.js'

// ---- LAMP ----

export function createLampMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'lamp'

  const sphereGeo = new THREE.SphereGeometry(0.22, 16, 12)
  const mat = device.on ? createLampOnMaterial(device.color) : createLampOffMaterial()
  const sphere = new THREE.Mesh(sphereGeo, mat)
  sphere.userData.deviceId = device.id
  group.add(sphere)

  if (device.on) {
    const color = new THREE.Color(device.color)
    const intensity = (device.brightness / 100) * 7
    const light = new THREE.PointLight(color, intensity, 12, 1.2)
    light.castShadow = false
    group.add(light)

    // Glow halo
    const glowGeo = new THREE.SphereGeometry(0.5, 16, 12)
    const glowMat = createLampGlowMaterial(device.color, device.brightness)
    const glow = new THREE.Mesh(glowGeo, glowMat)
    glow.userData.isGlow = true
    group.add(glow)
  }

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

export function updateLampMarker(group, device) {
  const sphere = group.children.find(c => c.isMesh && !c.userData.isGlow)
  if (!sphere) return

  // Remove old material
  sphere.material.dispose()

  // Remove old PointLight
  const oldLight = group.children.find(c => c.isPointLight)
  if (oldLight) {
    group.remove(oldLight)
  }

  // Remove old glow
  const oldGlow = group.children.find(c => c.isMesh && c.userData.isGlow)
  if (oldGlow) {
    oldGlow.geometry.dispose()
    oldGlow.material.dispose()
    group.remove(oldGlow)
  }

  if (device.on) {
    sphere.material = createLampOnMaterial(device.color)
    const color = new THREE.Color(device.color)
    const intensity = (device.brightness / 100) * 7
    const light = new THREE.PointLight(color, intensity, 12, 1.2)
    light.castShadow = false
    group.add(light)

    // New glow halo
    const glowGeo = new THREE.SphereGeometry(0.5, 16, 12)
    const glowMat = createLampGlowMaterial(device.color, device.brightness)
    const glow = new THREE.Mesh(glowGeo, glowMat)
    glow.userData.isGlow = true
    group.add(glow)
  } else {
    sphere.material = createLampOffMaterial()
  }
}

// ---- DOOR ----

export function createDoorMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'door'

  // Door panel
  const doorGeo = new THREE.BoxGeometry(0.8, 2.0, 0.05)
  const doorMat = createDoorMaterial()
  const door = new THREE.Mesh(doorGeo, doorMat)
  door.position.set(0.4, 1.0, 0)
  door.userData.deviceId = device.id
  door.userData.isDoorPanel = true
  group.add(door)

  if (device.on) {
    door.position.set(0.4, 1.0, -0.4)
    door.rotation.y = -80 * (Math.PI / 180)
  }

  // Lock indicator (larger)
  const lockGeo = new THREE.SphereGeometry(0.12, 8, 8)
  const lockMat = createLockMaterial(device.locked)
  const lock = new THREE.Mesh(lockGeo, lockMat)
  lock.position.set(0.4, 2.2, 0)
  lock.userData.deviceId = device.id
  lock.userData.isLockIndicator = true
  group.add(lock)

  // Lock glow ring
  const lockGlowGeo = new THREE.SphereGeometry(0.22, 12, 12)
  const lockGlowMat = createLockGlowMaterial(device.locked)
  const lockGlow = new THREE.Mesh(lockGlowGeo, lockGlowMat)
  lockGlow.position.set(0.4, 2.2, 0)
  lockGlow.userData.isLockGlow = true
  group.add(lockGlow)

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

export function updateDoorMarker(group, device) {
  const door = group.children.find(c => c.userData && c.userData.isDoorPanel)
  const lock = group.children.find(c => c.userData && c.userData.isLockIndicator)
  const lockGlow = group.children.find(c => c.userData && c.userData.isLockGlow)

  if (door) {
    const targetRotY = device.on ? -80 * (Math.PI / 180) : 0
    const targetZ = device.on ? -0.4 : 0

    new TWEEN.Tween(door.rotation)
      .to({ y: targetRotY }, 400)
      .easing(TWEEN.Easing.Quadratic.InOut)
      .start()

    new TWEEN.Tween(door.position)
      .to({ z: targetZ }, 400)
      .easing(TWEEN.Easing.Quadratic.InOut)
      .start()
  }

  if (lock) {
    lock.material.dispose()
    lock.material = createLockMaterial(device.locked)
  }

  if (lockGlow) {
    lockGlow.material.dispose()
    lockGlow.material = createLockGlowMaterial(device.locked)
  }
}

// ---- BLINDS ----

export function createBlindsMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'blinds'

  const windowWidth = 1.2
  const windowHeight = 1.4

  const windowGeo = new THREE.PlaneGeometry(windowWidth, windowHeight)
  const windowMat = createWindowMaterial()
  const windowMesh = new THREE.Mesh(windowGeo, windowMat)
  windowMesh.userData.deviceId = device.id
  group.add(windowMesh)

  const curtainCoverage = (100 - device.position) / 100
  const curtainHeight = windowHeight * curtainCoverage
  if (curtainHeight > 0.01) {
    const curtainGeo = new THREE.PlaneGeometry(windowWidth, curtainHeight)
    const curtainMat = createBlindsMaterial()
    const curtain = new THREE.Mesh(curtainGeo, curtainMat)
    curtain.position.set(0, (windowHeight - curtainHeight) / 2, 0.01)
    curtain.userData.isCurtain = true
    group.add(curtain)
  }

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

export function updateBlindsMarker(group, device) {
  const oldCurtain = group.children.find(c => c.userData && c.userData.isCurtain)
  if (oldCurtain) {
    oldCurtain.geometry.dispose()
    oldCurtain.material.dispose()
    group.remove(oldCurtain)
  }

  const windowHeight = 1.4
  const windowWidth = 1.2
  const curtainCoverage = (100 - device.position) / 100
  const curtainHeight = windowHeight * curtainCoverage

  if (curtainHeight > 0.01) {
    const curtainGeo = new THREE.PlaneGeometry(windowWidth, curtainHeight)
    const curtainMat = createBlindsMaterial()
    const curtain = new THREE.Mesh(curtainGeo, curtainMat)
    curtain.position.set(0, (windowHeight - curtainHeight) / 2, 0.01)
    curtain.userData.isCurtain = true
    group.add(curtain)
  }
}

// ---- ALARM ----

export function createAlarmMarker(device, plan) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'alarm'

  // Use _allRooms for bounds if available (covers all floors)
  const boundsSource = plan._allRooms && plan._allRooms.length > 0
    ? { rooms: plan._allRooms }
    : plan
  const bounds = getPlanBounds(boundsSource)
  const pad = plan.exterior.perimeterPadding
  const y = 0.1

  // Build a closed path for TubeGeometry
  const corners = [
    new THREE.Vector3(bounds.minX - pad, y, bounds.minZ - pad),
    new THREE.Vector3(bounds.maxX + pad, y, bounds.minZ - pad),
    new THREE.Vector3(bounds.maxX + pad, y, bounds.maxZ + pad),
    new THREE.Vector3(bounds.minX - pad, y, bounds.maxZ + pad),
    new THREE.Vector3(bounds.minX - pad, y, bounds.minZ - pad)
  ]

  const curve = new THREE.CatmullRomCurve3(corners, false, 'catmullrom', 0)
  const tubeGeo = new THREE.TubeGeometry(curve, 64, 0.04, 8, false)
  const tubeMat = createAlarmTubeMaterial()

  if (!device.armed) {
    tubeMat.opacity = 0
    tubeMat.emissiveIntensity = 0
  }

  const tube = new THREE.Mesh(tubeGeo, tubeMat)
  tube.userData.deviceId = device.id
  tube.userData.isAlarmTube = true
  tube.userData.armed = device.armed

  group.add(tube)
  return group
}

export function updateAlarmMarker(group, device) {
  const tube = group.children.find(c => c.userData && c.userData.isAlarmTube)
  if (!tube) return

  if (device.armed) {
    tube.material.opacity = 1.0
    tube.material.emissiveIntensity = 1.0
    tube.userData.armed = true
  } else {
    tube.material.opacity = 0
    tube.material.emissiveIntensity = 0
    tube.userData.armed = false
  }
}

/**
 * Pulse armed alarm tubes in the animation loop.
 * Animates both opacity and emissiveIntensity.
 */
export function animateAlarmPulse(group, time) {
  const tube = group.children.find(c => c.userData && c.userData.isAlarmTube)
  if (!tube || !tube.userData.armed) return

  const t = (Math.sin(time * 0.002 * Math.PI) + 1) / 2
  tube.material.opacity = 0.3 + t * 0.7
  tube.material.emissiveIntensity = 0.5 + t * 1.5
}

// ---- FAUCET ----

export function createFaucetMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'faucet'

  const bodyGeo = new THREE.CylinderGeometry(0.06, 0.08, 0.4, 8)
  const bodyMat = createFaucetMaterial()
  const body = new THREE.Mesh(bodyGeo, bodyMat)
  body.position.set(0, 0.2, 0)
  body.userData.deviceId = device.id
  group.add(body)

  const spoutGeo = new THREE.CylinderGeometry(0.04, 0.04, 0.3, 8)
  const spoutMat = createFaucetMaterial()
  const spout = new THREE.Mesh(spoutGeo, spoutMat)
  spout.rotation.z = Math.PI / 2
  spout.position.set(0.15, 0.35, 0)
  group.add(spout)

  if (device.on) {
    addWaterDrops(group, device.flow)
  }

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

function addWaterDrops(group, flow) {
  const dropCount = Math.max(2, Math.floor(flow / 25))
  const dropMat = createWaterDropMaterial()

  for (let i = 0; i < dropCount; i++) {
    const dropGeo = new THREE.SphereGeometry(0.03, 6, 6)
    const drop = new THREE.Mesh(dropGeo, dropMat.clone())
    drop.position.set(
      0.3,
      0.3 - (i * 0.15),
      (Math.random() - 0.5) * 0.05
    )
    drop.userData.isWaterDrop = true
    drop.userData.baseY = drop.position.y
    drop.userData.speed = 0.5 + (flow / 100) * 1.5
    drop.userData.offset = i * 0.3
    group.add(drop)
  }
}

function removeWaterDrops(group) {
  const drops = group.children.filter(c => c.userData && c.userData.isWaterDrop)
  for (const drop of drops) {
    drop.geometry.dispose()
    drop.material.dispose()
    group.remove(drop)
  }
}

export function updateFaucetMarker(group, device) {
  removeWaterDrops(group)
  if (device.on) {
    addWaterDrops(group, device.flow)
  }
}

/**
 * Animate water drops falling and resetting.
 */
export function animateWaterDrops(group, time) {
  group.children.forEach((child) => {
    if (!child.userData || !child.userData.isWaterDrop) return
    const t = ((time * 0.001 * child.userData.speed) + child.userData.offset) % 1
    child.position.y = child.userData.baseY - t * 0.5
    child.material.opacity = 0.7 * (1 - t)
  })
}

// ---- FACTORY ----

/**
 * Create a device marker based on device type.
 * For alarm, pass the full plan as anchor.
 */
export function createDeviceMarker(device, anchor, plan) {
  switch (device.type) {
    case 'lamp': return createLampMarker(device, anchor)
    case 'door': return createDoorMarker(device, anchor)
    case 'blinds': return createBlindsMarker(device, anchor)
    case 'alarm': return createAlarmMarker(device, plan)
    case 'faucet': return createFaucetMarker(device, anchor)
    default: return null
  }
}

/**
 * Update a device marker based on device type.
 */
export function updateDeviceMarkerState(group, device) {
  switch (device.type) {
    case 'lamp': return updateLampMarker(group, device)
    case 'door': return updateDoorMarker(group, device)
    case 'blinds': return updateBlindsMarker(group, device)
    case 'alarm': return updateAlarmMarker(group, device)
    case 'faucet': return updateFaucetMarker(group, device)
  }
}
