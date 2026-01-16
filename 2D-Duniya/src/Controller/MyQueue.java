package Controller;

/**
 * A generic circular queue implementation using an array.
 * Supports enqueue, dequeue, peek, and other queue operations.
 * @param <T> the type of elements in the queue
 */
public class MyQueue<T> {

    // The array to store queue elements
    private T[] queueArray;
    // Index of the front element
    private int front;
    // Index of the rear element
    private int rear;
    // Current number of elements in the queue
    private int size;
    // Maximum capacity of the queue
    private int maxSize;

    @SuppressWarnings("unchecked")
    /**
     * Constructs a new MyQueue with the specified maximum size.
     * @param maxSize the maximum number of elements the queue can hold
     */
    public MyQueue(int maxSize) {
        this.maxSize = maxSize;
        queueArray = (T[]) new Object[maxSize];
        front = 0;
        rear = -1;
        size = 0;
    }

    /**
     * Adds an element to the rear of the queue.
     * @param item the element to add
     */
    public void enqueue(T item) {
        if (isFull()) {
            System.out.println("Queue full! Cannot enqueue: " + item);
            return;
        }
        rear = (rear + 1) % maxSize; // circular increment
        queueArray[rear] = item;
        size++;
    }

    /**
     * Removes and returns the element at the front of the queue.
     * @return the front element, or null if empty
     */
    public T dequeue() {
        if (isEmpty()) {
            System.out.println("Queue empty!");
            return null;
        }
        T item = queueArray[front];
        queueArray[front] = null; // remove reference
        front = (front + 1) % maxSize; // circular increment
        size--;
        return item;
    }

    /**
     * Returns the element at the front without removing it.
     * @return the front element, or null if empty
     */
    public T peek() {
        if (isEmpty()) return null;
        return queueArray[front];
    }

    /**
     * Checks if the queue is empty.
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if the queue is full.
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        return size == maxSize;
    }

    /**
     * Returns the current number of elements in the queue.
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the queue.
     */
    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
    }

    /**
     * Removes the first occurrence of the specified element.
     * @param obj the element to remove
     * @return true if removed, false otherwise
     */
    public boolean remove(T obj) {
        if (isEmpty()) return false;

        int count = size; // number of elements to check
        boolean removed = false;

        while (count-- > 0) {
            T item = dequeue(); // remove from front
            if (!removed && item.equals(obj)) {
                removed = true; // skip adding back
            } else {
                enqueue(item); // add back to rear
            }
        }
        return removed;
    }

    /**
     * Removes and returns the first element.
     * @return the first element, or null if empty
     */
    public T removeFirst() {
        return dequeue();
    }
}

