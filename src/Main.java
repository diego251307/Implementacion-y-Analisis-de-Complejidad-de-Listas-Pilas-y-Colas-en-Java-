import java.io.*;
import java.util.*;
import SinglyLinkedList.*;
import DoublyLinkedList.*;
import Stack.*;
import Queue.*;

public class Main {

    // CONFIGURACION DE LA PRUEBA A REALIZAR
    // Se define que se va a medir, que estructuras, metodos y tamanos de las
    // estructuras
    // Despues se encuentra el codigo "fijo", logica de la medicion que no se
    // modifica, ya que se adapta de acuerdo a lo que se desee medir

    // Ruta donde se guarda el CSV con los resultados
    static final String CSV_FILE = "out/datos.csv";

    // SIZES: tamaños para métodos O(1). Establecido hasta 10^8 para mostrar
    // estabilidad constante, aplicable por los tiempos de ejecucion aceptables.
    // SIZES_LINEAR: tamaños para métodos O(n). Se corta en 10^5 ya que con mayores
    // potencias el tiempo de prueba es excesivo y que ya se evidencia la tendencia
    // lineal
    static final int[] SIZES = { 10, 100, 1_000, 10_000, 100_000, 1_000_000 };
    static final int[] SIZES_LINEAR = { 10, 100, 1_000, 10_000, 100_000 };

    // Tabla de pruebas a correr. (Matriz
    // Cada fila es independiente: estructura, método, tamaños.
    // Para agregar una prueba se agrega una fila. Para quitarla solo se quita
    // Se grafica cada fila por separado, orden no importa.
    static final Object[][] PRUEBAS = {
            { "SinglyNoTail", "pushFront", SIZES },
            { "DoublyNoTail", "pushFront", SIZES },
            { "SinglyWithTail", "pushBack", SIZES },
            { "DoublyWithTail", "pushBack", SIZES },
            { "StackArray", "push", SIZES },
            { "QueueArray", "enqueue", SIZES },
    };

    // Otros casos aplicables. Se descomenta el PRUEBAS que desee correr (solo 1
    // activo a la vez)
    /*
     * METODOS O(1) - PUSH DE ESTRUCTURA ESPECIFICA
     * static final Object[][] PRUEBAS = {
     * { "SinglyNoTail", "pushFront", SIZES },
     * { "DoublyNoTail", "pushFront", SIZES },
     * { "SinglyWithTail", "pushBack", SIZES },
     * { "DoublyWithTail", "pushBack", SIZES },
     * { "StackArray", "push", SIZES },
     * { "QueueArray", "enqueue", SIZES },
     * };
     */

    // CODIGO DE MEDICIÓN. Este es fijo, se adapta automáticamente a lo que se
    // defina en PRUEBAS.

    // Número de repeticiones por cada (estructura, método, N).
    // REPS_BASE se usa para N grandes donde la varianza ya es baja. Reduce tiempos
    // de ejecucion.
    // REPS_SMALL se usa para N <= N_SMALL_THRESHOLD porque con pocos
    // elementos la varianza es muy alta y se necesitan más muestras
    // para que el promedio sea estable y representativo. Al ser N bajos
    // los tiempos de ejecución son muy rápidos.
    static final int REPS_BASE = 7;
    static final int REPS_SMALL = 20;
    static final int N_SMALL_THRESHOLD = 100;

    // Generador de números aleatorios con semilla fija. La semilla fija
    // garantiza que los valores aleatorios sean idénticos en cada ejecución,
    // haciendo los resultados reproducibles entre ejecuciones distintas.
    static final Random RND = new Random(42);

    // Valor "centinela" usado en addBefore y addAfter. Se inserta al final
    // de la lista en crear() para que el find interno siempre recorra
    // toda la lista antes de encontrar el nodo de reference, representando
    // el peor caso O(n) de esas operaciones.
    static final int SENTINEL = -1;
    // Este valor no se genera aleatoriamente en los fills, se encontrara siempre al
    // final de la lista

    // Buffer en memoria donde se acumulan todas las filas del CSV.
    // Se escribe a disco una sola vez al final para no contaminar
    // los tiempos
    static final StringBuilder CSV = new StringBuilder("Estructura,Metodo,N,Run,TiempoPromedio_ns\n");
    // La estructura del CSV sera analizada por el graficador en Python para generar
    // las graficas

