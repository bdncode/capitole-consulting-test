package list;

public class DoublyLinkedList<E> {

    private Node<E> first;
    private Node<E> last;
    private int size;

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public void addFirst(E element) {
    }

    public E getFirst() {
        return null;
    }

    public void addLast(E element) {
    }

    public E getLast() {
        return null;
    }

    public void add(E element, int index) {
    }

    public E get(int index) {
        return null;
    }

    public E removeFirst() {
        return null;
    }

    public E removeLast() {
        return null;
    }

    public E remove(int index) {
        return null;
    }

    public boolean contains(E element) {
        return false;
    }

    public void clear() {
    }

    public int indexOf(E element) {
        return 0;
    }

    private class Node<E> {

        private E data;
        private Node<E> prev;
        private Node<E> next;

        public Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}
