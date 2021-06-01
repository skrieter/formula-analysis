/* -----------------------------------------------------------------------------
 * Formula-Analysis-Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis-Lib.
 * 
 * Formula-Analysis-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.clause.configuration.twise;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.configuration.twise.TWiseStatisticGenerator.*;
import org.spldev.formula.clause.solver.*;

/**
 * Tests whether a set of configurations achieves t-wise feature coverage.
 *
 * @author Sebastian Krieter
 */
public class TWiseConfigurationTester {

	private final TWiseConfigurationUtil util;

	private List<LiteralList> sample;
	private PresenceConditionManager presenceConditionManager;
	private int t;

	public TWiseConfigurationTester(CNF cnf) {
		if (!cnf.getClauses().isEmpty()) {
			util = new TWiseConfigurationUtil(cnf, new Sat4JSolver(cnf));
		} else {
			util = new TWiseConfigurationUtil(cnf, null);
		}

		getUtil().computeRandomSample(TWiseConfigurationGenerator.DEFAULT_RANDOM_SAMPLE_SIZE);
		if (!cnf.getClauses().isEmpty()) {
			getUtil().computeMIG(false, false);
		}
	}

	public void setNodes(List<List<ClauseList>> expressions) {
		presenceConditionManager = new PresenceConditionManager(getUtil(), expressions);
	}

	public void setNodes(PresenceConditionManager expressions) {
		presenceConditionManager = expressions;
	}

	public void setT(int t) {
		this.t = t;
	}

	public void setSample(List<LiteralList> sample) {
		this.sample = sample;
	}

	public List<LiteralList> getSample() {
		return sample;
	}

	/**
	 * Creates statistic values about covered combinations.<br>
	 * To get a percentage value of covered combinations use:<br>
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	CoverageStatistic coverage = getCoverage();
	 * 	double covered = (double) coverage.getNumberOfCoveredConditions() / coverage.getNumberOfValidConditions();
	 * }
	 * </pre>
	 *
	 *
	 * @return a statistic object containing multiple values:<br>
	 *         <ul>
	 *         <li>number of valid combinations
	 *         <li>number of invalid combinations
	 *         <li>number of covered combinations
	 *         <li>number of uncovered combinations
	 *         <li>value of each configuration
	 *         </ul>
	 */
	public CoverageStatistic getCoverage() {
		final List<CoverageStatistic> coveragePerSample = new TWiseStatisticGenerator(util).getCoverage(Arrays.asList(
			sample),
			presenceConditionManager.getGroupedPresenceConditions(), t, ConfigurationScore.NONE, true);
		return coveragePerSample.get(0);
	}

	public ValidityStatistic getValidity() {
		final List<ValidityStatistic> validityPerSample = new TWiseStatisticGenerator(util).getValidity(Arrays.asList(
			sample));
		return validityPerSample.get(0);
	}

	public boolean hasUncoveredConditions() {
		final List<ClauseList> uncoveredConditions = getUncoveredConditions(true);
		return !uncoveredConditions.isEmpty();
	}

	public ClauseList getFirstUncoveredCondition() {
		final List<ClauseList> uncoveredConditions = getUncoveredConditions(true);
		return uncoveredConditions.isEmpty() ? null : uncoveredConditions.get(0);
	}

	public List<ClauseList> getUncoveredConditions() {
		return getUncoveredConditions(false);
	}

	private List<ClauseList> getUncoveredConditions(boolean cancelAfterFirst) {
		final ArrayList<ClauseList> uncoveredConditions = new ArrayList<>();
		final TWiseCombiner combiner = new TWiseCombiner(getUtil().getCnf().getVariableMap().size());
		ClauseList combinedCondition = new ClauseList();

		groupLoop: for (final List<PresenceCondition> expressions : presenceConditionManager
			.getGroupedPresenceConditions()) {
			for (final ICombinationIterator iterator = new LexicographicIterator(t, expressions); iterator.hasNext();) {
				final PresenceCondition[] clauseListArray = iterator.next();
				if (clauseListArray == null) {
					break;
				}

				combinedCondition.clear();
				combiner.combineConditions(clauseListArray, combinedCondition);
				if (!TWiseConfigurationUtil.isCovered(combinedCondition, sample) && getUtil().isCombinationValid(
					combinedCondition)) {
					uncoveredConditions.add(combinedCondition);
					combinedCondition = new ClauseList();
					if (cancelAfterFirst) {
						break groupLoop;
					}
				}

			}
		}
		return uncoveredConditions;
	}

	public boolean hasInvalidSolutions() {
		final List<LiteralList> invalidSolutions = getInvalidSolutions(true);
		return !invalidSolutions.isEmpty();
	}

	public LiteralList getFirstInvalidSolution() {
		final List<LiteralList> invalidSolutions = getInvalidSolutions(true);
		return invalidSolutions.isEmpty() ? null : invalidSolutions.get(0);
	}

	public List<LiteralList> getInvalidSolutions() {
		return getInvalidSolutions(false);
	}

	private List<LiteralList> getInvalidSolutions(boolean cancelAfterFirst) {
		final ArrayList<LiteralList> invalidSolutions = new ArrayList<>();
		configLoop: for (final LiteralList solution : sample) {
			for (final LiteralList clause : getUtil().getCnf().getClauses()) {
				if (!solution.hasDuplicates(clause)) {
					invalidSolutions.add(solution);
					if (cancelAfterFirst) {
						break configLoop;
					}
				}
			}
		}
		return invalidSolutions;
	}

	public TWiseConfigurationUtil getUtil() {
		return util;
	}

}
