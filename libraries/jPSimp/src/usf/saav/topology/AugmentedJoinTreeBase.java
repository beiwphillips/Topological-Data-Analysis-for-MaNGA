package usf.saav.topology;

import java.util.Vector;

import usf.saav.common.BasicObject;
import usf.saav.topology.TopoTree;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.AugmentedJoinTree.AugmentedJoinTreeNode;

public class AugmentedJoinTreeBase extends BasicObject implements TopoTree {

    public   JoinTreeNode         head = null;
	
    protected AugmentedJoinTreeNode global_extreme;
    protected Float global_extreme_value;
	protected Vector<JoinTreeNode> nodes = new Vector<JoinTreeNode>();
	protected float				   simplify = 0.0f;
	protected float				   max_persistence = 0.0f;
	protected int                   max_volumn = 0;
	protected float                 max_hypervolumn = 0f;
	protected String                metric = "persistence";
	
	
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

	

	
	public float getMaxPersistence(){ return max_persistence; }
	public int   getMaxVolumn(){ return max_volumn; }
	public float getMaxHyperVolumn(){ return max_hypervolumn; }
	
	@Override 
	public void setSimplificationLevel( float threshold ){
		this.simplify = threshold;
	}
	
	@Override
	public void setSimplificationMetric( String metric ) {
	    this.metric = metric;
	}

	@Override
	public float getSimplificationLevel( ){
		return simplify;
	}
	
	@Override
	public String getSimplificationMetric( ) {
	    return metric;
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
	
	@Override
	public float getSimplePersistence(int i) {
//	    if (nodes.get(i).getValue() == global_extreme_value) {
//	        return Float.NaN;
//	    }
	    return nodes.get(i).getSimplePersistence();
	}

	@Override
	public int getVolumn(int i) {
//	    if (nodes.get(i).getValue() == global_extreme_value) {
//	        return Integer.MAX_VALUE;
//	    }
	    return nodes.get(i).getVolumn();
	}
	
	@Override
	public float getHyperVolumn(int i) {
//	    if (nodes.get(i).getValue() == global_extreme_value) {
//	        return Float.NaN;
//	    }
	    return nodes.get(i).getHyperVolumn();
	}
	
	public TopoTreeNode getNode(int i) {
		return nodes.get(i);
	}
	
	
}

