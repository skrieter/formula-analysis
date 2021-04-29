package org.spldev.formula.clause.cli;

import org.spldev.formula.clause.configuration.*;

/**
 * Generates configurations for a given propositional formula such that one-wise
 * feature coverage is achieved.
 *
 * @author Sebastian Krieter
 */
public class OneWiseAlgorithm extends AConfigurationGeneratorAlgorithm<OneWiseConfigurationGenerator> {

	@Override
	protected OneWiseConfigurationGenerator createConfigurationGenerator() {
		return new OneWiseConfigurationGenerator();
	}
	
	@Override
	public String getName() {
		return "onewise";
	}

}
