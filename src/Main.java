// Main.java - Benchmark de Listas, Pilas y Colas
// Tarea: Implementación y Análisis de Complejidad — ED 2026-I
//
// Estructuras:
//   List: SinglyLinkedListNoTail, SinglyLinkedListTail,
//         DoublyLinkedListNoTail, DoublyLinkedListWithTail
//   Stack: DynamicArrayStack (arreglo dinámico)
//   Queue: MyQueue (arreglo dinámico)
//
// El menú sigue las 3 partes del análisis del enunciado:
//   Parte 1 - Cada estructura de List por separado (todos sus métodos)
//   Parte 2 - Stack y Queue por separado (todos sus métodos)
//   Parte 3 - Comparativas List vs Stack/Queue por método equivalente

import java.io.*;
import java.util.*;
import SinglyLinkedList.*;
import DoublyLinkedList.DoublyLinkedListNoTail;
import DoublyLinkedList.DoublyLinkedListWithTail;
import Stack.*;
import Queue.*;

public class Main {

    // Tamaños completos para métodos O(1) o O(1) amortizado
    private static final int[] SIZES = { 10, 100, 1_000, 10_000, 100_000, 1_000_000 };

    // Tamaños reducidos para métodos O(n). Con N=1_000_000 y búsqueda lineal
    // el benchmark tardaría horas, así que lo limitamos a 100_000
    private static final int[] SIZES_LINEAR = { 10, 100, 1_000, 10_000, 100_000 };

    // Con 7 runs se descarta el mejor y el peor, quedando 5 para la media
    private static final int REPETICIONES = 7;

    // 300_000 operaciones de warm-up fuerzan al JIT a compilar todos los
    // hot-paths antes de la primera medición real
    private static final int GLOBAL_WARMUP_OPS = 300_000;

    // Semilla fija para reproducibilidad entre ejecuciones
    private static final Random RANDOM = new Random(42);

    // Buffer CSV. Run = -1 indica media recortada (trimmed mean)
    private static final StringBuilder CSV = new StringBuilder("Estructura,Metodo,N,Run,TiempoPromedio_ns\n");

    private static final String CSV_FILE = "datos.csv";
    private static final String PY_SCRIPT = "scripts/graficador.py";

    // Valor que siempre existe en la lista durante addBefore/addAfter.
    // Se inserta al final en create() para que find() recorra toda la lista
    private static final int SENTINEL = -1;

    // INTERFAZ StructureFactory
    // Desacopla la creación de la estructura (fuera del cronómetro) de la
    // operación medida (dentro del cronómetro)
    interface StructureFactory {
        Object create(int n);

        void apply(Object estructura, int value);
    }

    // MOTOR DE MEDICIÓN
    // Pre-computa los valores aleatorios, crea la estructura, cronometra
    // exactamente n operaciones y registra el resultado en el CSV
    private static void exec(String nombreEstructura, String metodo,
            int n, int run, StructureFactory factory) {

        // Los valores se pre-computan aquí para que random.nextInt() no sume
        // su latencia (~20-50 ns) al tiempo de la operación medida
        int[] valores = new int[n];
        for (int i = 0; i < n; i++)
            valores[i] = RANDOM.nextInt(1_000_000);

        // La estructura se crea aquí: asignación de memoria y pre-llenado
        // ocurren fuera del cronómetro
        Object estructura = factory.create(n);

        // System.nanoTime() da resolución de ~1 ns y no se ve afectado por
        // cambios de reloj del sistema (NTP, zona horaria, etc.)
        long inicio = System.nanoTime();
        for (int i = 0; i < n; i++)
            factory.apply(estructura, valores[i]);
        long fin = System.nanoTime();

        double nsPerOp = (double) (fin - inicio) / n;

        CSV.append(nombreEstructura).append(',')
                .append(metodo).append(',')
                .append(n).append(',')
                .append(run).append(',')
                .append(String.format(Locale.US, "%.4f", nsPerOp)).append('\n');

        System.out.printf("  [%-18s] %-12s | N=%-8d | run %d | %.3f ns/op%n",
                nombreEstructura, metodo, n, run, nsPerOp);
    }

    // HELPERS de pre-llenado
    // Se llaman desde create() para no contaminar el cronómetro con
    // la inserción de los elementos iniciales

