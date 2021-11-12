/**
 * Copyright (C) 2021
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.measure;

import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HowToTest {

		@ClassRule
		public static Recording recording = Recorder.with("HowToMeasureRuntime.md", TabSize.spaces(2));

		@Test
		public void vertex() {
				recording.begin();
				// code
				recording.end();
		}

		@Test
		public void createAFileInTempDir() {
				recording.begin();
				// part 1
				recording.end();
				// part 2
				recording.begin();
				// part 3
				recording.end();
		}

		@Test
		public void writeContentIntoFileInTempDir() {
				recording.begin();

				String dotFile = "";
				recording.end();

				recording.output("app.dot", dotFile.replace("\t", "  "));
		}

		@Test
		public void sampleWithReport() {
				final AtomicLong counter = new AtomicLong(0L);
				final AtomicReference<String> report = new AtomicReference<>("");

				Config config = Config.builder()
						.timeStampProvider(counter::getAndIncrement)
						.reportConsumer(report::set)
						.build();

				recording.begin();
				try (Hook root = Measure.root("root", config)) {
						try (Hook sub = Measure.start("block")) {
								// code block
						}
						try (Hook loop = Measure.start("loop")) {
								// different code block
						}
						try (Hook loop = Measure.start("loop")) {
								// other code block
						}
						try (Hook loop = Measure.start("loop")) {
								// maybe a for loop
								try (Hook sub = Measure.start("sub")) {
										// code inside a loop
								}
						}
				}
				recording.end();

				assertEquals("-----------------------------\n"
						+ "root:block -> 1(started: 1, stopped: 2)\n"
						+ "root:loop -> 1(started: 3, stopped: 4)\n"
						+ "root:loop -> 1(started: 5, stopped: 6)\n"
						+ "root:loop:sub -> 1(started: 8, stopped: 9)\n"
						+ "root:loop -> 3(started: 7, stopped: 10)\n"
						+ "root -> 11(started: 0, stopped: 11)\n"
						+ "- - - - - - - - - - - - - - - \n"
						+ "root -> 11ms (1 loops, min: 11ms, max: 11ms)\n"
						+ "root:block -> 1ms (1 loops, min: 1ms, max: 1ms)\n"
						+ "root:loop -> 5ms (3 loops, min: 1ms, max: 3ms)\n"
						+ "root:loop:sub -> 1ms (1 loops, min: 1ms, max: 1ms)\n"
						+ "-----------------------------\n", report.get());

				recording.output("report", report.get());
		}

}
