package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.mig.*;
import org.spldev.formula.clause.solver.*;

public class Sat4JMIGSolver extends Solver {
	public MIG mig;
	public Sat4JSolver sat4j;

	public Sat4JMIGSolver(ModelRepresentation c) {
		super();
		mig = c.get(MIGProvider.fromCNF());
		sat4j = new Sat4JSolver(c);
	}

	@Override
	public void reset() {
		sat4j.reset();
	}

}
