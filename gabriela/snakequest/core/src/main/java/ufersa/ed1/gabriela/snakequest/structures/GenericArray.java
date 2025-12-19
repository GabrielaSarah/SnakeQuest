package ufersa.ed1.gabriela.snakequest.structures;

//Vetor Dinâmico Genérico
public class GenericArray<T> {
    private Object[] data;
    private int size;

    public GenericArray(int capacity) {
        data = new Object[capacity];
        size = 0;
    }

    public void add(T element) {
        if (size == data.length) resize(data.length * 2);
        data[size++] = element;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        @SuppressWarnings("unchecked")
        T element = (T) data[index];
        return element;
    }

    public void set(int index, T element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        data[index] = element;
    }

    public int size() {
        return size;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) newData[i] = data[i];
        data = newData;
    }
}
