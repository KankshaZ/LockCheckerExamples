import org.checkerframework.checker.lock.qual.*;
import java.util.concurrent.ThreadLocalRandom;

public class Philosopher extends Thread{

	static int philosophersNumber = 5;
	static Philosopher philosophers[] = new Philosopher[philosophersNumber];
	static Fork forks[] = new Fork[philosophersNumber];

	private final @GuardedBy("<self>") Fork left;
	private final @GuardedBy("<self>") Fork right;

	Philosopher(String name, @GuardedBy("<self>") Fork left, @GuardedBy("<self>") Fork right){
		super(name);
		//a fixed ordering avoids deadlock 
		if(left.compareTo(right)<0){
			this.left = left;
			this.right = right;
		}
		else{
			this.left = right;
			this.right = left;
		}
	}

	public void run(){
		while (true) {
			think();
			synchronized(left){
				left.pickUp(this);
				synchronized(right){
					right.pickUp(this);
					eat();
					right.drop();
				}
				left.drop();
			}
		}
	}

	private void think(){
		System.out.println("The philosopher is thinking.");
	}

	@Holding({"left", "right"})
	private void eat(){
		System.out.println("The philosopher is eating.");
	}

	public static void main(String[] args) throws Exception {
 
        final Philosopher[] philosophers = new Philosopher[5];
        Fork[] forks = new Fork[philosophers.length];
 
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Fork();
        }
 
        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];
 
            if (i == philosophers.length - 1) {
                 
                // The last philosopher picks up the right fork first
                philosophers[i] = new Philosopher(Integer.toString(i), rightFork, leftFork); 
            } else {
                philosophers[i] = new Philosopher(Integer.toString(i), leftFork, rightFork);
            }
             
            Thread t 
              = new Thread(philosophers[i], "Philosopher " + (i + 1));
            t.start();
        }
    }
}

class Fork implements Comparable<Fork> {
	private static int nextId = 0;
	private final int id = nextId++;
	//who is holding the fork, or null if on the table
	private Philosopher usedBy = null;

	void pickUp(Philosopher philosopher){
		this.usedBy = philosopher;
	}

	void drop(){
		this.usedBy = null;
	}

	Boolean isFree()
	{
		if(this.usedBy!=null)
			return false;
		else
			return true;
	}

	public int compareTo(@GuardedBy("<self>") Fork other) {
		return id-other.id;		
	}

	public synchronized String toString(){
		if(usedBy!=null)
			return "fork " + id + " sused by " + usedBy.getName();
		else
			return "fork " + id + " on the table";
	}
}