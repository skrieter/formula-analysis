package org.spldev.formula.clause.configuration;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public interface ITWiseConfigurationGenerator extends IConfigurationGenerator {

	enum Deduce {
		DP, AC, NONE
	}

	enum Order {
		RANDOM, SORTED
	}

	enum Phase {
		MULTI, SINGLE
	}

}
