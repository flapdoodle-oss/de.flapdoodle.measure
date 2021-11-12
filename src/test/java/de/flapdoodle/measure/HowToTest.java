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
}
