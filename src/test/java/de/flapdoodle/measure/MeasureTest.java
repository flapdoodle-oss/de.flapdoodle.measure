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

import static org.assertj.core.api.Assertions.assertThat;

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

				Config config = Config.builder()
						.timeStampProvider(counter::getAndIncrement)
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
}