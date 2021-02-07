package org.spldev.formula.clause;

import java.util.*;

/**
 * Compares list of clauses by he number of literals.
 *
 * @author Sebastian Krieter
 */
public class ClauseListLengthComparatorDsc implements Comparator<List<LiteralList>> {

	@Override
	public int compare(List<LiteralList> o1, List<LiteralList> o2) {
		return addLengths(o2) - addLengths(o1);
	}

	protected int addLengths(List<LiteralList> o) {
		int count = 0;
		for (final LiteralList literalSet : o) {
			count += literalSet.getLiterals().length;
		}
		return count;
	}

}
