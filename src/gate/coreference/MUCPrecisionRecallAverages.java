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

import java.util.LinkedList;
import java.util.List;

/**
 * Precision and recall micro and macro averages for MUC scores.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class MUCPrecisionRecallAverages extends PrecisionRecallAverages {
	List<Integer> precisionNumeratorTerms = new LinkedList<Integer>();
	List<Integer> precisionDenominatorTerms = new LinkedList<Integer>();
	List<Integer> recallNumeratorTerms = new LinkedList<Integer>();
	List<Integer> recallDenominatorTerms = new LinkedList<Integer>();

	/**
	 * @param numeratorTerms
	 * 
	 * @param denominatorTerms
	 */
	public void addPrecisionTerms(List<Integer> numeratorTerms,
			List<Integer> denominatorTerms) {
		this.precisionNumeratorTerms.addAll(numeratorTerms);
		this.precisionDenominatorTerms.addAll(denominatorTerms);
	}

	/**
	 * @param numeratorTerms
	 * 
	 * @param denominatorTerms
	 */
	public void addRecallTerms(List<Integer> numeratorTerms,
			List<Integer> denominatorTerms) {
		this.recallNumeratorTerms.addAll(numeratorTerms);
		this.recallDenominatorTerms.addAll(denominatorTerms);
	}

	@Override
	public PrecisionRecall getMacroAverage() {
		double precision = calculateRatioFromTerms(precisionNumeratorTerms,
				precisionDenominatorTerms);
		double recall = calculateRatioFromTerms(recallNumeratorTerms,
				recallDenominatorTerms);

		return new PrecisionRecall(precision, recall);
	}

	/**
	 * Calculate a ratio given the terms in its numerator and denominator.
	 * 
	 * @param numeratorTerms
	 *            terms in the numerator
	 * @param denominatorTerms
	 *            terms in the denominator
	 * @return sum of numerator terms divided by sum of denominator terms
	 */
	private double calculateRatioFromTerms(List<Integer> numeratorTerms,
			List<Integer> denominatorTerms) {
		double numerator = 0;
		double denominator = 0;

		for (double term : numeratorTerms)
			numerator += term;
		for (double term : denominatorTerms)
			denominator += term;

		return numerator / denominator;
	}

}
