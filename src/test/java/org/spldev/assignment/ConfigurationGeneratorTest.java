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

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.cli.*;
import org.spldev.formula.clause.configuration.sample.*;
import org.spldev.formula.clause.io.*;
import org.spldev.formula.expression.io.*;
import org.spldev.util.extension.*;
import org.spldev.util.io.*;
import org.spldev.util.logging.*;

/**
 * Tests sampling algorithms.
 *
 * @author Sebastian Krieter
 */
public class ConfigurationGeneratorTest {

	static {
		ExtensionLoader.load();
	}

	private final static Path modelDirectory = Paths.get("src/test/resources/testFeatureModels");

	private final List<String> modelNames = Arrays.asList( //
		"basic", //
		"simple", //
		"car", //
		"gpl_medium_model"
		);

	@Test
	public void AllCoverage() {
		testSize("basic", "all", 1);
		testSize("simple", "all", 2);
		testSize("car", "all", 7);
		testSize("gpl_medium_model", "all", 960);
	}

	@Test
	public void AllLimit() {
		testLimitedSize("basic", "all", 1, 0);
		testLimitedSize("basic", "all", 1, 1);
		testLimitedSize("basic", "all", 1, 2);
		testLimitedSize("simple", "all", 2, 1);
		testLimitedSize("simple", "all", 2, 2);
		testLimitedSize("simple", "all", 2, 3);
		testLimitedSize("car", "all", 7, 0);
		testLimitedSize("car", "all", 7, 1);
		testLimitedSize("car", "all", 7, 5);
		testLimitedSize("car", "all", 7, 7);
		testLimitedSize("car", "all", 7, 10);
		testLimitedSize("car", "all", 7, Integer.MAX_VALUE);
		testLimitedSize("gpl_medium_model", "all", 960, 10);
		testLimitedSize("gpl_medium_model", "all", 960, 960);
		testLimitedSize("gpl_medium_model", "all", 960, Integer.MAX_VALUE);
	}

	@Test
	public void RandomLimit() {
		testLimitedSize("basic", "random", 1, 0);
		testLimitedSize("basic", "random", 1, 1);
		testLimitedSize("basic", "random", 1, 2);
		testLimitedSize("simple", "random", 2, 1);
		testLimitedSize("simple", "random", 2, 2);
		testLimitedSize("simple", "random", 2, 3);
		testLimitedSize("car", "random", 7, 0);
		testLimitedSize("car", "random", 7, 1);
		testLimitedSize("car", "random", 7, 5);
		testLimitedSize("car", "random", 7, 7);
		testLimitedSize("car", "random", 7, 10);
		testLimitedSize("car", "random", 7, Integer.MAX_VALUE);
		testLimitedSize("gpl_medium_model", "random", 960, 10);
		testLimitedSize("gpl_medium_model", "random", 960, 960);
		testLimitedSize("gpl_medium_model", "random", 960, Integer.MAX_VALUE);
		testLimitedSize("apl_model", "random", 100, 100);
	}

	@Test
	public void InclingLimit() {
		testPairWiseLimitedSize("gpl_medium_model", "incling", 5);
	}

	@Test
	public void YASALimit() {
		testTWiseLimitedSize("gpl_medium_model", "yasa", 1, 5);
		testTWiseLimitedSize("gpl_medium_model", "yasa", 2, 5);
		testTWiseLimitedSize("gpl_medium_model", "yasa", 3, 5);
	}

	@Test
	public void YASAOneWiseCoverage() {
		testCoverageAndDeterminism("yasa", 1, modelNames);
	}

	@Test
	public void YASATwoWiseCoverage() {
		testCoverageAndDeterminism("yasa", 2, modelNames);
	}

	@Test
	public void YASAThreeWiseCoverage() {
		testCoverageAndDeterminism("yasa", 3, modelNames);
	}

	@Test
	public void InclingTwoWiseCoverage() {
		testPairWiseCoverageAndDeterminism("incling", modelNames);
	}

	private void testCoverageAndDeterminism(final String algorithmName, final int t, final List<String> modelNameList) {
		for (final String modelName : modelNameList) {
			final Path modelFile = modelDirectory.resolve(modelName + ".xml");
			final CNF cnf = loadCNF(modelFile);
			final SolutionList sample = sample(modelFile, algorithmName, t, null);
			assertTrue(sample.getInvalidSolutions(cnf).findFirst().isEmpty(), "Invalid solutions for " + modelFile);
			final TWiseCoverageMetrics tWiseCoverageMetrics = new TWiseCoverageMetrics();
			tWiseCoverageMetrics.setCNF(cnf);
			assertEquals(1.0, tWiseCoverageMetrics.getTWiseCoverageMetric(t).get(sample), 0.0,
				"Wrong coverage for " + modelName);
			final SolutionList sample2 = sample(modelFile, algorithmName, t, null);
			assertEquals(sample.getSolutions().size(), sample2.getSolutions().size(), "Wrong size for " + modelName);
		}
	}

