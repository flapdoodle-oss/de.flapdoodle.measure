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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Measure {

		// visible for testing
		static final ThreadLocal<Recording> recording =new ThreadLocal<>();

		public static Hook root(String label) {
				return root(label, Config.defaults());
		}

		public static Hook root(String label, Config config) {
				Recording old = Measure.recording.get();
				if (old !=null) throw new IllegalArgumentException("already recording",old.alreadyRunningException());
				
				Recording recording = new Recording(label, config);
				Measure.recording.set(recording);
				return recording;
		}

		public static Hook start(String label) {
				Recording current = recording.get();
				if (current!=null) {
						return current.start(label);
				}

				return () -> {
						// do nothing
				};
		}

		static class Recording implements Hook {
				private final IllegalArgumentException alreadyRunning=new IllegalArgumentException("recording already started");

				// visible for testing
				final List<Record> records=new ArrayList<>();

				private final Config config;

				private Path path;
				private final long started;

				public Recording(String label, Config config) {
						this.config=config;
						this.path=Path.of(label);
						this.started = config.timeStampProvider().get();
				}

				public Hook start(String label) {
						Path oldPath = path;
						path = path.append(label);
						return new Sub(path, oldPath);
				}

				@Override public void close() {
						records.add(Record.of(path,started,config.timeStampProvider().get()));
						recording.set(null);
				}

				public IllegalArgumentException alreadyRunningException() {
						return alreadyRunning;
				}

				class Sub implements Hook {

						private final Path current;
						private final Path oldPath;
						private final long started;

						public Sub(Path current, Path oldPath) {
								this.started=config.timeStampProvider().get();
								this.current = current;
								this.oldPath = oldPath;
						}
						@Override public void close() {
								records.add(Record.of(current,started,config.timeStampProvider().get()));
								Recording.this.path=oldPath;
						}
				}
		}
}
