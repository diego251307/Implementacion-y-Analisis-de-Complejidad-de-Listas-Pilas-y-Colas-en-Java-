import Stack.MyStack;
// import Queue.MyQueue;
// import SinglyLinkedList.SinglyLinkedListNoTail;
// import SinglyLinkedList.SinglyLinkedListWithTail;
// import DoublyLinkedList.DoublyLinkedListNoTail;
// import DoublyLinkedList.DoublyLinkedListWithTail;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Benchmark de estructuras de datos.
// Uso: descomenta UN bloque en main(), corre con: java -Xmx4g Main
// Los datos se acumulan en out/datos.csv con append. Grafica con graficador.py.
public class Main {

    static final int RUNS = 5; // mediciones por N, se toma la mediana
    static final int K = 5; // operaciones aleatorias medidas por run, se promedian
    static final int WARMUP = 2; // pasadas previas para que el JIT compile antes de medir
    static final long SEED = 42L; // semilla fija para reproducibilidad

    // Tamanos de N: 10^1 hasta 10^8
    static final long[] NS = { 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L };

    @FunctionalInterface
    interface Op {
        void run(long v);
    }

    @FunctionalInterface
    interface Factory {
        Object make();
    }

    // Llena la estructura con N valores aleatorios sin medir (fill),
    // luego mide K operaciones individuales con argumentos aleatorios y devuelve el
    // promedio en ns.
    // El fill usa un unico Random(SEED+run) para que los valores sean distintos
    // entre runs
    // pero reproducibles. Los K argumentos de medicion salen del mismo RNG tras el
    // fill,
    // garantizando que sean distintos a los insertados (cubren caso promedio de
    // busqueda).
    static double measureOnce(Op fill, Op measure, long n, int run) {
        Random rng = new Random(SEED + run);
        for (long i = 0; i < n; i++)
            fill.run(rng.nextLong()); // fill sin cronometro
        System.gc();
        long total = 0;
        for (int k = 0; k < K; k++) {
            long arg = rng.nextLong(); // argumento aleatorio para la operacion
            long t0 = System.nanoTime();
            measure.run(arg); // medicion aislada: 1 op sobre N datos
            total += System.nanoTime() - t0;
        }
        return (double) total / K;
    }

    // Warmup con N pequeno para que el JIT compile los hot paths antes de medir.
    // Luego ejecuta RUNS mediciones reales y devuelve los tiempos en ns.
    static double[] bench(Factory factory, Op fill, Op measure, long n) {
        long wN = Math.min(n, 1_000);
        for (int w = 0; w < WARMUP; w++) {
            factory.make();
            measureOnce(fill, measure, wN, -w);
        }
        double[] res = new double[RUNS];
        for (int r = 0; r < RUNS; r++) {
            factory.make();
            res[r] = measureOnce(fill, measure, n, r);
        }
        return res;
    }

    static double median(double[] a) {
        double[] s = a.clone();
        Arrays.sort(s);
        return s[s.length / 2];
    }

    // Guarda con append al CSV para acumular resultados de distintas ejecuciones.
    // La cabecera solo se escribe si el archivo no existia.
    static void saveCSV(String est, String met, double[][] data) throws IOException {
        Files.createDirectories(Paths.get("out"));
        Path p = Paths.get("out/datos.csv");
        boolean nuevo = !Files.exists(p);
        try (PrintWriter pw = new PrintWriter(new FileWriter(p.toFile(), true))) {
            if (nuevo)
                pw.println("Estructura,Metodo,N,Run,TiempoPromedio_ns");
            for (int i = 0; i < NS.length; i++)
                for (int r = 0; r < data[i].length; r++)
                    pw.printf(Locale.US, "%s,%s,%d,%d,%.2f%n", est, met, NS[i], r, data[i][r]);
        }
        System.out.println("[CSV] " + est + "." + met + " guardado en out/datos.csv");
    }

    // Corre el benchmark para todos los N, imprime en consola y guarda en CSV.
    static void run(String est, String met, Factory factory, Op fill, Op measure) throws IOException {
        System.out.printf("%-20s %-12s |", est, met);
        double[][] data = new double[NS.length][];
        for (int i = 0; i < NS.length; i++) {
            data[i] = bench(factory, fill, measure, NS[i]);
            System.out.printf(" N=%-10d %6.1f ns |", NS[i], median(data[i]));
            System.out.flush();
        }
        System.out.println();
        saveCSV(est, met, data);
    }

    public static void main(String[] args) throws Exception {

        // MyStack
        // stackPreexp: pre-expande el arreglo interno hasta capacidad alta y lo vacia.
        // Asi push/pop/peek al medir nunca disparan resize() y se mide la op pura O(1).
        MyStack<Long>[] st = new MyStack[1];
        Factory stackPreexp = () -> {
            st[0] = new MyStack<>();
            Random r = new Random(SEED);
            for (long i = 0; i < 100_000L; i++)
                st[0].push(r.nextLong());
            while (!st[0].isEmpty())
                st[0].pop(); // vacia pero conserva capacidad interna
            return st[0];
        };

        // push O(1) amortizado: fill vacio, mide 1 push. Grafica debe ser horizontal.
        run("MyStack", "push", stackPreexp, v -> {
        }, v -> st[0].push(v));

        // pop O(1): fill inserta para que haya elemento que extraer, mide 1 pop.
        // run("MyStack", "pop", stackPreexp, v -> st[0].push(v), v -> st[0].pop());

        // peek O(1): solo lee el tope sin modificar, fill inserta al menos 1 elemento.
        // run("MyStack", "peek", stackPreexp, v -> st[0].push(v), v -> st[0].peek());

        // delete O(n): sin pre-expansion, estructura con N elementos reales.
        // El recorrido interno escala con N. Grafica debe crecer linealmente.
        // run("MyStack", "delete", () -> { st[0] = new MyStack<>(); return st[0]; },
        // v -> st[0].push(v), v -> st[0].delete(v));

    }
}