package ufersa.ed1.gabriela.snakequest.structures;

// Fila Genérica baseada na lista encadeada
public class Queue<T> implements Iterable<T> {
    private LinkedList<T> list;

    public Queue() {
        list = new LinkedList<>();
    }

    // Enfileira (adiciona no final)
    public void enqueue(T element) {
        list.addLast(element);
    }

    // Desenfileira (remove do início)
    public T dequeue() {
        return list.removeFirst();
    }

    // Olha o primeiro da fila
    public T front() {
        return list.getFirst();
    }

    // Olha o último da fila
    public T back() {
        return list.getLast();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return list.iterator();
    }
}
