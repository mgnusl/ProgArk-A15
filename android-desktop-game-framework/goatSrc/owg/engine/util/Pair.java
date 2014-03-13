package owg.engine.util;

import java.io.Serializable;

public class Pair<A,B> implements Serializable
	{
	private static final long serialVersionUID = 2021342480625784051L;
	
	public A a;
	public B b;
	
	public Pair(A a,B b)
		{
		this.a = a;
		this.b = b;
		}
	public Pair()
		{}
	public boolean equals(Object o)
		{
		if (o instanceof Pair)
			return (((Pair<?,?>) o).a==a && ((Pair<?,?>) o).b==b);
		else
			return false;
		}
	public int hashCode()
		{
		return a.hashCode()*512+b.hashCode();
		}
	}
