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
package usf.saav.mesh;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import usf.saav.common.IntegerX;
import usf.saav.common.algorithm.ArrayDisjointSet;


public class ConnectedComponentMesh extends Mesh {
	private static final long serialVersionUID = 8258598594472055291L;

	private Mesh oldComp;
	private ArrayDisjointSet djs;
	private Vector<ConnectedComponent> tmpVec;

	public ConnectedComponentMesh(Mesh oldComp){
	    this.oldComp = oldComp;
		djs = new ArrayDisjointSet( oldComp.size() );

		for( int i = 0; i < oldComp.size(); i++ ){
			float v0 = oldComp.get(i).value();
			for( int n : oldComp.get(i).neighbors() ){
				float v1 = oldComp.get(n).value();

				if( v0 == v1 ){
					djs.union(i, n);
				}
				if( Float.isNaN(v0) && Float.isNaN(v1) ){
					djs.union(i, n);
				}

			}
		}

		tmpVec = new Vector<ConnectedComponent>();
		for( int i = 0; i < oldComp.size(); i++ ){
			if( djs.find(i) == i ){
				ConnectedComponent m = new ConnectedComponent( size(), oldComp.get(i) );
				add(m);
				tmpVec.add( m );
			}
			else{
				tmpVec.add( tmpVec.get( djs.find(i) ) );
				tmpVec.get( djs.find(i) ).add( oldComp.get(i) );
			}
		}
	}
    
	@Override
	public int getVolumn() {
	    return oldComp.size();
	}

	public class ConnectedComponent extends Vector<Vertex> implements Vertex {
		private static final long serialVersionUID = -4014406590124257973L;
		private int id;

		ConnectedComponent( int id, Vertex c ){
			this.id = id;
			add(c);
		}

		@Override
		public float value() {
			return get(0).value();
		}
		
		@Override
		public int size() {
		    return super.size();
		}
		
		@Override
		public float integral() {
		    float sum = 0f; 
	        for (Vertex v : this) {
	            sum += v.value();
	        }
	        return sum;
		}

		@Override
		public int[] neighbors() {
			Set<Integer> set = new HashSet<Integer>();
			for( Vertex c : this ){
				for(int n : c.neighbors()){
					set.add(tmpVec.get(n).id);
				}
			}
			set.remove(id);

			return IntegerX.IntegerCollectionToArray(set);
		}

		@Override
		public int[] positions() {
			Vector<Integer> tmp = new Vector<Integer>();
			for( Vertex c : this ){
				for(int i : c.positions())
					tmp.add(i);
			}

			return IntegerX.IntegerCollectionToArray(tmp);
		}

		@Override
		public int id() {
			return id;
		}
	}
}
