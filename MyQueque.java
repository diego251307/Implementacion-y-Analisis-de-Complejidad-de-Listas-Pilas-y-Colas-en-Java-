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
    // front - Diego
    // isEmpty - Julian
    public bool isEmpty(){
        return size<=0;
    }
    // size - Diego
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
