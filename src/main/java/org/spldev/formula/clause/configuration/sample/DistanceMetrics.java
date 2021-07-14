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
package org.spldev.formula.clause.configuration.sample;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.clause.*;

public class DistanceMetrics {

	public final class DistanceMetric implements SampleMetric {
		private final String name;
		private final DoubleSupplier aggregate;

		public DistanceMetric(String name, DoubleSupplier aggregate) {
			this.name = name;
			this.aggregate = aggregate;
		}

		@Override
		public double get(SolutionList sample) {
			setSample(sample);
			return aggregate.getAsDouble();
		}

		@Override
		public String getName() {
			return name + distanceFunction.getName() + "Distance";
		}

	}

	private final DistanceFunction distanceFunction;

	private SolutionList sample;

	private double[] distances = null;
	private double min = -1;
	private double max = -1;
	private double mean = -1;
	private double median = -1;
	private double variance = -1;
	private double standardDeviation = -1;
	private double leastMean = -1;
	private double mostMean = -1;

	public DistanceMetrics(DistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	public static List<DistanceMetric> getAllAggregates(DistanceFunction distanceFunction) {
		final DistanceMetrics metrics = new DistanceMetrics(distanceFunction);
		final List<DistanceMetric> aggregates = new ArrayList<>(8);
		aggregates.add(metrics.getMinDistance());
		aggregates.add(metrics.getMaxDistance());
		aggregates.add(metrics.getMeanDistance());
		aggregates.add(metrics.getMedianDistance());
		aggregates.add(metrics.getVarianceDistance());
		aggregates.add(metrics.getStandardDeviationDistance());
		aggregates.add(metrics.getLeastMeanDistance());
		aggregates.add(metrics.getMostMeanDistance());
		return aggregates;
	}

	public double[] getDistances() {
		if (distances == null) {
			final List<LiteralList> solutions = sample.getSolutions();
			final int size = solutions.size();
			distances = new double[(size * (size - 1)) >> 1];
			int index = 0;
			for (int i = 0; i < (size - 1); i++) {
				final int[] literals1 = solutions.get(i).getLiterals();
				for (int j = i + 1; j < size; j++) {
					distances[index++] = distanceFunction.computeDistance(literals1, solutions.get(j).getLiterals());
				}
			}
		}
		return distances;
	}

	public void setSample(SolutionList sample) {
		if ((this.sample == null) || (this.sample != sample)) {
			this.sample = sample;
			distances = null;
			min = -1;
			max = -1;
			mean = -1;
			median = -1;
			variance = -1;
			standardDeviation = -1;
			leastMean = -1;
			mostMean = -1;
		}
	}

	public DistanceMetric getMinDistance() {
		return new DistanceMetric("Min", this::getMin);
	}

	public DistanceMetric getMaxDistance() {
		return new DistanceMetric("Max", this::getMax);
	}

	public DistanceMetric getMeanDistance() {
		return new DistanceMetric("Mean", this::getMean);
	}

	public DistanceMetric getMedianDistance() {
		return new DistanceMetric("Median", this::getMedian);
	}

	public DistanceMetric getVarianceDistance() {
		return new DistanceMetric("Variance", this::getVariance);
	}

	public DistanceMetric getStandardDeviationDistance() {
		return new DistanceMetric("StandardDeviation", this::getStandardDeviation);
	}

	public DistanceMetric getLeastMeanDistance() {
		return new DistanceMetric("LeastMean", this::getLeastMean);
	}

	public DistanceMetric getMostMeanDistance() {
		return new DistanceMetric("MostMean", this::getMostMean);
	}

	private double getMin() {
		if (min < 0) {
			final double[] distances = getDistances();
			if (distances.length == 0) {
				min = 0;
			} else {
				min = Double.MAX_VALUE;
				for (final double distance : distances) {
					if (min > distance) {
						min = distance;
					}
				}
			}
		}
		return min;
	}

	private double getMax() {
		if (max < 0) {
			max = 0;
			for (final double distance : getDistances()) {
				if (max < distance) {
					max = distance;
				}
			}
		}
		return max;
	}

	private double getMean() {
		if (mean < 0) {
			mean = 0;
			final double[] distances = getDistances();
			if (distances.length > 0) {
				for (final double distance : distances) {
					mean += distance;
				}
				mean /= distances.length;
			}
		}
		return mean;
	}

	private double getMedian() {
		if (median < 0) {
			final double[] distances = getDistances();
			final int length = distances.length;
			if (length == 0) {
				median = 0;
			} else {
				final double[] sortedDistances = Arrays.copyOf(distances, length);
				Arrays.sort(sortedDistances);

				final int middle = length / 2;
				median = ((length & 1) == 1) //
					? sortedDistances[middle] //
					: (sortedDistances[middle - 1] + sortedDistances[middle]) / 2.0;
			}
		}
		return median;
	}

	private double getVariance() {
		if (variance < 0) {
			variance = 0;
			final double[] distances = getDistances();
			if (distances.length > 0) {
				final double mean = getMean();
				for (final double distance : getDistances()) {
					final double diff = distance - mean;
					variance += diff * diff;
				}
				variance /= distances.length;
			}
		}
		return variance;
	}

	private double getStandardDeviation() {
		if (standardDeviation < 0) {
			standardDeviation = Math.sqrt(getVariance());
		}
		return standardDeviation;
	}

	private double getLeastMean() {
		if (leastMean < 0) {
			final double[] distances = getDistances();
			if (distances.length == 0) {
				leastMean = 0;
			} else {
				final int size = (((int) Math.sqrt((distances.length << 3) + 1)) >> 1) + 1;

				double sum = 0;
				for (int i = 0; i < size; i++) {
					double minDistance = Double.MAX_VALUE;
					for (int j = 0; j < size; j++) {
						if (i != j) {
							final double d = getDistance(distances, size, i, j);
							if (minDistance > d) {
								minDistance = d;
							}
						}
					}
					sum += minDistance;
				}
				leastMean = sum / size;
			}
		}
		return leastMean;
	}

	private double getMostMean() {
		if (mostMean < 0) {
			final double[] distances = getDistances();
			if (distances.length == 0) {
				mostMean = 0;
			} else {
				final int size = (((int) Math.sqrt((distances.length << 3) + 1)) >> 1) + 1;

				double sum = 0;
				for (int i = 0; i < size; i++) {
					double maxDistance = 0;
					for (int j = 0; j < size; j++) {
						if (i != j) {
							final double d = getDistance(distances, size, i, j);
							if (maxDistance < d) {
								maxDistance = d;
							}
						}
					}
					sum += maxDistance;
				}
				mostMean = sum / size;
			}
		}
		return mostMean;
	}

	private double getDistance(final double[] distances, final int size, int i, int j) {
		return distances[i < j
			? (distances.length + j) - ((((size - i) * (size - i - 1)) >> 1) + i + 1)
			: (distances.length + i) - ((((size - j) * (size - j - 1)) >> 1) + j + 1)];
	}

}