	private void testPairWiseCoverageAndDeterminism(final String algorithmName, final List<String> modelNameList) {
		for (final String modelName : modelNameList) {
			final Path modelFile = modelDirectory.resolve(modelName + ".xml");
			final CNF cnf = loadCNF(modelFile);
			final SolutionList sample = sample(modelFile, algorithmName, null, null);
			assertTrue(sample.getInvalidSolutions(cnf).findFirst().isEmpty(), "Invalid solutions for " + modelFile);
			final TWiseCoverageMetrics tWiseCoverageMetrics = new TWiseCoverageMetrics();
			tWiseCoverageMetrics.setCNF(cnf);
			assertEquals(1.0, tWiseCoverageMetrics.getTWiseCoverageMetric(2).get(sample), 0.0,
				"Wrong coverage for " + modelName);
			final SolutionList sample2 = sample(modelFile, algorithmName, null, null);
			assertEquals(sample.getSolutions().size(), sample2.getSolutions().size(), "Wrong size for " + modelName);
		}
	}

	private static void testSize(String modelName, String algorithm, int numberOfConfigurations) {
		final Path modelFile = modelDirectory.resolve(modelName + ".xml");
		final CNF cnf = loadCNF(modelFile);
		final SolutionList sample = sample(modelFile, algorithm, null, null);
		assertTrue(sample.getInvalidSolutions(cnf).findFirst().isEmpty(), "Invalid solutions for " + modelFile);
		assertEquals(numberOfConfigurations, sample.getSolutions().size(), "Wrong number of configurations for " + modelName);
	}

	private static void testLimitedSize(String modelName, String algorithm, int numberOfConfigurations, int limit) {
		final Path modelFile = modelDirectory.resolve(modelName + ".xml");
		final CNF cnf = loadCNF(modelFile);
		final SolutionList sample = sample(modelFile, algorithm, null, limit);
		assertTrue(sample.getInvalidSolutions(cnf).findFirst().isEmpty(), "Invalid solutions for " + modelFile);
		assertTrue(limit >= sample.getSolutions().size(), "Number of configurations larger than limit for " + modelName);
		assertTrue(Math.min(limit, numberOfConfigurations) == sample.getSolutions().size(), "Wrong number of configurations for "
			+ modelName);
	}

	private static void testTWiseLimitedSize(String modelName, String algorithm, int t, int limit) {
		final Path modelFile = modelDirectory.resolve(modelName + ".xml");
		final SolutionList sample = sample(modelFile, algorithm, t, limit);
		assertTrue(limit >= sample.getSolutions().size(), "Number of configurations larger than limit for " + modelName);
	}

	private static void testPairWiseLimitedSize(String modelName, String algorithm, int limit) {
		final Path modelFile = modelDirectory.resolve(modelName + ".xml");
		final CNF cnf = loadCNF(modelFile);
		final SolutionList sample = sample(modelFile, algorithm, null, limit);
		assertTrue(sample.getInvalidSolutions(cnf).findFirst().isEmpty(), "Invalid solutions for " + modelFile);
		assertTrue(limit >= sample.getSolutions().size(), "Number of configurations larger than limit for " + modelName);
	}

	private static SolutionList sample(final Path modelFile, String algorithm, Integer t, Integer limit) {
		try {
			final Path inFile = Files.createTempFile("input", ".xml");
			Files.write(inFile, Files.readAllBytes(modelFile));

			final Path outFile = Files.createTempFile("output", "");

			final ArrayList<String> args = new ArrayList<>();
			args.add("-a");
			args.add(algorithm);
			args.add("-o");
			args.add(outFile.toString());
			args.add("-fm");
			args.add(inFile.toString());

			if (t != null) {
				args.add("-t");
				args.add(Integer.toString(t));
			}
			if (limit != null) {
				args.add("-l");
				args.add(Integer.toString(limit));
			}
			new ConfigurationGeneratorCLI().run(args);

			final SolutionList sample = FileHandler.parse(outFile, new ConfigurationListFormat()).orElse(
				Logger::logProblems);
			if (sample == null) {
				fail("Sample for " + modelFile.toString() + " could not be read!");
			}
			return sample;
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

	private static CNF loadCNF(final Path modelFile) {
		final CNF cnf = FileHandler.parse(modelFile, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF)
			.orElse(Logger::logProblems);
		if (cnf == null) {
			fail("CNF could not be read!");
		}
		return cnf;
	}
}
