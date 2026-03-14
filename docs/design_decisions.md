# HomeCore - Design Decisions

## 1. Color Palette

**Decision**: Dark theme with indigo (#6366f1) and amber (#f59e0b) accents.

**HCI Rationale**:

- **Visceral level (Norman)**: Dark themes convey sophistication and modernity, aligning with user expectations for a smart home technology product. The high contrast between the dark background and light text (#f1f5f9 on #0f0f14) exceeds the 7:1 ratio recommended by WCAG AA, improving readability.
- **Visual weight (Gestalt)**: Indigo as the primary action color creates a clear focal point on buttons and interactive elements, guiding user attention toward the main actions. Amber is reserved for active states and alerts, establishing a visual hierarchy through color.
- **Number of colors**: Limited to 4-5 functional colors to reduce visual cognitive load and maintain coherence (Miller, 1956 - working memory limit). Each color has a defined semantic role: indigo = action, amber = alert/active, green = success, red = danger.

---

## 2. Typography and Hierarchy

**Decision**: Single typeface family Inter, with weights from 400 (body) to 700 (headings). Size scale defined in CSS variables (0.75rem to 1.875rem).

**HCI Rationale**:

- **Cognitive load**: A single typeface family reduces extraneous load (Sweller, 1988). Differentiation is achieved through weight and size, not through font variety.
- **Gestalt - Similarity**: Elements with the same function share the same typographic style (all labels in font-size-sm/weight-500, all titles in font-size-lg/weight-600), allowing the user to quickly identify the function of each text element.
- **Readability**: Inter is optimized for screens, with wide character aperture and native ligature support. The 1.6 line-height for body text meets accessibility recommendations.

---

## 3. Navigation Structure

**Decision**: Left sidebar with 6 primary navigation items + 1 secondary item (Settings).

**HCI Rationale**:

- **Hick's Law**: Navigation options are limited to 6 primary items (Home, Devices, Rooms, Routines, History, Consumption). This falls within the range of 5-7 elements that working memory can process simultaneously (Miller, 1956), reducing user decision time.
- **Positional consistency (Nielsen #4)**: The sidebar remains fixed across all authenticated views, providing a constant spatial frame of reference. The active position is highlighted with a semi-transparent indigo background.
- **Recoverability**: The collapsible sidebar allows the user to expand the content area when more space is needed, while keeping SVG icons visible as reference (recognition over recall, Nielsen #6). The brand logo remains visible in both expanded and collapsed states, providing a persistent identity anchor.

---

## 4. Interactive Element Sizing

**Decision**: Buttons with a minimum height of 40px (md), 48px (lg). Toggle tap areas of 44px width. Click targets never smaller than 32px.

**HCI Rationale**:

- **Fitts' Law**: The time to reach a target is a function of distance and size. Device controls (toggles, sliders) have generous interaction areas (44px minimum) to reduce the index of difficulty. The "Execute" buttons on routines and device toggles are the most frequently used elements, so their size is proportionally larger.
- **Apple HIG / Material Design**: Both platform guidelines for minimum touchable area sizes are respected (44pt Apple, 48dp Material), anticipating a possible mobile adaptation.

---

## 5. Content Grouping

**Decision**: Cards as the grouping unit, with subtle borders and spacing-based separation.

**HCI Rationale**:

- **Gestalt - Proximity**: Related elements (device name, state, toggle) are grouped within the same card, with an internal padding of 24px (--hc-space-lg). The gap between cards (16px) is smaller than the gap between sections (32px), creating a clear grouping hierarchy.
- **Gestalt - Common region**: The slightly differentiated card background (#1a1a24 on #0f0f14) creates a common region that reinforces the perception of a group without the need for heavy borders.
- **Gestalt - Uniform connectedness**: In the rooms view, devices linked to a room are listed within the same card, creating a visual connection between the room and its devices.

---

## 6. Feedback and System Status

**Decision**: Multi-level feedback system: toasts for confirmations, real-time visual states for devices, numeric badges for notifications, loading indicators on buttons.

**HCI Rationale**:

- **Nielsen #1 - Visibility of system status**: Every user action receives immediate feedback:
  - Device toggle: Instant visual change (color, status text).
  - Routine execution: Confirmation toast ("Routine executed successfully").
  - Form submission: Button with loading spinner.
  - Unread notifications: Numeric badge on the header bell icon.
- **Response time**: CSS transitions (150ms-250ms) are calibrated to feel instantaneous yet visible, within the 100-300ms threshold recommended by Jakob Nielsen for direct manipulation feedback.
- **Mental model**: Device states use coherent visual metaphors (SVG lamp icon with glow = lamp on, lock icon = door locked, pulse animation = alarm armed). All visual metaphors use custom SVG icons rather than platform-dependent emoji, ensuring consistent communication across devices.

---

## 7. Error Prevention

**Decision**: Inline real-time validation, confirmation modals for destructive actions, sensible default values.

**HCI Rationale**:

- **Nielsen #5 - Error prevention**:
  - Login and registration forms validate email format on blur (onBlur), not on submit, providing early feedback.
  - The password field shows requirements ("Minimum 8 characters") before the user makes the mistake.
  - Room deletion requires modal confirmation with impact description ("Linked devices will not be deleted, only unlinked").
- **Default values**: Brightness sliders start at 80%, routine days pre-select Mon-Fri, reducing the number of decisions required.
- **Constraints**: Numeric inputs in the routine wizard are bounded (min 0, max 100), preventing invalid values by construction.

---

## 8. Consistent Terminology

**Decision**: Fixed glossary applied throughout the entire interface: "Hogar" (Home), "Habitacion" (Room), "Dispositivo" (Device), "Rutina" (Routine).

**HCI Rationale**:

- **Nielsen #4 - Consistency and standards**: Synonyms that could cause confusion are avoided. The user always sees "Habitacion" (never "cuarto", "sala", "ambiente"). "Dispositivo" is used uniformly (never "aparato", "equipo", "sensor").
- **Conceptual model**: The terminology reflects the hierarchical structure of the system: Home contains Rooms, Rooms contain Devices, Routines operate on Devices. This lexical coherence facilitates the construction of a correct mental model.
- **User language**: Everyday Rioplatense Spanish is used ("Olvide mi contrasena", "Repeti tu contrasena") to bring the interface closer to the cultural context of the target user.

---

## 9. User Control

**Decision**: Reversible actions, collapsible sidebar, filters in listings, "Back" navigation.

**HCI Rationale**:

- **Nielsen #3 - User control and freedom**:
  - Device toggles are bidirectional: turn on/off with a single click.
  - Routines can be enabled/disabled without deleting them.
  - The "Back" button in detail views allows exiting without commitment.
  - Device and history filters allow the user to control what information is displayed.
- **Conceptual undo**: Unlinking devices from rooms does not delete the device, it only breaks the association. Deleting rooms preserves the devices.
- **Non-modal**: The interface avoids excessive modal states. Modals are only used for destructive action confirmation, not for primary flows.

---

## 10. Recognition vs. Recall

**Decision**: Icons alongside text in the sidebar, status badges on devices, step-by-step wizard for routines.

**HCI Rationale**:

- **Nielsen #6 - Recognition rather than recall**:
  - The sidebar displays icons + text (not just icons) so the user can recognize the section without memorizing meanings.
  - Devices show their current state on the card (no need to navigate to the detail view to know if they are on).
  - The routine wizard presents available options at each step (action type, devices, days), instead of requiring the user to remember formats or commands.
- **Visual breadcrumbs**: The wizard steps show progress with numbers and checkmarks, allowing users to know which step they are on and which ones they have already completed.
- **Visible filters**: The filter selectors in Devices and History display available options in dropdowns, not hidden behind menus.

---

## 11. Iconography System

**Decision**: Custom SVG icon component (HcIcon) with inline stroke-based icons, replacing all emoji and unicode characters throughout the interface.

**HCI Rationale**:

- **Visual consistency (Nielsen #4)**: Emojis and HTML entities render differently across operating systems, browsers, and devices. A unified SVG icon set guarantees that every user sees identical icons regardless of platform, maintaining visual coherence.
- **Gestalt - Similarity**: All icons share the same visual language: 24x24 viewBox, stroke-based (not filled), rounded joins, consistent 2px stroke width. This uniformity allows users to quickly parse icons as a category of element (interactive affordance) distinct from text content.
- **Scalability and theming**: SVG icons inherit `currentColor`, meaning they automatically adapt to the surrounding text color and respond to state changes (active, hover, disabled) without requiring separate icon variants. This supports the dark theme and colored state indicators seamlessly.
- **Semantic clarity**: Each icon is purpose-designed for its context (lamp, door, alarm, faucet, blinds, routines, etc.) rather than relying on approximate emoji matches. This reduces ambiguity and reinforces the user's mental model of each device type.

---

## 12. Brand Identity and Logo

**Decision**: Custom hexagonal logo featuring a house silhouette with WiFi arcs, used consistently across the splash screen and sidebar.

**HCI Rationale**:

- **Visceral level (Norman)**: The hexagonal shape communicates technology and precision, while the house silhouette inside immediately establishes the product domain (smart home). The WiFi arcs reinforce the "connected" aspect. The blue color palette (#0C447C base, #378ADD accents) conveys trust and reliability.
- **Nielsen #4 - Consistency**: The same logo appears in the splash screen, the sidebar header, and the collapsed sidebar state. This repetition builds brand recognition and provides a spatial anchor for the user.
- **Gestalt - Closure**: The hexagonal border creates a contained region that groups the house and WiFi elements into a single perceived unit, making the logo immediately recognizable even at small sizes (32px in collapsed sidebar).

---

## 13. Splash Screen and Onboarding Animation

**Decision**: Full-screen splash with the HomeCore logo centered, followed by a zoom-in "immersion" animation that transitions into the main dashboard. Skippable via scroll, touch swipe, or click.

**HCI Rationale**:

- **Visceral level (Norman)**: The splash establishes an emotional first impression. The logo appears with a scale-up entrance animation (1s ease-out), followed by a staggered title and tagline fade-in. This choreographed sequence creates a sense of quality and intentionality before any interaction occurs.
- **Spatial metaphor**: The zoom-in exit animation (logo scales up 9x while fading out) creates the perception of "entering" the house -- a spatial metaphor that maps the act of opening the app to the act of walking through a front door. This leverages the user's real-world mental model of arriving home.
- **Nielsen #3 - User control and freedom**: The animation is fully skippable. A hint ("Scroll o click para continuar") appears after 1.5 seconds for users who may not intuit the interaction. Multiple input methods are supported (wheel, click, touch swipe) to accommodate different devices and user preferences. No user is forced to wait.
- **Progressive disclosure**: The splash serves as a brief pause that separates the "loading" phase from the "using" phase, giving the application time to initialize stores and render the layout behind the splash. This perceived performance optimization masks any rendering delay.
- **Animation timing**: The 800ms exit duration with an eased curve (smoothstep: t^2 * (3 - 2t)) is calibrated to feel swift but not jarring. The ease function decelerates toward the end, creating a natural "landing" sensation as the dashboard appears.

---

## 14. Prototype Authentication Strategy

**Decision**: Auto-login with a pre-authenticated user for the prototype phase. Login, registration, verification, and recovery views are preserved in the codebase but the auth guard is bypassed.

**HCI Rationale**:

- **Usability testing efficiency**: During prototype evaluation and user testing, requiring login credentials at every session adds friction that is unrelated to the core interaction flows being tested. Auto-login allows evaluators to immediately access the dashboard and test device controls, routines, and navigation without credential management overhead.
- **Preserving completeness**: The auth views (Login, Register, Verify, Recover) remain in the router and are accessible via direct URL navigation. This allows demonstrating the full flow when needed without maintaining two separate codebases.
- **Realistic context**: The pre-authenticated user ("Juani Raggio") has realistic data: two homes, notification preferences, and a PIN setting. This maintains ecological validity during testing -- evaluators interact with a believable user profile rather than a blank state.
