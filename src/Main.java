
// import Stack.MyStack;
// import Queue.MyQueue;
import SinglyLinkedList.SinglyLinkedListNoTail;
// import SinglyLinkedList.SinglyLinkedListWithTail;
// import DoublyLinkedList.DoublyLinkedListNoTail;
// import DoublyLinkedList.DoublyLinkedListWithTail;

// Se descomenta la estructura a usar arriba

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Pruebas de estructuras de datos

// Realizado con base de la guia de codigo dada en Moodle, indicaciones dadas por el monitor y apoyo de la inteligencia artificial para organizar codigo y revisar integridad de las mediciones
// Se ajustaba el main creando los run para cada metodo de cada clase, se acumulaba y corria el graficador cuando se consideraba completo datos.csv

// Para usar se descomenta un bloque run del main()
// Los datos se acumulan en out/datos.csv con append. Se realiza grafica con graficador.py.
public class Main {

    static final int RUNS = 3; // mediciones por N, se toma la mediana
    static final int K = 3; // operaciones aleatorias medidas por run, se promedian
    static final int WARMUP = 2; // pasadas previas para que el JIT compile antes de medir
    static final long SEED = 42L; // semilla fija para los randoms,para hacer reproducible las pruebas

    // Tamanos de N: 10^1 hasta 10^8 por guia
    static final long[] NS = { 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L };

    // Interfaz para operaciones a medir
    @FunctionalInterface
    interface Op {
        void run(long v);
    }

    // Interfaz para crear instancias de la estructura a medir, usada para warmup y
    // runs.
    @FunctionalInterface
    interface Factory {
        Object make();
    }

    // Se llena la estructura con N valores aleatorios sin medir (fill),
    // luego mide K operaciones individuales (se toma un tiempo por separado para
    // cada una) con argumentos aleatorios y devuelve el
    // promedio en ns.
    // El fill usa un unico Random(SEED+run) para que los valores sean distintos
    // entre runs pero reproducibles. Los K argumentos de medicion salen del mismo
    // RNG despues del fill.

    // fill y measure son lambdas que reciben un long aleatorio y hacen la operacion
    // correspondiente
    static double measureOnce(Op fill, Op measure, long n, int run) {
        Random rng = new Random(SEED + run); // Semilla distinta
        for (long i = 0; i < n; i++)
            fill.run(rng.nextLong()); // fill sin cronometro
        System.gc();
        long total = 0;
        for (int k = 0; k < K; k++) {
            long arg = rng.nextLong(); // argumento aleatorio para la operacion
            long t0 = System.nanoTime();
            measure.run(arg); // medicion aislada, 1 operacion sobre N datos
            total += System.nanoTime() - t0;
        }
        return (double) total / K;
    }

    // Warmup con N pequeno para que el JIT compile y caliente antes de medir.
    // Luego ejecuta RUNS con mediciones reales y devuelve los tiempos en ns.

    // Factory es una lambda que crea la estructura vacia a medir, se llama antes de
    // cada run para resetear el estado.
    // fill y measure son lambdas que hacen las operaciones correspondientes sobre
    // la estructura creada por factory.
    static double[] bench(Factory factory, Op fill, Op measure, long n) {
        long wN = Math.min(n, 1_000); // warmup con N pequeño para que sea rapido pero que sea significativo
        for (int w = 0; w < WARMUP; w++) {
            factory.make(); // reset de la estructura antes de cada warmup. Se crea y llena pero no se mide,
                            // solo para que el JIT compile el codigo.
            measureOnce(fill, measure, wN, -w); // Se llama a measureOnce que ejecuta el fill y el measure pero no se
                                                // guarda el resultado, solo para que el JIT compile el codigo caliente.
        }
        double[] res = new double[RUNS]; // Arreglo para guardar los tiempos de cada run real
        for (int r = 0; r < RUNS; r++) { // Runs reales con medicion, se repite el proceso pero ahora se guarda el
                                         // resultado de cada run en res[r]
            factory.make(); // Se crea la estructura vacia antes de cada run para resetear el estado asi
                            // cada run mide sobre una estructura nueva y no acumulada.
            res[r] = measureOnce(fill, measure, n, r); // Se llama a measureOnce que ejecuta el fill y el measure, pero
                                                       // esta vez se guarda el resultado del tiempo promedio de las K
                                                       // operaciones medidas en res[r].
        }
        return res; // Se devuelve el arreglo con los tiempos de cada run real, que luego se usara
                    // para calcular la mediana para mensaje en consola y guardar en CSV.
    }

    // Se utiliza la mediana para reportar el resultado de cada N en consola
    static double median(double[] a) {
        double[] s = a.clone();
        Arrays.sort(s);
        return s[s.length / 2];
    }

