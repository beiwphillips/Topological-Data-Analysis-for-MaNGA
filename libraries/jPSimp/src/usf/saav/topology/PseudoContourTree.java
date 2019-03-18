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

import java.text.DecimalFormat;

import usf.saav.common.BasicObject;
import usf.saav.mesh.Mesh;
import usf.saav.mesh.ScalarFieldMesh;
import usf.saav.scalarfield.ScalarField2D;
import usf.saav.topology.AugmentedJoinTree.AugmentedJoinTreeNode;

public class PseudoContourTree extends BasicObject implements TopoTree {

	private MergeTree mt;
	private SplitTree st;
	private AugmentedJoinTreeNode global_min;
	private AugmentedJoinTreeNode global_max;
	private float max_persistence = 0;
	private int max_volumn = 0;
	private float max_hypervolumn = 0;
	private float simplify = 0.0f;
	private String metric = "persistence";
	
	public PseudoContourTree( Mesh sf ){
		this(sf,false);
	}
	
	public PseudoContourTree( Mesh sf, boolean verbose){
		super(verbose);
		print_info_message("Building Contour Tree");
		
		this.mt = new MergeTree(sf);
		this.st = new SplitTree(sf);
		
		Thread t1 = new Thread( mt );
		Thread t2 = new Thread( st );
		
		t1.run();
		t2.run();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Merge Tree Nodes: "+mt.size());
		System.out.println("Merge Tree Max Persistence: "+mt.getMaxPersistence());
		System.out.println("Merge Tree Max Volumn: "+mt.getMaxVolumn());
		System.out.println("Merge Tree Max Hyper Volumn: "+mt.getMaxHyperVolumn());
		System.out.println("Split Tree Nodes: "+st.size());
		System.out.println("Split Tree Max Persistence: "+st.getMaxPersistence());
		System.out.println("Split Tree Max Volumn: "+st.getMaxVolumn());
		System.out.println("Split Tree Max Hyper Volumn: "+st.getMaxHyperVolumn());
		
		max_persistence = Math.max( mt.getMaxPersistence(), st.getMaxPersistence() );
		max_volumn = Math.max(mt.getMaxVolumn(), st.getMaxVolumn());
		max_hypervolumn = Math.max(mt.getMaxHyperVolumn(), st.getMaxHyperVolumn());
		global_min = mt.getGlobalExtreme();
		global_max = st.getGlobalExtreme();
		
		if( global_min != null && global_max != null ){
			global_min.setPartner(global_max);
			global_max.setPartner(global_min);
		}
		else{
			print_error_message("Error finding global min and/or max");
		}
	}

	
	public void setSimplificationLevel( float threshold ){ simplify = threshold; }
	public void setSimplificationMetric( String metric ){ this.metric = metric; }
	public float getSimplificationLevel( ){ return simplify; }
	public String getSimplificationMetric( ){ return metric; }
	
	public float getMaxPersistence(){ return max_persistence; }
	public int getMaxVolumn(){ return max_volumn; }
	public float getMaxHyperVolumn(){ return max_hypervolumn; }
	public AugmentedJoinTreeNode getGlobalMin(){ return global_min; }
	public AugmentedJoinTreeNode getGlobalMax(){ return global_max; }
	
	@Override
	public int size() {
		return mt.size() + st.size();
	}

	@Override
	public float getBirth(int i) {
		if( i < mt.size() )
			return mt.getBirth(i);
		return st.getBirth(i-mt.size());
	}

	@Override
	public float getDeath(int i) {
		if( i < mt.size() )
			return mt.getDeath(i);
		return st.getDeath(i-mt.size());
	}

	@Override
	public float getPersistence(int i) {
		if( i < mt.size() )
			return mt.getPersistence(i);
		return st.getPersistence(i-mt.size());
	}
	
	@Override
	public float getSimplePersistence(int i) {
	    if ( i < mt.size() )
	        return mt.getSimplePersistence(i);
	    return st.getSimplePersistence(i-mt.size());
	}
	
	@Override
    public int getVolumn(int i) {
        if (i < mt.size())
            return mt.getVolumn(i);
        return st.getVolumn(i-mt.size());
    }
    
	@Override
    public float getHyperVolumn(int i) {
        if (i < mt.size())
            return mt.getHyperVolumn(i);
        return st.getHyperVolumn(i-mt.size());
    }

	public TopoTreeNode getNode(int i) {
		if( i < mt.size() )
			return mt.getNode(i);
		return st.getNode(i-mt.size());
	}
	
	public boolean isActive(int i) {
	    if (metric.equals("persistence"))
	        return getPersistence(i) > simplify * max_persistence;
	    else if (metric.equals("volumn"))
	        return getVolumn(i) > simplify * max_volumn;
	    else if (metric.equals("hypervolumn"))
	        return getHyperVolumn(i) > simplify * max_hypervolumn;
        return false;
	}
       
    public boolean isPruning(int i) {
        if (metric.equals("persistence"))
            return getSimplePersistence(i) <= simplify * max_persistence;
        else if (metric.equals("volumn"))
            return getVolumn(i) <= simplify * max_volumn;
        else if (metric.equals("hypervolumn"))
            return getHyperVolumn(i) <= simplify * max_hypervolumn;
        return false;
    }
    
    public boolean isPruning(TopoTreeNode n) {
        if (metric.equals("persistence"))
            return n.getSimplePersistence() <= simplify * max_persistence;
        else if (metric.equals("volumn"))
            return n.getVolumn() <= simplify * max_volumn;
        else if (metric.equals("hypervolumn"))
            return n.getHyperVolumn() <= simplify * max_hypervolumn;
        return false;
    }

	public boolean checkTree() {
	    return mt.checkTree() && st.checkTree();
	}
	
	
	


	public static void main(String [] args ){

		int  sf_size   = 256;
		long total_exp = 200;

		DecimalFormat df = new DecimalFormat( "#.00" );
		long start,stop;
		long total_time = 0;
		
		for(int curr = 0; curr < total_exp; curr++)
		{
			ScalarField2D sf = new ScalarField2D.RandomField( sf_size, sf_size, curr );
			
			start=System.nanoTime();
			new PseudoContourTree( new ScalarFieldMesh( sf ) );
			stop=System.nanoTime();

			System.out.println( "PseudoContourTree ("+ sf.getWidth() + " x " + sf.getHeight() + ") " + 
								"Time: " + df.format( (double)(stop-start)*1.0e-6) + " ms" );
			
			total_time += (stop-start);
		}

		System.out.println("Average PseudoContourTree Time: " + df.format( (double)(total_time/total_exp)*1.0e-6) + " ms");

	}
	
	
}
