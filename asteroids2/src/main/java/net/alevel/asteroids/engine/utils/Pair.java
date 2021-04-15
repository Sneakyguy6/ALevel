package net.alevel.asteroids.engine.utils;

/**Generic class with 2 objects. The data types of both objects are specified at instantiation/declaration
 * @param <T1> data type of first object
 * @param <T2> data type of second object
 */
public class Pair<T1, T2> {
	private T1 o1;
	private T2 o2;
	
	public Pair(T1 o1, T2 o2) {
		this.o1 = o1;
		this.o2 = o2;
	}

	public T1 getO1() {
		return this.o1;
	}

	public void setO1(T1 o1) {
		this.o1 = o1;
	}

	public T2 getO2() {
		return this.o2;
	}

	public void setO2(T2 o2) {
		this.o2 = o2;
	}
}