    // Guarda con append al CSV para acumular resultados de distintas ejecuciones.
    // Se crea archivo si antes no existe
    static void saveCSV(String est, String met, double[][] data) throws IOException {
        Files.createDirectories(Paths.get("out"));
        Path p = Paths.get("out/datos.csv"); // ruta del archivo CSV
        boolean nuevo = !Files.exists(p);
        try (PrintWriter pw = new PrintWriter(new FileWriter(p.toFile(), true))) {
            if (nuevo)
                pw.println("Estructura,Metodo,N,Run,Tiempo_ns"); // Estructura del csv. Se agrega si archivo es nuevo
            for (int i = 0; i < NS.length; i++)
                for (int r = 0; r < data[i].length; r++)
                    pw.printf(Locale.US, "%s,%s,%d,%d,%.2f%n", est, met, NS[i], r, data[i][r]); // Formato de cada linea
                                                                                                // del csv: Estructura,
                                                                                                // Metodo, N, Run,
                                                                                                // Tiempo en ns
        }
        System.out.println("[CSV] " + est + "." + met + " guardado en out/datos.csv"); // Confirmacion
    }

    // Corre la prueba para todos los N, imprime en consola y guarda en CSV.
    // est es el nombre de la estructura, met el metodo, factory la lambda para
    // crear la estructura vacia, fill y measure las lambdas para llenar y medir
    // respectivamente.
    static void run(String est, String met, Factory factory, Op fill, Op measure) throws IOException {
        System.out.printf("%-20s %-12s |", est, met); // Formato para consola, estructura y metodo
        double[][] data = new double[NS.length][]; // Arreglo para guardar los tiempos de cada N, cada elemento es un
                                                   // arreglo con los tiempos de los RUNS para ese N
        for (int i = 0; i < NS.length; i++) { // Itera sobre cada N, corre la prueba y guarda los tiempos en data[i]
            data[i] = bench(factory, fill, measure, NS[i]); // bench corre la prueba para el N actual y devuelve los
                                                            // tiempos de cada run, que se guardan en data[i]
            System.out.printf(" N=%-10d %6.1f ns |", NS[i], median(data[i])); // Imprime en consola el N actual y la
                                                                              // MEDIANA de los tiempos de los RUNS para
                                                                              // ese N
            System.out.flush();
        }
        System.out.println(); // Espacio
        saveCSV(est, met, data); // Guarda los resultados en CSV con el formato especificado, usando el nombre de
                                 // la estructura y el metodo para identificar cada linea ya terminado todas las
                                 // pruebas
    }

    // Logica para aplicar la prueba a metodo de estructura que uno quiera
    public static void main(String[] args) throws Exception {

        // PARA SinglyLinkedListNoTail
        // Fill siempre con pushFront (O(1)) para construir N nodos rapido sin sesgo.
        // Para cualquier estructura pues utilizar el insert constante

        SinglyLinkedListNoTail<Long>[] l = new SinglyLinkedListNoTail[1]; // Arreglo para guardar la referencia a la
                                                                          // estructura creada por factory, se usa un
                                                                          // arreglo de tamaño 1 para poder modificar su
                                                                          // contenido dentro de las lambdas de fill y
                                                                          // measureed
        Factory f = () -> {
            l[0] = new SinglyLinkedListNoTail<>(); // Factory que crea una nueva instancia vacia de
                                                   // SinglyLinkedListNoTail y la guarda en l[0], asi cada run trabaja
                                                   // con una estructura nueva
            return l[0];
        };

        // pushBack O(n)
        run("SinglyNoTail", "pushBack", f, v -> l[0].pushFront(v), v -> l[0].pushBack(v));

        // popBack O(n)
        // run("SinglyNoTail", "popBack", f, v -> l[0].pushFront(v), v ->
        // l[0].popBack());

        // find O(n)
        // run("SinglyNoTail", "find", f, v -> l[0].pushFront(v), v -> l[0].find(v));

        // erase O(n)
        // run("SinglyNoTail", "erase", f, v -> l[0].pushFront(v), v -> l[0].erase(v));

        // popFront O(1)
        // run("SinglyNoTail", "popFront", f, v -> l[0].pushFront(v), v ->
        // l[0].popFront());

        // pushFront O(1)
        // run("SinglyNoTail", "pushFront", f, v -> l[0].pushFront(v), v ->
        // l[0].pushFront(v));

        // addBefore O(n)
        // run("SinglyNoTail", "addBefore", f, v -> l[0].pushFront(v), v ->
        // l[0].addBefore(v, v - 1));

        // addAfter O(n)
        // run("SinglyNoTail", "addAfter", f, v -> l[0].pushFront(v), v ->
        // l[0].addAfter(v, v - 1));
    }
}