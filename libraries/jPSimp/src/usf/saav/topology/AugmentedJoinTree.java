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

public abstract class AugmentedJoinTree extends AugmentedJoinTreeBase implements TopoTree, Runnable {
	
	protected Mesh cl;
	
	private Comparator<? super JoinTreeNode> comparator;

	
	protected AugmentedJoinTree( Mesh cl, Comparator<? super JoinTreeNode> comparator ){
		this.cl = cl;
		this.comparator = comparator;
	}
	
	@Override
	public void run() {
		print_info_message( "Building tree..." );

		// Build a join tree.
		JoinTree jt = new JoinTree( cl, comparator );
		jt.run();

		head = processTree( jt.getRoot() );
		
		calculatePersistence();
		
//        for(int i = 0; i < size(); i++){
//            float per = getPersistence(i);
//            if( Float.isNaN(per) ) {
//                global_extreme = (AugmentedJoinTreeNode) getNode(i);
//            }
//        }
		
		head = simpleProcessTree(jt.getRoot());
		
		print_info_message( "Building tree complete" );
	}
	
    protected  AugmentedJoinTreeNode processTree(JoinTreeNode current) {
        int cumulatedVolumn = current.getVolumn();
        while (current.childCount() == 1) {
            current = current.getChild(0);
            cumulatedVolumn += current.getVolumn();
        }
        if (current.childCount() == 0) {
            return createTreeNode(current.getPosition(), current.getValue(), cumulatedVolumn);
        } else {
            AugmentedJoinTreeNode prev = processTree(current.getChild(0));
            int i = 1;
            while(i < current.childCount()) {
                AugmentedJoinTreeNode newChild = processTree(current.getChild(i));
                prev = createTreeNode(current.getPosition(), current.getValue(), 
                                      prev.getVolumn() + newChild.getVolumn(), prev, newChild);
                i++;
            }
            prev.addVolumn(cumulatedVolumn);
            return prev;
        }
    }
	
    protected AugmentedJoinTreeNode simpleProcessTree(JoinTreeNode current) {
        int cumulatedVolumn = current.getVolumn();
        while (current.childCount() == 1) {
            current = current.getChild(0);
            cumulatedVolumn += current.getVolumn();
        }
        if (current.childCount() == 0) {
            nodes.add(createTreeNode(current.getPosition(), current.getValue(), cumulatedVolumn));
            return (AugmentedJoinTreeNode)nodes.lastElement();
        } else {
            AugmentedJoinTreeNode tmp = createTreeNode(current.getPosition(), current.getValue(), cumulatedVolumn);
            for (int i = 0; i < current.childCount(); i++) {
                AugmentedJoinTreeNode newChild = simpleProcessTree(current.getChild(i));
                tmp.addChild(newChild);
                newChild.setParent(tmp);
                tmp.addVolumn(newChild.getVolumn());
            }
            nodes.add(tmp);
            return tmp;
        }
    }

    protected void calculatePersistence(){
        print_info_message( "Finding Persistence");
        
        Stack<JoinTreeNode> pstack = new Stack<JoinTreeNode>( );
        pstack.push( this.head );
        
        while( !pstack.isEmpty() ){
            JoinTreeNode curr = pstack.pop();
            
            // leaf is only thing in the stack, done
            if( pstack.isEmpty() && curr.childCount() == 0 ) {
                global_extreme = (AugmentedJoinTreeNode) curr;
                global_extreme_value = curr.getValue();
                break;
            }
            
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
    public Float getGlobalExtremeValue(){ return global_extreme_value; }

	
	public abstract class AugmentedJoinTreeNode extends JoinTreeNode implements TopoTreeNode {
		
		AugmentedJoinTreeNode( int loc, float val, int volumn ){
			super(loc, val, volumn);
		}
		
		protected AugmentedJoinTreeNode( int loc, float val, int volumn, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 ){
			super(loc, val, volumn);
			this.addChild(c0);
			this.addChild(c1);
		}

	}
	
	protected abstract AugmentedJoinTreeNode createTreeNode( int loc, float val, int volumn );
	protected abstract AugmentedJoinTreeNode createTreeNode( int loc, float val, int volumn, AugmentedJoinTreeNode c0, AugmentedJoinTreeNode c1 );

	

}
