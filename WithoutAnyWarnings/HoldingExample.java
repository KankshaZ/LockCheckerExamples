import org.checkerframework.checker.lock.qual.*;

public class HoldingExample {

    void helper1(@GuardedBy("MyClass.myLock") Object a) {
        synchronized(MyClass.myLock) {
            a.toString();  // OK: the lock is held
        }
    }

    @Holding("MyClass.myLock")
    void helper2(@GuardedBy("MyClass.myLock") Object b) {
        b.toString(); // OK: the lock is held
    }

    void helper3(@GuardedBy("MyClass.myLock") Object d) {
        synchronized(MyClass.myLock) {
            d.toString();  // OK: the lock is held
        }
    }

    void myMethod2(@GuardedBy("MyClass.myLock") Object e) {
        helper1(e);  // OK to pass to another routine without holding the lock
        synchronized (MyClass.myLock) {
            e.toString();
            helper2(e);
            helper3(e); // OK, but helper3’s body still does not type-check
        }
    }
}

class MyClass {
    public static final Object myLock = new Object();
}