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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MeasureTest {

		@Test
		public void callingRootTwiceMustFail() {
				Assertions.assertThatThrownBy(() -> {
						try (Hook hook = Measure.root("root")) {
								Measure.root("subMustFail");
						}
				}).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void startMeasurementWithRoot() {
				AtomicLong counter = new AtomicLong(0);
				AtomicReference<String> report=new AtomicReference<>("");

				Config config = Config.builder()
						.timeStampProvider(counter::getAndIncrement)
						.reportConsumer(report::set)
						.build();

				final Measure.Recording recording;
				try (Hook hook = Measure.root("root", config)) {
						recording = Measure.recording.get();
				}

				assertThat(recording.records)
						.hasSize(1)
						.element(0).satisfies(entry -> {
								assertThat(entry.path()).isEqualTo(Path.of("root"));
								assertThat(entry.start()).isEqualTo(0L);
								assertThat(entry.stop()).isEqualTo(1L);
						});

		}

		@Test
		public void recordSubCall() {
				AtomicLong counter = new AtomicLong(0);

				Config config = Config.builder()
						.timeStampProvider(counter::getAndIncrement)
						.reportConsumer(report -> {})
						.build();

				final Measure.Recording recording;
				try (Hook hook = Measure.root("root", config)) {
						try (Hook sub = Measure.start("sub")) {

						}
						recording = Measure.recording.get();
				}

				assertThat(recording.records)
						.hasSize(2)
						.satisfiesExactly(
								entry -> {
										assertThat(entry.path()).isEqualTo(Path.of("root").append("sub"));
										assertThat(entry.start()).isEqualTo(1L);
										assertThat(entry.stop()).isEqualTo(2L);
								},
								entry -> {
										assertThat(entry.path()).isEqualTo(Path.of("root"));
										assertThat(entry.start()).isEqualTo(0L);
										assertThat(entry.stop()).isEqualTo(3L);
								}
						);

		}

		@Test
		public void reportShouldBuildTree() {
				final AtomicLong counter = new AtomicLong(0L);
				final AtomicReference<String> report=new AtomicReference<>("");

				try (Hook root = Measure.root("root", Config.builder()
						.timeStampProvider(counter::getAndIncrement)
								.reportConsumer(report::set)
						.build())) {
						try (Hook sub = Measure.start("block")) {

						}
						try (Hook loop = Measure.start("loop")) {
						}
						try (Hook loop = Measure.start("loop")) {
						}
						try (Hook loop = Measure.start("loop")) {
								try (Hook sub = Measure.start("sub")) {
								}
						}
				}

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
		}
}
