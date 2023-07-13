package list;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoublyLinkedListTest {

    private DoublyLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new DoublyLinkedList<>();
    }

    @Test
    void testAddFirstAndGetFirst() {
        list.addFirst(5);
        Assertions.assertEquals(5, list.getFirst());

        list.addFirst(10);
        Assertions.assertEquals(10, list.getFirst());
    }

    @Test
    void testAddLastAndGetLast() {
        list.addLast(15);
        Assertions.assertEquals(15, list.getLast());

        list.addLast(20);
        Assertions.assertEquals(20, list.getLast());
    }

    @Test
    void testAddAndGet() {
        list.add(25, 0);
        list.add(35, 1);
        list.add(30, 1);

        Assertions.assertEquals(25, list.get(0));
        Assertions.assertEquals(30, list.get(1));
        Assertions.assertEquals(35, list.get(2));
    }

    @Test
    void testRemoveFirst() {
        list.addFirst(40);
        list.addFirst(45);

        Assertions.assertEquals(45, list.removeFirst());
        Assertions.assertEquals(40, list.getFirst());
        Assertions.assertEquals(40, list.removeFirst());
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void testRemoveLast() {
        list.addLast(50);
        list.addLast(55);

        Assertions.assertEquals(55, list.removeLast());
        Assertions.assertEquals(50, list.getLast());
        Assertions.assertEquals(50, list.removeLast());
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void testRemove() {
        list.add(60, 0);
        list.add(70, 1);
        list.add(65, 1);

        Assertions.assertEquals(65, list.remove(1));
        Assertions.assertEquals(60, list.get(0));
        Assertions.assertEquals(70, list.get(1));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testContains() {
        list.add(75, 0);
        list.add(85, 1);

        Assertions.assertTrue(list.contains(75));
        Assertions.assertTrue(list.contains(85));
        Assertions.assertFalse(list.contains(80));
    }

    @Test
    void testIsEmpty() {
        Assertions.assertTrue(list.isEmpty());

        list.addFirst(90);
        Assertions.assertFalse(list.isEmpty());

        list.removeFirst();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void testSize() {
        Assertions.assertEquals(0, list.size());

        list.addFirst(95);
        Assertions.assertEquals(1, list.size());

        list.addLast(100);
        Assertions.assertEquals(2, list.size());

        list.removeFirst();
        Assertions.assertEquals(1, list.size());

        list.removeLast();
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void testClear() {
        list.addFirst(105);
        list.addLast(110);
        list.add(107, 1);

        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(3, list.size());

        list.clear();

        Assertions.assertTrue(list.isEmpty());
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void testIndexOf() {
        list.addFirst(115);
        list.addLast(120);
        list.add(117, 1);

        Assertions.assertEquals(0, list.indexOf(115));
        Assertions.assertEquals(1, list.indexOf(117));
        Assertions.assertEquals(2, list.indexOf(120));
        Assertions.assertEquals(-1, list.indexOf(125));
    }
}
