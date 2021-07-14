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

import java.util.*;
import java.util.function.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;
import org.spldev.formula.clause.solver.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class ConfigurationGenerator extends SatAnalysis
	implements Supplier<LiteralList>, Spliterator<LiteralList> {

	protected SatSolver solver;

	public final void init(CNF cnf) {
		init(createSolver(cnf));
	}

	public final void init(SatSolver solver) {
		prepareSolver(solver);
		this.solver = solver;
		init();
	}

	protected void init() {
	}

	@Override
	public int characteristics() {
		return NONNULL | IMMUTABLE;
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public boolean tryAdvance(Consumer<? super LiteralList> consumer) {
		final LiteralList literalList = get();
		if (literalList != null) {
			consumer.accept(literalList);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Spliterator<LiteralList> trySplit() {
		return null;
	}

}
