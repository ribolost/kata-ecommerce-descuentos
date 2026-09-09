# kata-ecommerce-descuentos

Kata técnica para la construcción de un *ecommerce* con descuentos acumulativos.

El proyecto está compuesto por un **Frontend Angular** y un **Backend Spring Boot** con MongoDB.

---

# Inicio rápido

## Requisitos

Antes de comenzar, asegúrate de tener instalado:

* Node.js
* npm 10.9.7 o compatible
* Java 21
* Maven o Maven Wrapper
* MongoDB

Comprueba las versiones:

```bash
node --version
npm --version
java --version
mvn --version
```

---

## 1. Frontend

Instalar dependencias:

```bash
npm install
```

Ejecutar la aplicación:

```bash
npm start
```

Ejecutar las pruebas:

```bash
npm test
```

Ejecutar una suite específica:

```bash
npm test -- --filter="CartStateService"
```

Ejecutar lint:

```bash
npm run lint
```

Generar build:

```bash
npm run build
```

---

## 2. Backend

### Configurar MongoDB

El backend utiliza MongoDB.

Ejemplo de URI para desarrollo local:

```text
mongodb://localhost:27017/kataecommerce
```

La URI utilizada realmente por la aplicación debe corresponder con la configuración definida en `application.properties` o `application.yml`.

### Ejecutar el backend

Con Maven Wrapper:

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

#### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

Con Maven instalado globalmente:

```bash
mvn spring-boot:run
```

---

## 3. Ejecutar las pruebas del backend

Todas las pruebas:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Con Maven:

```bash
mvn test
```

---

## 4. Validar cobertura con JaCoCo

El comando recomendado para validar completamente el backend es:

```bash
./mvnw clean verify
```

En Windows:

```powershell
.\mvnw.cmd clean verify
```

Con Maven:

```bash
mvn clean verify
```

Este comando:

1. limpia el proyecto;
2. compila el código;
3. ejecuta las pruebas;
4. genera el reporte de JaCoCo;
5. valida el mínimo de **80% de cobertura de líneas**.

El build falla si no se alcanza el porcentaje mínimo configurado.

El reporte HTML se genera en:

```text
target/site/jacoco/index.html
```

---

# Flujo completo recomendado

Para validar el proyecto desde cero:

### Frontend

```bash
npm install
npm run lint
npm test
npm run build
```

### Backend

```bash
./mvnw clean verify
```

---

# Configuración

## Variables de entorno

Spring Boot permite configurar las propiedades de la aplicación mediante variables de entorno.

Para MongoDB puede utilizarse:

```text
SPRING_DATA_MONGODB_URI
```

Ejemplo en Linux / macOS:

```bash
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/kataecommerce
```

Ejemplo en Windows PowerShell:

```powershell
$env:SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/kataecommerce"
```

También puede configurarse el perfil activo:

```text
SPRING_PROFILES_ACTIVE
```

Ejemplo:

```bash
export SPRING_PROFILES_ACTIVE=local
```

> Los nombres y valores concretos de las variables dependen de las propiedades definidas por la aplicación en `application.properties` o `application.yml`. No se deben agregar variables que no tengan una propiedad correspondiente.

---

# Pruebas

## Frontend

El frontend utiliza **Vitest**.

### Ejecutar todas las pruebas

```bash
npm test
```

### Ejecutar una suite específica

```bash
npm test -- --filter="CartStateService"
```

---

## Backend

El backend utiliza:

* JUnit
* Mockito
* AssertJ
* Spring Test
* Spring MVC Test
* Spring Data MongoDB Test
* JaCoCo

### Ejecutar pruebas

```bash
./mvnw test
```

### Ejecutar pruebas y validar cobertura

```bash
./mvnw verify
```

### Ejecutar desde cero

```bash
./mvnw clean verify
```

---

# Cobertura con JaCoCo

JaCoCo está configurado para:

* instrumentar el código durante la ejecución de pruebas;
* generar el reporte de cobertura;
* generar el reporte HTML/XML;
* validar un mínimo de **80% de cobertura de líneas**.

