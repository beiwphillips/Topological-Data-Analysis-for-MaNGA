/*
 *     jPSimp - Persistence calculation and simplification of scalar fields.
 *     Copyright (C) 2016 PAUL ROSEN
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the Paul Rosen at <prosen@usf.edu>.
 */
package usf.saav.topology;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import usf.saav.common.types.Pair;

public class JoinTreeNode implements TopoTreeNode {

    private int position;
    private float value;
    private int volumn;
    private float hypervolumn;
    private boolean valid = true;
    
	protected Vector<JoinTreeNode> children = new Vector<JoinTreeNode>( );
	protected JoinTreeNode parent  = null;
	protected JoinTreeNode partner = null;

    public JoinTreeNode( int position, float value, int volumn, float hypervolumn ) {
        this.position = position;
        this.value = value;
        this.volumn = volumn;
        this.hypervolumn = hypervolumn;
    }
	
    @Override public void addChild( JoinTreeNode c ){	 children.add(c); }
    @Override public void addChildren(Collection<JoinTreeNode> c){ children.addAll(c); }
    @Override public JoinTreeNode getChild( int idx ){ return children.get(idx); }
    @Override public List<JoinTreeNode> getChildren( ){ return children; }
    @Override public int getChildCount( ){ return children.size(); }
    @Override public boolean isChild(JoinTreeNode node){ return children.contains(node); }
    @Override public int childCount( ){ return children.size(); }
    @Override public boolean hasChildren( ){ return children.size() != 0; }
    @Override public boolean removeChild(JoinTreeNode node){ return children.remove(node); }

    @Override public void setPartner( JoinTreeNode jtn ) { partner = jtn; }
    @Override public TopoTreeNode getPartner() { return partner; }
    
    @Override public void setParent( JoinTreeNode p ){ parent = p; }
    @Override public TopoTreeNode getParent( ){ return parent; }
    @Override public boolean hasParent() { return parent != null; }
	
	@Override public float getBirth() { 
		if( partner == null ) return 0;
		return Math.min( getValue(), partner.getValue() );
	}
	@Override public float getDeath() { 
		if( partner == null ) return Float.MAX_VALUE;
		return Math.max( getValue(), partner.getValue() );
	}
	@Override public float getPersistence() { 
		if( partner == null ) return Float.NaN;
		return Math.abs( getValue()-partner.getValue() );
	}
	@Override public float getSimplePersistence() {
	    if( parent == null) return Float.NaN;
	    return Math.abs( getValue()-parent.getValue());
	}
	
    @Override public int getPosition( ){ return position; }
    @Override public float getValue( ){ return value; }
    @Override public int getVolumn( ){ return volumn; }
    
    @Override public float getHyperVolumn() {
        if( parent == null ) return Float.NaN;
        return Math.abs(hypervolumn - parent.getValue() * getVolumn()); 
    }
    @Override public float getAbsoluteHyperVolumn() {
        return hypervolumn;
    }
    
    public void setVolumn(int volumn){ this.volumn = volumn; }
    public void setHyperVolumn(float hyperVolumn){ this.hypervolumn = hyperVolumn; }
    public void addVolumn(int toAdd){ this.volumn += toAdd; }
    public void addHyperVolumn(float toAdd){ this.hypervolumn += toAdd; }
    
    @Override public void setValid(boolean valid) {this.valid = valid;}
    @Override public boolean isValid() {return this.valid;}
    
    @Override public NodeType getType() { return NodeType.UNKNOWN; }

	@Override public String toString(){
		StringBuffer bf = new StringBuffer( );
		toString(bf,"| ");
		return bf.toString();
	}
	
	
	private void toString( StringBuffer bf, String spaces ){
		bf.append( spaces + getPosition() + ": " + getValue() + "\n" );
		if( children != null ){
			for(JoinTreeNode child : children){ child.toString(bf, "  "+spaces); }
		}
	}
	
