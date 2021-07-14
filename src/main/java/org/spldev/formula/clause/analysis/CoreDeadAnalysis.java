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
package org.spldev.formula.clause.analysis;

import java.util.*;

import org.sat4j.core.*;
import org.sat4j.specs.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds core and dead features.
 *
 * @author Sebastian Krieter
 */
public class CoreDeadAnalysis extends AVariableAnalysis<LiteralList> {

	public static final Identifier<LiteralList> identifier = new Identifier<>();

	private SatSolver solver;

	@Override
	public Identifier<LiteralList> getIdentifier() {
		return identifier;
	}

	public CoreDeadAnalysis() {
		super();
	}

	public CoreDeadAnalysis(LiteralList variables) {
		super();
		this.variables = variables;
	}

	@Override
	public LiteralList analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		this.solver = solver;
		return analyze1(monitor);
	}

	@Override
	protected SatSolver createSolver(CNF satInstance) {
		try {
			return new Sat4JSolver(satInstance);
		} catch (final RuntimeContradictionException e) {
			return null;
		}
	}

	public LiteralList analyze2(SatSolver solver, InternalMonitor monitor) throws Exception {
		final int initialAssignmentLength = solver.getAssignmentSize();
		solver.setSelectionStrategy(SStrategy.positive());
		int[] model1 = solver.findSolution();

		if (model1 != null) {
			solver.setSelectionStrategy(SStrategy.negative());
			final int[] model2 = solver.findSolution();

			if (variables != null) {
				final int[] model3 = new int[model1.length];
				for (int i = 0; i < variables.getLiterals().length; i++) {
					final int index = variables.getLiterals()[i] - 1;
					if (index >= 0) {
						model3[index] = model1[index];
					}
				}
				model1 = model3;
			}

			for (int i = 0; i < initialAssignmentLength; i++) {
				model1[Math.abs(solver.assignmentGet(i)) - 1] = 0;
			}

			LiteralList.resetConflicts(model1, model2);
			solver.setSelectionStrategy(SStrategy.inverse(model1));

			vars = new VecInt(model1.length);
			split(model1, 0, model1.length);
		}
		return new LiteralList(solver.getAssignmentArray(initialAssignmentLength, solver.getAssignmentSize()));
	}

	VecInt vars;

	private void split(int[] model, int start, int end) {
		vars.clear();
		for (int j = start; j < end; j++) {
			final int var = model[j];
			if (var != 0) {
				vars.push(-var);
			}
		}
		switch (vars.size()) {
		case 0:
			return;
		case 1:
			test(model, 0);
			break;
		case 2:
			test(model, 0);
			test(model, 1);
			break;
		default:
			try {
				solver.addClause(new LiteralList(Arrays.copyOf(vars.toArray(), vars.size())));
				switch (solver.hasSolution()) {
				case FALSE:
					foundVariables(model, vars);
					break;
				case TIMEOUT:
					reportTimeout();
					break;
				case TRUE:
					LiteralList.resetConflicts(model, solver.getSolution());
					solver.shuffleOrder(getRandom());

					final int halfLength = (end - start) / 2;
					if (halfLength > 0) {
						split(model, start + halfLength, end);
						split(model, start, start + halfLength);
					}
					break;
				}
				solver.removeLastClause();
			} catch (final RuntimeContradictionException e) {
				foundVariables(model, vars);
			}
			break;
		}
	}

	private void test(int[] model, int i) {
		final int var = vars.get(i);
		solver.assignmentPush(var);
		switch (solver.hasSolution()) {
		case FALSE:
			solver.assignmentReplaceLast(-var);
			model[Math.abs(var) - 1] = 0;
			break;
		case TIMEOUT:
			solver.assignmentPop();
			reportTimeout();
			break;
		case TRUE:
			solver.assignmentPop();
			LiteralList.resetConflicts(model, solver.getSolution());
			solver.shuffleOrder(getRandom());
			break;
		}
	}

	private void foundVariables(int[] model, VecInt vars) {
		for (final IteratorInt iterator = vars.iterator(); iterator.hasNext();) {
			final int var = iterator.next();
			solver.assignmentPush(-var);
			model[Math.abs(var) - 1] = 0;
		}
	}

	public LiteralList analyze1(InternalMonitor monitor) throws Exception {
		final int initialAssignmentLength = solver.getAssignmentSize();
		solver.setSelectionStrategy(SStrategy.positive());
		int[] model1 = solver.findSolution();

		if (model1 != null) {
			solver.setSelectionStrategy(SStrategy.negative());
			final int[] model2 = solver.findSolution();

			if (variables != null) {
				final int[] model3 = new int[model1.length];
				for (int i = 0; i < variables.getLiterals().length; i++) {
					final int index = variables.getLiterals()[i] - 1;
					if (index >= 0) {
						model3[index] = model1[index];
					}
				}
				model1 = model3;
			}

			for (int i = 0; i < initialAssignmentLength; i++) {
				model1[Math.abs(solver.assignmentGet(i)) - 1] = 0;
			}

			LiteralList.resetConflicts(model1, model2);
			solver.setSelectionStrategy(SStrategy.inverse(model1));

			for (int i = 0; i < model1.length; i++) {
				final int varX = model1[i];
				if (varX != 0) {
					solver.assignmentPush(-varX);
					switch (solver.hasSolution()) {
					case FALSE:
						solver.assignmentReplaceLast(varX);
						break;
					case TIMEOUT:
						solver.assignmentPop();
						reportTimeout();
						break;
					case TRUE:
						solver.assignmentPop();
						LiteralList.resetConflicts(model1, solver.getSolution());
						solver.shuffleOrder(getRandom());
						break;
					}
				}
			}
		}

		return new LiteralList(solver.getAssignmentArray(initialAssignmentLength, solver.getAssignmentSize()));
	}

}
