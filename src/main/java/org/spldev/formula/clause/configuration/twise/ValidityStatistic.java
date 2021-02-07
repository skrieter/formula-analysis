package org.spldev.formula.clause.configuration.twise;

/**
 * Holds statistics regarding validity of configurations within a configuration
 * sample.
 *
 * @author Sebastian Krieter
 */
public class ValidityStatistic {

	protected final boolean[] configValidities;
	protected int numberOfValidConfigurations;

	public ValidityStatistic(int sampleSize) {
		configValidities = new boolean[sampleSize];
	}

	public void setConfigValidity(int index, boolean valid) {
		configValidities[index] = valid;
		if (valid) {
			numberOfValidConfigurations++;
		}
	}

	public boolean[] getConfigValidities() {
		return configValidities;
	}

	public int getNumberOfConfigurations() {
		return configValidities.length;
	}

	public int getNumberOfValidConfigurations() {
		return numberOfValidConfigurations;
	}

	public int getNumberOfInvalidConfigurations() {
		return configValidities.length - numberOfValidConfigurations;
	}

	public double getValidInvalidRatio() {
		if (configValidities.length != 0) {
			return ((double) numberOfValidConfigurations / (double) configValidities.length);
		} else {
			return 1.0;
		}
	}

}
