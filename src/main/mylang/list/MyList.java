package main.mylang.list;

import main.mylang.token.Token;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MyList implements Collection<Token> {

    private String name;
    private int size;
    private Node first;
    private Node last;
    private Node[] list;


    public MyList(String name) {
        this.size = 0;
        this.first = null;
        this.last = null;
        this.list = null;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int get(int index) {
        Node node = find(index);
        return node.getValue();
    }

    public void insert(int index, Object value) {
        Node node = find(index);
        Node newNode = new Node((Integer) value, node, node.getNext());

        if (node.getNext() != null) {
            node.getNext().setPrev(newNode);
        }
        if (node.getPrev() != null) {
            node.setNext(newNode);
        }
    }

    public void remove(int index) {
        Node node = find(index);

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        }

        --size;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<Token> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }

    @Override
    public boolean add(Token token) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Token> collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    public void clear() {
        first = last = null;
        size = 0;
    }

//    public String toString() {
//        StringBuilder builder = new StringBuilder("{");
//
//        boolean f = true;
//        for (int i = 0; i < size; ++i) {
//            if (!f) {
//                builder.append(", ");
//            }
//
//            builder.append(get(i));
//            f = false;
//        }
//        builder.append("}");
//
//        return builder.toString();
//    }
    public String toString() {
        StringBuilder builder = new StringBuilder("{");

        Node node = first;
        for (int i = 0; i < size; ++i) {
            if (node != first) {
                builder.append(", ");
            }

            builder.append(node.getValue());

            node = node.getPrev();
        }
        builder.append("}");

        return builder.toString();
    }

    public void add(int value) {
        Node node = new Node(value, null, last);

        if (last != null) {
            last.prev = node;
        }
        last = node;

        if (first == null) {
            first = last;
        }

        size++;
    }

    private Node find(int index) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }

        Node node = null;

        if (index < size / 2) {
            node = first;
            for (int i = 0; i < index; ++i) {
                node = node.getPrev();
            }
        }
        else {
            node = last;
            for (int i = 0; i < size - index - 1; ++i) {
                node = node.getNext();
            }
        }

        return node;
    }
}
