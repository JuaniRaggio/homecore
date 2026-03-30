import * as THREE from 'three'
import { CSS2DObject } from 'three/addons/renderers/CSS2DRenderer.js'
import {
  createFloorMaterial,
  createWallMaterial,
  createEdgeMaterial,
  createHoverHighlightMaterial
} from './materials.js'

/**
 * Build a Group of meshes representing a room:
 *  - floor plane
 *  - walls (configurable per side)
 *  - edge lines
 *  - room label (CSS2D)
 *  - hover highlight overlay
 */
export function buildRoom(roomDef) {
  const group = new THREE.Group()
  group.userData.roomId = roomDef.roomId

  const { width, depth, wallHeight, walls } = roomDef

  // Floor
  const floorGeo = new THREE.PlaneGeometry(width, depth)
  const floorMat = createFloorMaterial()
  const floor = new THREE.Mesh(floorGeo, floorMat)
  floor.rotation.x = -Math.PI / 2
  floor.position.set(0, 0, 0)
  floor.receiveShadow = true
  floor.userData.roomId = roomDef.roomId
  floor.userData.isFloor = true
  group.add(floor)

  // Hover highlight (invisible by default)
  const highlightGeo = new THREE.PlaneGeometry(width, depth)
  const highlightMat = createHoverHighlightMaterial()
  highlightMat.opacity = 0
  const highlight = new THREE.Mesh(highlightGeo, highlightMat)
  highlight.rotation.x = -Math.PI / 2
  highlight.position.set(0, 0.01, 0)
  highlight.userData.isHighlight = true
  group.add(highlight)

  // Walls
  const wallMat = createWallMaterial()
  const edgeMat = createEdgeMaterial()

  const wallConfigs = [
    { side: 'north', pos: [0, wallHeight / 2, -depth / 2], rot: [0, 0, 0], size: [width, wallHeight] },
    { side: 'south', pos: [0, wallHeight / 2, depth / 2], rot: [0, 0, 0], size: [width, wallHeight] },
    { side: 'west', pos: [-width / 2, wallHeight / 2, 0], rot: [0, Math.PI / 2, 0], size: [depth, wallHeight] },
    { side: 'east', pos: [width / 2, wallHeight / 2, 0], rot: [0, Math.PI / 2, 0], size: [depth, wallHeight] }
  ]

  for (const wc of wallConfigs) {
    if (!walls[wc.side]) continue

    const wallGeo = new THREE.PlaneGeometry(wc.size[0], wc.size[1])
    const wall = new THREE.Mesh(wallGeo, wallMat.clone())
    wall.position.set(wc.pos[0], wc.pos[1], wc.pos[2])
    wall.rotation.set(wc.rot[0], wc.rot[1], wc.rot[2])
    wall.castShadow = true
    wall.receiveShadow = true
    group.add(wall)

    // Edge lines on top of wall
    const edgePoints = []
    const hw = wc.size[0] / 2
    const hh = wc.size[1] / 2
    edgePoints.push(new THREE.Vector3(-hw, -hh, 0))
    edgePoints.push(new THREE.Vector3(hw, -hh, 0))
    edgePoints.push(new THREE.Vector3(hw, hh, 0))
    edgePoints.push(new THREE.Vector3(-hw, hh, 0))
    edgePoints.push(new THREE.Vector3(-hw, -hh, 0))
    const edgeGeo = new THREE.BufferGeometry().setFromPoints(edgePoints)
    const edge = new THREE.Line(edgeGeo, edgeMat)
    edge.position.copy(wall.position)
    edge.rotation.copy(wall.rotation)
    group.add(edge)
  }

  // Floor border lines
  const floorEdgePoints = [
    new THREE.Vector3(-width / 2, 0.01, -depth / 2),
    new THREE.Vector3(width / 2, 0.01, -depth / 2),
    new THREE.Vector3(width / 2, 0.01, depth / 2),
    new THREE.Vector3(-width / 2, 0.01, depth / 2),
    new THREE.Vector3(-width / 2, 0.01, -depth / 2)
  ]
  const floorEdgeGeo = new THREE.BufferGeometry().setFromPoints(floorEdgePoints)
  const floorEdge = new THREE.Line(floorEdgeGeo, edgeMat)
  group.add(floorEdge)

  // Room label (CSS2D)
  const labelDiv = document.createElement('div')
  labelDiv.textContent = roomDef.label
  labelDiv.style.color = '#94a3b8'
  labelDiv.style.fontSize = '11px'
  labelDiv.style.fontFamily = 'Inter, system-ui, sans-serif'
  labelDiv.style.fontWeight = '500'
  labelDiv.style.letterSpacing = '0.05em'
  labelDiv.style.textTransform = 'uppercase'
  labelDiv.style.pointerEvents = 'none'
  labelDiv.style.userSelect = 'none'
  const label = new CSS2DObject(labelDiv)
  label.position.set(0, 0.05, 0)
  group.add(label)

  // Position the group
  group.position.set(roomDef.x, 0, roomDef.z)

  return group
}

/**
 * Build all rooms from a floor plan.
 * Returns an array of room Groups.
 */
export function buildAllRooms(plan) {
  return plan.rooms.map(roomDef => buildRoom(roomDef))
}

/**
 * Set hover highlight on a room group.
 */
export function setRoomHighlight(roomGroup, active) {
  roomGroup.traverse((child) => {
    if (child.userData && child.userData.isHighlight) {
      child.material.opacity = active ? 0.08 : 0
    }
  })
}
