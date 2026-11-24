# Java completo: ejercicios de Café Sintaxis

Este proyecto reúne soluciones de ejercicios de programación de una guía de Java, organizadas en once niveles. Se excluyeron las actividades de teoría, XP y evaluación.

## Estructura

Cada carpeta contiene un archivo Java independiente. El nombre del archivo coincide con el nivel y su clase principal:

| Carpeta | Archivo | Temas de Java |
|---|---|---|
| `Nivel_01` | `Nivel01.java` | Clases, objetos, atributos y métodos |
| `Nivel_02` | `Nivel02.java` | Encapsulamiento, atributos privados, validación y excepciones |
| `Nivel_03` | `Nivel03.java` | Constructores, encadenamiento con `this(...)` y sobrecarga |
| `Nivel_04` | `Nivel04.java` | Herencia, composición, clases de productos y pedidos, sobrescritura |
| `Nivel_05` | `Nivel05.java` | Clases abstractas, interfaces, implementaciones y `instanceof` con patrón |
| `Nivel_06` | `Nivel06.java` | Enumeraciones con datos, `switch` y `EnumMap` |
| `Nivel_07` | `Nivel07.java` | Principios SOLID, interfaces, inyección de dependencias y separación de responsabilidades |
| `Nivel_08` | `Nivel08.java` | Genéricos, límites de tipo, comodines y colecciones tipadas |
| `Nivel_09` | `Nivel09.java` | CRUD con listas, `Optional`, registros y menú interactivo |
| `Nivel_10` | `Nivel10.java` | `record`, inmutabilidad, constructores compactos y patrones de records |
| `Nivel_11` | `Nivel11.java` | Archivos, CSV, serialización, filtros de entrada y persistencia |

`Nivel_11` también contiene el reto final integrador de los temas de la guía.

## Requisitos

- JDK 25 o posterior.
- Ejecutar los comandos desde la carpeta raíz del proyecto.

## Ejecutar los niveles

Java permite ejecutar directamente cada archivo fuente:

```bash
java Nivel_01/Nivel01.java
java Nivel_02/Nivel02.java
java Nivel_03/Nivel03.java
java Nivel_04/Nivel04.java
java Nivel_05/Nivel05.java
java Nivel_06/Nivel06.java
java Nivel_07/Nivel07.java
java Nivel_08/Nivel08.java
java Nivel_09/Nivel09.java
java Nivel_10/Nivel10.java
java Nivel_11/Nivel11.java
```

Cada comando ejecuta los ejercicios incluidos en ese nivel. También es posible compilar todos los archivos de una vez y luego ejecutar una clase:

```bash
mkdir -p out
javac -d out Nivel_*/Nivel*.java
java -cp out Nivel01
```

Para ejecutar otro nivel, reemplaza `Nivel01` por la clase correspondiente (`Nivel02`, …, `Nivel11`).

## Menús interactivos

El nivel 9 ofrece un menú interactivo para crear, listar, actualizar y cancelar reservas:

```bash
java Nivel_09/Nivel09.java --menu
```

El nivel 11 ofrece un menú de reservas persistente. Guarda las reservas en `reservas.bin` al salir y permite exportarlas a `reservas.csv`:

```bash
java Nivel_11/Nivel11.java --menu
```

Estos archivos se crean en el directorio desde donde se ejecuta el comando.

Para más detalle sobre las soluciones, consulta [`SOLUCIONES.md`](SOLUCIONES.md).
