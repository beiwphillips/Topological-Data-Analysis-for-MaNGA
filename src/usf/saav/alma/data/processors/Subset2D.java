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

import usf.saav.common.range.IntRange1D;
import usf.saav.scalarfield.ScalarField2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Subset2D.
 */
public class Subset2D extends ScalarField2D.Default {
	ScalarField2D src;
	int x0, xN;
	int y0, yN;

	/**
	 * Instantiates a new subset 2 D.
	 *
	 * @param src the src
	 * @param x_range the x range
	 * @param y_range the y range
	 */
	public Subset2D( ScalarField2D src, int [] x_range, int [] y_range ){
		this.src = src;

		if( x_range.length == 0 ){
			this.x0 = 0;
			this.xN = src.getWidth();
		}
		if( x_range.length == 1 ){
			this.x0 = 0;
			this.xN = x_range[0];
		}
		if( x_range.length >= 2 ){
			this.x0 = x_range[0];
			this.xN = x_range[1]-x_range[0]+1;
		}

		if( y_range.length == 0 ){
			this.y0 = 0;
			this.yN = src.getHeight();
		}
		if( y_range.length == 1 ){
			this.y0 = 0;
			this.yN = y_range[0];
		}
		if( y_range.length >= 2 ){
			this.y0 = y_range[0];
			this.yN = y_range[1]-y_range[0]+1;
		}
	}

	public Subset2D( ScalarField2D src, IntRange1D x_range, IntRange1D y_range ){
		this.src = src;
		this.x0 = x_range.start();
		this.xN = x_range.length();
		
		this.y0 = y_range.start();
		this.yN = y_range.length();

	}

	/* (non-Javadoc)
	 * @see usf.saav.common.algorithm.Surface2D#getWidth()
	 */
	@Override public int getWidth() { return xN; }
	
	/* (non-Javadoc)
	 * @see usf.saav.common.algorithm.Surface2D#getHeight()
	 */
	@Override public int getHeight() { return yN; }
	
	/* (non-Javadoc)
	 * @see usf.saav.alma.data.ScalarField2D#getValue(int, int)
	 */
	@Override public float getValue(int x, int y) { return src.getValue(x+x0, y+y0); }
	
	/* (non-Javadoc)
	 * @see usf.saav.alma.data.ScalarField2D.Default#getCoordinate(int, int)
	 */
	@Override public double[] getCoordinate(int x, int y) { return src.getCoordinate(x+x0, y+y0); }
}
