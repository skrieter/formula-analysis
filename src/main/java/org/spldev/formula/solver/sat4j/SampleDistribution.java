/* -----------------------------------------------------------------------------
 * Formula-Analysis Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis Lib.
 * 
 * Formula-Analysis Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula-analysis> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.solver.sat4j;

import java.util.*;

import org.spldev.formula.clauses.*;

/**
 * Uses a sample of configurations to achieve a phase selection that corresponds
 * to a uniform distribution of configurations in the configuration space.
 *
 * @author Sebastian Krieter
 */
public class SampleDistribution extends LiteralDistribution {

	private final ArrayList<LiteralList> samples = new ArrayList<>();
	private int startIndex;

	private final byte[] model;

	public SampleDistribution(List<LiteralList> sample) {
		samples.addAll(sample);
		startIndex = 0;
		model = new byte[sample.get(0).size()];
	}

	@Override
	public void reset() {
		Arrays.fill(model, (byte) 0);
		startIndex = 0;
	}

	@Override
	public void unset(int var) {
		final int index = var - 1;
		final byte sign = model[index];
		if (sign != 0) {
			model[index] = 0;
			final int literal = sign > 0 ? var : -var;
			for (int i = 0; i < startIndex; i++) {
				if (samples.get(i).getLiterals()[index] == -literal) {
					Collections.swap(samples, i--, --startIndex);
				}
			}
		}
	}

	@Override
	public void set(int literal) {
		final int index = Math.abs(literal) - 1;
		if (model[index] == 0) {
			model[index] = (byte) (literal > 0 ? 1 : -1);
			for (int i = startIndex; i < samples.size(); i++) {
				if (samples.get(i).getLiterals()[index] == -literal) {
					Collections.swap(samples, i, startIndex++);
				}
			}
		}
	}

	@Override
	public int getRandomLiteral(int var) {
		if (samples.size() > (startIndex + 1)) {
			return (random.nextInt((samples.size() - startIndex) + 2) < (getPositiveCount(var - 1) + 1)) ? var : -var;
		} else {
			return random.nextBoolean() ? var : -var;
		}
	}

	public int getPositiveCount(int index) {
		int sum = 0;
		for (final LiteralList l : samples.subList(startIndex, samples.size())) {
			sum += (~l.getLiterals()[index]) >>> 31;
		}
		return sum;
	}

	public int getTotalCount() {
		return samples.size() + startIndex;
	}

}
