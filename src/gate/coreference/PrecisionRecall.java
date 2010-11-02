/**
 * This file is part of the GATE Coreference Plugin.
 *
 * The GATE Coreference Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *   
 * The GATE Coreference Plugin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *   
 * You should have received a copy of the GNU General Public License along with the GATE
 * Coreference Plugin.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2010 W.P. McNeill
 */

package gate.coreference;

import java.util.Formatter;

/**
 * Precision and recall.
 * <p>
 * This object contains precision and recall scores and calculates F-score.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class PrecisionRecall {
	private double precision;
	private double recall;

	/**
	 * @param precision
	 *            precision score
	 * @param recall
	 *            recall score
	 */
	public PrecisionRecall(double precision, double recall) {
		this.precision = precision;
		this.recall = recall;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		Formatter formatter = new Formatter(s);
		formatter.format("Precision = %f, Recall = %f, F-score = %f",
				precision, recall, getFScore());
		return s.toString();
	}

	/**
	 * Precision/recall scores are equal if their precision and recall values
	 * are the same.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if ((null != other) && other.getClass().equals(this.getClass())) {
			double otherPrecision = ((PrecisionRecall) other).getPrecision();
			double otherRecall = ((PrecisionRecall) other).getRecall();
			// NaN values are equivalent.
			return ((precision == otherPrecision) || (Double.isNaN(precision) && Double
					.isNaN(otherPrecision)))
					&& ((recall == otherRecall) || (Double.isNaN(recall) && Double
							.isNaN(otherRecall)));
		}
		return false;
	}

	/**
	 * Create a hash code consistent with equals.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Multiply each value by 100,000 to include five decimal places in the
		// integer representation.
		int hash = (int) (precision * 100000);
		// Multiply by a prime to spread the combined values.
		hash *= 17;
		hash += (int) (recall * 100000);
		return super.hashCode();
	}

	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}

	/**
	 * @param recall
	 *            the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}

	/**
	 * Default F-score
	 * 
	 * @return harmonic mean of precision and recall
	 */
	public double getFScore() {
		return getFScore(1);
	}

	/**
	 * Weighted default F-score
	 * 
	 * @param beta
	 *            weighting factor
	 * @return weighted harmonic mean of precision and recall
	 */
	public double getFScore(double beta) {
		double betaSquared = Math.pow(beta, 2);
		return (1 + betaSquared) * precision * recall
				/ (betaSquared * (precision + recall));
	}
}
