/**
 * Computes world-space device anchor positions based on device type
 * and the room's dimensions. Used for rooms that don't have curated
 * deviceAnchors (i.e. dynamically added rooms).
 */

/**
 * @param {object} device - device object with at least { type }
 * @param {object} roomDef - room layout with { x, z, width, depth, wallHeight }
 * @returns {{ x: number, y: number, z: number }}
 */
export function computeDeviceAnchor(device, roomDef) {
  const { x, z, width, depth, wallHeight } = roomDef

  switch (device.type) {
    case 'lamp':
      return { x: x, y: wallHeight - 0.2, z: z }

    case 'door':
      return { x: x - width / 2, y: 0, z: z }

    case 'blinds':
      return { x: x, y: wallHeight * 0.5, z: z - depth / 2 }

    case 'faucet':
      return { x: x + width / 4, y: 1.0, z: z - depth / 4 }

    default:
      // Generic: center of room at floor level
      return { x: x, y: 0.5, z: z }
  }
}

/**
 * Compute anchor for an exterior device that doesn't have a preset position.
 * Places it outside the bounding box of all rooms.
 * @param {object} device
 * @param {object} bounds - { minX, maxX, minZ, maxZ }
 * @param {number} padding
 * @returns {{ x: number, y: number, z: number }}
 */
export function computeExteriorAnchor(device, bounds, padding) {
  switch (device.type) {
    case 'door':
      return { x: bounds.maxX + padding, y: 0, z: (bounds.minZ + bounds.maxZ) / 2 }

    case 'faucet':
      return { x: bounds.minX - padding, y: 0.3, z: bounds.minZ }

    default:
      return { x: bounds.maxX + padding, y: 0.5, z: bounds.maxZ }
  }
}
