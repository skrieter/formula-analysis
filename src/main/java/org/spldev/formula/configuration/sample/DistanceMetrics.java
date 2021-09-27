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
package org.spldev.formula.configuration.sample;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.clauses.*;

public class DistanceMetrics extends AggregatableMetrics {

	private final DistanceFunction function;

	private double leastMean = EMPTY;
	private double mostMean = EMPTY;
	private double meanMin = EMPTY;
	private double meanMax = EMPTY;

	public DistanceMetrics(DistanceFunction function) {
		this.function = function;
	}

	public static List<SampleMetric> getAllAggregates(DistanceFunction distanceFunction) {
		final DistanceMetrics metrics = new DistanceMetrics(distanceFunction);
		final List<SampleMetric> aggregates = metrics.getAllAggregates();
		aggregates.add(metrics.getAggregate("leastMean", metrics::getLeastMean));
		aggregates.add(metrics.getAggregate("mostMean", metrics::getMostMean));
		aggregates.add(metrics.getAggregate("meanMin", metrics::getMeanMin));
		aggregates.add(metrics.getAggregate("meanMax", metrics::getMeanMax));
		return aggregates;
	}

	@Override
	public SampleMetric getAggregate(String name, DoubleSupplier aggregate) {
		return new DoubleMetric(function.getName() + "_distance_" + name, aggregate);
	}

	@Override
	protected double[] computeValues() {
		final List<LiteralList> solutions = sample.getSolutions();
		final int size = solutions.size();
		final double[] values = new double[(size * (size - 1)) >> 1];
		int index = 0;
		for (int i = 0; i < (size - 1); i++) {
			final int[] literals1 = solutions.get(i).getLiterals();
			for (int j = i + 1; j < size; j++) {
				values[index++] = function.computeDistance(literals1, solutions.get(j).getLiterals());
			}
		}
		return values;
	}

	@Override
	protected void reset() {
		super.reset();
		leastMean = EMPTY;
		mostMean = EMPTY;
		meanMin = EMPTY;
		meanMax = EMPTY;
	}

	private double getLeastMean() {
		if (leastMean == EMPTY) {
			final double[] distances = getValues();
			if (distances.length == 0) {
				leastMean = INVALID;
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
		if (mostMean == EMPTY) {
			final double[] distances = getValues();
			if (distances.length == 0) {
				mostMean = INVALID;
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

	private double getMeanMin() {
		if (meanMin == EMPTY) {
			final double[] distances = getValues();
			if (distances.length == 0) {
				meanMin = INVALID;
			} else {
				final int size = (((int) Math.sqrt((distances.length << 3) + 1)) >> 1) + 1;
				double minLocalMean = Double.MAX_VALUE;
				for (int i = 0; i < size; i++) {
					double localMean = 0;
					for (int j = 0; j < size; j++) {
						if (i != j) {
							localMean += getDistance(distances, size, i, j);
						}
					}
					localMean /= size;
					if (localMean < minLocalMean) {
						minLocalMean = localMean;
					}
				}
				meanMin = minLocalMean;
			}
		}
		return meanMin;
	}

	private double getMeanMax() {
		if (meanMax == EMPTY) {
			final double[] distances = getValues();
			if (distances.length == 0) {
				meanMax = INVALID;
			} else {
				final int size = (((int) Math.sqrt((distances.length << 3) + 1)) >> 1) + 1;
				double maxLocalMean = 0;
				for (int i = 0; i < size; i++) {
					double localMean = 0;
					for (int j = 0; j < size; j++) {
						if (i != j) {
							localMean += getDistance(distances, size, i, j);
						}
					}
					localMean /= size;
					if (localMean > maxLocalMean) {
						maxLocalMean = localMean;
					}
				}
				meanMax = maxLocalMean;
			}
		}
		return meanMax;
	}

	private double getDistance(final double[] distances, final int size, int i, int j) {
		return distances[i < j
			? (distances.length + j) - ((((size - i) * (size - i - 1)) >> 1) + i + 1)
			: (distances.length + i) - ((((size - j) * (size - j - 1)) >> 1) + j + 1)];
	}

}
