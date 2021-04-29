package org.spldev.formula.clause.cli;

import org.spldev.formula.clause.configuration.*;

/**
 * Generates all configurations for a given propositional formula.
 *
 * @author Sebastian Krieter
 */
public class AllAlgorithm extends AConfigurationGeneratorAlgorithm<AllConfigurationGenerator> {

	@Override
	protected AllConfigurationGenerator createConfigurationGenerator() {
		return new AllConfigurationGenerator();
	}

	@Override
	public String getName() {
		return "all";
	}

}
