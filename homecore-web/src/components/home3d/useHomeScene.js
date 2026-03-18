import { ref, onBeforeUnmount, onMounted } from 'vue'
import * as THREE from 'three'
import { CSS2DRenderer } from 'three/addons/renderers/CSS2DRenderer.js'
import TWEEN from '@tweenjs/tween.js'

export function useHomeScene(containerRef) {
  const scene = new THREE.Scene()
  const clock = new THREE.Clock()

  let renderer = null
  let labelRenderer = null
  let camera = null
  let animationId = null
  let isDisposed = false

  const frustumSize = 14
  let aspect = 1

  function createCamera(width, height) {
    aspect = width / height
    const cam = new THREE.OrthographicCamera(
      -frustumSize * aspect / 2,
      frustumSize * aspect / 2,
      frustumSize / 2,
      -frustumSize / 2,
      0.1,
      100
    )
    cam.position.set(10, 12, 10)
    cam.lookAt(0, 0, 0)
    return cam
  }

  function createLighting() {
    const ambient = new THREE.AmbientLight(0x6366f1, 0.15)
    scene.add(ambient)

    const directional = new THREE.DirectionalLight(0xc8c8d8, 0.4)
    directional.position.set(5, 10, 5)
    directional.castShadow = true
    directional.shadow.mapSize.width = 1024
    directional.shadow.mapSize.height = 1024
    directional.shadow.camera.near = 0.5
    directional.shadow.camera.far = 50
    directional.shadow.camera.left = -10
    directional.shadow.camera.right = 10
    directional.shadow.camera.top = 10
    directional.shadow.camera.bottom = -10
    scene.add(directional)
  }

  function init() {
    const container = containerRef.value
    if (!container) return

    const width = container.clientWidth
    const height = container.clientHeight

    renderer = new THREE.WebGLRenderer({
      antialias: true,
      alpha: true,
      powerPreference: 'default'
    })
    renderer.setSize(width, height)
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    renderer.shadowMap.enabled = true
    renderer.shadowMap.type = THREE.PCFSoftShadowMap
    renderer.outputColorSpace = THREE.SRGBColorSpace
    container.appendChild(renderer.domElement)

    labelRenderer = new CSS2DRenderer()
    labelRenderer.setSize(width, height)
    labelRenderer.domElement.style.position = 'absolute'
    labelRenderer.domElement.style.top = '0'
    labelRenderer.domElement.style.left = '0'
    labelRenderer.domElement.style.pointerEvents = 'none'
    container.appendChild(labelRenderer.domElement)

    camera = createCamera(width, height)
    createLighting()

    animate()
  }

  let lastFrameTime = 0
  const targetInterval = 1000 / 30

  function animate(time) {
    if (isDisposed) return
    animationId = requestAnimationFrame(animate)

    if (document.hidden) return

    if (time - lastFrameTime < targetInterval) return
    lastFrameTime = time

    TWEEN.update(time)

    if (renderer && camera) {
      renderer.render(scene, camera)
      labelRenderer.render(scene, camera)
    }
  }

  function onResize() {
    const container = containerRef.value
    if (!container || !renderer || !camera) return

    const width = container.clientWidth
    const height = container.clientHeight
    aspect = width / height

    camera.left = -frustumSize * aspect / 2
    camera.right = frustumSize * aspect / 2
    camera.top = frustumSize / 2
    camera.bottom = -frustumSize / 2
    camera.updateProjectionMatrix()

    renderer.setSize(width, height)
    labelRenderer.setSize(width, height)
  }

  function dispose() {
    isDisposed = true
    if (animationId !== null) {
      cancelAnimationFrame(animationId)
      animationId = null
    }

    scene.traverse((obj) => {
      if (obj.geometry) obj.geometry.dispose()
      if (obj.material) {
        if (Array.isArray(obj.material)) {
          obj.material.forEach(m => m.dispose())
        } else {
          obj.material.dispose()
        }
      }
    })

    while (scene.children.length > 0) {
      scene.remove(scene.children[0])
    }

    if (renderer) {
      renderer.dispose()
      const container = containerRef.value
      if (container && renderer.domElement.parentNode === container) {
        container.removeChild(renderer.domElement)
      }
      renderer = null
    }

    if (labelRenderer) {
      const container = containerRef.value
      if (container && labelRenderer.domElement.parentNode === container) {
        container.removeChild(labelRenderer.domElement)
      }
      labelRenderer = null
    }
  }

  function getCamera() {
    return camera
  }

  function getRenderer() {
    return renderer
  }

  return {
    scene,
    init,
    dispose,
    onResize,
    getCamera,
    getRenderer
  }
}
