# Gobernanza de IA

Este documento describe las reglas, el flujo de trabajo y la trazabilidad del uso de Inteligencia Artificial Generativa
durante el diseño e implementación de la solución "Core E-Commerce con Sistema de Descuentos Acumulativos".

## Tabla de contenido

1. [_IAs_ utilizadas](#ias-utilizadas)
2. [Contrato de uso](#contrato-de-uso)
3. [Metodología de desarrollo con IA](#metodología-de-desarrollo-con-ia)
4. [_Prompts_](#prompts)
5. [Agentes](#agentes)
6. [Bitácora de co-creación](#bitácora-de-co-creación)
7. [Log de decisiones rechazadas por el desarrollador](#log-de-decisiones-rechazadas-por-el-desarrollador)

---

## IAs utilizadas

| IA      | Proveedor | Uso principal en el proyecto                                               |
| ------- | --------- | -------------------------------------------------------------------------- |
| Claude  | Anthropic | Programación y diseño en conjunto con el desarrollador; manejo de agentes. |
| Gemini  | Google    | Consultas puntuales, ejemplos de código y búsqueda de documentación.       |
| ChatGPT | OpenAI    | Redacción y organización de documentos, y consulta de dudas.               |

---

## Contrato de uso

A continuación se definen las reglas de uso de todas las IA para el diseño e implementación de la solución.

### Uso permitido

El uso permitido abarca la creación y uso de _prompts_ y agentes de IA. Este uso debe quedar documentado en las
secciones «_Prompts_» y «Agentes» correspondientes. A continuación se detallan las funciones permitidas de acuerdo con los
flujos de trabajo del desarrollo de software.

#### Diseño

- Responder consultas sobre ideas de diseño, como el uso de patrones o buenas prácticas de desarrollo.
- Realizar sugerencias de corrección o modificación a partir de modelos ya diseñados.
- Crear pruebas de concepto para validar diseños realizados.
- Crear la estructura de carpetas, clases, DTOs, etc., a partir de un diseño de arquitectura ya definido.

#### Implementación

- Generar código repetitivo e implementaciones genéricas (CRUD, repositorios, etc.).
- Crear ejemplos de implementación para un problema de programación delimitado.
- Realizar sugerencias de optimización, corrección y buenas prácticas sobre el código fuente existente.
- Validar la seguridad de la implementación mediante pruebas _SAST_ (Static Application Security Testing), _SCA_ (
  Software Composition Analysis) y _Secret Scanning_.
- Generar código fuente a partir de una especificación (_spec_) previamente diseñada.

#### Pruebas

- Responder consultas sobre buenas prácticas en el diseño e implementación de casos de prueba.
- Realizar sugerencias de corrección o modificación de diseño e implementación de pruebas.
- Generar pruebas unitarias a partir de una colección de casos de prueba previamente diseñados, o sin ellos.
- Verificar la cobertura y la creación de pruebas unitarias que validen funcionalidad real (no pruebas triviales o
  vacías).

#### Transversal

- Apoyar en la revisión, corrección y sugerencia de cambios en los documentos de arquitectura, en los _prompts_, en los
  agentes y en la definición de _specs_ que serán procesados por IA.
- Generar _commits_ y _PRs_ a partir de los cambios realizados en el código fuente.
- Generar documentación del código fuente.

#### Auditoría de IA

- Generar reportes concisos sobre las modificaciones realizadas y el porcentaje de código generado por esta.

### Restricciones

Las restricciones son los límites que tiene la IA al ejecutar las funciones permitidas.

- La IA debe especificar todos los archivos, líneas de código o configuraciones donde realizó modificaciones.
- Cuando existan ambigüedades o falte información necesaria para ejecutar un _prompt_ o agente, la IA debe preguntar
  explícitamente cómo resolver el conflicto antes de proceder.
- La IA debe estar configurada para alertar sobre la exposición de información privada de la organización y no debe
  ejecutar ninguna instrucción hasta que dicha exposición se resuelva.

---

## Metodología de desarrollo con IA

Esta sección define el flujo de trabajo del desarrollador con la IA, en línea con el contrato especificado
anteriormente. Las actividades del flujo son:

1. **Documentación de _prompt_ o agente.** Si el desarrollador crea un _prompt_ o agente nuevo que considera reutilizable
   o valioso, lo agrega a la sección correspondiente de este documento. No es obligatorio documentar cada interacción
   puntual; queda a criterio del desarrollador identificar cuáles aportan valor de reutilización.
2. **Uso de IA.** Se usan agentes o _prompts_ según las necesidades del desarrollador y dentro de las condiciones
   definidas en el contrato de uso. Pueden realizarse varias iteraciones hasta obtener un resultado aceptable.
3. **Scaffold commit.** Se realiza un _commit_ que contiene exclusivamente los cambios generados por la IA, sin
   modificaciones manuales. Sigue el formato _Conventional Commits_, con el marcador `[ai-scaffold]` en el asunto y el
   trailer `AI-Stage: scaffold` (puede apoyarse en un agente para redactar el mensaje).
4. **Refinamiento del desarrollador.** Se revisa el contenido generado por la IA, se corrige lo necesario y se completa
   lo que haga falta.
5. **Commits de refinamiento.** Se realizan uno o varios _commits_ con los cambios manuales del desarrollador, también
   en formato _Conventional Commits_, con el trailer `AI-Stage: refined`. La redacción del mensaje también puede
   apoyarse en IA.

Al ser un flujo iterativo, estas actividades pueden repetirse en distinto orden y cantidad según la tarea. La única
condición que debe mantenerse es que **por cada _scaffold commit_ existan _commits_ de refinamiento posteriores** que
permitan contrastar el aporte de la IA frente al trabajo manual del desarrollador.

Adicionalmente, todo _commit_ con participación de IA (sea scaffold o refinamiento asistido) incluye el trailer
`AI-Assisted: true`.

```
feat(discount-engine): scaffold cascading discount calculator [ai-scaffold]

AI-Assisted: true
AI-Stage: scaffold
```

```
refactor(discount-engine): extract volume discount into Strategy impl

AI-Assisted: true
AI-Stage: refined
```

Opcionalmente puede complementarse con una estrategia de ramificación en Git para dar mayor orden entre los _commits_
relacionados con IA y los de desarrollo manual, aunque no es obligatoria dentro de este flujo.

---

## _Prompts_

_Prompts_ utilizados en el diseño e implementación del proyecto. La categoría corresponde a los flujos de trabajo
definidos en el contrato de uso (diseño, implementación, pruebas, transversal o auditoría).

| Categoría      | _Prompt_                                                                               | Descripción                                                                                                                                                                                                                                                                                                                                                                     |
| -------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Implementación | Refinamiento del módulo `order` (validación de stock, mappers, caso de uso `GetOrder`) | Se pidió modificar los archivos existentes de `order` para: lanzar excepción de negocio cuando falla el decremento de stock (manejada luego por el `GlobalExceptionHandler`), extraer la lógica de mapeo a `infrastructure/mappers`, y crear `GetOrder` como caso de uso nuevo implementado por `PlaceOrderService`, propagando una excepción de negocio si la orden no existe. |
| Diseño         | Generación de estructura de carpetas del backend a partir de la arquitectura           | Se pidió generar, a partir de la sección "Arquitectura interna" del documento de arquitectura, el árbol de carpetas y archivos `.java` vacíos del backend (`catalog`, `checkout/discount`, `checkout/order`, `shared/error`), excluyendo la ruta `src/main/java/...`.                                                                                                           |
| Implementación | Implementación del frontend (Angular 22) — `prompts/generacion-front.md`               | System prompt para construir el frontend completo a partir de `arq.md` y del documento de arquitectura interna de frontend, bajo un enfoque Spec Driven Development. Define la implementación de Atomic Design (atoms, molecules, organisms, templates, pages), Mobile First, BEM y Signals; el estado único del carrito (sin copias locales en catálogo, sidebar o cards); la visualización de `appliedDiscounts` (`CATEGORY`, `VOLUME`, `COUPON`, `TOTAL`) y el mensaje del límite del 35%; el flujo de cupón, la creación de la orden, routing con lazy loading, interceptors funcionales y manejo de errores por capa; e incluye reglas explícitas anti-alucinación para no inventar endpoints, payloads, campos ni reglas de negocio fuera de lo definido en los contratos. |

---

## Agentes

Agentes utilizados en el diseño e implementación del proyecto. La categoría corresponde a los flujos de trabajo
definidos en el contrato de uso (diseño, implementación, pruebas, transversal o auditoría).

| Categoría      | Agente                                                                                                      | Descripción                                                                                                                                                                                                                                                                         |
| -------------- | ----------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Transversal    | **Subagente de creación de commits**<br>_Prompt_ de creación del agente: docs/agents/commit-generator.md    | Genera mensajes de _commit_ en formato Conventional Commits, diferenciando entre scaffolds de IA, refinamiento manual con IA y commits 100% manuales sin IA, según la metodología de desarrollo con IA definida en este documento.                                                  |
| Implementación | **Subagente de auditoría de seguridad**<br>_Prompt_ de creación del agente: docs/agents/security-auditor.md | Ejecuta e interpreta análisis de seguridad _SAST_, _SCA_ y _Secret Scanning_ sobre el proyecto, según el contrato de uso de IA definido en docs/ia.md. Úsalo antes de un _PR_, antes de la sustentación, o cuando el desarrollador pida explícitamente una validación de seguridad. |

---

## Bitácora de co-creación

El objetivo de la bitácora es documentar el uso de la IA junto con el desarrollo real, apoyándose en el control de
versiones (Git) en lugar de un registro manual paralelo. Esto evita agregar esfuerzo adicional al flujo de trabajo
diario del desarrollador.

### Consultas de trazabilidad

- **Commits generados por IA (scaffold):**

  ```bash
  git log --grep="ai-scaffold" --oneline
  ```

- **Commits de refinamiento del desarrollador:**

  ```bash
  git log --grep="AI-Stage: refined" --oneline
  ```

- **Todos los commits con participación de IA (scaffold + refinamiento asistido):**

  ```bash
  git log --grep="AI-Assisted: true" --oneline
  ```

- **Commits 100% manuales, sin IA:**

  ```bash
  git log --grep="AI-Assisted: true" --invert-grep --oneline
  ```

- **Porcentaje real de diferencia entre el código generado por IA y el estado final de un archivo:**

  ```bash
  git diff --stat <scaffold-commit-hash> HEAD -- <ruta-del-archivo>
  ```

### Comparativa de aporte por rama

Script para ejecutar sobre una rama específica, que compara commits y líneas de código entre IA y desarrollador, y
calcula el porcentaje de cada uno respetando los trailers `AI-Assisted: true` / `AI-Stage` definidos en la
metodología. A diferencia de una versión anterior de este script, la rama ya no se deja como marcador de posición
(`<rama>`) dentro del cuerpo del script —eso impedía ejecutarlo directamente, porque al copiarlo y pegarlo tal cual
`git log` recibía literalmente `<rama>` como referencia y fallaba con `unknown revision`—. Ahora la rama se recibe
como parámetro del script (`$1`), con la rama actual como valor por defecto si no se indica ninguna:

```bash
#!/usr/bin/env bash
set -euo pipefail

RAMA="${1:-$(git rev-parse --abbrev-ref HEAD)}"

# --- Commits ---
TOTAL_COMMITS=$(git log "$RAMA" --oneline | wc -l)
COMMITS_IA=$(git log "$RAMA" --grep="AI-Assisted: true" --oneline | wc -l)
COMMITS_MANUALES=$(git log "$RAMA" --grep="AI-Assisted: true" --invert-grep --oneline | wc -l)

# --- Líneas modificadas (agregadas + eliminadas) ---
LINEAS_IA=$(git log "$RAMA" --grep="AI-Assisted: true" --pretty=tformat: --numstat \
  | awk '{ add+=$1; del+=$2 } END { print add+del+0 }')
LINEAS_TOTAL=$(git log "$RAMA" --pretty=tformat: --numstat \
  | awk '{ add+=$1; del+=$2 } END { print add+del+0 }')
LINEAS_MANUALES=$((LINEAS_TOTAL - LINEAS_IA))

echo "Rama: $RAMA"
echo "----------------------------------------"
echo "Commits totales:        $TOTAL_COMMITS"
echo "Commits con IA:         $COMMITS_IA"
echo "Commits manuales:       $COMMITS_MANUALES"
echo "----------------------------------------"
echo "Líneas totales:         $LINEAS_TOTAL"
echo "Líneas con IA:          $LINEAS_IA"
echo "Líneas manuales:        $LINEAS_MANUALES"
echo "----------------------------------------"
awk -v ci="$COMMITS_IA" -v ct="$TOTAL_COMMITS" -v li="$LINEAS_IA" -v lt="$LINEAS_TOTAL" 'BEGIN {
  if (ct > 0) printf "%% commits con IA:  %.2f%%\n", (ci/ct)*100
  if (lt > 0) printf "%% líneas con IA:   %.2f%%\n", (li/lt)*100
}'
```

Para ejecutarlo, guárdalo como archivo (por ejemplo `scripts/comparativa-rama.sh`), dale permisos de ejecución y
llámalo indicando la rama a analizar; sobre Windows requiere una shell compatible con Bash (Git Bash o WSL), ya que
usa `wc`, `awk` y aritmética de Bash:

```bash
chmod +x scripts/comparativa-rama.sh
./scripts/comparativa-rama.sh feature/backend-ia
```

Si se omite el argumento, el script toma la rama actualmente activa (`git rev-parse --abbrev-ref HEAD`).

- `AI-Assisted: true` agrupa tanto commits `[ai-scaffold]` como refinamientos asistidos por IA, tal como se define en
  la metodología; por eso el porcentaje de commits/líneas "con IA" incluye ambos casos.
- El porcentaje por **líneas** es más representativo que el de commits, ya que un commit de refinamiento pequeño no
  pesa igual que un scaffold que generó cientos de líneas.
- Para comparar el aporte de IA únicamente contra el refinamiento manual posterior (sin contar el scaffold inicial),
  se puede acotar el rango con `git log <scaffold-commit-hash>..HEAD` en lugar de la rama completa.

---

## Log de decisiones rechazadas por el desarrollador

Registro de contenido generado por IA que fue rechazado o corregido por el desarrollador, junto con el criterio técnico
de ingeniería que motivó la corrección.

| Módulo / Archivo                                                      | Sugerencia de la IA                                                                                                                                                                                                        | Motivo del rechazo / corrección                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ---------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Diseño de arquitectura — Agregados DDD (`Coupon`)                       | Al consultar sugerencias de diseño sobre los agregados del modelo DDD, la IA propuso modelar `Coupon` como un agregado independiente, con su propia raíz e identidad dentro del dominio de descuentos.                    | Se rechazó porque un agregado se justifica cuando existe una entidad con ciclo de vida e identidad propios que deben persistirse y cuya consistencia transaccional debe protegerse como una unidad. En este dominio, `Coupon` no se persiste como entidad independiente ni requiere una raíz de agregado propia: su validación (existencia y vigencia) es una regla que se resuelve en el contexto de la aplicación de descuentos, no un ciclo de vida autónomo. Elevarlo a agregado habría introducido complejidad estructural sin aportar valor de consistencia transaccional, contradiciendo el criterio de "agregado mínimo necesario" de DDD.                                                                        |
| `checkout/discount` — Fábrica de reglas de descuento (`Discount Factory`) | Al revisar el código fuente del módulo `discount`, la IA implementó la fábrica encargada de construir las reglas de descuento incluyendo, dentro de la propia fábrica, lógica de validación de dichas reglas (por ejemplo, comprobar que una regla fuera aplicable) además de su ensamblado. | Se rechazó el diseño porque una fábrica debe limitarse a instanciar la regla de descuento correcta de acuerdo con la política de descuentos vigente (a partir del tipo de descuento), sin asumir responsabilidades de validación de negocio. Mezclar creación y validación en una misma clase viola el principio de responsabilidad única (SRP), acopla dos preocupaciones que deben evolucionar de forma independiente, y dificulta probar cada una de forma aislada (una prueba de la fábrica termina verificando también reglas de validación que no le corresponden). Se corrigió separando la validación hacia la política/regla de dominio correspondiente, dejando la fábrica exclusivamente como responsable de la creación/ensamblado de las reglas de descuento. |