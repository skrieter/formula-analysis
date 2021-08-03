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

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds atomic sets.
 *
 * @author Sebastian Krieter
 */
public class AtomicSetAnalysis extends Sat4JAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public AtomicSetAnalysis() {
		super();
	}

	@Override
	public List<LiteralList> analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
		final List<LiteralList> result = new ArrayList<>();

		solver.setSelectionStrategy(SStrategy.positive());
		final int[] model1 = solver.findSolution();
		final List<LiteralList> solutions = solver.rememberSolutionHistory(1000);

		if (model1 != null) {
			solver.setSelectionStrategy(SStrategy.negative());
			final int[] model2 = solver.findSolution();
			solver.setSelectionStrategy(SStrategy.positive());

			final byte[] done = new byte[model1.length];

			final int[] model1Copy = Arrays.copyOf(model1, model1.length);

			LiteralList.resetConflicts(model1Copy, model2);
			for (int i = 0; i < model1Copy.length; i++) {
				final int varX = model1Copy[i];
				if (varX != 0) {
					solver.assignmentPush(-varX);
					switch (solver.hasSolution()) {
					case FALSE:
						done[i] = 2;
						solver.assignmentReplaceLast(varX);
						break;
					case TIMEOUT:
						solver.assignmentPop();
						reportTimeout();
						break;
					case TRUE:
						solver.assignmentPop();
						LiteralList.resetConflicts(model1Copy, solver.getSolution());
						solver.shuffleOrder(getRandom());
						break;
					}
				}
			}
			final int fixedSize = solver.getAssignmentSize();
			result.add(new LiteralList(solver.getAssignmentArray(0, fixedSize)));

			solver.setSelectionStrategy(SStrategy.random(getRandom()));

			for (int i = 0; i < model1.length; i++) {
				if (done[i] == 0) {
					done[i] = 2;

					int[] xModel0 = Arrays.copyOf(model1, model1.length);

					final int mx0 = xModel0[i];
					solver.assignmentPush(mx0);

					inner: for (int j = i + 1; j < xModel0.length; j++) {
						final int my0 = xModel0[j];
						if ((my0 != 0) && (done[j] == 0)) {
							for (final LiteralList solution : solutions) {
								final int mxI = solution.getLiterals()[i];
								final int myI = solution.getLiterals()[j];
								if ((mx0 == mxI) != (my0 == myI)) {
									continue inner;
								}
							}

							solver.assignmentPush(-my0);

							switch (solver.hasSolution()) {
							case FALSE:
								done[j] = 1;
								break;
							case TIMEOUT:
								reportTimeout();
								break;
							case TRUE:
								LiteralList.resetConflicts(xModel0, solver.getSolution());
								solver.shuffleOrder(getRandom());
								break;
							}
							solver.assignmentPop();
						}
					}

					solver.assignmentPop();
					solver.assignmentPush(-mx0);

					switch (solver.hasSolution()) {
					case FALSE:
						break;
					case TIMEOUT:
						for (int j = i + 1; j < xModel0.length; j++) {
							done[j] = 0;
						}
						reportTimeout();
						break;
					case TRUE:
						xModel0 = solver.getSolution();
						break;
					}

					for (int j = i + 1; j < xModel0.length; j++) {
						if (done[j] == 1) {
							final int my0 = xModel0[j];
							if (my0 != 0) {
								solver.assignmentPush(-my0);

								switch (solver.hasSolution()) {
								case FALSE:
									done[j] = 2;
									solver.assignmentReplaceLast(my0);
									break;
								case TIMEOUT:
									done[j] = 0;
									solver.assignmentPop();
									reportTimeout();
									break;
								case TRUE:
									done[j] = 0;
									LiteralList.resetConflicts(xModel0, solver.getSolution());
									solver.shuffleOrder(getRandom());
									solver.assignmentPop();
									break;
								}
							} else {
								done[j] = 0;
							}
						}
					}

					result.add(new LiteralList(solver.getAssignmentArray(fixedSize, solver.getAssignmentSize())));
					solver.assignmentClear(fixedSize);
				}
			}
		}
		return result;
	}

}
