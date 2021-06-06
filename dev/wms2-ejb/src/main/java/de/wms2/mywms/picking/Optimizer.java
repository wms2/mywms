/* 
Copyright 2021 Matthias Krane
info@krane.engineer

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.picking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * Find best results of combinations
 * 
 * @author krane
 *
 */
public class Optimizer {
	private static final Logger logger = Logger.getLogger(Optimizer.class.getName());

	private long calculationEndTime;
	private long callCounter;
	private long maxCalculationTime = 10000;
	private long maxCalculations = Integer.MAX_VALUE;

	public void setMaxCaculations(long maxCalculations) {
		this.maxCalculations = maxCalculations;
	}

	public void setMaxCalculationTime(long maxCalculationTime) {
		this.maxCalculationTime = maxCalculationTime;
	}

	public Optimizer withMaxCalculations(long maxCalculations) {
		this.maxCalculations = maxCalculations;
		return this;
	}

	public Optimizer withMaxCalculationTime(long maxCalculationTime) {
		this.maxCalculationTime = maxCalculationTime;
		return this;
	}

	/**
	 * Find the best combination of the given candidates
	 * <p>
	 * Calculation of combinations or permutations of a list can be very expensive.
	 * 2^n operations. So this algorithm is limited to a maximal number of
	 * operations and to a maximal operation time.
	 * 
	 * @param <T>        The type of the objects to combine
	 * @param candidates The objects to combine
	 * @param score      Function to give a score value for a result. This score is
	 *                   used for optimization. The target is to get a score of 0.
	 *                   Positive and negative differences are possible, The minimal
	 *                   absolute value will be the best.
	 *                   <p>
	 *                   Parameters is the list of candidates to be scored and the
	 *                   given target value. Return of null value means, that the
	 *                   combination is not possible. Further combinations on this
	 *                   base will not be checked. <br>
	 *                   Return of 0 value means, that this combination is perfect.
	 *                   No further combinations are checked at all and the result
	 *                   is returned.
	 * @param target     This value is given to the score function
	 * @return null means that no calculation has been done.<br>
	 *         An empty List means that no valid combination can be found.<br>
	 *         A filled list is best possible combination.
	 */
	public <T> List<T> findBestCombination(List<T> candidates, BiFunction<Collection<T>, T, Long> score, T target) {
		callCounter = 0;
		calculationEndTime = System.currentTimeMillis() + maxCalculationTime;
		Long baseScore = score.apply(new ArrayList<>(), target);
		Result<T> result = combine(candidates, new Result<>(baseScore, new ArrayList<>()), score, target, 0);
		if (result != null) {
			logger.finest("findBestCombination score=" + result.score + ", target=" + target + ", values="
					+ result.candidates + ", candidates=" + candidates);
			return result.candidates;
		}
		logger.finest("findBestCombination did not find valid combination of candidates=" + candidates);
		return null;
	}

	private <T> Result<T> combine(List<T> candidates, Result<T> baseResult, BiFunction<Collection<T>, T, Long> score,
			T target, int startIndex) {
		Result<T> result = baseResult;
		for (int i = startIndex; i < candidates.size(); i++) {
			callCounter++;
			if (callCounter > maxCalculations) {
				if (startIndex == 0) {
					logger.warning("Too much combinations. Abort. Calculated " + callCounter + " combinations");
				}
				return null;
			}
			if (System.currentTimeMillis() > calculationEndTime) {
				if (startIndex == 0) {
					logger.warning("Timeout in calculation of combinations. Abort. Calculated " + callCounter
							+ " combinations in " + maxCalculationTime + " ms");
				}
				return null;
			}

			T nextCandidate = candidates.get(i);
			List<T> resultCandidates = new ArrayList<>(baseResult.candidates);
			resultCandidates.add(nextCandidate);

			Long newScore = score.apply(resultCandidates, target);
			if (newScore == null) {
				continue;
			}
			Result<T> newResult = new Result<>(newScore, resultCandidates);
			if (newResult.isPerfect()) {
				return newResult;
			}
			if (newResult.isBetter(result)) {
				result = newResult;
			}
			if (newResult.score > 0) {
				continue;
			}

			Result<T> subResult = combine(candidates, newResult, score, target, i + 1);
			if (subResult == null) {
				continue;
			}
			if (subResult.isPerfect()) {
				return subResult;
			}
			if (subResult.isBetter(result)) {
				result = subResult;
			}

		}

		return result;
	}

	private static class Result<T> {
		public long score;
		public List<T> candidates;

		public Result(long score, List<T> candidates) {
			this.score = score;
			this.candidates = candidates;
		}

		@Override
		public String toString() {
			return "(Score=" + score + ", candidates=" + candidates + ")";
		}

		public boolean isPerfect() {
			return score == 0;
		}

		public boolean isBetter(Result<T> other) {
			if (other == null) {
				return true;
			}
			long absoluteScore = score;
			if (absoluteScore < 0)
				absoluteScore *= -1;
			long otherAbsoluteScore = other.score;
			if (otherAbsoluteScore < 0)
				otherAbsoluteScore *= -1;
			return absoluteScore < otherAbsoluteScore;
		}
	}

}