    private static SinglyLinkedListNoTail<Integer> fillSinglyNoTail(int n) {
        SinglyLinkedListNoTail<Integer> l = new SinglyLinkedListNoTail<>();
        for (int i = 0; i < n; i++)
            l.pushFront(i);
        return l;
    }

    private static SinglyLinkedListTail<Integer> fillSinglyWithTail(int n) {
        SinglyLinkedListTail<Integer> l = new SinglyLinkedListTail<>();
        for (int i = 0; i < n; i++)
            l.pushFront(i);
        return l;
    }

    private static DoublyLinkedListNoTail<Integer> fillDoublyNoTail(int n) {
        DoublyLinkedListNoTail<Integer> l = new DoublyLinkedListNoTail<>();
        for (int i = 0; i < n; i++)
            l.pushFront(i);
        return l;
    }

    private static DoublyLinkedListWithTail<Integer> fillDoublyWithTail(int n) {
        DoublyLinkedListWithTail<Integer> l = new DoublyLinkedListWithTail<>();
        for (int i = 0; i < n; i++)
            l.pushFront(i);
        return l;
    }

    // BENCHMARKS POR ESTRUCTURA DE LIST
    // Convención: bench_<metodo>_<Estructura>(n, run)
    // Cada variante es idéntica salvo por el tipo concreto que instancia

