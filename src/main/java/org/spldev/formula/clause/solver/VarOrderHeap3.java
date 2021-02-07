package org.spldev.formula.clause.solver;

import java.util.*;

import org.sat4j.minisat.orders.*;
import org.sat4j.specs.*;
import org.spldev.formula.clause.*;

/**
 * Modified variable order for {@link ISolver}.<br>
 * Uses the {@link UniformRandomSelectionStrategy}.
 *
 * @author Sebastian Krieter
 */
public class VarOrderHeap3 extends VarOrderHeap {

	private static final long serialVersionUID = 1L;

	private final UniformRandomSelectionStrategy selectionStrategy;

	public VarOrderHeap3(List<LiteralList> sample) {
		super(new UniformRandomSelectionStrategy(sample));
		selectionStrategy = (UniformRandomSelectionStrategy) phaseStrategy;
	}

	@Override
	public void undo(int x) {
		super.undo(x);
		selectionStrategy.undo(x);
	}

	@Override
	public void assignLiteral(int p) {
		super.assignLiteral(p);
	}

}
