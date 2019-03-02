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
 
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface TopoTreeNode {


	public enum NodeType {
		LEAF_MIN, LEAF_MAX, MERGE, SPLIT, UNKNOWN, LEAF, SADDLE, NONCRITICAL
	}
	
	
	NodeType getType();
	
	public void addChild( JoinTreeNode c );
	public void addChildren(Collection<JoinTreeNode> c);
    public JoinTreeNode getChild( int idx );
    public List<JoinTreeNode> getChildren();
    public int getChildCount();
    public boolean isChild(JoinTreeNode node);
    public int childCount();
    public boolean hasChildren();
    public void removeChild(JoinTreeNode node);

	void setPartner(JoinTreeNode jtn);
	TopoTreeNode getPartner();
	
	void setParent(JoinTreeNode p);
	TopoTreeNode getParent();
	boolean hasParent();

	float getBirth();
	float getDeath();
	float getPersistence();
	float getSimplePersistence();
	
	int getPosition();
    float getValue();
	int getVolumn();
	

	
	
    public static class ComparePersistenceAscending implements Comparator<TopoTreeNode> {
        @Override public int compare(TopoTreeNode o1, TopoTreeNode o2) {
            if( o1.getPersistence() > o2.getPersistence() ) return  1;
            if( o1.getPersistence() < o2.getPersistence() ) return -1;
            return 0;
        }
    }

    public static class ComparePersistenceDescending implements Comparator<TopoTreeNode> {
        @Override public int compare(TopoTreeNode o1, TopoTreeNode o2) {
            if( o1.getPersistence() < o2.getPersistence() ) return  1;
            if( o1.getPersistence() > o2.getPersistence() ) return -1;
            return 0;
        }
    }
    
    
    public static class CompareSimplePersistenceAscending implements Comparator<TopoTreeNode> {
        @Override public int compare(TopoTreeNode o1, TopoTreeNode o2) {
            if( o1.getSimplePersistence() > o2.getSimplePersistence() ) return  1;
            if( o1.getSimplePersistence() < o2.getSimplePersistence() ) return -1;
            return 0;
        }
    }
    
    public static class CompareSimplePersistenceDescending implements Comparator<TopoTreeNode> {
        @Override public int compare(TopoTreeNode o1, TopoTreeNode o2) {
            if( o1.getSimplePersistence() < o2.getSimplePersistence() ) return  1;
            if( o1.getSimplePersistence() > o2.getSimplePersistence() ) return -1;
            return 0;
        }
    }
    

	public static class CompareVolumnAscending implements Comparator<TopoTreeNode> {
        @Override
        public int compare(TopoTreeNode o1, TopoTreeNode o2) {
            if (o1.getVolumn() > o2.getVolumn()) return 1;
            if (o1.getVolumn() < o2.getVolumn()) return -1;
            return 0;
        }
    }
	
	public static class CompareVolumnDescending implements Comparator<TopoTreeNode> {
        @Override
        public int compare(TopoTreeNode o1, TopoTreeNode o2) {
            if (o1.getVolumn() < o2.getVolumn()) return 1;
            if (o1.getVolumn() > o2.getVolumn()) return -1;
            return 0;
        }
    }
	
}

