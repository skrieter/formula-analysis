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
package org.spldev.formula.clause.configuration.sample;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.configuration.twise.*;
import org.spldev.formula.clause.solver.*;

/**
 * Tests whether a set of configurations achieves t-wise feature coverage.
 *
 * @author Sebastian Krieter
 */
public class TWiseCoverageMetrics {

	public final class TWiseCoverageMetric implements SampleMetric {
		private final int t;

		public TWiseCoverageMetric(int t) {
			this.t = t;
		}

		@Override
		public double get(SolutionList sample) {
			final TWiseStatisticGenerator tWiseStatisticGenerator = new TWiseStatisticGenerator(util);

			final CoverageStatistic statistic = tWiseStatisticGenerator
				.getCoverage(Arrays.asList(sample.getSolutions()), //
					presenceConditionManager.getGroupedPresenceConditions(), //
					t, //
					TWiseStatisticGenerator.ConfigurationScore.NONE, //
					true).get(0);

			final long numberOfValidConditions = statistic.getNumberOfValidConditions();
			final long numberOfCoveredConditions = statistic.getNumberOfCoveredConditions();
			if (numberOfValidConditions == 0) {
				return 1;
			} else {
				return (double) numberOfCoveredConditions / numberOfValidConditions;
			}
		}

		@Override
		public String getName() {
			return "T" + t + "Coverage";
		}

	}

	private TWiseConfigurationUtil util;
	private PresenceConditionManager presenceConditionManager;

	public void setCNF(CNF cnf) {
		if (!cnf.getClauses().isEmpty()) {
			util = new TWiseConfigurationUtil(cnf, new Sat4JSolver(cnf));
		} else {
			util = new TWiseConfigurationUtil(cnf, null);
		}

		util.computeRandomSample(1000);
		if (!cnf.getClauses().isEmpty()) {
			util.computeMIG(false, false);
		}
		presenceConditionManager = new PresenceConditionManager(util,
			TWiseConfigurationGenerator.convertLiterals(Clauses.getLiterals(cnf
				.getVariables())));
	}

	public TWiseCoverageMetric getTWiseCoverageMetric(int t) {
		return new TWiseCoverageMetric(t);
	}

	public static List<TWiseCoverageMetric> getTWiseCoverageMetrics(CNF cnf, int... tValues) {
		final TWiseCoverageMetrics metrics = new TWiseCoverageMetrics();
		if (cnf != null) {
			metrics.setCNF(cnf);
		}
		final List<TWiseCoverageMetric> coverageMetrics = new ArrayList<>(tValues.length);
		for (int t : tValues) {
			coverageMetrics.add(metrics.getTWiseCoverageMetric(t));
		}
		return coverageMetrics;
	}

}
