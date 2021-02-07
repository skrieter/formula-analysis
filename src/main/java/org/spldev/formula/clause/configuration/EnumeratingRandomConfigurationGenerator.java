package org.spldev.formula.clause.configuration;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class EnumeratingRandomConfigurationGenerator extends ARandomConfigurationGenerator {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public EnumeratingRandomConfigurationGenerator(int maxNumber) {
		super(maxNumber);
	}

	@Override
	protected void generate(SatSolver solver, InternalMonitor monitor) throws Exception {
		monitor.setTotalWork(2 * maxSampleSize);

		final List<LiteralList> allConfigurations = new ArrayList<>(
			new AllConfigurationGenerator(maxSampleSize).execute(solver, monitor.subTask(maxSampleSize)));
		if (!allowDuplicates) {
			Collections.shuffle(allConfigurations, getRandom());
		}

		for (int i = 0; i < maxSampleSize; i++) {
			if (allowDuplicates) {
				addResult(allConfigurations.get(getRandom().nextInt(allConfigurations.size())));
			} else {
				if (i >= allConfigurations.size()) {
					break;
				} else {
					addResult(allConfigurations.get(i));
				}
			}
			monitor.step();
		}
	}

}
