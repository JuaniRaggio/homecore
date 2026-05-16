# HomeCore Design Tokens

Sistema de diseño compartido entre web y mobile.

## Paleta de Colores

### Fondos
- **bg_main**: `#0f0f14`
- **bg_sidebar**: `#1a1a24`
- **bg_topbar**: `#1a1a24`
- **bg_card**: `#1a1a24`
- **bg_card_alt**: `#252532`
- **card_hover**: `#818cf81a`

### Acentos
- **accent**: `#818cf8`
- **accent_hover**: `#a5b4fc`
- **amber**: `#fbbf24`

### Semánticos
- **success**: `#34d399`
- **danger**: `#f87171`
- **warning**: `#fbbf24`
- **border**: `#3a3a4a`

### Fondos semánticos (con alpha)
- **danger_bg**: `rgba(248, 113, 113, 0.1)`
- **success_bg**: `rgba(52, 211, 153, 0.1)`
- **accent_bg**: `rgba(129, 140, 248, 0.1)`

### Texto
- **text_primary**: `#f1f5f9`
- **text_secondary**: `#b0bdd0`
- **text_muted**: `#8494a7`
- **text_on_accent**: `#ffffff`

### Toggle
- **toggle_on**: `#6a78f5`
- **toggle_off**: `#2e2e2e`

## Colores por tipo de dispositivo

| Tipo     | Color     | Uso                    |
|----------|-----------|------------------------|
| lamp     | `#f5a623` | Luces                  |
| door     | `#6c8ebf` | Puertas                |
| alarm    | `#e05252` | Alarmas                |
| water    | `#4fc3f7` | Grifos/Canillas        |
| curtain  | `#81c784` | Cortinas               |
| ac       | `#ba68c8` | Aire acondicionado     |
| speaker  | `#ff8a65` | Parlantes              |
| vacuum   | `#90a4ae` | Aspiradoras            |
| fridge   | `#4dd0e1` | Heladeras              |
| oven     | `#ff7043` | Hornos                 |
| lock     | `#2196f3` | Cerraduras             |

### Paleta de fallback
Para tipos de dispositivos sin color definido:
- `#9c59d1`
- `#2ecc71`
- `#e67e22`
- `#1abc9c`
- `#e91e63`
- `#00bcd4`

## Tipografía (adaptación móvil)

Web usa escala 10px-28px. Mobile debe usar `sp` (scale-independent pixels):

| Nombre   | Web (px) | Mobile (sp) |
|----------|----------|-------------|
| 2xs      | 10       | 10          |
| xs       | 11       | 11          |
| sm       | 12       | 12          |
| base     | 13       | 14          |
| md       | 14       | 15          |
| lg       | 15       | 16          |
| xl       | 16       | 18          |
| 2xl      | 18       | 20          |
| 3xl      | 20       | 22          |
| 4xl      | 22       | 24          |
| 5xl      | 24       | 28          |
| 6xl      | 28       | 32          |

## Espaciado (dp en mobile)

| Nombre   | Valor |
|----------|-------|
| 2xs      | 4dp   |
| xs       | 6dp   |
| sm       | 8dp   |
| md       | 10dp  |
| base     | 12dp  |
| lg       | 14dp  |
| xl       | 16dp  |
| 2xl      | 18dp  |
| 3xl      | 20dp  |
| 4xl      | 24dp  |
| 5xl      | 28dp  |

## Border Radius (dp en mobile)

| Nombre   | Valor |
|----------|-------|
| xs       | 4dp   |
| sm       | 6dp   |
| md       | 8dp   |
| lg       | 10dp  |
| xl       | 12dp  |
| 2xl      | 14dp  |
| full     | 20dp  |

## Iconografía

Web usa **Font Awesome**. Mobile debe usar **Material Icons** o equivalente:

| Tipo     | Web Icon                    | Mobile Icon (Material) |
|----------|-----------------------------|-----------------------|
| light    | fa-regular fa-lightbulb    | lightbulb_outline     |
| door     | fa-regular fa-door-open    | door_front            |
| alarm    | fa-regular fa-clock        | alarm                 |
| water    | fa-solid fa-faucet         | water_drop            |
| curtain  | fa-solid fa-table-list     | blinds                |
| ac       | fa-solid fa-temperature... | ac_unit               |
| speaker  | fa-solid fa-volume-high    | volume_up             |
| vacuum   | fa-solid fa-broom          | clean_hands           |
| fridge   | fa-solid fa-snowflake      | kitchen               |
| oven     | fa-solid fa-fire-burner    | oven_gen              |
| lock     | fa-solid fa-lock           | lock                  |

## Estados por tipo de dispositivo

Ver archivo `homecore-web/src/config/device-types.js` para mapeo completo de:
- Estados on/off
- Acciones (turnOn, open, armAway, etc.)
- Textos localizados en español

## API Backend

- Base URL: configurar en env
- WebSocket: Socket.IO para notificaciones en tiempo real
- Autenticación: JWT en header `Authorization: Bearer <token>`

## Implementación

### Web (CSS Variables)
Definidas en `homecore-web/src/assets/styles/variables.css`

### Mobile (Android)
Definir en `res/values/colors.xml` y `res/values/dimens.xml`
