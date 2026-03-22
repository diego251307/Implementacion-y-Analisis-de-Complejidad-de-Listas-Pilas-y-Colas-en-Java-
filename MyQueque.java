public class MyQueque<T extends Comparable<T>> {

    private T[] arreglo;
    private int size=0;

    @SuppressWarnings("unchecked")
    public MyQueque(){
        arreglo = (T[])new Object[2];
    }
    // enqueque - Julian
    @SuppressWarnings("unchecked")
    public void enqueque(T x){
        if(size>=arreglo.length){
            T[] nArreglo = (T[])new Object[arreglo.length*2]
            for (int i=0;i<arreglo.length;i++){
                nArreglo[i]=arreglo[i];
            }
            arreglo=nArreglo;
        }
        arreglo[size]=x;
        size++;
    }
    // dequeque - Diego
    public T dequeue(){
        T dq = arreglo[0];
        for (int i = 0; i<size, i++){
            arreglo[i] == arreglo[i+1];
        }
        size--;
        return dq;
    }
    // front - Diego
    public T front(){
        if (!isEmpty()){
            return arreglo[0];
        }
    }
    // isEmpty - Julian
    public bool isEmpty(){
        return size<=0;
    }
    // size - Diego
    public int size(){
        return size;
    }
    // delete - Julian
    public void delete(T n){
        bool aux=false;
        for (int i=0;i<arreglo.length-1;i++){
            if(n==arreglo[i]) aux=true;
            if(aux){
                arreglo[i]=arreglo[i+1]
            }
        }
        if(aux || arreglo[arreglo.length-1]==n) size--;

    }

}
