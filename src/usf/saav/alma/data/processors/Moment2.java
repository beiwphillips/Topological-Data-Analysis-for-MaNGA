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

import java.util.Arrays;

import usf.saav.scalarfield.ScalarField2D;
import usf.saav.scalarfield.ScalarField3D;


// TODO: Auto-generated Javadoc
/**
 * The Class Moment2.
 */
public class Moment2 extends ScalarField2D.Default {

	int w,h;
	double [] data;
	ScalarField3D src;
	Moment0 m0;
	Moment1 m1;

	/**
	 * Instantiates a new moment 2.
	 *
	 * @param src the src
	 */
	public Moment2( ScalarField3D src ){
		this( src, new Moment0(src) );
	}

	public Moment2( ScalarField3D src, Moment0 mom0 ){
		this( src,mom0,new Moment1(src,mom0));
	}

	public Moment2( ScalarField3D src, Moment0 mom0, Moment1 mom1 ){

		this.src = src;
		this.m0 = mom0;
		this.m1 = mom1;
		w = src.getWidth();
		h = src.getHeight();
		data = new double[w*h];

		Arrays.fill(data, Double.NaN);
	}

	/* (non-Javadoc)
	 * @see usf.saav.common.algorithm.Surface2D#getWidth()
	 */
	@Override public int getWidth() { return w; }
	
	/* (non-Javadoc)
	 * @see usf.saav.common.algorithm.Surface2D#getHeight()
	 */
	@Override public int getHeight() { return h; }
	
	/* (non-Javadoc)
	 * @see usf.saav.alma.data.ScalarField2D#getValue(int, int)
	 */
	@Override public float getValue(int x, int y) {
		if( Double.isNaN(data[y*w+x]) ){
			double denom = 0;
			data[y*w+x] = 0;
			
			//if( m0.getValue(x,y) < 2.5 ) return (float) data[y*w+x];


			for( int z = 0; z < src.getDepth(); z++){
						double m = m1.getValue(x, y);
						double av = Math.abs(src.getValue(x, y, z));
						data[y*w+x]  += (z-m)*(z-m)*av;
						denom += av;
			}
			data[y*w+x] = Math.sqrt( data[y*w+x]/denom );
		}
		return (float) data[y*w+x];
	}

}