    // crear() construye e inicializa la estructura antes de medir.
    // Se llama FUERA de la medicion para no sesgar los tiempos
    // Para pushFront y pushBack se devuelve la lista ya llena con N
    // elementos. Sino la lista estara vacia y las primeras
    // operaciones trabajan sobre una lista pequeña, mientras que las últimas sobre
    // una lista grande.
    // Esto sesga el resultado al tamano de lo que se busca medir
    // Pre-llenando con N se garantiza que todas las operaciones ocurran
    // sobre una estructura de tamaño estable y condiciones de caché uniformes.
    //
    // Para popFront, popBack, erase y find se llena con 2*N elementos.
    // Los métodos pop y erase consumen elementos de la lista con cada
    // llamada. Si se llenara con exactamente N, hacia el final del run
    // la lista estaría casi vacía y las últimas operaciones no harían
    // trabajo real. Con 2*N elementos quedan N elementos disponibles después de N
    // operaciones, garantizando que todas las operaciones sean reales.
    //
    // Para find también se usa 2*N para que el recorrido sea sobre una lista de
    // tamaño representativo.
    //
    // Para addBefore y addAfter se llena con N elementos y se inserta
    // el SENTINEL al final. El SENTINEL es el nodo de referencia que
    // buscan internamente esos métodos. Al estar al final, el find interno
    // recorre toda la lista antes de encontrarlo, midiendo el peor caso.
    //
    // Para Stack y Queue se pre-llena con N elementos incluyendo push
    // y enqueue, por la misma razón del tamaño explicada arriba.
    static Object crear(String est, String met, int n) {
        switch (est) { // Switch exterior selecciona la estructura
            case "SinglyNoTail":
                return switch (met) { // Switch interno selecciona el metodo
                    case "pushFront", "pushBack" -> fillSNT(n);
                    case "popFront", "popBack", "erase", "find" -> fillSNT(n * 2);
                    case "addBefore", "addAfter" -> {
                        var l = fillSNT(n);
                        l.pushBack(SENTINEL);
                        yield l;
                    }
                    default -> new SinglyLinkedListNoTail<Integer>(); // Si no es un metodo de los mencionados da la
                                                                      // estructura vacia. Demas casos aplciados se
                                                                      // llena como fue ya explicado
                };
            case "SinglyWithTail":
                return switch (met) {
                    case "pushFront", "pushBack" -> fillSWT(n);
                    case "popFront", "popBack", "erase", "find" -> fillSWT(n * 2);
                    case "addBefore", "addAfter" -> {
                        var l = fillSWT(n);
                        l.pushBack(SENTINEL);
                        yield l;
                    }
                    default -> new SinglyLinkedListWithTail<Integer>();
                };
            case "DoublyNoTail":
                return switch (met) {
                    case "pushFront", "pushBack" -> fillDNT(n);
                    case "popFront", "popBack", "erase", "find" -> fillDNT(n * 2);
                    case "addBefore", "addAfter" -> {
                        var l = fillDNT(n);
                        l.pushBack(SENTINEL);
                        yield l;
                    }
                    default -> new DoublyLinkedListNoTail<Integer>();
                };
            case "DoublyWithTail":
                return switch (met) {
                    case "pushFront", "pushBack" -> fillDWT(n);
                    case "popFront", "popBack", "erase", "find" -> fillDWT(n * 2);
                    case "addBefore", "addAfter" -> {
                        var l = fillDWT(n);
                        l.pushBack(SENTINEL);
                        yield l;
                    }
                    default -> new DoublyLinkedListWithTail<Integer>();
                };
            case "StackArray": {
                var s = new MyStack<Integer>();
                for (int i = 0; i < n; i++)
                    s.push(RND.nextInt(1_000_000));
                return s;
            }
            case "QueueArray": {
                var q = new MyQueue<Integer>();
                for (int i = 0; i < n; i++)
                    q.enqueue(RND.nextInt(1_000_000));
                return q;
            }
            default:
                throw new IllegalArgumentException("Estructura desconocida: " + est); // Estructura no reconocida y se
                                                                                      // lanza excepcion
        }
    }

