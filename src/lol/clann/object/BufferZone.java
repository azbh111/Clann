package lol.clann.object;

import java.util.*;

public class BufferZone<T> {

    private List<T> data = new LinkedList();

    public void add(T p) {
        synchronized (data) {
            data.add(p);
            data.notifyAll();
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public T get() throws InterruptedException {
        while (true) {
            synchronized (data) {
                if (data.isEmpty()) {
                    data.wait();
                } else {
                    return data.remove(0);
                }
            }
        }
    }
}
