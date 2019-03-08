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

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import usf.saav.common.algorithm.ArrayDisjointSet;
import usf.saav.mesh.Mesh;
import usf.saav.mesh.ScalarFieldMesh;
import usf.saav.scalarfield.ScalarField2D;


public class JoinTree implements Runnable {
 
	private   Comparator<? super JoinTreeNode> comparator;
	private   Mesh sf;
	private   int width;
	private   JoinTreeNode head;
	protected boolean operationComplete = false;

	public JoinTree( Mesh sf ) {
		this.sf = sf;
		this.comparator = new JoinTreeNode.ComparatorValueAscending();
	}
	
	protected JoinTree( Mesh sf, Comparator<? super JoinTreeNode> comparator  ) {
		this.sf = sf;
		this.comparator = comparator;
	}

	
	public JoinTreeNode getRoot( ){
		if( !operationComplete ) return null;
		return head;
	}
		
	public String toString( ){
		if( head == null ){ return "<empty>"; }
		return head.toString();
	}


	@Override
	public void run() {
		
		if( operationComplete ) return;

		this.width = sf.size();
		JoinTreeNode [] grid;
		grid = new JoinTreeNode[width];

		// We first order the points for adding to the tree.
		Queue< JoinTreeNode > tq = new PriorityQueue< JoinTreeNode >( width, comparator );
		for(int i = 0; i < sf.size(); i++ ){
			tq.add( new JoinTreeNode( i, sf.get(i).value(), sf.get(i).size() ) );
		}
		
		// Disjoint Set used to mark which set a points belongs to
		ArrayDisjointSet dj = new ArrayDisjointSet( sf.size() );
		
		// Mask for marking who has been processed
		boolean [] bm = new boolean[sf.size()];
		Arrays.fill(bm, false );
		
		// start popping elements off the of the list
		while( tq.size() > 0 ){
			head = tq.poll();
			init_mergeWithNeighbors( grid, head, sf, bm, dj );
			bm[head.getPosition()] = true;
		}
		
		operationComplete = true;
		
	}
	

	private void init_mergeWithNeighbors( JoinTreeNode [] grid, JoinTreeNode me, Mesh sf, boolean [] bm, ArrayDisjointSet dj ) {
		
		int [] neighbors = sf.get( me.getPosition() ).neighbors();
		
		// set any neighbor sets as children
		for( int n : neighbors ){
			if( bm[ n ] ){
				int setIdx = dj.find( n );
				if( !me.isChild( grid[setIdx] ) ){
					me.addChild( grid[setIdx] );
					grid[setIdx].setParent(me);
				}
			}
		}

		// update the disjoint set with new connection
		for( int n : neighbors ){
			if( bm[ n ] ){
				dj.union( me.getPosition(), n );
			}
		}
		
		// update the root for the set
		grid[ dj.find( me.getPosition() ) ] = me;

	}
	
	public static void main( String args[] ){
		ScalarField2D sf = new ScalarField2D.RandomField( 10, 10 );
		JoinTree jt = new JoinTree( new ScalarFieldMesh( sf ) );
		System.out.println(sf.toString());
		System.out.println(jt);
	}
	
}
