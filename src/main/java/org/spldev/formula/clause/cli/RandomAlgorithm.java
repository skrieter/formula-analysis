package org.spldev.formula.clause.cli;

import org.spldev.formula.clause.configuration.*;

/**
 * Generates random configurations for a given propositional formula.
 *
 * @author Sebastian Krieter
 */
public class RandomAlgorithm extends AConfigurationGeneratorAlgorithm<RandomConfigurationGenerator> {

	@Override
	protected RandomConfigurationGenerator createConfigurationGenerator() {
		return new RandomConfigurationGenerator();
	}
	
	@Override
	public String getName() {
		return "random";
	}

}
