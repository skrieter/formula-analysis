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
package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.cli.*;
import org.spldev.util.extension.*;

/**
 * Tests sampling algorithms.
 *
 * @author Sebastian Krieter
 */
public class AnalysisTest {

	static {
		ExtensionLoader.load();
	}

	private final static Path modelDirectory = Paths.get("src/test/resources/testFeatureModels");
	private final static String resultPrefix = "Result:\n";

	private final List<String> modelNames = Arrays.asList( //
		"basic", //
		"simple", //
		"car", //
		"gpl_medium_model");

	private static final ByteArrayOutputStream newOut = new ByteArrayOutputStream();
	private static final PrintStream orgOut = System.out;

	@BeforeEach
	public void setStreams() {
		System.setOut(new PrintStream(newOut));
	}

	@AfterEach
	public void restoreInitialStreams() {
		System.setOut(orgOut);
	}

	@Test
	public void Satisfiable() {
		final String result = analyze(modelDirectory.resolve(modelNames.get(0) + ".xml"), "satisfiable");
		assertEquals("true", result);
	}

	private static String analyze(final Path modelFile, String algorithm) {
		try {
			final Path inFile = Files.createTempFile("input", ".xml");
			try {
				Files.write(inFile, Files.readAllBytes(modelFile));
				final ArrayList<String> args = new ArrayList<>();
				args.add("-a");
				args.add(algorithm);
				args.add("-fm");
				args.add(inFile.toString());
				new AnalysisCLI().run(args);
				final String output = newOut.toString();
				final int index = output.indexOf(resultPrefix);
				return (index < 0) ? null : output.substring(index + resultPrefix.length()).trim();
			} finally {
				Files.deleteIfExists(inFile);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}

}
