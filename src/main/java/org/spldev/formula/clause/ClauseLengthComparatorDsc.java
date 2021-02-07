package org.spldev.formula.clause;

import java.util.*;

/**
 * Compares clauses by he number of literals.
 *
 * @author Sebastian Krieter
 */
public class ClauseLengthComparatorDsc implements Comparator<LiteralList> {

	@Override
	public int compare(LiteralList o1, LiteralList o2) {
		return o2.getLiterals().length - o1.getLiterals().length;
	}

}
