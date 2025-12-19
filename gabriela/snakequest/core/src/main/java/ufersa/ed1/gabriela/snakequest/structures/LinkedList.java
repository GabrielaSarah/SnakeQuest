package ufersa.ed1.gabriela.snakequest.structures;
import java.util.Iterator;

//Lista Encadeada Simples com Iterador
public class LinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // Adiciona no início
    public void addFirst(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null) {
            // Lista vazia: head e tail apontam para o novo nó
            head = tail = newNode;
        } else {
            // Novo nó aponta para o antigo head
            newNode.next = head;
            // Head passa a ser o novo nó
            head = newNode;
        }
        size++;
    }

    // Adiciona no final
    public void addLast(T element) {
        Node<T> newNode = new Node<>(element);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // Remove do início
    public T removeFirst() {
        if (head == null) return null;
        T element = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return element;
    }

    // Remove do final
    public T removeLast() {
        if (head == null) return null; // Lista vazia

        T element;
        if (head == tail) {
            // Só existe um elemento
            element = head.data;
            head = tail = null;
        } else {
            // Percorre até o penúltimo nó
            Node<T> current = head;
            while (current.next != tail) {
                current = current.next;
            }
            element = tail.data;
            tail = current;
            tail.next = null;
        }
        size--;
        return element;
    }

    // Retorna o primeiro elemento
    public T getFirst() {
        return (head != null) ? head.data : null;
    }

    // Retorna o último elemento
    public T getLast() {
        return (tail != null) ? tail.data : null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // Iterador para usar for-each
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T element = current.data;
                current = current.next;
                return element;
            }
        };
    }
}
