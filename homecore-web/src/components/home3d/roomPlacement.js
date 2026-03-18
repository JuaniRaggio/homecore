/**
 * Auto-positioning algorithm for dynamically added rooms.
 * Places new rooms to the right of existing ones, wrapping to a new row
 * when the total width would exceed a maximum threshold.
 */

const MAX_ROW_WIDTH = 16
const GAP = 0.3

/**
 * Compute position (x, z) for a new room given existing layouts.
 * @param {Array} existingLayouts - array of { x, z, width, depth }
 * @param {number} newWidth - width of the new room
 * @param {number} newDepth - depth of the new room
 * @returns {{ x: number, z: number }}
 */
export function computeAutoPosition(existingLayouts, newWidth, newDepth) {
  if (existingLayouts.length === 0) {
    return { x: 0, z: 0 }
  }

  // Find the rightmost edge and the current row's z range
  let maxRight = -Infinity
  let rowMinZ = Infinity
  let rowMaxZ = -Infinity

  for (const layout of existingLayouts) {
    const right = layout.x + layout.width / 2
    const top = layout.z - layout.depth / 2
    const bottom = layout.z + layout.depth / 2
    if (right > maxRight) maxRight = right
    if (top < rowMinZ) rowMinZ = top
    if (bottom > rowMaxZ) rowMaxZ = bottom
  }

  // Try placing to the right
  const candidateX = maxRight + GAP + newWidth / 2

  // Check if total width would exceed limit
  let minLeft = Infinity
  for (const layout of existingLayouts) {
    const left = layout.x - layout.width / 2
    if (left < minLeft) minLeft = left
  }

  const totalWidth = (candidateX + newWidth / 2) - minLeft
  if (totalWidth <= MAX_ROW_WIDTH) {
    // Place on the same row, aligned to the average z
    const avgZ = (rowMinZ + rowMaxZ) / 2
    return { x: candidateX, z: avgZ }
  }

  // Start a new row below
  const newRowZ = rowMaxZ + GAP + newDepth / 2
  return { x: minLeft + newWidth / 2, z: newRowZ }
}
