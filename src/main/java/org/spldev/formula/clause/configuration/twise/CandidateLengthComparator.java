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
import org.spldev.util.data.*;

/**
 * Compares two candidates for covering, consisting of a partial configuration
 * and a literal set. Considers number of literals in the partial configuration
 * and in the literal set.
 *
 * @author Sebastian Krieter
 */
class CandidateLengthComparator implements Comparator<Pair<LiteralList, TWiseConfiguration>> {

	@Override
	public int compare(Pair<LiteralList, TWiseConfiguration> o1, Pair<LiteralList, TWiseConfiguration> o2) {
		final int diff = o2.getValue().countLiterals - o1.getValue().countLiterals;
		return diff != 0 ? diff : o2.getKey().size() - o1.getKey().size();
	}

}