## Reporte

Después de ejecutar:

```bash
./mvnw test
```

o:

```bash
./mvnw verify
```

el reporte HTML estará disponible en:

```text
target/site/jacoco/index.html
```

Desde este reporte pueden consultarse:

* cobertura por paquete;
* cobertura por clase;
* cobertura por método;
* líneas cubiertas;
* líneas no cubiertas.

---

# Alcance de la cobertura

La validación de JaCoCo está enfocada en el código que contiene lógica funcional y de negocio.

Se excluyen del cálculo determinadas clases de infraestructura, configuración, DTOs y componentes que no contienen lógica de negocio propia.

Entre las exclusiones configuradas se encuentran:

```text
KataecommerceApplication
infrastructure/controller
infrastructure/adapter/in
infrastructure/adapter/out
infrastructure/repository
infrastructure/persistence
infrastructure/mapper
infrastructure/mappers
shared/seed
shared/error
application/exception
application/dto
```

El dominio y la lógica de aplicación permanecen dentro del cálculo de cobertura.

---

# Comandos principales

## Frontend

| Objetivo              | Comando                                   |
| --------------------- | ----------------------------------------- |
| Instalar dependencias | `npm install`                             |
| Ejecutar aplicación   | `npm start`                               |
| Ejecutar pruebas      | `npm test`                                |
| Ejecutar una suite    | `npm test -- --filter="CartStateService"` |
| Generar build         | `npm run build`                           |
| Build en modo watch   | `npm run watch`                           |
| Ejecutar SSR          | `npm run serve:ssr:frontend`              |
| Ejecutar lint         | `npm run lint`                            |
| Corregir lint         | `npm run lint:fix`                        |

## Backend

| Objetivo            | Comando                  |
| ------------------- | ------------------------ |
| Instalar/compilar   | `./mvnw clean install`   |
| Ejecutar aplicación | `./mvnw spring-boot:run` |
| Ejecutar pruebas    | `./mvnw test`            |
| Validar cobertura   | `./mvnw verify`          |
| Validación completa | `./mvnw clean verify`    |

En Windows:

```powershell
.\mvnw.cmd
```

---

# Stack tecnológico

## Frontend

* Angular 22
* TypeScript 6
* RxJS 7
* Vitest
* ESLint
* Angular SSR
* Express

## Backend

* Java 21
* Spring Boot 4.1.1
* Spring MVC
* Spring Data MongoDB
* Bean Validation
* Spring Boot Actuator
* JUnit
* Mockito
* AssertJ
* JaCoCo
* Maven

## Base de datos

* MongoDB

---

# Objetivo de las pruebas

Las pruebas deben centrarse en el comportamiento funcional y las reglas de negocio del *ecommerce*.

En particular, el backend debe proteger mediante pruebas la lógica relacionada con:

* descuentos acumulativos;
* cálculo de descuentos;
* validación de stock;
* creación y procesamiento de pedidos;
* servicios con lógica de aplicación.

En el frontend deben cubrirse principalmente:

* estado del carrito;
* servicios;
* comportamiento de componentes;
* validaciones;
* reglas de interacción;
* transformaciones relevantes.

---

# Desarrollo local

El flujo habitual de desarrollo es:

```text
Frontend
   │
   ├── npm install
   ├── npm start
   └── npm test
        │
        ▼
Backend
   │
   ├── MongoDB
   ├── mvnw spring-boot:run
   └── mvnw clean verify
        │
        ▼
JaCoCo
   └── target/site/jacoco/index.html
```

---

# Información del proyecto

| Componente       | Tecnología                |
| ---------------- | ------------------------- |
| Frontend         | Angular 22                |
| Runtime Frontend | Node.js                   |
| Package manager  | npm 10.9.7                |
| Testing Frontend | Vitest                    |
| Backend          | Spring Boot 4.1.1         |
| Java             | 21                        |
| Build Backend    | Maven                     |
| Testing Backend  | JUnit / Mockito / AssertJ |
| Cobertura        | JaCoCo                    |
| Base de datos    | MongoDB                   |