    // operar() ejecuta una sola operación sobre la estructura.

    // Este es el codigo que corre DENTRO de la medicion del tiempo.

    // Recibe la estructura como Object y hace el cast al tipo concreto.

    // El switch exterior selecciona la estructura y el interior el método.

    // El parámetro v es el valor aleatorio ya generado que se usa como
    // dato a insertar, buscar o eliminar según corresponda. Se tomo la
    // consieracion que este valor aleatorio no sea
    // generado dentro de la medicion del tiempo

    // El overhead del switch anidado existe pero es minimo e igual para
    // todas las estructuras, así que no afecta las comparativas entre ellas.
    @SuppressWarnings("unchecked") // Para evitar warnings de cast en tiempo de compilación
    static void operar(String est, String met, Object obj, int v) { // Argumentos como fueron descritos
        switch (est) { // Swithc exterior selecciona la estructura
            case "SinglyNoTail" -> {
                SinglyLinkedListNoTail<Integer> l = (SinglyLinkedListNoTail<Integer>) obj; // Cast al tipo concreto
                switch (met) { // Switch interior selecciona el metodo
                    case "pushFront" -> l.pushFront(v); // No hay condion de vacio
                    case "pushBack" -> l.pushBack(v);
                    case "popFront" -> {
                        if (!l.empty()) // Se revisa que no este vacia para evitar excepcion
                            l.popFront();
                    }
                    case "popBack" -> {
                        if (!l.empty())
                            l.popBack();
                    }
                    case "find" -> l.find(v % l.size()); // Se usa v mod size para garantizar que el valor exista y se
                                                         // recorra toda la lista
                    case "erase" -> {
                        if (!l.empty())
                            l.erase(v % l.size()); // SE usa v mod size para garantizar que el valor exista y se recorra
                                                   // toda la lista.
                    }
                    case "addBefore" -> l.addBefore(SENTINEL, v); // Se usa SENTINEL como nodo de referencia para medir
                                                                  // el peor caso O(n)
                    case "addAfter" -> l.addAfter(SENTINEL, v); // Se usa SENTINEL como nodo de referencia para medir el
                                                                // peor caso O(n)
                }
            }
            case "SinglyWithTail" -> {
                SinglyLinkedListWithTail<Integer> l = (SinglyLinkedListWithTail<Integer>) obj;
                switch (met) {
                    case "pushFront" -> l.pushFront(v);
                    case "pushBack" -> l.pushBack(v);
                    case "popFront" -> {
                        if (!l.empty())
                            l.popFront();
                    }
                    case "popBack" -> {
                        if (!l.empty())
                            l.popBack();
                    }
                    case "find" -> l.find(v % l.size());
                    case "erase" -> {
                        if (!l.empty())
                            l.erase(v % l.size());
                    }
                    case "addBefore" -> l.addBefore(SENTINEL, v);
                    case "addAfter" -> l.addAfter(SENTINEL, v);
                }
            }
            case "DoublyNoTail" -> {
                DoublyLinkedListNoTail<Integer> l = (DoublyLinkedListNoTail<Integer>) obj;
                switch (met) {
                    case "pushFront" -> l.pushFront(v);
                    case "pushBack" -> l.pushBack(v);
                    case "popFront" -> {
                        if (!l.isEmpty())
                            l.popFront();
                    }
                    case "popBack" -> {
                        if (!l.isEmpty())
                            l.popBack();
                    }
                    case "find" -> l.find(v % l.size());
                    case "erase" -> {
                        if (!l.isEmpty())
                            l.erase(v % l.size());
                    }
                    case "addBefore" -> l.addBefore(SENTINEL, v);
                    case "addAfter" -> l.addAfter(SENTINEL, v);
                }
            }
            case "DoublyWithTail" -> {
                DoublyLinkedListWithTail<Integer> l = (DoublyLinkedListWithTail<Integer>) obj;
                switch (met) {
                    case "pushFront" -> l.pushFront(v);
                    case "pushBack" -> l.pushBack(v);
                    case "popFront" -> {
                        if (!l.isEmpty())
                            l.popFront();
                    }
                    case "popBack" -> {
                        if (!l.isEmpty())
                            l.popBack();
                    }
                    case "find" -> l.find(v % l.size());
                    case "erase" -> {
                        if (!l.isEmpty())
                            l.erase(v % l.size());
                    }
                    case "addBefore" -> l.addBefore(SENTINEL, v);
                    case "addAfter" -> l.addAfter(SENTINEL, v);
                }
            }
            case "StackArray" -> {
                var s = (MyStack<Integer>) obj;
                switch (met) {
                    case "push" -> s.push(v); // No hay condicion de vacio para push
                    case "pop" -> {
                        if (!s.isEmpty()) // Se revisa que no este vacia para evitar excepcion
                            s.pop();
                    }
                    case "peek" -> {
                        if (!s.isEmpty())
                            s.peek();
                    }
                    case "delete" -> {
                        if (!s.isEmpty())
                            s.delete(v % s.size());
                    }
                }
            }
            case "QueueArray" -> {
                var q = (MyQueue<Integer>) obj;
                switch (met) {
                    case "enqueue" -> q.enqueue(v); // No hay condicion de vacio para enqueue
                    case "dequeue" -> {
                        if (!q.isEmpty()) // Se revisa que no este vacia para evitar excepcion
                            q.dequeue();
                    }
                    case "front" -> {
                        if (!q.isEmpty())
                            q.front();
                    }
                    case "delete" -> {
                        if (!q.isEmpty())
                            q.delete(v % q.size());
                    }
                }
            }
        }
    }

