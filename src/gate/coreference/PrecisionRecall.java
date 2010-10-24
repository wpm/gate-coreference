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
 * Precision and recall scores
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
