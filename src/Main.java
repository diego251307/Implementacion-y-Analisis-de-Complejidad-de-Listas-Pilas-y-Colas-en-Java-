import java.io.*;
import java.util.*;
import SinglyLinkedList.*;
import DoublyLinkedList.*;
import Stack.*;
import Queue.*;

public class Main {

    // ZONA DE CONFIGURACIÓN — solo toca esto

    static final String CSV_FILE = "out/datos.csv";

    // Tamaños a probar (usa SIZES_O1 para O(1), SIZES_ON para O(n))
    static final int[] SIZES_O1 = { 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000 };
    static final int[] SIZES_ON = { 10, 100, 1_000, 10_000, 100_000 };

    // Benchmarks a correr: agrega o quita entradas aquí
    // Formato: { "NombreEstructura", "NombreMetodo", SIZES_A_USAR }
    static final Object[][] PRUEBAS = {
            { "SinglyNoTail", "pushFront", SIZES_O1 },
            { "DoublyNoTail", "pushFront", SIZES_O1 },
            { "StackArray", "push", SIZES_O1 },
            { "QueueArray", "enqueue", SIZES_O1 },
    };

    /*
     * static final Object[][] PRUEBAS = {
     * { "SinglyNoTail", "pushFront", SIZES },
     * { "DoublyNoTail", "pushFront", SIZES },
     * { "SinglyWithTail", "pushBack", SIZES },
     * { "DoublyWithTail", "pushBack", SIZES },
     * { "StackArray", "push", SIZES },
     * { "QueueArray", "enqueue", SIZES },
     * };
     */
    // NO TOCAR DE AQUÍ PARA ABAJO

    static final int REPS = 7;
    static final Random RND = new Random(42);
    static final int SENTINEL = -1;
    static final StringBuilder CSV = new StringBuilder("Estructura,Metodo,N,Run,TiempoPromedio_ns\n");

    @SuppressWarnings("unchecked")
    static Object crear(String est, String met, int n) {
        switch (est) {
            case "SinglyNoTail":
                return switch (met) {
                    case "popFront", "popBack", "erase", "find" -> fillSNT(n * 2);
                    case "addBefore", "addAfter" -> {
                        var l = fillSNT(n);
                        l.pushBack(SENTINEL);
                        yield l;
                    }
                    default -> new SinglyLinkedListNoTail<Integer>();
                };
            case "SinglyWithTail":
                return switch (met) {
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
                if (!met.equals("push"))
                    for (int i = 0; i < n * 2; i++)
                        s.push(i);
                return s;
            }
            case "QueueArray": {
                var q = new MyQueue<Integer>();
                if (!met.equals("enqueue"))
                    for (int i = 0; i < n * 2; i++)
                        q.enqueue(i);
                return q;
            }
            default:
                throw new IllegalArgumentException("Estructura desconocida: " + est);
        }
    }

    @SuppressWarnings("unchecked")
    static void operar(String est, String met, Object obj, int v) {
        switch (est) {
            case "SinglyNoTail" -> {
                SinglyLinkedListNoTail<Integer> l = (SinglyLinkedListNoTail<Integer>) obj;
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
                    case "push" -> s.push(v);
                    case "pop" -> {
                        if (!s.isEmpty())
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
                    case "enqueue" -> q.enqueue(v);
                    case "dequeue" -> {
                        if (!q.isEmpty())
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

    static SinglyLinkedListNoTail<Integer> fillSNT(int n) {
        var l = new SinglyLinkedListNoTail<Integer>();
        for (int i = 0; i < n; i++)
            l.pushFront(RND.nextInt(1_000_000));
        return l;
    }

    static SinglyLinkedListWithTail<Integer> fillSWT(int n) {
        var l = new SinglyLinkedListWithTail<Integer>();
        for (int i = 0; i < n; i++)
            l.pushFront(RND.nextInt(1_000_000));
        return l;
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

    static void medir(String est, String met, int n, int run) {
        int[] vals = new int[n];
        for (int i = 0; i < n; i++)
            vals[i] = RND.nextInt(1_000_000);

        Object obj = crear(est, met, n);

        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++)
            operar(est, met, obj, vals[i]);
        double ns = (double) (System.nanoTime() - t0) / n;

        CSV.append(est).append(',').append(met).append(',')
                .append(n).append(',').append(run).append(',')
                .append(String.format(Locale.US, "%.4f", ns)).append('\n');

        System.out.printf("[%-16s] %-12s N=%-8d run%d  %.3f ns/op%n", est, met, n, run, ns);
    }

    public static void main(String[] args) throws Exception {

        // Warm-up rápido para que el JIT compile antes de medir
        var w = new SinglyLinkedListNoTail<Integer>();
        for (int i = 0; i < 100_000; i++) {
            w.pushFront(i);
            if (i % 2 == 0)
                w.popFront();
        }

        // Corre todas las pruebas definidas en PRUEBAS
        for (var p : PRUEBAS) {
            String est = (String) p[0];
            String met = (String) p[1];
            int[] szs = (int[]) p[2];
            for (int n : szs)
                for (int r = 0; r < REPS; r++)
                    medir(est, met, n, r);
        }

        // Exporta CSV a /out
        try (var w2 = new PrintWriter(new FileWriter(CSV_FILE))) {
            w2.print(CSV);
        }
        System.out.println("CSV exportado -> " + CSV_FILE);

        // Lanza el graficador desde la carpeta hermana /scripts
        // La graficacion va DESPUES del CSV para no contaminar los tiempos medidos
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "scripts/graficador.py");
            pb.inheritIO();
            int code = pb.start().waitFor();
            if (code != 0)
                System.err.println("[Python] Error, codigo: " + code);
            else
                System.out.println("[Python] Graficacion completada.");
        } catch (Exception e) {
            System.err.println("[Python] No se pudo ejecutar: " + e.getMessage());
        }
    }
}