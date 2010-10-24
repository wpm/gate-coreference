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
 * Micro and macro averages for a set of B-cubed scores.
 * 
 * @author <a href="mailto:billmcn@gmail.com">W.P. McNeill</a>
 */
public class BCubedPrecisionRecallAverages implements PrecisionRecallAverages {
	List<PrecisionRecall> scores = new LinkedList<PrecisionRecall>();
	List<Double> elementPrecisions = new LinkedList<Double>();
	List<Double> elementRecalls = new LinkedList<Double>();

	@Override
	public Iterable<PrecisionRecall> getScores() {
		return scores;
	}

	@Override
	public PrecisionRecall getMicroAverage() {
		double precision = 0;
		double recall = 0;
		for (PrecisionRecall score : scores) {
			precision += score.getPrecision();
			recall += score.getRecall();
		}
		int n = scores.size();
		precision /= n;
		recall /= n;
		return new PrecisionRecall(precision, recall);
	}

	@Override
	public PrecisionRecall getMacroAverage() {
		double precision = 0;
		double recall = 0;

		for (double elementPrecision : elementPrecisions)
			precision += elementPrecision;
		precision /= elementPrecisions.size();

		for (double elementRecall : elementRecalls)
			recall += elementRecall;
		recall /= elementRecalls.size();

		return new PrecisionRecall(precision, recall);
	}

	/**
	 * @param score
	 *            precision and recall for a pair of equivalence sets
	 * @param elementPrecisions
	 *            element precision scores
	 * @param elementRecalls
	 *            element recall scores
	 */
	public void addScores(PrecisionRecall score,
			List<Double> elementPrecisions, List<Double> elementRecalls) {
		scores.add(score);
		this.elementPrecisions.addAll(elementPrecisions);
		this.elementRecalls.addAll(elementRecalls);
	}
}
