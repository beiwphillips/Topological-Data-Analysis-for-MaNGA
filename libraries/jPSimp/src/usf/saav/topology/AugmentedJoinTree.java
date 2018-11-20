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

import java.util.Comparator;
import java.util.Stack;

import usf.saav.mesh.Mesh;
import usf.saav.topology.TopoTree;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.JoinTree.Node;

public abstract class AugmentedJoinTree extends AugmentedJoinTreeBase implements TopoTree, Runnable {
	
	protected AugmentedJoinTreeNode global_extreme;
	protected Mesh cl;
	
	private Comparator<? super Node> comparator;

	
	protected AugmentedJoinTree( Mesh cl, Comparator<? super Node> comparator ){
		this.cl = cl;
		this.comparator = comparator;
	}
	
	@Override
	public void run() {
		print_info_message( "Building tree..." );

		// Build a join tree.
		JoinTree jt = new JoinTree( cl, comparator );
		jt.run();

		head = processTreeV2( jt.getRoot() );
		
		calculatePersistence();
		
		for(int i = 0; i < size(); i++){
			float per = getPersistence(i);
			if( Float.isNaN(per) )
				global_extreme = (AugmentedJoinTreeNode) getNode(i);
		}
		
		print_info_message( "Building tree complete" );
	}
	

	protected  AugmentedJoinTreeNode processTreeV2(JoinTreeNode current) {
	    while (current.childCount() == 1) {
	        current = current.getChild(0);
	    }
	    if (current.childCount() == 0) {
	        nodes.add(createTreeNode(current.getPosition(), current.getValue()));
	        return (AugmentedJoinTreeNode)nodes.lastElement();
	    } else {
	        AugmentedJoinTreeNode prev = processTreeV2(current.getChild(0));
	        int i = 1;
	        while(i < current.childCount()) {
	            prev = createTreeNode(current.getPosition(), current.getValue(), 
	                                  prev, processTreeV2(current.getChild(i)));
	            nodes.add(prev);
	            i++;
	        }
	        return prev;
	    }
	}
	
	protected AugmentedJoinTreeNode processTree( JoinTreeNode current ){
		
		while( current.childCount() == 1 ){
			current = current.getChild(0);
		}
		if( current.childCount() == 0 ){
			nodes.add( createTreeNode( current.getPosition(), current.getValue() ) );
			return (AugmentedJoinTreeNode)nodes.lastElement();
		}
		if( current.childCount() == 2 ){
			nodes.add( createTreeNode( current.getPosition(), current.getValue(),
							processTree( current.getChild(0) ),
							processTree( current.getChild(1) ) 
						) );
			return (AugmentedJoinTreeNode)nodes.lastElement();
		}
		// Monkey saddle --- should probably do something a little smarter here
		if( current.childCount() == 3 ){
			AugmentedJoinTreeNode child = createTreeNode( current.getPosition(), current.getValue(),
														processTree( current.getChild(0) ),
														processTree( current.getChild(1) ) 
													);
			AugmentedJoinTreeNode parent = createTreeNode( current.getPosition(), current.getValue(),
														child,
														processTree( current.getChild(2) ) 
				);
			nodes.add(child);
			nodes.add(parent);
			
			return parent;
		}
		// 4-way saddle --- yicks!
		if( current.childCount() == 4 ){
			AugmentedJoinTreeNode child1 = createTreeNode( current.getPosition(), current.getValue(),
														processTree( current.getChild(0) ),
														processTree( current.getChild(1) ) 
													);
			AugmentedJoinTreeNode child0 = createTreeNode( current.getPosition(), current.getValue(),
														child1,
														processTree( current.getChild(2) ) 
				);

			AugmentedJoinTreeNode parent = createTreeNode( current.getPosition(), current.getValue(),
														child0,
														processTree( current.getChild(3) ) 
				);

			nodes.add(child1);
			nodes.add(child0);
			nodes.add(parent);
			
			return parent;
		}
		
		print_warning_message( "Error, split with " + current.childCount() + " children" );
		return null;
	}
	

    protected void calculatePersistence(){
        print_info_message( "Finding Persistence");
        
        Stack<JoinTreeNode> pstack = new Stack<JoinTreeNode>( );
        pstack.push( this.head );
        
        while( !pstack.isEmpty() ){
            JoinTreeNode curr = pstack.pop();
            
            // leaf is only thing in the stack, done
            if( pstack.isEmpty() && curr.childCount() == 0 ) break;
            
            // saddle point, push children onto stack
            if( curr.childCount() == 2 ){
                pstack.push(curr);
                pstack.push((JoinTreeNode)curr.getChild(0));
                pstack.push((JoinTreeNode)curr.getChild(1));
            }
            
            // leaf node, 2 options
            if( curr.childCount() == 0 ) {
                JoinTreeNode sibling = pstack.pop();
                JoinTreeNode parent  = pstack.pop();
                
                // sibling is a saddle, restack.
                if( sibling.childCount() == 2 ){
                    pstack.push( parent );
                    pstack.push( curr );
                    pstack.push( sibling );
                }
                
                // sibling is a leaf, we can match a partner.
                if( sibling.childCount() == 0 ){
                    // curr value is closer to parent than sibling
                    if( Math.abs(curr.getValue()-parent.getValue()) < Math.abs(sibling.getValue()-parent.getValue()) ){
                        curr.setPartner(parent);
                        parent.setPartner(curr);
                        pstack.push( sibling );
                    }
                    // sibling value is closer to parent than curr
                    else {
                        sibling.setPartner(parent);
                        parent.setPartner(sibling);
                        pstack.push( curr );
                    }
                    max_persistence = Math.max(max_persistence,parent.getPersistence());
                }
                
            }
        }
    
        
    }
	
	public AugmentedJoinTreeNode getGlobalExtreme(){ return global_extreme; }

	
	public abstract class AugmentedJoinTreeNode extends JoinTreeNode implements TopoTreeNode {
		
		private int location;
		private float value;
		private int size;
		
		
		protected AugmentedJoinTreeNode( int loc, float val, int size ){
			this.location = loc;
			this.value = val;
			this.size = size;
		}
		
		protected AugmentedJoinTreeNode( int loc, float val, int size, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 ){
			this.location = loc;
			this.value = val;
			this.size = size;
			this.addChild(c0);
			this.addChild(c1);
		}
		
		
		@Override public int	getPosition() { return location; }
		@Override public float getValue() { return value; }
		@Override public int getSize() { return size; }

	}
	
	protected abstract AugmentedJoinTreeNode createTreeNode( int loc, float val, int size );
	protected abstract AugmentedJoinTreeNode createTreeNode( int loc, float val, int size, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 );

	

}
