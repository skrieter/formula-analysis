package org.spldev.formula.clause.solver;

import static org.sat4j.core.LiteralsUtils.*;

import org.sat4j.minisat.core.*;

public class FixedLiteralSelectionStrategy implements IPhaseSelectionStrategy {

	private static final long serialVersionUID = -1687370944480053808L;

	private final int[] model, phase;
	private final boolean min, inverse;

	public FixedLiteralSelectionStrategy(int[] model, boolean min, boolean inverse) {
		this.model = model;
		this.min = min;
		this.inverse = inverse;
		phase = new int[model.length + 1];
		reset(model.length + 1);
	}

	@Override
	public void updateVar(int p) {
	}

	@Override
	public void assignLiteral(int p) {
		final int var = var(p);
		if (model[var - 1] == 0) {
			phase[var] = p;
		}
	}

	@Override
	public void updateVarAtDecisionLevel(int q) {
	}

	@Override
	public void init(int nlength) {
		reset(nlength);
	}

	private void reset(int nlength) {
		if (inverse) {
			if (min) {
				for (int i = 1; i < nlength; i++) {
					phase[i] = model[i - 1] >= 0 ? negLit(i) : posLit(i);
				}
			} else {
				for (int i = 1; i < nlength; i++) {
					phase[i] = model[i - 1] > 0 ? negLit(i) : posLit(i);
				}
			}
		} else {
			if (min) {
				for (int i = 1; i < nlength; i++) {
					phase[i] = model[i - 1] >= 0 ? posLit(i) : negLit(i);
				}
			} else {
				for (int i = 1; i < nlength; i++) {
					phase[i] = model[i - 1] > 0 ? posLit(i) : negLit(i);
				}
			}
		}
	}

	@Override
	public void init(int var, int p) {
	}

	@Override
	public int select(int var) {
		return phase[var];
	}

}
