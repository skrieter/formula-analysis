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
package org.spldev.assignment;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.spldev.formula.analysis.sat4j.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.expression.io.*;
import org.spldev.util.extension.*;
import org.spldev.util.io.*;
import org.spldev.util.logging.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class Main {

	private static int count = 0;

	public static void main(String[] args) {
		ExtensionLoader.load();
		final Path root = Paths.get("/home/sebas/Documents/coding/FeatureIDE/");
		final Path examples = root.resolve("plugins/de.ovgu.featureide.examples/featureide_examples/");

		final Path p = examples.resolve("GPL-FH-Java/model.xml");
//		final Path p = root.resolve(
//				"featuremodels/E-Shop/model.xml");
//		final Path p = examples.resolve(
//				"FeatureModels/Automotive01/model.xml");
//		
		CNF cnf = FileHandler.load(p, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF).orElse(Logger::logProblems);

//		final RandomConfigurationGenerator gen = new FastRandomConfigurationGenerator();
//		gen.setAllowDuplicates(false);
//		gen.setRandom(new Random(0));
//		List<LiteralList> sample = Executor.run(new ConfigurationSampler(gen, 1000), cnf).orElse(Logger::logProblems);
//		if (sample == null || sample.isEmpty()) {
//			return;
//		}
//		System.out.println(sample.size());
//		SampleDistribution sampleDistribution = new SampleDistribution(sample);

		RandomConfigurationGenerator gen = new MIGRandomConfigurationGenerator();
//		RandomConfigurationGenerator gen = new FastRandomConfigurationGenerator();
		gen.setAllowDuplicates(true);
		gen.setRandom(new Random(0));
		long start = System.nanoTime();
//		gen.init(cnf);
		long end = System.nanoTime();
		Logger.logInfo("Time: " + (((end - start) / 1_000_000) / 1_000.0) + "s");
		HashMap<LiteralList, Integer> solutionCounter = new HashMap<>();
		
		count = 100_000;
		Stream.generate(gen)
			.limit(count)
			.forEach(solution -> {
//				Logger.logInfo(count--);
				final Integer counter = solutionCounter.get(solution);
				if (counter == null) {
					solutionCounter.put(solution, 1);
				} else {
					solutionCounter.put(solution, counter + 1);
				}
			});
//		for (LiteralList solution : sample) {
//			final Integer counter = solutionCounter.get(solution);
//			if (counter == null) {
//				solutionCounter.put(solution, 1);
//			} else {
//				solutionCounter.put(solution, counter + 1);
//			}
//		}
		List<Integer> counts = new ArrayList<>(solutionCounter.values());
//		Collections.sort(counts);
//		for (Integer count : counts) {
//			Logger.logInfo(count);
//		}
		Logger.logInfo("--------");
		Logger.logInfo(counts.stream().mapToInt(Integer::intValue).min());
		Logger.logInfo(counts.stream().mapToInt(Integer::intValue).max());
		Logger.logInfo(counts.stream().mapToInt(Integer::intValue).sum());
	}

//	private static double getScore(double strong, double weak, double total) {
//		return Math.log((((strong + weak) / (total-1)) + 1)) / Math.log(2);
//	}
//	
//	class Stats {
//		double[] data = new double[6];
//		static final int literal = 0;
//		static final int posCount = 1;
//		static final int strongInPositive = 2;
//		static final int strongInNegative = 3;
//		static final int weakInPositive = 4;
//		static final int weakInNegative = 5;
////		static final int strongOutPositive = 4;
////		static final int strongOutNegative = 5;
////		static final int weakIn = 6;
////		static final int weakOut = 7;
//
//		@Override
//		public String toString() {
//			DecimalFormat format = new DecimalFormat(" 000.000;-000.000");
//			StringBuilder sb = new StringBuilder();
//			for (double dataItem : data) {
//				sb.append(format.format(dataItem));
//				sb.append(" ");
//			}
//			return sb.toString();
//		}
//	}
//
//	ArrayList<Stats> list = new ArrayList<>();
//	for (int i = 0; i < cnf.getVariables().size(); i++) {
//		list.add(new Stats());
//	}
//	
//	int count = 0;
//	for (Vertex vertex : mig.getVertices()) {
//		if (vertex.isNormal()) {
//			count++;
//		}
//	}
//	count /=2;
//
//	for (Vertex vertex : mig.getVertices()) {
//		int literal = vertex.getVar();
//
//		if (literal > 0) {
//			Stats stats = list.get(literal - 1);
//			stats.data[Stats.literal] = literal;
//			stats.data[Stats.posCount] = (double) sampleDistribution.getPositiveCount(literal - 1)
//					/ sampleDistribution.getTotalCount();
////			stats.data[Stats.strongOutPositive] = vertex.getStrongEdges().size();
//
////			stats.data[Stats.strongInPositive]++;
////			stats.data[Stats.strongInNegative]++;
//		} else {
//			Stats stats = list.get(-literal - 1);
////			stats.data[Stats.strongOutNegative] = vertex.getStrongEdges().size();
//		}
//		for (Vertex strong : vertex.getStrongEdges()) {
//			int strongLiteral = strong.getVar();
//			if (strongLiteral > 0) {
//				Stats strongStats = list.get(strongLiteral - 1);
//				strongStats.data[Stats.strongInPositive]++;
//			} else {
//				Stats strongStats = list.get(-strongLiteral - 1);
//				strongStats.data[Stats.strongInNegative]++;
//			}
//		}
//		for (LiteralList weak : vertex.getComplexClauses()) {
//			for (int l : weak.getLiterals()) {
//				if (l > 0) {
//					Stats strongStats = list.get(l - 1);
//					strongStats.data[Stats.weakInPositive] += 1.0 / (weak.getLiterals().length - 1);
//				} else {
//					Stats strongStats = list.get(-l - 1);
//					strongStats.data[Stats.weakInNegative] += 1.0 / (weak.getLiterals().length - 1);
//				}
//			}
//		}
//	}
//
//	Collections.sort(list, Comparator.comparingDouble(s -> s.data[Stats.posCount]));
//
//	for (Stats stats : list) {
//		if (stats.data[Stats.posCount] < sampleDistribution.getTotalCount()) {
//			double score = 1;
//			score -=getScore(stats.data[Stats.weakInNegative], stats.data[Stats.strongInNegative], count);
//			score +=getScore(stats.data[Stats.weakInPositive], stats.data[Stats.strongInPositive], count);
//			score *= 0.5;
//			score -= stats.data[Stats.posCount];
//			System.out.println(stats.toString() + " -> " + score);
//		}
//	}

}
