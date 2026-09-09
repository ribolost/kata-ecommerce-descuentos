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

| IA      | Proveedor | Uso principal en el proyecto |
| ------- | --------- | ---------------------------- |
| Claude  | Anthropic |                              |
| Gemini  | Google    |                              |
| ChatGPT | OpenAI    |                              |

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

| Categoría | _Prompt_ | Descripción |
| --------- | -------- | ----------- |
|           |          |             |
|           |          |             |

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

### Resumen final

| Métrica                                                                | Valor |
| ---------------------------------------------------------------------- | ----- |
| Total de commits                                                       |       |
| Commits con asistencia de IA                                           |       |
| Commits 100% manuales                                                  |       |
| % de líneas modificadas sobre scaffolds de IA (ejemplo representativo) |       |

---

## Log de decisiones rechazadas por el desarrollador

Registro de contenido generado por IA que fue rechazado o corregido por el desarrollador, junto con el criterio técnico
de ingeniería que motivó la corrección.

| Módulo / Archivo | Sugerencia de la IA | Motivo del rechazo / corrección |
| ---------------- | ------------------- | ------------------------------- |
|                  |                     |                                 |
|                  |                     |                                 |
