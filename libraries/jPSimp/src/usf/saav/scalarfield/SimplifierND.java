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
package usf.saav.scalarfield;
 
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import usf.saav.common.monitoredvariables.Callback;
import usf.saav.mesh.Mesh;
import usf.saav.mesh.Mesh.Vertex;
import usf.saav.topology.JoinTreeNode;
import usf.saav.topology.PseudoContourTree;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.TopoTreeNode.NodeType;

public abstract class SimplifierND extends ScalarFieldND.Default implements ScalarFieldND, Runnable {

	private ScalarFieldND sf;
	private PseudoContourTree ct;
	private Mesh cl;
	private float simplification;
	private String metric;

	private float [] img;

	private boolean hasRun = false;

	protected Callback cb = null;


	public SimplifierND( ScalarFieldND sf, PseudoContourTree ct, Mesh cl, 
	        float simplification, String metric, boolean runImmediately ){
		this( sf, ct, cl, simplification, metric, runImmediately, true );
	}

	public SimplifierND( ScalarFieldND sf, PseudoContourTree ct, Mesh cl, 
	        float simplification, String metric, boolean runImmediately, boolean verbose ){
		super( verbose );
		this.sf = sf;
		this.ct = ct;
		this.cl = cl;
		this.simplification = simplification;
		this.metric = metric;
		if( runImmediately ) run( );
	}


	public PseudoContourTree			getTree( ){				return ct; }
	public Mesh			getComponentList(){ 	return cl; }
	public ScalarFieldND			getScalarField( ){  	return sf; }

	@Override public int getSize() {	return sf.getSize();	}
	@Override public float getValue(int idx) { 	return img[idx]; }

	public abstract void setCallback( Object obj, String func_name );

	
	
	@Override
	public void run() {
		if( hasRun ){
			return;
		}

		print_info_message("Building Simplification");

		// Copy the existing field
		img = new float[sf.getSize()];
		for(int i = 0; i < img.length; i++){
			img[i] = sf.getValue(i);
		}

//		Vector<TopoTreeNode> workList = new Vector<TopoTreeNode>();
		Queue<TopoTreeNode> workList;
		
		switch (this.metric) {
            case "persistence":
                workList = new PriorityQueue<TopoTreeNode>( 
                        sf.getSize(), 
                        new TopoTreeNode.CompareSimplePersistenceAscending()
                        );
                break;
            case "volume":
                workList = new PriorityQueue<TopoTreeNode>( 
                        sf.getSize(), 
                        new TopoTreeNode.CompareVolumnAscending()
                        );
                break;
            case "hypervolume":
                workList = new PriorityQueue<TopoTreeNode>( 
                        sf.getSize(), 
                        new TopoTreeNode.CompareHyperVolumnAscending()
                        );
                break;
            default:
                throw new IllegalArgumentException();
        }

		// Simplify the field, component by component
		for(int i = 0; i < ct.size(); i++){
//		    if (ct.isPruning(i)) {
		        TopoTreeNode n = ct.getNode(i);
		        switch( n.getType() ){
        				case LEAF_MAX:
        			    case LEAF_MIN:
        				    workList.add(n);
        		            break;
        			    default:
        			        break;
        			}
		}
		
		int num_simplified = 0;
		while (num_simplified < simplification * ct.getNumLeaves()) {
//		while (!workList.isEmpty()) {
//            System.out.println("Legal Tree: "+ct.checkTree());
		    TopoTreeNode n = workList.poll();
		    if (!n.hasParent()) {
                break;
            }
		    TopoTreeNode p = n.getParent();
		    pruneLeaf(n, p);
//		    findVolumeBalancingValue(n, p);
	        float volumeChange = modifyScalarField(n, p);
	        if (p.hasParent() && p.getChildCount() == 1) {
	            TopoTreeNode newVertex = reduceVertex(n, p, volumeChange);
	            if (workList.remove(newVertex) && 
//	                    ct.isPruning(newVertex) && 
	                    (newVertex.getType() == NodeType.LEAF_MAX || newVertex.getType() == NodeType.LEAF_MIN)) {
                    workList.add(newVertex);
	            }
	        }
	        num_simplified++;
		}

		print_info_message("Build Complete");

		hasRun = true;
		if( cb != null ){
			cb.call( this );
		}

	}
	
	private void pruneLeaf(TopoTreeNode n, TopoTreeNode p) {
        float nHyperVolume = n.getAbsoluteHyperVolumn() - n.getValue() * n.getVolumn();
        TopoTreeNode cur = p;
        while (cur != null) {
            cur.addHyperVolumn(-nHyperVolume);
            cur = cur.getParent();
        }
        
	    n.setParent(null);
	    p.removeChild((JoinTreeNode) n);
	    n.setValid(false);
	}
	
