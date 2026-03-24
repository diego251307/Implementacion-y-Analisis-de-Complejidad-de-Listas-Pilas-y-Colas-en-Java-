# Implementación y Análisis de Complejidad de Listas, Pilas y Colas en Java

> ## Estructuras de Datos (2016699) - Grupo 2
> Profesor: David Alberto Herrera Álvarez - dherreraal@unal.edu.co
>
> Monitor: Daniel Alfonso Cely Infante - dcelyi@unal.edu.co

## *Contenido:*

1. **Estructuras:**
    Se encuentran los cuatro tipos de listas, stack dinámico y queue dinámico circular. Ubicadas en la carpeta `/src` en su subcarpeta respectiva; cada tipo de estructura tiene su propio package.

2. **Main:**
    Main de pruebas realizado con base en la guía PDF, código guía dado, aclaraciones del monitor y orientado y verificado con inteligencia artificial. En el `main()` manualmente se establecen los métodos a probar, se ejecuta el código y se hace *append* a un CSV que será utilizado para graficar. Se encuentran los `run()` para la lista sencillamente enlazada, sin cola (*tail*).

3. **CSV:**
    Tiene el formato dado en el Main que guarda los distintos registros de las pruebas; cuando se considere completo, se corre el `graficador.py`.

4. **Graficador:**
    Versión base realizada y posteriormente ampliada en estructura y diseño utilizando inteligencia artificial. Se toman los datos del CSV y se procesan adecuadamente calculando las medianas. Se dibuja la gráfica y la tabla. Como es explicado en el PDF de la entrega, se hace un cálculo para determinar si se utilizan nanosegundos o microsegundos para la escala de tiempo y mejorar la lectura.

5. **Carpeta /out:**
    Se encuentran las imágenes de los resultados utilizados en el PDF de la entrega; aquí también se generan el CSV y el `.png`.

## *Descripción de la tarea:*

1. **Implementar la Estructura List:**
    Estructura de datos "List" basada en listas enlazadas, realizando un análisis de complejidad de los métodos asociados como `PushFront`, `PushBack`, `PopFront`, `PopBack`, `Find`, `Erase`, `AddBefore`, `AddAfter`, etc.

2. **Implementación de Pilas y Colas en Java:**
    Implementación de las interfaces `MyStack` y `MyQueue` con los métodos fundamentales.

3. **Análisis de Complejidad:**
    Estudio de la eficiencia (Big O) de cada método en las distintas implementaciones.

4. **Visualización y Graficación de Resultados:**
    Pruebas experimentales para las operaciones implementadas, además de graficación de los resultados obtenidos para comparar rendimientos.

5. **Conclusiones del trabajo:**
    Detalles de las condiciones para determinar qué método es mejor teniendo en cuenta las diferentes implementaciones de los métodos y el uso de recursos.

## *Objetivo:*
Implementación de las estructuras de datos Stack y Queue en Java, usando desde arreglos dinámicos hasta listas enlazadas, igualmente realizando un análisis de complejidad de los métodos asociados.

## *Métodos usados en cada estructura de datos:*

### Para 'List'
* `PushFront`: Inserción de un elemento al inicio de la lista.
* `PushBack`: Inserción de un elemento al final de la lista.
* `PopFront`: Eliminación del primer elemento de la lista.
* `PopBack`: Eliminación del último elemento de la lista.
* `Find`: Retorna la referencia del elemento en la lista.
* `Erase`: Eliminación del elemento indicado de la lista.
* `AddBefore`: Inserción de un elemento antes del elemento indicado en la lista.
* `AddAfter`: Inserción de un elemento después del elemento indicado en la lista.

### Para 'MyStack<T>'
* `push(T x)`: Inserta un elemento en la cima.
* `pop()`: Elimina y retorna el elemento en la cima.
* `peek()`: Retorna el elemento en la cima sin eliminarlo.
* `isEmpty()`: Verifica si la pila está vacía.
* `size()`: Retorna el número de elementos en la pila.
* `delete(n)`: Elimina el primer valor `n` que encuentra en la estructura.

### Para 'MyQueue<T>'
* `enqueue(T x)`: Inserta un elemento al final.
* `dequeue()`: Elimina y retorna el primer elemento.
* `front()`: Retorna el primer elemento sin eliminarlo.
* `isEmpty()`: Verifica si la cola está vacía.
* `size()`: Retorna el número de elementos en la cola.
* `delete(n)`: Elimina el primer valor `n` que encuentra en la estructura.

## Desarrollado por:
- Diego Alejandro Prieto Badillo - diprietob@unal.edu.co
- Julián Ricardo Rodríguez Villamizar - julrodriguezvi@unal.edu.co
- Sara Mariana Sanabria Ortiz - sasanabriao@unal.edu.co
- Carlos Stiven Romero Sicacha - cromerosi@unal.edu.co
- Miguel Ángel Suárez Montiel - migsuarezmo@unal.edu.co

--- 

## Herramientas:

- Java
- Python
- Git