    // Los cuatro métodos fill crean una lista del tipo indicado y la
    // llenan con n valores aleatorios, ya aplicado el tamano n depende como ya fue
    // explicado. Están separados para evitar
    // repetición de código y porque cada uno devuelve el tipo genérico
    // correcto, lo que evita problemas de cast en tiempo de ejecución.

    static SinglyLinkedListNoTail<Integer> fillSNT(int n) { // Se llena con n elementos para push y 2n para pop, find y
                                                            // erase, dependiendo del caso
        var l = new SinglyLinkedListNoTail<Integer>();
        for (int i = 0; i < n; i++)
            l.pushFront(RND.nextInt(1_000_000)); // Se realiza pushFront para llenar la lista, entradas aleatorias entre
                                                 // 0 y 999999
        return l; // Se devuelve la lista llena
    }

    static SinglyLinkedListWithTail<Integer> fillSWT(int n) { // se llena con n elementos para push y 2n para pop, find
                                                              // y erase, dependiendo del caso
        var l = new SinglyLinkedListWithTail<Integer>();
        for (int i = 0; i < n; i++)
            l.pushFront(RND.nextInt(1_000_000)); // Se realiza pushFront para llenar la lista, entradas aleatorias entre
                                                 // 0 y 999999
        return l; // Se devuelve la lista llena
    }

    static DoublyLinkedListNoTail<Integer> fillDNT(int n) {
        var l = new DoublyLinkedListNoTail<Integer>();
        for (int i = 0; i < n; i++)
            l.pushFront(RND.nextInt(1_000_000));
        return l;
    }

    static DoublyLinkedListWithTail<Integer> fillDWT(int n) {
        var l = new DoublyLinkedListWithTail<Integer>();
        for (int i = 0; i < n; i++)
            l.pushFront(RND.nextInt(1_000_000));
        return l;
    }

    // medir() quien realiza el benchmark individual.
    // Recibe la estructura, el método, el tamaño N y el índice de run.

    // Run es el número de repetición externa que viene del loop en main() y se usa
    // para que el graficador calcule la mediana y el RANGO INTERCUARTIL sobre los 7
    // valores que produce cada (estructura, método, N).

