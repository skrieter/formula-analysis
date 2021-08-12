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
package org.spldev.formula.solver.mig;

import org.spldev.formula.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.solver.*;
import org.spldev.formula.solver.sat4j.*;

/**
 * Sat solver using Sat4J and MIGs.
 * 
 * @author Sebastian Krieter
 */
public class Sat4JMIGSolver implements Solver {
	public MIG mig;
	public Sat4JSolver sat4j;

	public Sat4JMIGSolver(ModelRepresentation c) {
		mig = c.get(MIGProvider.fromCNF());
		sat4j = new Sat4JSolver(c);
	}

	public Sat4JMIGSolver(MIG mig, CNF cnf) {
		this.mig = mig;
		sat4j = new Sat4JSolver(cnf);
	}

	@Override
	public void reset() {
		sat4j.reset();
	}

}
