package org.spldev.formula.clause.configuration.twise;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.util.data.*;

/**
 * Covers a given {@link ClauseList expressions} within a list of
 * {@link TWiseConfiguration solutions}.
 *
 * @author Sebastian Krieter
 */
class CoverAll implements ICoverStrategy {

	private final TWiseConfigurationUtil util;

	public CoverAll(TWiseConfigurationUtil util) {
		this.util = util;
	}

	private final List<Pair<LiteralList, TWiseConfiguration>> candidatesList = new ArrayList<>();

	@Override
	public CombinationStatus cover(ClauseList nextCondition) {
		if (util.isCovered(nextCondition)) {
			return CombinationStatus.COVERED;
		}

		util.initCandidatesList(nextCondition, candidatesList);

		if (util.coverSol(candidatesList)) {
			return CombinationStatus.COVERED;
		}

		if (util.removeInvalidClauses(nextCondition, candidatesList)) {
			return CombinationStatus.INVALID;
		}

		if (candidatesList.size() < 32) {
			if (util.coverSat(candidatesList)) {
				return CombinationStatus.COVERED;
			}
		} else {
			if (util.coverSatPara(candidatesList)) {
				return CombinationStatus.COVERED;
			}
		}

		util.newConfiguration(nextCondition.get(0));
		return CombinationStatus.COVERED;
	}

}