    // En el CSV se produce una fila por cada llamada a medir() desde el loop
    // externo, y el graficador luego agrupa esas filas por (estructura, método, N)
    // para calcular la mediana y el RANGO INTERCUARTIL de cada punto en la gráfica.
    static void medir(String est, String met, int n, int run) {

        // Usar más repeticiones para N pequeños. Con N=10 o N=100 la varianza
        // entre pruebas es muy alta.
        // Con 20 repeticiones la media recortada descarta más "ruido".
        int reps = (n <= N_SMALL_THRESHOLD) ? REPS_SMALL : REPS_BASE; // Es un if ternario. Se asigna el numero de
                                                                      // repeticiones segun el
                                                                      // tamano N, mas para N pequeños y menos para N
                                                                      // grandes, como ya fue explicado.

        // Los valores aleatorios se generan aquí, fuera de la medicion.
        // Si se generaran dentro del loop de medición,
        // se sesgaría el resultado.
        int[] vals = new int[n];
        for (int i = 0; i < n; i++)
            vals[i] = RND.nextInt(1_000_000); // Se generan n valores aleatorios entre 0 y 999999 para usar en las
                                              // operaciones

        // La estructura se crea aquí, fuera de la medicion.
        // La asignación de memoria y el llenado previo
        // no deben contarse como parte del tiempo de la operación medida.
        Object obj = crear(est, met, n);

        // Se ejecutan reps mediciones independientes y se guarda cada tiempo.
        // Cada medición cronometra exactamente n operaciones y divide por n
        // para obtener el tiempo promedio por operación en nanosegundos.
        // Se usa System.nanoTime() porque tiene mejor resolución y no
        // se ve afectado por factores externos
        double[] tiempos = new double[reps]; // Array para guardar los tiempos de cada repetición
        for (int r = 0; r < reps; r++) { // Loop de repeticiones
            long t0 = System.nanoTime(); // Tiempo inicial en nanosegundos
            for (int i = 0; i < n; i++)
                operar(est, met, obj, vals[i]); // Se ejecutan n operaciones usando los valores aleatorios que ya fueron
                                                // generados
            tiempos[r] = (double) (System.nanoTime() - t0) / n; // Tiempo final menos tiempo inicial, dividido por n
                                                                // para obtener el tiempo promedio por operación
            // Se guarda el tiempo promedio de esta repetición en el array de tiempos
        }

        // Media recortada: se ordena el array de tiempos y se descarta
        // el run más rápido y el más lento antes de promediar.

        // El más lento puede tener sesgo externo.

        // El más rápido puede tener sesgo de optimizaciones.

        // Los valores del medio son los más aplicables para representar el
        // tiempo real de la operación en condiciones normales.
        Arrays.sort(tiempos); // Se ordenan los tiempos para luego descartar el más rápido y el más lento
        double suma = 0; // Variable para acumular la suma de los tiempos que se van a promediar
        int desde = (reps > 4) ? 1 : 0; // Si hay más de 4 repeticiones, se descarta el más rápido (índice 0), sino se
                                        // incluye. Esto es para evitar descartar datos si el número de repeticiones es
                                        // muy bajo.
        int hasta = (reps > 4) ? reps - 1 : reps; // Si hay más de 4 repeticiones, se descarta el más lento (índice
                                                  // reps-1), sino se incluye. Esto es para evitar
                                                  // descartar datos si el número de repeticiones es muy bajo.
        for (int i = desde; i < hasta; i++)
            suma += tiempos[i]; // Se suman todos los tiempos
        double avg = suma / (hasta - desde); // Se calcula el promedio dividiendo la suma por el número de tiempos
                                             // incluidos en el promedio

        // El parámetro run viene del loop externo en main() y sirve para
        // que el graficador calcule la mediana y el RANGO INTERCUARTIL sobre los 7
        // valores ue produce cada (estructura, método, N) — uno por cada llamada
        // a medir() desde el loop externo.
        CSV.append(est).append(',').append(met).append(',')
                .append(n).append(',').append(run).append(',')
                .append(String.format(Locale.US, "%.4f", avg)).append('\n');

        // Formato CSV. Se agrega una fila al CSV con los datos de esta medición. El
        // tiempo promedio
        // se formatea con 4 decimales y se usa punto como separador decimal.

        System.out.printf("[%-16s] %-12s N=%-8d run%d  %.3f ns/op%n", est, met, n, run, avg);
        // Se imprime en consola un resumen de esta medición con formato organizado. Se
        // muestra la estructura, el método, el tamaño N, el número de run y el tiempo
        // promedio en nanosegundos por operación.
    }

