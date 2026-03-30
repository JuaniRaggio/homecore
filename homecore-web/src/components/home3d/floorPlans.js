/**
 * Floor plan definitions for casa and departamento models.
 * Each room maps to roomIds from the rooms store.
 * Device anchors map device IDs to 3D coordinates within each room.
 * Coordinates are in world space (Three.js units).
 * Wall config: true = wall present, false = open (doorway/passage).
 */

export const floorPlans = {
  casa: {
    rooms: [
      {
        roomId: 'room-1',
        label: 'Living',
        x: -3,
        z: -1.5,
        width: 5,
        depth: 4,
        wallHeight: 2.8,
        walls: { north: true, south: false, east: false, west: true },
        deviceAnchors: {
          'lamp-1': { x: -1.5, y: 2.6, z: -0.5 },
          'door-1': { x: -3, y: 0, z: 0.5 },
          'blinds-1': { x: -0.5, y: 1.4, z: -3.5 }
        }
      },
      {
        roomId: 'room-3',
        label: 'Cocina',
        x: 3,
        z: -1.5,
        width: 4,
        depth: 4,
        wallHeight: 2.8,
        walls: { north: true, south: true, east: true, west: false },
        deviceAnchors: {
          'lamp-3': { x: 4, y: 2.6, z: -0.5 },
          'faucet-2': { x: 4.5, y: 1.0, z: -2.5 }
        }
      },
      {
        roomId: 'room-2',
        label: 'Dormitorio',
        x: -3,
        z: 4,
        width: 5,
        depth: 3.5,
        wallHeight: 2.8,
        walls: { north: false, south: true, east: false, west: true },
        deviceAnchors: {
          'lamp-2': { x: -1.5, y: 2.6, z: 5 },
          'blinds-2': { x: -0.5, y: 1.4, z: 5.75 }
        }
      },
      {
        roomId: 'room-4',
        label: 'Bano',
        x: 3,
        z: 4,
        width: 3,
        depth: 3.5,
        wallHeight: 2.8,
        walls: { north: false, south: true, east: true, west: false }
      },
      {
        roomId: 'room-5',
        label: 'Oficina',
        x: 6.5,
        z: 4,
        width: 2.5,
        depth: 3.5,
        wallHeight: 2.8,
        walls: { north: false, south: true, east: true, west: true }
      }
    ],
    exterior: {
      perimeterPadding: 1.5,
      deviceAnchors: {
        'alarm-1': { type: 'perimeter' },
        'faucet-1': { x: -4.5, y: 0.3, z: -3 },
        'door-2': { x: 7.5, y: 0, z: -0.5 }
      }
    }
  },

  departamento: {
    rooms: [
      {
        roomId: 'room-1',
        label: 'Living',
        x: -2,
        z: -1,
        width: 5.5,
        depth: 3,
        wallHeight: 2.6,
        walls: { north: true, south: false, east: false, west: true },
        deviceAnchors: {
          'lamp-1': { x: 0, y: 2.4, z: -0.2 },
          'door-1': { x: -2, y: 0, z: 0.2 },
          'blinds-1': { x: 0.5, y: 1.3, z: -2.5 }
        }
      },
      {
        roomId: 'room-3',
        label: 'Cocina',
        x: 4.5,
        z: -1,
        width: 3,
        depth: 3,
        wallHeight: 2.6,
        walls: { north: true, south: true, east: true, west: false },
        deviceAnchors: {
          'lamp-3': { x: 5.5, y: 2.4, z: -0.2 },
          'faucet-2': { x: 6, y: 1.0, z: -1.8 }
        }
      },
      {
        roomId: 'room-2',
        label: 'Dormitorio',
        x: -2,
        z: 3,
        width: 4,
        depth: 3,
        wallHeight: 2.6,
        walls: { north: false, south: true, east: false, west: true },
        deviceAnchors: {
          'lamp-2': { x: -0.5, y: 2.4, z: 4 },
          'blinds-2': { x: 0, y: 1.3, z: 6 }
        }
      },
      {
        roomId: 'room-4',
        label: 'Bano',
        x: 3,
        z: 3,
        width: 2.5,
        depth: 3,
        wallHeight: 2.6,
        walls: { north: false, south: true, east: false, west: false }
      },
      {
        roomId: 'room-5',
        label: 'Oficina',
        x: 6,
        z: 3,
        width: 2.5,
        depth: 3,
        wallHeight: 2.6,
        walls: { north: false, south: true, east: true, west: false }
      }
    ],
    exterior: {
      perimeterPadding: 1.0,
      deviceAnchors: {
        'alarm-1': { type: 'perimeter' },
        'faucet-1': null,
        'door-2': null
      }
    }
  }
}

/**
 * Compute the bounding box of all rooms in a floor plan.
 * Returns { minX, maxX, minZ, maxZ }
 */
export function getPlanBounds(plan) {
  let minX = Infinity, maxX = -Infinity
  let minZ = Infinity, maxZ = -Infinity
  for (const room of plan.rooms) {
    const left = room.x - room.width / 2
    const right = room.x + room.width / 2
    const front = room.z - room.depth / 2
    const back = room.z + room.depth / 2
    if (left < minX) minX = left
    if (right > maxX) maxX = right
    if (front < minZ) minZ = front
    if (back > maxZ) maxZ = back
  }
  return { minX, maxX, minZ, maxZ }
}
