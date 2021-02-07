package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Compares two solutions regarding their number of contained literals.
 *
 * @author Sebastian Krieter
 */
class TWiseConfigurationLengthComparator implements Comparator<TWiseConfiguration> {

	@Override
	public int compare(TWiseConfiguration arg0, TWiseConfiguration arg1) {
		return arg1.countLiterals - arg0.countLiterals;
	}

}