    public static void main(String[] args) throws Exception {

        // Explicacion IA: "Warm-up: antes de medir nada se ejercita cada par
        // (estructura, método)
        // que aparece en PRUEBAS con 5 pasadas de N=10_000 operaciones cada una.
        // La JVM no compila el bytecode a código nativo de inmediato. Primero lo
        // interpreta, luego aplica una compilación rápida llamada C1, y finalmente
        // tras suficientes ejecuciones aplica la compilación optimizada C2.
        // Con una sola pasada de 10_000 operaciones puede quedarse en C1.
        // Con 5 pasadas de 10_000 cada una, sumando 50_000 ejecuciones del
        // hot-path, se garantiza llegar a C2 antes de la primera medición real.
        // Sin esto, los primeros runs de N=10 y N=100 medirían bytecode
        // interpretado y darían tiempos 5-10x más altos que los reales,
        // generando los picos "falsos"."

        // El Set yaCalentados evita repetir el warm-up si la misma combinación
        // aparece más de una vez en PRUEBAS.
        System.out.println("[Warmup] Calentando..."); // Descriptivo
        Set<String> yaCalentados = new HashSet<>(); // Set para llevar registro de las combinaciones ya "calentadas"
        for (var p : PRUEBAS) { // Itera sobre cada fila de PRUEBAS para realizar el warm-up
            String est = (String) p[0];
            String met = (String) p[1];
            String key = est + ":" + met;
            if (yaCalentados.contains(key))
                continue;
            yaCalentados.add(key);
            int wn = 10_000;
            int[] wvals = new int[wn];
            for (int i = 0; i < wn; i++)
                wvals[i] = RND.nextInt(1_000_000); // Se generan 10_000 valores aleatorios para usar en las operaciones
                                                   // del warm-up
            for (int pass = 0; pass < 5; pass++) {
                Object wobj = crear(est, met, wn);
                for (int i = 0; i < wn; i++)
                    operar(est, met, wobj, wvals[i]); // Se realizan 5 pasadas de 10_000 operaciones cada una para
                                                      // "calentar" la JVM y asegurar que el código esté optimizado
                                                      // antes de las mediciones reales
            }
        }
        System.out.println("[Warmup] Listo.\n"); // Descriptivo

        // Loop principal: itera sobre PRUEBAS y para cada entrada corre
        // todos los tamaños N con REPS_BASE repeticiones externas.
        // Por cada N se llama REPS_BASE veces a medir(), produciendo
        // REPS_BASE filas en el CSV. El graficador luego usa esas filas
        // para calcular la mediana y la banda RANGO INTERCUARTIL de cada punto en la
        // gráfica.
        for (var p : PRUEBAS) {
            String est = (String) p[0]; // Se extrae la estructura, el método y los tamaños de cada fila de PRUEBAS
            String met = (String) p[1];
            int[] szs = (int[]) p[2];
            for (int n : szs)
                for (int r = 0; r < REPS_BASE; r++)
                    medir(est, met, n, r); // Se llama a medir() para cada combinación de estructura, método, tamaño y
                                           // repetición, acumulando los resultados en el buffer CSV
        }

        // Se escribe el buffer CSV a disco en una sola operación al final.
        // Escribir a disco durante las pruebas sesga los tiempos
        try (var w2 = new PrintWriter(new FileWriter(CSV_FILE))) {
            w2.print(CSV);
        }
        System.out.println("CSV exportado -> " + CSV_FILE);

        // Se lanza el graficador Python como proceso externo después de
        // exportar el CSV. Importantisimo que ocurra despues y no durante la
        // medición porque matplotlib tiene un overhead muy significativo.

        try { // Utilizamos try y catch para manejar posibles excepciones al ejecutar el
              // proceso externo
            ProcessBuilder pb = new ProcessBuilder("python3", "scripts/graficador.py");
            // Se crea un ProcessBuilder para ejecutar el comando "python3
            // scripts/graficador.py"

            pb.inheritIO(); // Python comparte consola con Java
            int code = pb.start().waitFor();
            if (code != 0)
                System.err.println("[Python] Error, codigo: " + code); // Descriptivo
            else
                System.out.println("[Python] Graficacion completada."); // Descriptivo
        } catch (Exception e) {
            System.err.println("[Python] No se pudo ejecutar: " + e.getMessage());
            // Si ocurre una excepción al ejecutar el proceso externo, se imprime un mensaje
            // de error con la descripción de la excepción
        }
    }
}