package uk.org.cezary.t5conduit.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.Invokable;

/**
 * A simple object pool.
 * @author Cezary Biernacki
 *
 */
public class Pool<T> {
	private final Object lock = new Object();
	private final List<T> stored = new ArrayList<T>();
	private final Invokable<T> factory;
	private final int max;
	private int taken;
	
	public static interface Processor<V, T> {
		V process(T o);
	}

	public Pool(int max, Invokable<T> factory) {
		this.factory = factory;
		this.max = max;
	}
	
	
	public <V> V withObject(Processor<V, T> p) {
		T o = null;
		boolean hasObject = false;
		boolean wasInterrupted = false;
		boolean needsCreation = false;
		int waitCount = 0; // prevent waiting forever
		
		try {
			synchronized(lock) {
				boolean wasTaken = false;
				while (!hasObject) {
					if (stored.isEmpty()) {
						if (taken < max || waitCount > 5) {
							needsCreation = true;
							hasObject = true;
							if (taken < max) {
								wasTaken = true;
							}
						} else {
							try {
								lock.wait(1000);
							} catch (InterruptedException e) {
								wasInterrupted = true;
							}
						}
					} else {
						int last = stored.size() - 1;
						o = stored.get(last);
						stored.remove(last);
						wasTaken = true;
						hasObject = true;
					}
				}
				
				if (wasTaken) {
					++ taken;
				}
			}
			
			if (needsCreation) {
				o = factory.invoke();
			}
			V result = p.process(o);
			
			return result;
			
		} finally {
			
			synchronized(lock) {
				if (stored.size() < max) {
					-- taken;
					stored.add(o);
					lock.notify();
				}

				if (wasInterrupted) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	

}
