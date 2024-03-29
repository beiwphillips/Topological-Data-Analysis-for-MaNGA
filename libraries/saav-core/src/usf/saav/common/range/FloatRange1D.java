/*
 *     saav-core - A (very boring) software development support library.
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
package usf.saav.common.range;


/**
 * Class for building 1D value ranges.
 * 
 * Last code review: 2005/01/20
 * 
 * @author Paul Rosen
 *
 */
public class FloatRange1D {

	private double r_min = Double.MAX_VALUE;
	private double r_max = -Double.MAX_VALUE;
	private double mean_value = 0;
	private long element_count = 0;

	/**
	 * Default constructor creates an invalid range
	 */
	public FloatRange1D() {
	}


	/**
	 * Constructor that starts with a prefixed range
	 * 
	 * @param _min
	 * @param _max
	 */
	/*
	public FloatRange1D(double _min, double _max) {
		r_min = _min;
		r_max = _max;
	}

	public FloatRange1D( XML xml ) {
		r_min = xml.getDouble( "min", r_min );
		r_max = xml.getDouble( "max", r_max );
		mean_value = xml.getDouble( "mean", mean_value );
		element_count = xml.getLong( "count", element_count );
	}
	
	public FloatRange1D( double value ) {
		r_min = value;
		r_max = value;
	}
	*/
	
	public FloatRange1D(double ... valueRange) {
		for( double d : valueRange ){
			expand(d);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public FloatRange1D clone() {
		FloatRange1D ret = new FloatRange1D();
		ret.r_min = r_min;
		ret.r_max = r_max;
		ret.mean_value = mean_value;
		ret.element_count = element_count;
		return ret;
	}

	/**
	 * Get the minimum range value
	 * 
	 * @return
	 */
	public double getMinimum() {
		return r_min;
	}

	/**
	 * Get the maximum range value
	 * 
	 * @return
	 */
	public double getMaximum() {
		return r_max;
	}

	/**
	 * Get the size of the range
	 * 
	 * @return
	 */
	public double getRange() {
		return r_max - r_min;
	}

	/**
	 * Get the mean value of the range
	 * 
	 * @return
	 */
	public double getMean() {
		return mean_value;
	}

	/**
	 * Get the exact center of the range
	 * 
	 * @return
	 */
	public double getCenter() {
		return (r_max + r_min) / 2.0;
	}

	/**
	 * Normalizes a value from [range_min,range_max] to [0,1].
	 * 
	 * @param v
	 *            The value to normalize
	 * @return
	 */
	public double getNormalized(double v) {
		if ((r_max - r_min) == 0)
			return 0.5;
		return (v - r_min) / (r_max - r_min);
	}

	/**
	 * Normalize a value from [range_min,range_max] to [min_v,max_v].
	 * 
	 * @param v
	 * @param min_v
	 * @param max_v
	 * @return
	 */
	public double getNormalizedToRange(double v, double min_v, double max_v) {
		return getNormalized(v) * (max_v - min_v) + min_v;
	}

	/**
	 * Calculate a mean centered value. This function simply offsets the value
	 * by the mean.
	 * 
	 * @param v
	 * @return
	 */
	public double getMeanCentered(double v) {
		return v - mean_value;
	}

	/**
	 * Check to see if a value is outside of the range.
	 * 
	 * @param p
	 * @return
	 */
	public boolean isOutside(float p) {
		return (p < r_min || p > r_max);
	}
	
	public boolean isOutside(double p) {
		return (p < r_min || p > r_max);
	}

	/**
	 * Check to see if a value is inside of the range.
	 * 
	 * @param p
	 * @return
	 */
	public boolean isInside(float p) {
		return (p <= r_min && p <= r_max);
	}

	public boolean isInside(double p) {
		return (p <= r_min && p <= r_max);
	}
	
	/**
	 * Expand the range based upon values of another range.
	 * 
	 * @param br
	 */
	public void expand(FloatRange1D br) {
		r_min = Math.min(br.r_min, r_min);
		r_max = Math.max(br.r_max, r_max);
		double m1 = getMean() * element_count;
		double m2 = br.getMean() * br.element_count;
		element_count += br.element_count;
		mean_value = (m1 + m2) / element_count;
	}

	/**
	 * Expand the range based upon a single value.
	 * 
	 * @param v
	 */
	public void expand(double v) {
		if (Double.isNaN(v))
			return;
		r_min = Math.min(v, r_min);
		r_max = Math.max(v, r_max);
		double m1 = getMean() * element_count + v;
		element_count++;
		mean_value = m1 / element_count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + r_min + ", " + r_max + "]";
	}

	/*
	public void serializeXML(XML xml) {
		xml.setDouble( "min", r_min );
		xml.setDouble( "max", r_max );
		xml.setDouble( "mean", mean_value );
		xml.setLong( "count", element_count );
	}
	*/

	public void expand(double[] r) {
		for( double d : r ){
			expand(d);
		}
	}


	public double clamp( double val ) {
		if( val <= this.r_min ) return this.r_min;
		if( val >= this.r_max ) return this.r_max;
		return val;
	}

}
