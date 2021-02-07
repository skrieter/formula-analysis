package org.spldev.formula.clause.configuration.twise;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.util.data.*;

/**
 * Calculates statistics regarding t-wise feature coverage of a set of
 * solutions.
 *
 * @author Sebastian Krieter
 */
public class TWiseStatisticFastGenerator {

	private final TWiseConfigurationUtil util;

	public TWiseStatisticFastGenerator(TWiseConfigurationUtil util) {
		this.util = util;
	}

	public CoverageStatistic getCoverage(List<? extends LiteralList> sample,
		List<List<PresenceCondition>> groupedPresenceConditions, int t) {
		final CoverageStatistic statistic = new CoverageStatistic();
		statistic.initScores(sample.size());

		final ArrayList<List<Pair<Integer, LiteralList>>> lists = new ArrayList<>(t);
		{
			for (int i = 0; i < t; i++) {
				lists.add(new ArrayList<Pair<Integer, LiteralList>>(sample.size()));
			}
			final List<Pair<Integer, LiteralList>> list = lists.get(0);
			int confIndex = 0;
			for (final LiteralList configuration : sample) {
				list.add(new Pair<>(confIndex++, configuration));
			}
		}

		for (List<PresenceCondition> expressions : groupedPresenceConditions) {
			if (expressions.size() < t) {
				if (expressions.size() == 0) {
					continue;
				}
				final ArrayList<PresenceCondition> paddedExpressions = new ArrayList<>(t);
				paddedExpressions.addAll(expressions);
				for (int i = expressions.size(); i < t; i++) {
					paddedExpressions.add(expressions.get(0));
				}
				expressions = paddedExpressions;
			}
			final int n = expressions.size();
			final int[] c = new int[t + 1];
			c[0] = -1;
			for (int i = 1; i <= t; i++) {
				c[i] = n - (t - i);
			}
			boolean first = true;

			combinationLoop: while (true) {
				int i = t;
				for (; i > 0; i--) {
					final int ci = ++c[i];
					if (ci < ((n - t) + i)) {
						break;
					}
				}

				if (i == 0) {
					if (first) {
						first = false;
					} else {
						break combinationLoop;
					}
				}

				for (int j = i + 1; j <= t; j++) {
					c[j] = c[j - 1] + 1;
				}

				for (int j = i; j < t; j++) {
					if (j > 0) {
						final List<Pair<Integer, LiteralList>> prevList = lists.get(j - 1);
						final List<Pair<Integer, LiteralList>> curList = lists.get(j);
						curList.clear();
						final PresenceCondition presenceCondition = expressions.get(c[j]);
						entryLoop: for (final Pair<Integer, LiteralList> entry : prevList) {
							for (final LiteralList literals : presenceCondition) {
								if (entry.getValue().containsAll(literals)) {
									curList.add(entry);
									continue entryLoop;
								}
							}
						}
					}
				}

				Pair<Integer, LiteralList> curEntry = null;
				final PresenceCondition presenceCondition = expressions.get(c[t]);
				entryLoop: for (final Pair<Integer, LiteralList> entry : lists.get(t - 1)) {
					for (final LiteralList literals : presenceCondition) {
						if (entry.getValue().containsAll(literals)) {
							if (curEntry == null) {
								statistic.incNumberOfCoveredConditions();
								curEntry = entry;
								continue entryLoop;
							} else {
								continue combinationLoop;
							}
						}
					}
				}

				if (curEntry != null) {
					statistic.addToScore(curEntry.getKey(), 1);
				} else {
					statistic.incNumberOfUncoveredConditions();
				}
			}
		}
		int confIndex = 0;
		for (final LiteralList configuration : sample) {
			int count = 0;
			for (final int literal : configuration.getLiterals()) {
				if (literal == 0) {
					count++;
				}
			}
			final double d = (double) count / configuration.size();
			final double factor = (2 - (d * d));
			statistic.setScore(confIndex, statistic.getScore(confIndex) * factor);
			confIndex++;
		}
		return statistic;
	}

	public List<ValidityStatistic> getValidity(List<List<? extends LiteralList>> samples) {
		final List<ValidityStatistic> statisticList = new ArrayList<>(samples.size());
		for (final List<? extends LiteralList> sample : samples) {
			final ValidityStatistic statistic = new ValidityStatistic(sample.size());

			int configurationIndex = 0;
			configLoop: for (final LiteralList configuration : sample) {
				for (final LiteralList clause : util.getCnf().getClauses()) {
					if (!configuration.hasDuplicates(clause)) {
						statistic.setConfigValidity(configurationIndex++, false);
						continue configLoop;
					}
				}
				statistic.setConfigValidity(configurationIndex++, true);
			}
			statisticList.add(statistic);
		}
		return statisticList;
	}

}
