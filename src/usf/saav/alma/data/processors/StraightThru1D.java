/*
 *     ALMA TDA - Contour tree based simplification and visualization for ALMA
 *     data cubes.
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
package usf.saav.alma.data.processors;

import usf.saav.scalarfield.ScalarField1D;


// TODO: Auto-generated Javadoc
/**
 * The Class StraightThru1D.
 */
public class StraightThru1D implements ScalarField1D {

	ScalarField1D src;

	/**
	 * Instantiates a new straight thru 1 D.
	 *
	 * @param src the src
	 */
	public StraightThru1D( ScalarField1D src ){
		this.src = src;
	}

	/* (non-Javadoc)
	 * @see usf.saav.common.algorithm.Surface1D#getWidth()
	 */
	@Override public int getWidth() { return src.getSize(); }
	
	/* (non-Javadoc)
	 * @see usf.saav.alma.data.ScalarFieldND#getSize()
	 */
	@Override public int getSize() { return src.getSize(); }
	
	/* (non-Javadoc)
	 * @see usf.saav.alma.data.ScalarFieldND#getValue(int)
	 */
	@Override public float getValue(int nodeID) { return src.getValue(nodeID); }
}