    // pushFront: O(1) en todas
    static void bench_pushFront_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "pushFront", n, run, new StructureFactory() {
            public Object create(int n) {
                return new SinglyLinkedListNoTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListNoTail<Integer>) e).pushFront(v);
            }
        });
    }

    static void bench_pushFront_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "pushFront", n, run, new StructureFactory() {
            public Object create(int n) {
                return new SinglyLinkedListTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListTail<Integer>) e).pushFront(v);
            }
        });
    }

    static void bench_pushFront_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "pushFront", n, run, new StructureFactory() {
            public Object create(int n) {
                return new DoublyLinkedListNoTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListNoTail<Integer>) e).pushFront(v);
            }
        });
    }

    static void bench_pushFront_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "pushFront", n, run, new StructureFactory() {
            public Object create(int n) {
                return new DoublyLinkedListWithTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListWithTail<Integer>) e).pushFront(v);
            }
        });
    }

    // pushBack: NoTail=O(n) porque recorre la lista, WithTail=O(1) con puntero tail
    static void bench_pushBack_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "pushBack", n, run, new StructureFactory() {
            public Object create(int n) {
                return new SinglyLinkedListNoTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListNoTail<Integer>) e).pushBack(v);
            }
        });
    }

    static void bench_pushBack_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "pushBack", n, run, new StructureFactory() {
            public Object create(int n) {
                return new SinglyLinkedListTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListTail<Integer>) e).pushBack(v);
            }
        });
    }

    static void bench_pushBack_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "pushBack", n, run, new StructureFactory() {
            public Object create(int n) {
                return new DoublyLinkedListNoTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListNoTail<Integer>) e).pushBack(v);
            }
        });
    }

    static void bench_pushBack_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "pushBack", n, run, new StructureFactory() {
            public Object create(int n) {
                return new DoublyLinkedListWithTail<Integer>();
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListWithTail<Integer>) e).pushBack(v);
            }
        });
    }

    // popFront: O(1) en todas. Pre-llenado con 2*N para que no se agote
    static void bench_popFront_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "popFront", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyNoTail(size * 2);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListNoTail<Integer> l = (SinglyLinkedListNoTail<Integer>) e;
                if (!l.empty())
                    l.popFront();
            }
        });
    }

    static void bench_popFront_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "popFront", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyWithTail(size * 2);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListTail<Integer> l = (SinglyLinkedListTail<Integer>) e;
                if (!l.empty())
                    l.popFront();
            }
        });
    }

    static void bench_popFront_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "popFront", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyNoTail(size * 2);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListNoTail<Integer> l = (DoublyLinkedListNoTail<Integer>) e;
                if (!l.isEmpty())
                    l.popFront();
            }
        });
    }

    static void bench_popFront_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "popFront", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyWithTail(size * 2);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListWithTail<Integer> l = (DoublyLinkedListWithTail<Integer>) e;
                if (!l.isEmpty())
                    l.popFront();
            }
        });
    }

    // popBack: DoublyWithTail=O(1) gracias a tail+prev. El resto=O(n) porque
    // sin prev hay que recorrer hasta el penúltimo
    static void bench_popBack_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "popBack", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyNoTail(size * 2);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListNoTail<Integer> l = (SinglyLinkedListNoTail<Integer>) e;
                if (!l.empty())
                    l.popBack();
            }
        });
    }

    static void bench_popBack_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "popBack", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyWithTail(size * 2);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListTail<Integer> l = (SinglyLinkedListTail<Integer>) e;
                if (!l.empty())
                    l.popBack();
            }
        });
    }

    static void bench_popBack_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "popBack", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyNoTail(size * 2);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListNoTail<Integer> l = (DoublyLinkedListNoTail<Integer>) e;
                if (!l.isEmpty())
                    l.popBack();
            }
        });
    }

    static void bench_popBack_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "popBack", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyWithTail(size * 2);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListWithTail<Integer> l = (DoublyLinkedListWithTail<Integer>) e;
                if (!l.isEmpty())
                    l.popBack();
            }
        });
    }

    // find: O(n) en todas. El valor buscado es v%size para garantizar
    // que siempre existe y que el recorrido sea real
    static void bench_find_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "find", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyNoTail(size);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListNoTail<Integer> l = (SinglyLinkedListNoTail<Integer>) e;
                l.find(v % l.size());
            }
        });
    }

    static void bench_find_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "find", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyWithTail(size);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListTail<Integer> l = (SinglyLinkedListTail<Integer>) e;
                l.find(v % l.size());
            }
        });
    }

    static void bench_find_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "find", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyNoTail(size);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListNoTail<Integer> l = (DoublyLinkedListNoTail<Integer>) e;
                l.find(v % l.size());
            }
        });
    }

    static void bench_find_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "find", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyWithTail(size);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListWithTail<Integer> l = (DoublyLinkedListWithTail<Integer>) e;
                l.find(v % l.size());
            }
        });
    }

    // erase: O(n) en todas. Pre-llenado con 2*N para compensar eliminaciones
    static void bench_erase_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "erase", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyNoTail(size * 2);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListNoTail<Integer> l = (SinglyLinkedListNoTail<Integer>) e;
                if (!l.empty())
                    l.erase(v % l.size());
            }
        });
    }

    static void bench_erase_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "erase", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillSinglyWithTail(size * 2);
            }

            public void apply(Object e, int v) {
                SinglyLinkedListTail<Integer> l = (SinglyLinkedListTail<Integer>) e;
                if (!l.empty())
                    l.erase(v % l.size());
            }
        });
    }

    static void bench_erase_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "erase", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyNoTail(size * 2);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListNoTail<Integer> l = (DoublyLinkedListNoTail<Integer>) e;
                if (!l.isEmpty())
                    l.erase(v % l.size());
            }
        });
    }

    static void bench_erase_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "erase", n, run, new StructureFactory() {
            public Object create(int size) {
                return fillDoublyWithTail(size * 2);
            }

            public void apply(Object e, int v) {
                DoublyLinkedListWithTail<Integer> l = (DoublyLinkedListWithTail<Integer>) e;
                if (!l.isEmpty())
                    l.erase(v % l.size());
            }
        });
    }

    // addBefore: O(n) en todas. SENTINEL al final hace que find recorra toda
    // la lista antes de encontrar el nodo de referencia
    static void bench_addBefore_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "addBefore", n, run, new StructureFactory() {
            public Object create(int size) {
                SinglyLinkedListNoTail<Integer> l = fillSinglyNoTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListNoTail<Integer>) e).addBefore(SENTINEL, v);
            }
        });
    }

    static void bench_addBefore_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "addBefore", n, run, new StructureFactory() {
            public Object create(int size) {
                SinglyLinkedListTail<Integer> l = fillSinglyWithTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListTail<Integer>) e).addBefore(SENTINEL, v);
            }
        });
    }

    static void bench_addBefore_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "addBefore", n, run, new StructureFactory() {
            public Object create(int size) {
                DoublyLinkedListNoTail<Integer> l = fillDoublyNoTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListNoTail<Integer>) e).addBefore(SENTINEL, v);
            }
        });
    }

    static void bench_addBefore_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "addBefore", n, run, new StructureFactory() {
            public Object create(int size) {
                DoublyLinkedListWithTail<Integer> l = fillDoublyWithTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListWithTail<Integer>) e).addBefore(SENTINEL, v);
            }
        });
    }

    // addAfter: O(n) en todas. Mismo patrón SENTINEL que addBefore
    static void bench_addAfter_SinglyNoTail(int n, int run) {
        exec("SinglyNoTail", "addAfter", n, run, new StructureFactory() {
            public Object create(int size) {
                SinglyLinkedListNoTail<Integer> l = fillSinglyNoTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListNoTail<Integer>) e).addAfter(SENTINEL, v);
            }
        });
    }

    static void bench_addAfter_SinglyWithTail(int n, int run) {
        exec("SinglyWithTail", "addAfter", n, run, new StructureFactory() {
            public Object create(int size) {
                SinglyLinkedListTail<Integer> l = fillSinglyWithTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((SinglyLinkedListTail<Integer>) e).addAfter(SENTINEL, v);
            }
        });
    }

    static void bench_addAfter_DoublyNoTail(int n, int run) {
        exec("DoublyNoTail", "addAfter", n, run, new StructureFactory() {
            public Object create(int size) {
                DoublyLinkedListNoTail<Integer> l = fillDoublyNoTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListNoTail<Integer>) e).addAfter(SENTINEL, v);
            }
        });
    }

    static void bench_addAfter_DoublyWithTail(int n, int run) {
        exec("DoublyWithTail", "addAfter", n, run, new StructureFactory() {
            public Object create(int size) {
                DoublyLinkedListWithTail<Integer> l = fillDoublyWithTail(size);
                l.pushBack(SENTINEL);
                return l;
            }

            public void apply(Object e, int v) {
                ((DoublyLinkedListWithTail<Integer>) e).addAfter(SENTINEL, v);
            }
        });
    }

    // BENCHMARKS DE STACK
    // push/pop/peek son O(1) amortizado. delete es O(n) por búsqueda lineal

    static void bench_push_Stack(int n, int run) {
        exec("StackArray", "push", n, run, new StructureFactory() {
            public Object create(int n) {
                return new DynamicArrayStack<Integer>();
            }

            public void apply(Object e, int v) {
                ((DynamicArrayStack<Integer>) e).push(v);
            }
        });
    }

    static void bench_pop_Stack(int n, int run) {
        exec("StackArray", "pop", n, run, new StructureFactory() {
            public Object create(int size) {
                DynamicArrayStack<Integer> s = new DynamicArrayStack<>();
                for (int i = 0; i < size * 2; i++)
                    s.push(RANDOM.nextInt(1_000_000));
                return s;
            }

            public void apply(Object e, int v) {
                DynamicArrayStack<Integer> s = (DynamicArrayStack<Integer>) e;
                if (!s.isEmpty())
                    s.pop();
            }
        });
    }

    static void bench_peek_Stack(int n, int run) {
        exec("StackArray", "peek", n, run, new StructureFactory() {
            public Object create(int size) {
                DynamicArrayStack<Integer> s = new DynamicArrayStack<>();
                for (int i = 0; i < size; i++)
                    s.push(RANDOM.nextInt(1_000_000));
                return s;
            }

            public void apply(Object e, int v) {
                DynamicArrayStack<Integer> s = (DynamicArrayStack<Integer>) e;
                if (!s.isEmpty())
                    s.peek();
            }
        });
    }

    static void bench_delete_Stack(int n, int run) {
        exec("StackArray", "delete", n, run, new StructureFactory() {
            public Object create(int size) {
                DynamicArrayStack<Integer> s = new DynamicArrayStack<>();
                for (int i = 0; i < size * 2; i++)
                    s.push(i);
                return s;
            }

            public void apply(Object e, int v) {
                DynamicArrayStack<Integer> s = (DynamicArrayStack<Integer>) e;
                if (!s.isEmpty())
                    s.delete(v % s.size());
            }
        });
    }

    // BENCHMARKS DE QUEUE
    // enqueue/dequeue/front son O(1) amortizado. delete es O(n)

    static void bench_enqueue_Queue(int n, int run) {
        exec("QueueArray", "enqueue", n, run, new StructureFactory() {
            public Object create(int n) {
                return new MyQueue<Integer>();
            }

            public void apply(Object e, int v) {
                ((MyQueue<Integer>) e).enqueue(v);
            }
        });
    }

    static void bench_dequeue_Queue(int n, int run) {
        exec("QueueArray", "dequeue", n, run, new StructureFactory() {
            public Object create(int size) {
                MyQueue<Integer> q = new MyQueue<>();
                for (int i = 0; i < size * 2; i++)
                    q.enqueue(RANDOM.nextInt(1_000_000));
                return q;
            }

            public void apply(Object e, int v) {
                MyQueue<Integer> q = (MyQueue<Integer>) e;
                if (!q.isEmpty())
                    q.dequeue();
            }
        });
    }

    static void bench_front_Queue(int n, int run) {
        exec("QueueArray", "front", n, run, new StructureFactory() {
            public Object create(int size) {
                MyQueue<Integer> q = new MyQueue<>();
                for (int i = 0; i < size; i++)
                    q.enqueue(RANDOM.nextInt(1_000_000));
                return q;
            }

            public void apply(Object e, int v) {
                MyQueue<Integer> q = (MyQueue<Integer>) e;
                if (!q.isEmpty())
                    q.front();
            }
        });
    }

    static void bench_delete_Queue(int n, int run) {
        exec("QueueArray", "delete", n, run, new StructureFactory() {
            public Object create(int size) {
                MyQueue<Integer> q = new MyQueue<>();
                for (int i = 0; i < size * 2; i++)
                    q.enqueue(i);
                return q;
            }

            public void apply(Object e, int v) {
                MyQueue<Integer> q = (MyQueue<Integer>) e;
                if (!q.isEmpty())
                    q.delete(v % q.size());
            }
        });
    }

    // PARTE 1 - Runners por estructura de List
    // Cada uno corre todos los métodos de esa implementación sola.
    // Los métodos O(n) usan SIZES_LINEAR; los O(1) usan SIZES completo

    private static void runList_SinglyNoTail() {
        System.out.println("\nParte 1 - SinglyLinkedListNoTail");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushFront_SinglyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushBack_SinglyNoTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popFront_SinglyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popBack_SinglyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_find_SinglyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_erase_SinglyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addBefore_SinglyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addAfter_SinglyNoTail(n, r);
    }

    private static void runList_SinglyWithTail() {
        System.out.println("\nParte 1 - SinglyLinkedListTail");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushFront_SinglyWithTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushBack_SinglyWithTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popFront_SinglyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popBack_SinglyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_find_SinglyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_erase_SinglyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addBefore_SinglyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addAfter_SinglyWithTail(n, r);
    }

    private static void runList_DoublyNoTail() {
        System.out.println("\nParte 1 - DoublyLinkedListNoTail");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushFront_DoublyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushBack_DoublyNoTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popFront_DoublyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popBack_DoublyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_find_DoublyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_erase_DoublyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addBefore_DoublyNoTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addAfter_DoublyNoTail(n, r);
    }

    private static void runList_DoublyWithTail() {
        System.out.println("\nParte 1 - DoublyLinkedListWithTail");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushFront_DoublyWithTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pushBack_DoublyWithTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popFront_DoublyWithTail(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_popBack_DoublyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_find_DoublyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_erase_DoublyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addBefore_DoublyWithTail(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_addAfter_DoublyWithTail(n, r);
    }

    // PARTE 2 - Runners de Stack y Queue por separado

    private static void runStack() {
        System.out.println("\nParte 2 - DynamicArrayStack");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_push_Stack(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_pop_Stack(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_peek_Stack(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_delete_Stack(n, r);
    }

    private static void runQueue() {
        System.out.println("\nParte 2 - MyQueue");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_enqueue_Queue(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_dequeue_Queue(n, r);
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++)
                bench_front_Queue(n, r);
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++)
                bench_delete_Queue(n, r);
    }

    // PARTE 3 - Comparativas List vs Stack/Queue por método equivalente
    // Se elige la implementación de List más óptima para cada método:
    // pushFront -> SinglyNoTail (O(1), mínimo overhead, sin prev ni tail)
    // pushBack -> SinglyWithTail o DoublyWithTail (O(1) con tail)
    // popFront -> SinglyNoTail (O(1), más simple)
    // popBack -> DoublyWithTail (única con O(1) real gracias a prev+tail)
    // find/erase -> DoublyNoTail (O(n) en todas, pero prev acelera el re-enlace)

    private static void runComparativa_pushFront_vs_push() {
        System.out.println("\nParte 3 - pushFront (SinglyNoTail) vs push (Stack)");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++) {
                bench_pushFront_SinglyNoTail(n, r);
                bench_push_Stack(n, r);
            }
    }

    private static void runComparativa_pushBack_vs_enqueue() {
        System.out.println("\nParte 3 - pushBack (DoublyWithTail) vs enqueue (Queue)");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++) {
                bench_pushBack_DoublyWithTail(n, r);
                bench_enqueue_Queue(n, r);
            }
    }

    private static void runComparativa_popFront_vs_pop() {
        System.out.println("\nParte 3 - popFront (SinglyNoTail) vs pop (Stack)");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++) {
                bench_popFront_SinglyNoTail(n, r);
                bench_pop_Stack(n, r);
            }
    }

    private static void runComparativa_popBack_vs_dequeue() {
        System.out.println("\nParte 3 - popBack (DoublyWithTail) vs dequeue (Queue)");
        for (int n : SIZES)
            for (int r = 0; r < REPETICIONES; r++) {
                bench_popBack_DoublyWithTail(n, r);
                bench_dequeue_Queue(n, r);
            }
    }

    private static void runComparativa_find_vs_peek() {
        System.out.println("\nParte 3 - find (DoublyNoTail) vs peek (Stack) vs front (Queue)");
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++) {
                bench_find_DoublyNoTail(n, r);
                bench_peek_Stack(n, r);
                bench_front_Queue(n, r);
            }
    }

    private static void runComparativa_erase_vs_delete() {
        System.out.println("\nParte 3 - erase (DoublyNoTail) vs delete (Stack) vs delete (Queue)");
        for (int n : SIZES_LINEAR)
            for (int r = 0; r < REPETICIONES; r++) {
                bench_erase_DoublyNoTail(n, r);
                bench_delete_Stack(n, r);
                bench_delete_Queue(n, r);
            }
    }

    // WARM-UP GLOBAL
    // Ejercita todos los hot-paths para que el JIT compile el bytecode
    // antes de la primera medición. Sin esto los primeros runs son 5-10x
    // más lentos por ejecutar en modo interpretado

    private static void globalWarmup() {
        System.out.println("[Warmup] Calentando JVM (" + GLOBAL_WARMUP_OPS + " ops)...");

        SinglyLinkedListNoTail<Integer> snt = new SinglyLinkedListNoTail<>();
        SinglyLinkedListTail<Integer> swt = new SinglyLinkedListTail<>();
        DoublyLinkedListNoTail<Integer> dnt = new DoublyLinkedListNoTail<>();
        DoublyLinkedListWithTail<Integer> dwt = new DoublyLinkedListWithTail<>();

        for (int i = 0; i < GLOBAL_WARMUP_OPS; i++) {
            snt.pushFront(i);
            swt.pushFront(i);
            dnt.pushFront(i);
            dwt.pushFront(i);
            if (i % 5 == 0) {
                snt.pushBack(i);
                swt.pushBack(i);
                dnt.pushBack(i);
                dwt.pushBack(i);
            }
            if (i % 3 == 0) {
                if (!snt.empty())
                    snt.popFront();
                if (!swt.empty())
                    swt.popFront();
                if (!dnt.isEmpty())
                    dnt.popFront();
                if (!dwt.isEmpty())
                    dwt.popFront();
            }
            if (i % 7 == 0) {
                if (!snt.empty())
                    snt.popBack();
                if (!swt.empty())
                    swt.popBack();
                if (!dnt.isEmpty())
                    dnt.popBack();
                if (!dwt.isEmpty())
                    dwt.popBack();
            }
        }

        DynamicArrayStack<Integer> stack = new DynamicArrayStack<>();
        MyQueue<Integer> queue = new MyQueue<>();
        for (int i = 0; i < GLOBAL_WARMUP_OPS; i++) {
            stack.push(i);
            queue.enqueue(i);
            if (i % 3 == 0 && !stack.isEmpty())
                stack.pop();
            if (i % 3 == 0 && !queue.isEmpty())
                queue.dequeue();
        }

        System.out.println("[Warmup] Listo.\n");
    }

    // EXPORT Y GRAFICACION

    private static void exportToCSV() {
        try (PrintWriter w = new PrintWriter(new FileWriter(CSV_FILE))) {
            w.print(CSV.toString());
            System.out.println("\n[CSV] Exportado -> " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("[CSV] Error: " + e.getMessage());
        }
    }

    // El graficador se lanza DESPUÉS de exportar para no contaminar
    // las mediciones con el overhead de matplotlib
    private static void runPythonScript() {
        System.out.println("[Python] Ejecutando " + PY_SCRIPT + " ...");
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", PY_SCRIPT);
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

    // MENU PRINCIPAL
    // Organizado en las 3 partes del análisis del enunciado

    public static void main(String[] args) {

        globalWarmup();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nBENCHMARK - Listas, Pilas y Colas | ED 2026-I");
            System.out.println();
            System.out.println("PARTE 1 - Cada estructura de List por separado");
            System.out.println("  1. SinglyLinkedListNoTail   (todos los metodos)");
            System.out.println("  2. SinglyLinkedListTail     (todos los metodos)");
            System.out.println("  3. DoublyLinkedListNoTail   (todos los metodos)");
            System.out.println("  4. DoublyLinkedListWithTail (todos los metodos)");
            System.out.println();
            System.out.println("PARTE 2 - Stack y Queue por separado");
            System.out.println("  5. DynamicArrayStack        (todos los metodos)");
            System.out.println("  6. MyQueue                  (todos los metodos)");
            System.out.println();
            System.out.println("PARTE 3 - Comparativa List vs Stack/Queue (mejor List vs equivalente)");
            System.out.println("  7.  pushFront  (SinglyNoTail)   vs  push   (Stack)");
            System.out.println("  8.  pushBack   (DoublyWithTail) vs  enqueue (Queue)");
            System.out.println("  9.  popFront   (SinglyNoTail)   vs  pop    (Stack)");
            System.out.println("  10. popBack    (DoublyWithTail) vs  dequeue (Queue)");
            System.out.println("  11. find       (DoublyNoTail)   vs  peek   (Stack) vs front (Queue)");
            System.out.println("  12. erase      (DoublyNoTail)   vs  delete (Stack) vs delete (Queue)");
            System.out.println();
            System.out.println("UTILIDADES");
            System.out.println("  13. Correr todo (parte 1 + 2 + 3)");
            System.out.println("  14. Exportar CSV y graficar");
            System.out.println("   0. Salir");
            System.out.println();
            System.out.print("Opcion: ");

            int op = sc.nextInt();

            switch (op) {
                case 0:
                    exportToCSV();
                    System.out.println("Hasta luego.");
                    sc.close();
                    return;
                case 1:
                    runList_SinglyNoTail();
                    break;
                case 2:
                    runList_SinglyWithTail();
                    break;
                case 3:
                    runList_DoublyNoTail();
                    break;
                case 4:
                    runList_DoublyWithTail();
                    break;
                case 5:
                    runStack();
                    break;
                case 6:
                    runQueue();
                    break;
                case 7:
                    runComparativa_pushFront_vs_push();
                    break;
                case 8:
                    runComparativa_pushBack_vs_enqueue();
                    break;
                case 9:
                    runComparativa_popFront_vs_pop();
                    break;
                case 10:
                    runComparativa_popBack_vs_dequeue();
                    break;
                case 11:
                    runComparativa_find_vs_peek();
                    break;
                case 12:
                    runComparativa_erase_vs_delete();
                    break;
                case 13:
                    System.out.println("\nCorriendo analisis completo...");
                    runList_SinglyNoTail();
                    runList_SinglyWithTail();
                    runList_DoublyNoTail();
                    runList_DoublyWithTail();
                    runStack();
                    runQueue();
                    runComparativa_pushFront_vs_push();
                    runComparativa_pushBack_vs_enqueue();
                    runComparativa_popFront_vs_pop();
                    runComparativa_popBack_vs_dequeue();
                    runComparativa_find_vs_peek();
                    runComparativa_erase_vs_delete();
                    exportToCSV();
                    runPythonScript();
                    break;
                case 14:
                    exportToCSV();
                    runPythonScript();
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }
}