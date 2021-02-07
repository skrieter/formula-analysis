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
