package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Compares two solutions regarding their calculated rank and number of
 * contained literals.
 *
 * @author Sebastian Krieter
 */
class TWiseConfigurationRankComparator implements Comparator<TWiseConfiguration> {

	@Override
	public int compare(TWiseConfiguration arg0, TWiseConfiguration arg1) {
		final int rankDiff = arg1.rank - arg0.rank;
		return rankDiff != 0 ? rankDiff : arg0.countLiterals - arg1.countLiterals;
	}

}
