package Stack;

/**
 * Implementación genérica de una pila (Stack) usando un arreglo dinámico.
 * 
 * - Estructura LIFO: el último elemento en entrar es el primero en salir.
 * - Internamente se almacena en un Object[] por limitaciones de genéricos en
 * Java.
 * 
 * @param <T> Tipo de dato que almacenará la pila.
 */
public class MyStack<T> {

    /** Arreglo interno donde se guardan los elementos de la pila. */
    private Object[] data;

    /** Cantidad de elementos actualmente almacenados en la pila. */
    private int size;

    /**
     * Capacidad actual del arreglo interno (cuántos elementos puede soportar antes
     * de crecer).
     */
    private int capacity;

    /** Capacidad inicial por defecto al crear una pila nueva. */
    private static final int INITIAL_CAPACITY = 8;

    /**
     * Constructor: inicializa la pila vacía con capacidad inicial.
     */
    public MyStack() {
        this.capacity = INITIAL_CAPACITY;
        this.data = new Object[capacity];
        this.size = 0;
    }

    /**
     * Duplica la capacidad del arreglo cuando se queda sin espacio.
     * Copia los elementos existentes al nuevo arreglo.
     * 
     * Complejidad: O(n) por la copia de elementos.
     */
    private void resize() {
        capacity = capacity * 2;
        Object[] newData = new Object[capacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    /**
     * Inserta un elemento en la cima de la pila.
     * Si el arreglo está lleno, primero redimensiona.
     * 
     * Complejidad amortizada: O(1) (aunque el resize es O(n) cuando ocurre).
     */
    public void push(T x) {
        if (size == capacity) {
            resize();
        }
        data[size] = x;
        size++;
    }

    /**
     * Elimina y retorna el elemento en la cima de la pila.
     * 
     * @return elemento en la cima
     * @throws RuntimeException si la pila está vacía
     * 
     *                          Complejidad: O(1)
     */
    public T pop() {
        if (isEmpty())
            throw new RuntimeException("Pila vacía");

        // La cima está en el índice size - 1
        T top = (T) data[size - 1];

        // Se limpia la referencia para ayudar al recolector de basura (evita
        // "loitering")
        data[size - 1] = null;

        // Se reduce el tamaño lógico de la pila
        size--;
        return top;
    }

    /**
     * Retorna el elemento en la cima sin eliminarlo.
     * 
     * @return elemento en la cima
     * @throws RuntimeException si la pila está vacía
     * 
     *                          Complejidad: O(1)
     */
    public T peek() {
        if (isEmpty())
            throw new RuntimeException("Pila vacía");
        return (T) data[size - 1];
    }

    /**
     * Indica si la pila no contiene elementos.
     * 
     * @return true si está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna cuántos elementos hay actualmente en la pila.
     */
    public int size() {
        return size;
    }

    /**
     * Elimina la primera ocurrencia del elemento n buscando desde la cima hacia el
     * fondo.
     * 
     * - Recorre desde el último elemento (cima) hasta el primero (fondo).
     * - Si lo encuentra, "corre" los elementos para cerrar el hueco.
     * 
     * Nota: Esto no es una operación típica de una pila estricta, pero se incluye
     * como utilidad.
     * 
     * Complejidad: O(n) por la búsqueda + posible desplazamiento.
     */
    public void delete(T n) {
        // Buscar desde la cima hacia abajo para borrar la ocurrencia más "reciente"
        for (int i = size - 1; i >= 0; i--) {
            if (((T) data[i]).equals(n)) {
                // Desplazar elementos una posición a la izquierda para cubrir el eliminado
                for (int j = i; j < size - 1; j++) {
                    data[j] = data[j + 1];
                }
                // Limpiar la última posición (que queda duplicada tras el corrimiento)
                data[size - 1] = null;
                size--;
                return; // Solo elimina una ocurrencia
            }
        }
    }

    /**
     * Retorna la capacidad actual del arreglo interno.
     * (No confundir con size(), que es la cantidad de elementos reales
     * almacenados.)
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Representación en texto de la pila, mostrando la cima primero y el fondo al
     * final.
     */
    public String toString() {
        if (isEmpty())
            return "Pila vacía []";

        StringBuilder sb = new StringBuilder("Cima → [");
        // Se imprime desde la cima hacia el fondo para visualizar el orden LIFO
        for (int i = size - 1; i >= 0; i--) {
            sb.append(data[i]);
            if (i > 0)
                sb.append(", ");
        }
        sb.append("] ← Fondo");
        return sb.toString();
    }
}