	public void toDot(StringBuffer dot_node, StringBuffer dot_edge) {
		Queue<JoinTreeNode> queue = new LinkedList<JoinTreeNode>( );
		queue.add(this);
		
		while( !queue.isEmpty() ){
			JoinTreeNode curr = queue.poll();
			dot_node.append( "\t" + curr.getPosition() + "[label=\"" + curr.getPosition() + " (" + curr.getValue() + ")\"];\n");
			for( JoinTreeNode n : curr.getChildren() ){
				dot_edge.append( "\t" + curr.getPosition() + " -> " + n.getPosition() + "\n");
			}
			queue.addAll( curr.getChildren() );
		}
		/*
		dot_node.append( "\t" + this.getPosition() + "[label=\"" + this.getPosition() + " (" + this.getValue() + ")\"];\n");
		for( JoinTreeNode n : this.getChildren() ){
			dot_edge.append( "\t" + this.getPosition() + " -> " + n.getPosition() + "\n");
			n.toDot( dot_node, dot_edge );
		}
		*/
	}
	

	public void toDot(StringBuffer dot_node, StringBuffer dot_edge, int maxdepth) {
		Queue<Pair<JoinTreeNode,Integer>> queue = new LinkedList<Pair<JoinTreeNode,Integer>>( );
		queue.add( new Pair<JoinTreeNode,Integer>(this,0) );
		
		while( !queue.isEmpty() ){
			Pair<JoinTreeNode,Integer> curr = queue.poll();
			if( curr.getSecond() < maxdepth ){
				dot_node.append( "\t" + curr.getFirst().getPosition() + "[label=\"" + curr.getFirst().getPosition() + " (" + curr.getFirst().getValue() + ")\"];\n");
				for( JoinTreeNode n : curr.getFirst().getChildren() ){
					dot_edge.append( "\t" + curr.getFirst().getPosition() + " -> " + n.getPosition() + "\n");
					queue.add( new Pair<JoinTreeNode,Integer>( n, curr.getSecond()+1 ));
				}
			}
		}
	}
 

	public static List<JoinTreeNode> findLeaves( JoinTreeNode root ) {
		List<JoinTreeNode>  ret = new Vector<JoinTreeNode>( );
		Deque<JoinTreeNode> work_stack = new ArrayDeque<JoinTreeNode>();
		
		work_stack.push(root);
		while( !work_stack.isEmpty() ){
			JoinTreeNode curr = work_stack.pop();
			if( !curr.hasChildren() ) ret.add(curr);
			for( JoinTreeNode child : curr.children )
				work_stack.push( child );
		}
		
		return ret;
	}
	
	public static void findParents( JoinTreeNode root ) {
		Deque<JoinTreeNode> work_stack = new ArrayDeque<JoinTreeNode>();
		
		work_stack.push(root);
		while( !work_stack.isEmpty() ){
			JoinTreeNode curr = work_stack.pop();
			for( JoinTreeNode child : curr.children ){
				child.setParent( curr );
				work_stack.push( child );
			}
		}
	}



	public static class ComparatorValueAscending implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if( o1 instanceof JoinTreeNode && o2 instanceof JoinTreeNode ){
				if( ((JoinTreeNode)o1).getValue() > ((JoinTreeNode)o2).getValue() ) return  1;
				if( ((JoinTreeNode)o1).getValue() < ((JoinTreeNode)o2).getValue() ) return -1;
				if( ((JoinTreeNode)o1).getPosition() < ((JoinTreeNode)o2).getPosition() ) return  1;
				if( ((JoinTreeNode)o1).getPosition() > ((JoinTreeNode)o2).getPosition() ) return -1;
			}
			return 0;
		}	
	}

	public static class ComparatorValueDescending implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if( o1 instanceof JoinTreeNode && o2 instanceof JoinTreeNode ){
				if( ((JoinTreeNode)o1).getValue() > ((JoinTreeNode)o2).getValue() ) return -1;
				if( ((JoinTreeNode)o1).getValue() < ((JoinTreeNode)o2).getValue() ) return  1;
				if( ((JoinTreeNode)o1).getPosition() < ((JoinTreeNode)o2).getPosition() ) return -1;
				if( ((JoinTreeNode)o1).getPosition() > ((JoinTreeNode)o2).getPosition() ) return  1;
			}
			return 0;
		}	
	}

	
	
	
}
