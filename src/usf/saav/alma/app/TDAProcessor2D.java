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
package usf.saav.alma.app;

import java.io.IOException;

import usf.saav.alma.data.fits.FitsReader;
import usf.saav.common.BasicObject;
import usf.saav.common.range.IntRange1D;
import usf.saav.mesh.ConnectedComponentMesh;
import usf.saav.mesh.ScalarFieldMesh;
import usf.saav.scalarfield.PersistenceSimplifier2D;
import usf.saav.scalarfield.ScalarField2D;
import usf.saav.scalarfield.Simplifier2D;
import usf.saav.topology.PseudoContourTree;

public class TDAProcessor2D extends BasicObject {

//	PersistenceSimplifier2D ps2d;
	Simplifier2D s2d;
	ScalarField2D slice;

	public TDAProcessor2D( ){
		super(true);
	}

	public void process( FitsReader fits, IntRange1D xr, IntRange1D yr, int z, 
	        float simplification, String metric ){

		try {
			process( fits.getSlice( xr,  yr,  z, 0 ), simplification, metric);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public void process( ScalarField2D _slice, float simplification, String metric ){
		
		slice = _slice;

		this.print_info_message("Constructing Mesh");
		ConnectedComponentMesh cl = new ConnectedComponentMesh( new ScalarFieldMesh( slice ) );
		this.print_info_message("Constructing Tree");
		PseudoContourTree ct = new PseudoContourTree( cl );

		System.out.println( "Regional Maximum Persistence: " + ct.getMaxPersistence() );

		this.print_info_message("Simplifying Tree");
		ct.setSimplificationLevel( simplification );
		ct.setSimplificationMetric( metric );
		this.print_info_message("Simplfying Field");
//		ps2d = new PersistenceSimplifier2D( slice, ct, cl, true );
		s2d = new Simplifier2D(slice, ct, cl, simplification, metric, true);

	}

}
