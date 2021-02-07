package org.spldev.formula.clause.configuration.twise;

/**
 * Holds statistics regarding coverage of a configuration sample.
 *
 * @author Sebastian Krieter
 */
public class CoverageStatistic {

	protected long numberOfValidConditions;
	protected long numberOfInvalidConditions;
	protected long numberOfCoveredConditions;
	protected long numberOfUncoveredConditions;

	protected double[] configScores;

	protected void initScores(int sampleSize) {
		configScores = new double[sampleSize];
	}

	protected void setScore(int index, double score) {
		configScores[index] = score;
	}

	protected void addToScore(int index, double score) {
		configScores[index] += score;
	}

	protected double getScore(int index) {
		return configScores[index];
	}

	public double[] getConfigScores() {
		return configScores;
	}

	protected void setNumberOfValidConditions(long numberOfValidConditions) {
		this.numberOfValidConditions = numberOfValidConditions;
	}

	protected void setNumberOfInvalidConditions(long numberOfInvalidConditions) {
		this.numberOfInvalidConditions = numberOfInvalidConditions;
	}

	protected void setNumberOfCoveredConditions(long numberOfCoveredConditions) {
		this.numberOfCoveredConditions = numberOfCoveredConditions;
	}

	protected void setNumberOfUncoveredConditions(long numberOfUncoveredConditions) {
		this.numberOfUncoveredConditions = numberOfUncoveredConditions;
	}

	protected void incNumberOfValidConditions() {
		numberOfValidConditions++;
	}

	protected void incNumberOfInvalidConditions() {
		numberOfInvalidConditions++;
	}

	protected void incNumberOfCoveredConditions() {
		numberOfCoveredConditions++;
	}

	protected void incNumberOfUncoveredConditions() {
		numberOfUncoveredConditions++;
	}

	public long getNumberOfValidConditions() {
		return numberOfValidConditions;
	}

	public long getNumberOfInvalidConditions() {
		return numberOfInvalidConditions;
	}

	public long getNumberOfCoveredConditions() {
		return numberOfCoveredConditions;
	}

	public long getNumberOfUncoveredConditions() {
		return numberOfUncoveredConditions;
	}

	public double getCoverage() {
		if (numberOfValidConditions != 0) {
			return (double) numberOfCoveredConditions / (double) numberOfValidConditions;
		} else {
			if (numberOfInvalidConditions == 0) {
				return (double) numberOfCoveredConditions / (double) (numberOfCoveredConditions
					+ numberOfUncoveredConditions);
			} else {
				return 1.0;
			}
		}
	}

}
