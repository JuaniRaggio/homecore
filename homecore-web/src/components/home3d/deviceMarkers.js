import * as THREE from 'three'
import TWEEN from '@tweenjs/tween.js'
import {
  createLampOffMaterial,
  createLampOnMaterial,
  createDoorMaterial,
  createLockMaterial,
  createBlindsMaterial,
  createWindowMaterial,
  createAlarmLineMaterial,
  createFaucetMaterial,
  createWaterDropMaterial
} from './materials.js'
import { getPlanBounds } from './floorPlans.js'

// ---- LAMP ----

export function createLampMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'lamp'

  const sphereGeo = new THREE.SphereGeometry(0.15, 16, 12)
  const mat = device.on ? createLampOnMaterial(device.color) : createLampOffMaterial()
  const sphere = new THREE.Mesh(sphereGeo, mat)
  sphere.userData.deviceId = device.id
  group.add(sphere)

  // PointLight when on
  if (device.on) {
    const color = new THREE.Color(device.color)
    const intensity = (device.brightness / 100) * 3
    const light = new THREE.PointLight(color, intensity, 8, 1.5)
    light.castShadow = false
    group.add(light)
  }

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

export function updateLampMarker(group, device) {
  const sphere = group.children.find(c => c.isMesh)
  if (!sphere) return

  // Remove old material and light
  sphere.material.dispose()

  // Remove old PointLight if any
  const oldLight = group.children.find(c => c.isPointLight)
  if (oldLight) {
    group.remove(oldLight)
  }

  if (device.on) {
    sphere.material = createLampOnMaterial(device.color)
    const color = new THREE.Color(device.color)
    const intensity = (device.brightness / 100) * 3
    const light = new THREE.PointLight(color, intensity, 8, 1.5)
    light.castShadow = false
    group.add(light)
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

  // Pivot point for door rotation is at left edge (x=0)
  // If door is open, rotate it
  if (device.on) {
    door.position.set(0.4, 1.0, -0.4)
    door.rotation.y = -80 * (Math.PI / 180)
  }

  // Lock indicator
  const lockGeo = new THREE.SphereGeometry(0.08, 8, 8)
  const lockMat = createLockMaterial(device.locked)
  const lock = new THREE.Mesh(lockGeo, lockMat)
  lock.position.set(0.4, 2.2, 0)
  lock.userData.deviceId = device.id
  lock.userData.isLockIndicator = true
  group.add(lock)

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

export function updateDoorMarker(group, device) {
  const door = group.children.find(c => c.userData && c.userData.isDoorPanel)
  const lock = group.children.find(c => c.userData && c.userData.isLockIndicator)

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
}

// ---- BLINDS ----

export function createBlindsMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'blinds'

  const windowWidth = 1.2
  const windowHeight = 1.4

  // Window background
  const windowGeo = new THREE.PlaneGeometry(windowWidth, windowHeight)
  const windowMat = createWindowMaterial()
  const windowMesh = new THREE.Mesh(windowGeo, windowMat)
  windowMesh.userData.deviceId = device.id
  group.add(windowMesh)

  // Curtain overlay
  const curtainCoverage = (100 - device.position) / 100
  const curtainHeight = windowHeight * curtainCoverage
  if (curtainHeight > 0.01) {
    const curtainGeo = new THREE.PlaneGeometry(windowWidth, curtainHeight)
    const curtainMat = createBlindsMaterial()
    const curtain = new THREE.Mesh(curtainGeo, curtainMat)
    // Position from top of window
    curtain.position.set(0, (windowHeight - curtainHeight) / 2, 0.01)
    curtain.userData.isCurtain = true
    group.add(curtain)
  }

  group.position.set(anchor.x, anchor.y, anchor.z)
  return group
}

export function updateBlindsMarker(group, device) {
  // Remove old curtain
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

  const bounds = getPlanBounds(plan)
  const pad = plan.exterior.perimeterPadding
  const y = 0.1

  const points = [
    new THREE.Vector3(bounds.minX - pad, y, bounds.minZ - pad),
    new THREE.Vector3(bounds.maxX + pad, y, bounds.minZ - pad),
    new THREE.Vector3(bounds.maxX + pad, y, bounds.maxZ + pad),
    new THREE.Vector3(bounds.minX - pad, y, bounds.maxZ + pad),
    new THREE.Vector3(bounds.minX - pad, y, bounds.minZ - pad)
  ]

  const geo = new THREE.BufferGeometry().setFromPoints(points)
  const mat = createAlarmLineMaterial()
  const line = new THREE.Line(geo, mat)
  line.userData.deviceId = device.id
  line.userData.isAlarmLine = true

  if (!device.armed) {
    mat.opacity = 0
  }

  group.add(line)
  return group
}

export function updateAlarmMarker(group, device) {
  const line = group.children.find(c => c.userData && c.userData.isAlarmLine)
  if (!line) return

  if (device.armed) {
    // Start pulsing animation - handled in animation loop
    line.material.opacity = 1.0
    line.userData.armed = true
  } else {
    line.material.opacity = 0
    line.userData.armed = false
  }
}

/**
 * Call this in the animation loop to pulse armed alarm lines.
 */
export function animateAlarmPulse(group, time) {
  const line = group.children.find(c => c.userData && c.userData.isAlarmLine)
  if (!line || !line.userData.armed) return
  // Pulse between 0.3 and 1.0 over 2 seconds
  const t = (Math.sin(time * 0.002 * Math.PI) + 1) / 2
  line.material.opacity = 0.3 + t * 0.7
}

// ---- FAUCET ----

export function createFaucetMarker(device, anchor) {
  const group = new THREE.Group()
  group.userData.deviceId = device.id
  group.userData.deviceType = 'faucet'

  // Faucet body (cylinder)
  const bodyGeo = new THREE.CylinderGeometry(0.06, 0.08, 0.4, 8)
  const bodyMat = createFaucetMaterial()
  const body = new THREE.Mesh(bodyGeo, bodyMat)
  body.position.set(0, 0.2, 0)
  body.userData.deviceId = device.id
  group.add(body)

  // Spout (horizontal cylinder)
  const spoutGeo = new THREE.CylinderGeometry(0.04, 0.04, 0.3, 8)
  const spoutMat = createFaucetMaterial()
  const spout = new THREE.Mesh(spoutGeo, spoutMat)
  spout.rotation.z = Math.PI / 2
  spout.position.set(0.15, 0.35, 0)
  group.add(spout)

  // Water drops (only when on)
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
