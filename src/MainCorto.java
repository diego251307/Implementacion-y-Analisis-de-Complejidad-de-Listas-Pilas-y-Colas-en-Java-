import java.io.*;
import java.util.*;
import SinglyLinkedList.*;
import DoublyLinkedList.*;
import Stack.*;
import Queue.*;

public class Main {

    static final int[] SIZES = { 10, 100, 1_000, 10_000, 100_000, 1_000_000 };
    static final int[] SIZES_LINEAR = { 10, 100, 1_000, 10_000, 100_000 };
    static final int REPS = 7;
    static final Random RND = new Random(42);
    static final int SENTINEL = -1;

    static final StringBuilder CSV = new StringBuilder("Estructura,Metodo,N,Run,TiempoPromedio_ns\n");

    // Mide n operaciones y guarda el resultado en el CSV
    static void medir(String est, String met, int n, int run,
            Runnable setup, Runnable op) {
        setup.run(); // crea/pre-llena la estructura fuera del cronómetro
        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++)
            op.run();
        double ns = (double) (System.nanoTime() - t0) / n;
        CSV.append(est).append(',').append(met).append(',')
                .append(n).append(',').append(run).append(',')
                .append(String.format(Locale.US, "%.4f", ns)).append('\n');
        System.out.printf("[%-18s] %-12s N=%-8d run%d %.3f ns/op%n", est, met, n, run, ns);
    }

    // Corre REPS veces el mismo benchmark sobre el array de tamaños dado
    static void correr(String est, String met, int[] sizes,
            java.util.function.IntSupplier makeList,
            java.util.function.IntConsumer opFn) {
        // warm-up: 50_000 ops para que el JIT compile antes de medir
        for (int i = 0; i < 50_000; i++) {
            makeList.getAsInt();
            opFn.accept(RND.nextInt());
        }

        for (int n : sizes)
            for (int r = 0; r < REPS; r++) {
                // usamos un array de 1 elemento como "puntero" mutable al objeto
                final Object[] ref = { null };
                medir(est, met, n, r,
                        () -> ref[0] = makeList.getAsInt(), // setup
                        () -> opFn.accept(RND.nextInt()) // op
                );
            }
    }

    static void exportar() throws Exception {
        try (PrintWriter w = new PrintWriter(new FileWriter("datos.csv"))) {
            w.print(CSV);
            System.out.println("CSV exportado.");
        }
    }

    // CADA METODO es un bloque compacto de ~5 lineas
    // Estructura: crea la lista, define la operacion, llama a correr()

    // pushFront
    static void pushFront_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "pushFront", SIZES,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    return 0;
                },
                v -> r[0].pushFront(v));
    }

    static void pushFront_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "pushFront", SIZES,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    return 0;
                },
                v -> r[0].pushFront(v));
    }

    static void pushFront_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "pushFront", SIZES,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    return 0;
                },
                v -> r[0].pushFront(v));
    }

    static void pushFront_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "pushFront", SIZES,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    return 0;
                },
                v -> r[0].pushFront(v));
    }

    // pushBack
    static void pushBack_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "pushBack", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    return 0;
                },
                v -> r[0].pushBack(v));
    }

    static void pushBack_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "pushBack", SIZES,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    return 0;
                },
                v -> r[0].pushBack(v));
    }

    static void pushBack_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "pushBack", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    return 0;
                },
                v -> r[0].pushBack(v));
    }

    static void pushBack_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "pushBack", SIZES,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    return 0;
                },
                v -> r[0].pushBack(v));
    }

    // popFront (lista pre-llenada con 2*N)
    static void popFront_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "popFront", SIZES,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].empty())
                        r[0].popFront();
                });
    }

    static void popFront_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "popFront", SIZES,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].empty())
                        r[0].popFront();
                });
    }

    static void popFront_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "popFront", SIZES,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].popFront();
                });
    }

    static void popFront_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "popFront", SIZES,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].popFront();
                });
    }

    // popBack (lista pre-llenada)
    static void popBack_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "popBack", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].empty())
                        r[0].popBack();
                });
    }

    static void popBack_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "popBack", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].empty())
                        r[0].popBack();
                });
    }

    static void popBack_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "popBack", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].popBack();
                });
    }

    static void popBack_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "popBack", SIZES,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].popBack();
                });
    }

    // find (lista pre-llenada con valores 0..N-1, busca v%size)
    static void find_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "find", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> r[0].find(Math.abs(v) % r[0].size()));
    }

    static void find_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "find", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> r[0].find(Math.abs(v) % r[0].size()));
    }

    static void find_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "find", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> r[0].find(Math.abs(v) % r[0].size()));
    }

    static void find_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "find", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> r[0].find(Math.abs(v) % r[0].size()));
    }

    // erase (lista pre-llenada con 2*N valores únicos)
    static void erase_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "erase", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].empty())
                        r[0].erase(Math.abs(v) % r[0].size());
                });
    }

    static void erase_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "erase", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].empty())
                        r[0].erase(Math.abs(v) % r[0].size());
                });
    }

    static void erase_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "erase", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].erase(Math.abs(v) % r[0].size());
                });
    }

    static void erase_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "erase", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].pushFront(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].erase(Math.abs(v) % r[0].size());
                });
    }

    // addBefore / addAfter (SENTINEL al final para que find recorra toda la lista)
    static void addBefore_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "addBefore", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addBefore(SENTINEL, v));
    }

    static void addBefore_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "addBefore", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addBefore(SENTINEL, v));
    }

    static void addBefore_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "addBefore", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addBefore(SENTINEL, v));
    }

    static void addBefore_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "addBefore", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addBefore(SENTINEL, v));
    }

    static void addAfter_SinglyNoTail() {
        SinglyLinkedListNoTail<Integer>[] r = new SinglyLinkedListNoTail[1];
        correr("SinglyNoTail", "addAfter", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListNoTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addAfter(SENTINEL, v));
    }

    static void addAfter_SinglyWithTail() {
        SinglyLinkedListTail<Integer>[] r = new SinglyLinkedListTail[1];
        correr("SinglyWithTail", "addAfter", SIZES_LINEAR,
                () -> {
                    r[0] = new SinglyLinkedListTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addAfter(SENTINEL, v));
    }

    static void addAfter_DoublyNoTail() {
        DoublyLinkedListNoTail<Integer>[] r = new DoublyLinkedListNoTail[1];
        correr("DoublyNoTail", "addAfter", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListNoTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addAfter(SENTINEL, v));
    }

    static void addAfter_DoublyWithTail() {
        DoublyLinkedListWithTail<Integer>[] r = new DoublyLinkedListWithTail[1];
        correr("DoublyWithTail", "addAfter", SIZES_LINEAR,
                () -> {
                    r[0] = new DoublyLinkedListWithTail<>();
                    for (int i = 0; i < 100_000; i++)
                        r[0].pushFront(i);
                    r[0].pushBack(SENTINEL);
                    return 0;
                },
                v -> r[0].addAfter(SENTINEL, v));
    }

    // Stack
    static void push_Stack() {
        DynamicArrayStack<Integer>[] r = new DynamicArrayStack[1];
        correr("StackArray", "push", SIZES,
                () -> {
                    r[0] = new DynamicArrayStack<>();
                    return 0;
                },
                v -> r[0].push(v));
    }

    static void pop_Stack() {
        DynamicArrayStack<Integer>[] r = new DynamicArrayStack[1];
        correr("StackArray", "pop", SIZES,
                () -> {
                    r[0] = new DynamicArrayStack<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].push(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].pop();
                });
    }

    static void peek_Stack() {
        DynamicArrayStack<Integer>[] r = new DynamicArrayStack[1];
        correr("StackArray", "peek", SIZES,
                () -> {
                    r[0] = new DynamicArrayStack<>();
                    for (int i = 0; i < 1_000_000; i++)
                        r[0].push(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].peek();
                });
    }

    static void delete_Stack() {
        DynamicArrayStack<Integer>[] r = new DynamicArrayStack[1];
        correr("StackArray", "delete", SIZES_LINEAR,
                () -> {
                    r[0] = new DynamicArrayStack<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].push(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].delete(Math.abs(v) % r[0].size());
                });
    }

    // Queue
    static void enqueue_Queue() {
        MyQueue<Integer>[] r = new MyQueue[1];
        correr("QueueArray", "enqueue", SIZES,
                () -> {
                    r[0] = new MyQueue<>();
                    return 0;
                },
                v -> r[0].enqueue(v));
    }

    static void dequeue_Queue() {
        MyQueue<Integer>[] r = new MyQueue[1];
        correr("QueueArray", "dequeue", SIZES,
                () -> {
                    r[0] = new MyQueue<>();
                    for (int i = 0; i < 2_000_000; i++)
                        r[0].enqueue(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].dequeue();
                });
    }

    static void front_Queue() {
        MyQueue<Integer>[] r = new MyQueue[1];
        correr("QueueArray", "front", SIZES,
                () -> {
                    r[0] = new MyQueue<>();
                    for (int i = 0; i < 1_000_000; i++)
                        r[0].enqueue(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].front();
                });
    }

    static void delete_Queue() {
        MyQueue<Integer>[] r = new MyQueue[1];
        correr("QueueArray", "delete", SIZES_LINEAR,
                () -> {
                    r[0] = new MyQueue<>();
                    for (int i = 0; i < 200_000; i++)
                        r[0].enqueue(i);
                    return 0;
                },
                v -> {
                    if (!r[0].isEmpty())
                        r[0].delete(Math.abs(v) % r[0].size());
                });
    }

    // MAIN: recibe el nombre del metodo como argumento
    // Uso: java Main pushFront_SinglyNoTail
    // java Main pushBack_DoublyNoTail
    // java Main todo
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java Main <metodo>");
            System.out.println("Metodos disponibles:");
            System.out.println(
                    "  pushFront_SinglyNoTail  pushFront_SinglyWithTail  pushFront_DoublyNoTail  pushFront_DoublyWithTail");
            System.out.println(
                    "  pushBack_SinglyNoTail   pushBack_SinglyWithTail   pushBack_DoublyNoTail   pushBack_DoublyWithTail");
            System.out.println(
                    "  popFront_SinglyNoTail   popFront_SinglyWithTail   popFront_DoublyNoTail   popFront_DoublyWithTail");
            System.out.println(
                    "  popBack_SinglyNoTail    popBack_SinglyWithTail    popBack_DoublyNoTail    popBack_DoublyWithTail");
            System.out.println(
                    "  find_SinglyNoTail       find_SinglyWithTail       find_DoublyNoTail       find_DoublyWithTail");
            System.out.println(
                    "  erase_SinglyNoTail      erase_SinglyWithTail      erase_DoublyNoTail      erase_DoublyWithTail");
            System.out.println(
                    "  addBefore_SinglyNoTail  addBefore_SinglyWithTail  addBefore_DoublyNoTail  addBefore_DoublyWithTail");
            System.out.println(
                    "  addAfter_SinglyNoTail   addAfter_SinglyWithTail   addAfter_DoublyNoTail   addAfter_DoublyWithTail");
            System.out.println("  push_Stack  pop_Stack  peek_Stack  delete_Stack");
            System.out.println("  enqueue_Queue  dequeue_Queue  front_Queue  delete_Queue");
            System.out.println("  todo");
            return;
        }

        switch (args[0]) {
            case "pushFront_SinglyNoTail":
                pushFront_SinglyNoTail();
                break;
            case "pushFront_SinglyWithTail":
                pushFront_SinglyWithTail();
                break;
            case "pushFront_DoublyNoTail":
                pushFront_DoublyNoTail();
                break;
            case "pushFront_DoublyWithTail":
                pushFront_DoublyWithTail();
                break;
            case "pushBack_SinglyNoTail":
                pushBack_SinglyNoTail();
                break;
            case "pushBack_SinglyWithTail":
                pushBack_SinglyWithTail();
                break;
            case "pushBack_DoublyNoTail":
                pushBack_DoublyNoTail();
                break;
            case "pushBack_DoublyWithTail":
                pushBack_DoublyWithTail();
                break;
            case "popFront_SinglyNoTail":
                popFront_SinglyNoTail();
                break;
            case "popFront_SinglyWithTail":
                popFront_SinglyWithTail();
                break;
            case "popFront_DoublyNoTail":
                popFront_DoublyNoTail();
                break;
            case "popFront_DoublyWithTail":
                popFront_DoublyWithTail();
                break;
            case "popBack_SinglyNoTail":
                popBack_SinglyNoTail();
                break;
            case "popBack_SinglyWithTail":
                popBack_SinglyWithTail();
                break;
            case "popBack_DoublyNoTail":
                popBack_DoublyNoTail();
                break;
            case "popBack_DoublyWithTail":
                popBack_DoublyWithTail();
                break;
            case "find_SinglyNoTail":
                find_SinglyNoTail();
                break;
            case "find_SinglyWithTail":
                find_SinglyWithTail();
                break;
            case "find_DoublyNoTail":
                find_DoublyNoTail();
                break;
            case "find_DoublyWithTail":
                find_DoublyWithTail();
                break;
            case "erase_SinglyNoTail":
                erase_SinglyNoTail();
                break;
            case "erase_SinglyWithTail":
                erase_SinglyWithTail();
                break;
            case "erase_DoublyNoTail":
                erase_DoublyNoTail();
                break;
            case "erase_DoublyWithTail":
                erase_DoublyWithTail();
                break;
            case "addBefore_SinglyNoTail":
                addBefore_SinglyNoTail();
                break;
            case "addBefore_SinglyWithTail":
                addBefore_SinglyWithTail();
                break;
            case "addBefore_DoublyNoTail":
                addBefore_DoublyNoTail();
                break;
            case "addBefore_DoublyWithTail":
                addBefore_DoublyWithTail();
                break;
            case "addAfter_SinglyNoTail":
                addAfter_SinglyNoTail();
                break;
            case "addAfter_SinglyWithTail":
                addAfter_SinglyWithTail();
                break;
            case "addAfter_DoublyNoTail":
                addAfter_DoublyNoTail();
                break;
            case "addAfter_DoublyWithTail":
                addAfter_DoublyWithTail();
                break;
            case "push_Stack":
                push_Stack();
                break;
            case "pop_Stack":
                pop_Stack();
                break;
            case "peek_Stack":
                peek_Stack();
                break;
            case "delete_Stack":
                delete_Stack();
                break;
            case "enqueue_Queue":
                enqueue_Queue();
                break;
            case "dequeue_Queue":
                dequeue_Queue();
                break;
            case "front_Queue":
                front_Queue();
                break;
            case "delete_Queue":
                delete_Queue();
                break;
            case "todo":
                pushFront_SinglyNoTail();
                pushFront_SinglyWithTail();
                pushFront_DoublyNoTail();
                pushFront_DoublyWithTail();
                pushBack_SinglyNoTail();
                pushBack_SinglyWithTail();
                pushBack_DoublyNoTail();
                pushBack_DoublyWithTail();
                popFront_SinglyNoTail();
                popFront_SinglyWithTail();
                popFront_DoublyNoTail();
                popFront_DoublyWithTail();
                popBack_SinglyNoTail();
                popBack_SinglyWithTail();
                popBack_DoublyNoTail();
                popBack_DoublyWithTail();
                find_SinglyNoTail();
                find_SinglyWithTail();
                find_DoublyNoTail();
                find_DoublyWithTail();
                erase_SinglyNoTail();
                erase_SinglyWithTail();
                erase_DoublyNoTail();
                erase_DoublyWithTail();
                addBefore_SinglyNoTail();
                addBefore_SinglyWithTail();
                addBefore_DoublyNoTail();
                addBefore_DoublyWithTail();
                addAfter_SinglyNoTail();
                addAfter_SinglyWithTail();
                addAfter_DoublyNoTail();
                addAfter_DoublyWithTail();
                push_Stack();
                pop_Stack();
                peek_Stack();
                delete_Stack();
                enqueue_Queue();
                dequeue_Queue();
                front_Queue();
                delete_Queue();
                break;
            default:
                System.out.println("Metodo no reconocido: " + args[0]);
                return;
        }

        exportar();
    }
}