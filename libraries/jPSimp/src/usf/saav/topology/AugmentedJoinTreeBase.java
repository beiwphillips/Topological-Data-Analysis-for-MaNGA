package usf.saav.topology;

import java.util.Vector;

import usf.saav.common.BasicObject;
import usf.saav.topology.TopoTree;
import usf.saav.topology.TopoTreeNode;

public class AugmentedJoinTreeBase extends BasicObject implements TopoTree {

	public	  JoinTreeNode		   head = null;

	protected Vector<JoinTreeNode> nodes = new Vector<JoinTreeNode>();
	protected float				   simplify = 0.0f;
	protected float				   max_persistence = 0.0f;
	
	
	protected AugmentedJoinTreeBase( ){ }
	protected AugmentedJoinTreeBase( boolean verbose ){ super(verbose); }
	
	public String toString( ){
		if( head == null ){ return "<empty>"; }
		return head.toString();
	}
	
	public String toDot( ){
		if( head == null ){ return "Digraph{\n}"; }
		else {
			StringBuffer dot_node = new StringBuffer( );
			StringBuffer dot_edge = new StringBuffer( );
			head.toDot( dot_node, dot_edge );
			return "Digraph{\n" + dot_node + dot_edge + "}"; 
		}
	}

	public String toDot( int maxdepth ){
		if( head == null ){ return "Digraph{\n}"; }
		else {
			StringBuffer dot_node = new StringBuffer( );
			StringBuffer dot_edge = new StringBuffer( );
			head.toDot( dot_node, dot_edge, maxdepth );
			return "Digraph{\n" + dot_node + dot_edge + "}"; 
		}
	}

	

	
	@Override public float getMaxPersistence(){ return max_persistence; }
	
	@Override 
	public void setPersistentSimplification( float threshold ){
		simplify = threshold;
	}

	@Override
	public float getPersistentSimplification( ){
		return simplify;
	}

	@Override
	public boolean isActive(int i){
		return getPersistence(i) > simplify * max_persistence;
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public float getBirth(int i) {
		return nodes.get(i).getBirth();
	}

	@Override
	public float getDeath(int i) {
		return nodes.get(i).getDeath();
	}

	@Override
	public float getPersistence(int i) {
		return nodes.get(i).getPersistence();
	}

	public TopoTreeNode getNode(int i) {
		return nodes.get(i);
	}
	
	public boolean isSubject(int i) {
	    return getSize(i) > simplify;
	}
	
	public int getSize(int i) {
	    return nodes.get(i).getSize();
	}
	
}

