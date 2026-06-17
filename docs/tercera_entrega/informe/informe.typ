// ============================================================
// HomeCore -- TP Gestión de Casas Inteligentes (Tercera Entrega)
// Aplicación móvil (Android / Jetpack Compose)
//
// Documento orquestador: aplica el estilo de `template.typ` e incluye
// cada sección desde su propio archivo (ver 0X_*.typ).
// ============================================================

#import "template.typ": conf, portada, indice

#show: conf

#portada()
#indice()
#counter(page).update(1)

#include "01_introduccion.typ"
#include "02_requisitos.typ"
#include "03_modificaciones.typ"
#include "04_usabilidad.typ"
#include "05_conclusion.typ"
#include "06_anexo.typ"
