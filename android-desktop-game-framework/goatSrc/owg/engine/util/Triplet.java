package owg.engine.util;

import java.io.Serializable;

public class Triplet<A, B, C> implements Serializable
	{
	private static final long serialVersionUID = 2021342480625784051L;
	
	public A a;
	public B b;
	public C c;
	
	public Triplet(A a, B b, C c)
		{
		this.a = a;
		this.b = b;
		this.c = c;
		}
	public boolean equals(Object o)
		{
		if (o instanceof Triplet)
			return (((Triplet<?,?,?>) o).a==a && ((Triplet<?,?,?>) o).b==b && ((Triplet<?,?,?>) o).c==c);
		else
			return false;
		}
	public int hashCode()
		{
		return a.hashCode()*997+b.hashCode()*33+c.hashCode();
		}
	}
