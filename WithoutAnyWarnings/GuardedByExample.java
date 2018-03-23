import org.checkerframework.checker.lock.qual.*;


public class GuardedByExample {
    private static final Object myLock = new Object();

    @GuardedBy("GuardedByExample.myLock") MyClass myMethod() {
        @GuardedBy("GuardedByExample.myLock") MyClass m = new MyClass();
        return m;
    }

    public void test() {
        // reassignments without holding the lock are OK.
        @GuardedBy("GuardedByExample.myLock") MyClass x = myMethod();
        @GuardedBy("GuardedByExample.myLock") MyClass y = x;
        synchronized (GuardedByExample.myLock) {
            x.toString(); // OK: the lock is held
            x.field = new Object(); // OK: the lock is held
            y.toString(); // OK: the lock is held
            y.field = new Object(); // OK: the lock is held
        }
    }
}

class MyClass {
    public Object field;
}