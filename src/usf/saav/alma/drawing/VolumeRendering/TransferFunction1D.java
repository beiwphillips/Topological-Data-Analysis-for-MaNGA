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
package usf.saav.alma.drawing.VolumeRendering;

import usf.saav.common.monitoredvariables.MonitoredTrigger;
import usf.saav.common.types.Float4;

// TODO: Auto-generated Javadoc
/**
 * The Interface TransferFunction1D.
 */
public interface TransferFunction1D  {

	public int size();
	public Float4 get( int idx );
	public float getOffset( );
	public float getScale( );
	
	public void addModifiedCallback( Object obj, String func );

	/**
	 * The Class Default.
	 */
	public class Default implements TransferFunction1D {

		private static final Float4 transferFunc[] = {
									new Float4 ( 0.0f, 0.0f, 0.0f, 0.0f ),
									new Float4 ( 1.0f, 0.0f, 0.0f, 1.0f ),
									new Float4 ( 1.0f, 0.5f, 0.0f, 1.0f ),
									new Float4 ( 1.0f, 1.0f, 0.0f, 1.0f ),
									new Float4 ( 0.0f, 1.0f, 0.0f, 1.0f ),
									new Float4 ( 0.0f, 1.0f, 1.0f, 1.0f ),
									new Float4 ( 0.0f, 0.0f, 1.0f, 1.0f ),
									new Float4 ( 1.0f, 0.0f, 1.0f, 1.0f ),
									new Float4 ( 0.0f, 0.0f, 0.0f, 0.0f )
							};

		/* (non-Javadoc)
		 * @see usf.saav.alma.drawing.VolumeRendering.TransferFunction1D#size()
		 */
		@Override
		public int size() {
			return transferFunc.length;
		}

		/* (non-Javadoc)
		 * @see usf.saav.alma.drawing.VolumeRendering.TransferFunction1D#get(int)
		 */
		@Override
		public Float4 get(int idx) {
			return transferFunc[idx];
		}

		/* (non-Javadoc)
		 * @see usf.saav.alma.drawing.VolumeRendering.TransferFunction1D#getOffset()
		 */
		@Override
		public float getOffset() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see usf.saav.alma.drawing.VolumeRendering.TransferFunction1D#getScale()
		 */
		@Override
		public float getScale() {
			return 1;
		}
		
		MonitoredTrigger modifiedCB = new MonitoredTrigger( );

		public void addModifiedCallback( Object obj, String func ){
			modifiedCB.addMonitor( obj,  func );
		}

	}
}
