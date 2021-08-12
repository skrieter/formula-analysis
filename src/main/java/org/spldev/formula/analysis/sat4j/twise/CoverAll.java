/* -----------------------------------------------------------------------------
 * Formula-Analysis Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis Lib.
 * 
 * Formula-Analysis Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula-analysis> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.analysis.sat4j.twise;

import java.util.*;

import org.spldev.formula.clauses.*;
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

		if (util.hasSolver) {
			if (util.coverSol(candidatesList)) {
				return CombinationStatus.COVERED;
			}

			if (util.removeInvalidClauses(nextCondition, candidatesList)) {
				return CombinationStatus.INVALID;
			}

			if (util.coverSat(candidatesList)) {
				return CombinationStatus.COVERED;
			}
		} else {
			if (util.coverNoSat(candidatesList)) {
				return CombinationStatus.COVERED;
			}
		}

		util.newConfiguration(nextCondition.get(0));
		return CombinationStatus.COVERED;
	}

}