	private TopoTreeNode reduceVertex(TopoTreeNode n, TopoTreeNode p, float volumeChange) {
	    JoinTreeNode sbl = p.getChild(0);
        JoinTreeNode np = (JoinTreeNode) p.getParent();
        sbl.setParent(np);
        p.setParent(null);
        p.removeChild(sbl);
        np.removeChild((JoinTreeNode) p);
        np.addChild((JoinTreeNode) sbl);
        sbl.setVolumn(p.getVolumn());
        sbl.setHyperVolumn(p.getAbsoluteHyperVolumn());
        p.setValid(false);
        return sbl;
	}
	
	private float modifyScalarField(TopoTreeNode n, TopoTreeNode p) {

        Set<Integer>   compUsed = new HashSet<Integer>();
        Queue<Integer> workList = null;
        Set<Integer>   pModify  = new HashSet<Integer>();
        float volumeChange = 0;
        
        if( n.getType() == NodeType.LEAF_MIN ) 
            workList = new PriorityQueue<Integer>( 11, new ComponentComparatorAscending() );
        if( n.getType() == NodeType.LEAF_MAX ) 
            workList = new PriorityQueue<Integer>( 11, new ComponentComparatorDescending() );

        compUsed.add( n.getPosition() );
        workList.add( n.getPosition() );

        while( !workList.isEmpty() ){

            int cur = workList.poll();

            // Add to list of components modified
            pModify.add(cur);

            // If the component is the parent, we're done
            if( cur == p.getPosition() ) break;

            // Add neighbors who haven't already been processed to the process queue
            for( int neighbor : cl.get(cur).neighbors() ){
                if( !compUsed.contains( neighbor ) ){
                    workList.add(neighbor);
                    compUsed.add(neighbor);
                }
            }
        }
        
        // modify values
        for( Integer c : pModify ){
            Vertex cur = cl.get(c);
            for( int pos : cur.positions() ) {
                if( n.getType() == NodeType.LEAF_MIN ) {
                    volumeChange += Math.abs( img[pos] - p.getValue() );
                    img[pos] = Math.max( img[pos], p.getValue() );
                }
                if( n.getType() == NodeType.LEAF_MAX ) {
                    volumeChange += Math.abs( img[pos] - p.getValue() );
                    img[pos] = Math.min( img[pos], p.getValue() );
                }
            }
        }
        
        return volumeChange;
	}
	
	private void printHyperVolumn() {
	    for(int i = 0; i < ct.size(); i++){
            TopoTreeNode n = ct.getNode(i);
            if (n.isValid())
                System.out.println(n.getValue()+"; "+n.getHyperVolumn()+"; "+n.getAbsoluteHyperVolumn()+"; "+n.getType()+"; "+ct.isPruning(n));
        }
	}

	class ComponentComparatorAscending implements Comparator<Integer>{
		@Override public int compare(Integer o1, Integer o2) {
			if( cl.get(o1).value() < cl.get(o2).value() ) return -1;
			if( cl.get(o1).value() > cl.get(o2).value() ) return  1;
			return 0;
		}
	}

	class ComponentComparatorDescending implements Comparator<Integer>{
		@Override public int compare(Integer o1, Integer o2) {
			if( cl.get(o1).value() < cl.get(o2).value() ) return  1;
			if( cl.get(o1).value() > cl.get(o2).value() ) return -1;
			return 0;
		}
	}

	class CurrentFieldValueAscending implements Comparator<Integer>{
		@Override public int compare(Integer o1, Integer o2) {
			if( img[o1] < img[o2] ) return -1;
			if( img[o1] > img[o2] ) return  1;
			return 0;
		}
	}

	class CurrentFieldValueDescending implements Comparator<Integer>{
		@Override public int compare(Integer o1, Integer o2) {
			if( img[o1] < img[o2] ) return  1;
			if( img[o1] > img[o2] ) return -1;
			return 0;
		}
	}

	class OriginalFieldValueComparator implements Comparator<Integer>{
		int inv = 1;
		OriginalFieldValueComparator( boolean invert ){
			inv = invert ? -1 : 1;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			if( sf.getValue(o1) < sf.getValue(o2) ) return -1*inv;
			if( sf.getValue(o1) > sf.getValue(o2) ) return  1*inv;
			return 0;
		}
	}
}
