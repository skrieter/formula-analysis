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
package org.spldev.formula.clause.configuration;

import java.util.List;

import org.spldev.formula.clause.LiteralList;
import org.spldev.formula.clause.solver.SStrategy;
import org.spldev.formula.clause.solver.SampleDistribution;
import org.spldev.formula.clause.solver.SatSolver;
import org.spldev.formula.clause.solver.SatSolver.SatResult;
import org.spldev.util.job.Executor;
import org.spldev.util.logging.Logger;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class UniformRandomConfigurationGenerator extends RandomConfigurationGenerator {

	private int sampleSize = 100;
	private List<LiteralList> sample;
	private SampleDistribution dist;

	@Override
	protected void init() {
		satisfiable = findCoreFeatures(solver);
		if (!satisfiable) {
			return;
		}

		final RandomConfigurationGenerator gen = new FastRandomConfigurationGenerator();
		gen.setAllowDuplicates(false);
		gen.setRandom(getRandom());
		sample = Executor.run(new ConfigurationSampler(gen, sampleSize), solver.getCnf()).orElse(Logger::logProblems);
		if (sample == null || sample.isEmpty()) {
			satisfiable = false;
			return;
		}
		System.out.println(sample.size());

		dist = new SampleDistribution(sample);
		dist.setRandom(getRandom());
		solver.setSelectionStrategy(SStrategy.uniform(dist));
	}

	@Override
	protected void reset() {
		dist.reset();
	}

	private boolean findCoreFeatures(SatSolver solver) {
		int[] fixedFeatures = solver.findSolution();
		if (fixedFeatures == null) {
			return false;
		}
		solver.setSelectionStrategy(SStrategy.inverse(fixedFeatures));

		// find core/dead features
		for (int i = 0; i < fixedFeatures.length; i++) {
			final int varX = fixedFeatures[i];
			if (varX != 0) {
				solver.assignmentPush(-varX);
				final SatResult hasSolution = solver.hasSolution();
				switch (hasSolution) {
				case FALSE:
					solver.assignmentReplaceLast(varX);
					break;
				case TIMEOUT:
					solver.assignmentPop();
					break;
				case TRUE:
					solver.assignmentPop();
					LiteralList.resetConflicts(fixedFeatures, solver.getSolution());
					solver.shuffleOrder(getRandom());
					break;
				}
			}
		}
		return true;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

}
