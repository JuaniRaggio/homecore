// ============================================================
// HomeCore -- Tercera Entrega -- Plantilla y helpers compartidos
// Estilo, portada, índice y utilidades de figura/encabezado.
// ============================================================

// --- Encabezado de requisito (RF/RNF): es una etiqueta, no una sección,
//     por eso no se numera (pero sí aparece en el índice). -------------------
#let req(body) = heading(level: 3, numbering: none)[#body]

// --- Encabezado sin numerar (Introducción, Anexo y sus subsecciones). ------
#let seccion-sin-numero(body, level: 1) = heading(level: level, numbering: none)[#body]

// --- Figura de una sola imagen (captura de teléfono: ancho acotado). -------
#let fig(path, cap, w: 45%) = figure(image(path, width: w), caption: cap)

// --- Figura de varias imágenes en grilla. Por defecto una columna por
//     imagen; se puede forzar la cantidad de columnas con `cols`. -----------
#let figrow(paths, cap, cols: auto) = figure(
  grid(
    columns: if cols == auto { paths.len() } else { cols },
    gutter: 12pt,
    ..paths.map(p => image(p, width: 100%)),
  ),
  caption: cap,
)

// --- Portada -----------------------------------------------------------------
#let portada() = page(numbering: none, header: none, footer: none)[
  #v(2fr)
  #align(center)[
    #text(size: 32pt, weight: "bold")[HomeCore - HCI]
    #v(0.5em)
    #text(size: 18pt)[Gestión de Casas Inteligentes -- Aplicación Móvil]
    #v(0.3em)
    #text(size: 14pt, fill: gray)[Trabajo Práctico N°3 -- Grupo 15]
    #v(1.5em)
    #image("images/image52.png", width: 35%)
    #v(1.5em)
    #line(length: 60%, stroke: 1pt + gray)
    #v(1.5em)
    #text(size: 12pt)[*Integrantes*]
    #v(0.5em)
    #table(
      columns: (auto, auto),
      align: (left, center),
      stroke: 0.5pt,
      inset: 10pt,
      fill: (x, y) => if y == 0 { gray.lighten(80%) },
      table.header([*Nombre*], [*Legajo*]),
      [Matias Bernasconi], [64188],
      [Maria del Pilar Resek], [65528],
      [Juan Ignacio Garcia Vautrin Raggio], [63319],
      [Victoria Park], [64498],
    )
    #v(1.5em)
    #text(size: 11pt, fill: gray)[
      Instituto Tecnológico de Buenos Aires \
      Fecha de entrega: 15/06/2026
    ]
  ]
  #v(2fr)
]

// --- Índice ------------------------------------------------------------------
#let indice() = page(numbering: none, header: none, footer: none)[
  #outline(title: [Índice], indent: 1.5em, depth: 3)
]

// --- Estilo global (se aplica con `#show: conf`). ---------------------------
#let conf(doc) = {
  set document(
    title: "HomeCore - TP 3 Implementación Mobile",
    author: "Grupo 15",
  )

  set page(
    paper: "a4",
    margin: (top: 2.5cm, bottom: 2.5cm, left: 2cm, right: 2cm),
    numbering: "1",
    number-align: bottom + right,
    header: context {
      if counter(page).get().first() > 1 [
        #set text(size: 9pt, fill: gray)
        #grid(
          columns: (1fr, 1fr, 1fr),
          align: (left, center, right),
          [HomeCore], [Tercera Entrega -- Grupo 15], [#datetime.today().display("[day]/[month]/[year]")],
        )
        #line(length: 100%, stroke: 0.5pt + gray)
      ]
    },
    footer: context [
      #set text(size: 9pt, fill: gray)
      #line(length: 100%, stroke: 0.5pt + gray)
      #v(0.2em)
      #align(center)[
        Página #counter(page).display() de #counter(page).final().first()
      ]
    ],
  )

  set text(font: "New Computer Modern", size: 11pt, lang: "es", hyphenate: true)
  set par(justify: true, leading: 0.65em, first-line-indent: 0em, spacing: 1.2em)

  set heading(numbering: "1.1")
  show heading.where(level: 1): set text(size: 16pt, weight: "bold")
  show heading.where(level: 2): set text(size: 14pt, weight: "bold")
  show heading.where(level: 3): set text(size: 12pt, weight: "bold")
  show heading.where(level: 4): set text(size: 11pt, weight: "bold")
  show heading: it => {
    v(0.5em)
    it
    v(0.3em)
  }

  set list(indent: 1em, marker: ("--", "-", "."))
  set enum(indent: 1em, numbering: "1.a.")
  show link: underline

  // Las figuras conservan el epígrafe textual del informe ("Imagen N: ..."),
  // sin el prefijo "Figura N:", para respetar las referencias internas.
  set figure(numbering: none)
  show figure.caption: it => text(size: 9pt, fill: gray)[#it.body]

  doc
}
