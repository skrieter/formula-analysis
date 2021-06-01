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
package org.spldev.formula.clause.solver;

public abstract class SStrategy<T> {

	public static final class DefaultStrategy extends SStrategy<Void> {
	};

	public static final class NegativeStrategy extends SStrategy<Void> {
	};

	public static final class PositiveStrategy extends SStrategy<Void> {
	};

	public static final class FixedStrategy extends SStrategy<int[]> {

		public FixedStrategy(int[] model) {
			super(model);
		}
	};

	public static final class ReverseFixedStrategy extends SStrategy<int[]> {

		public ReverseFixedStrategy(int[] model) {
			super(model);
		}
	};

	public static final class FastRandomStrategy extends SStrategy<Void> {
	};

	public static final class UniformRandomStrategy extends SStrategy<SampleDistribution> {
		public UniformRandomStrategy(SampleDistribution dist) {
			super(dist);
		}
	};

	public static final class MIGRandomStrategy extends SStrategy<MIGDistribution> {
		public MIGRandomStrategy(MIGDistribution dist) {
			super(dist);
		}
	};

	protected final T parameter;

	public SStrategy() {
		parameter = null;
	}

	public SStrategy(T parameter) {
		this.parameter = parameter;
	}

	public static DefaultStrategy orgiginal() {
		return new DefaultStrategy();
	}

	public static NegativeStrategy negative() {
		return new NegativeStrategy();
	}

	public static PositiveStrategy positive() {
		return new PositiveStrategy();
	}

	public static FastRandomStrategy random() {
		return new FastRandomStrategy();
	}

	public static FixedStrategy fixed(int[] model) {
		return new FixedStrategy(model);
	}

	public static ReverseFixedStrategy reversed(int[] model) {
		return new ReverseFixedStrategy(model);
	}

	public static UniformRandomStrategy uniform(SampleDistribution dist) {
		return new UniformRandomStrategy(dist);
	}

	public static MIGRandomStrategy mig(MIGDistribution dist) {
		return new MIGRandomStrategy(dist);
	}

}
