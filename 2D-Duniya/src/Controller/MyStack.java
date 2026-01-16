package Controller;

/**
 * A generic stack implementation using an array.
 * Supports push, pop, peek, and other stack operations.
 * @param <T> the type of elements in the stack
 */
public class MyStack<T> {

    // The array to store stack elements
    private T[] stackArray;
    // Index of the top element
    private int top;
    // Maximum capacity of the stack
    private int maxSize;

    /**
     * Constructs a new MyStack with the specified maximum size.
     * @param size the maximum number of elements the stack can hold
     */
    @SuppressWarnings("unchecked")
    public MyStack(int size) {
        maxSize = size;
        stackArray = (T[]) new Object[size]; // generic array creation
        top = -1; // empty stack
    }

    /**
     * Pushes an element onto the top of the stack.
     * @param item the element to push
     */
    public void push(T item) {
        if (isFull()) {
            System.out.println("Stack full! Cannot push: " + item);
            return;
        }
        stackArray[++top] = item;
    }

    /**
     * Pops and returns the element at the top of the stack.
     * @return the top element, or null if empty
     */
    public T pop() {
        if (isEmpty()) {
            System.out.println("Stack empty!");
            return null;
        }
        T item = stackArray[top];
        stackArray[top--] = null; // remove reference
        return item;
    }

    /**
     * Returns the element at the top without removing it.
     * @return the top element, or null if empty
     */
    public T peek() {
        if (isEmpty()) return null;
        return stackArray[top];
    }

    /**
     * Checks if the stack is empty.
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Checks if the stack is full.
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        return top == maxSize - 1;
    }

    /**
     * Returns the current number of elements in the stack.
     * @return the size
     */
    public int size() {
        return top + 1;
    }

    /**
     * Removes all elements from the stack.
     */
    public void clear() {
        while (!isEmpty()) {
            stackArray[top--] = null;
        }
    }

    /**
     * Removes the first occurrence of the specified element.
     * @param obj the element to remove
     * @return true if removed, false otherwise
     */
    public boolean removeElement(T obj) {
        for (int i = 0; i <= top; i++) {
            if (stackArray[i].equals(obj)) {
                for (int j = i; j < top; j++) {
                    stackArray[j] = stackArray[j + 1]; // shift down
                }
                stackArray[top] = null;
                top--;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the first occurrence of the specified element.
     * @param obj the element to remove
     * @return true if removed, false otherwise
     */
    public boolean remove(T obj) {
        return removeElement(obj);
    }
}

