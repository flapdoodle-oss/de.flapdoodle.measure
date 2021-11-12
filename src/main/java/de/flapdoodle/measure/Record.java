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

import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
abstract class Record {
		public abstract Path path();

		public abstract long start();

		public abstract long stop();

		public static Record of(Path path, long start, long stop) {
				return ImmutableRecord.builder()
						.path(path)
						.start(start)
						.stop(stop)
						.build();
		}

		@Auxiliary
		public String asHumanReadable(long recordingStartedAt) {
				return path()
						+ " -> "
						+ diff(start(), stop())
						+ "(started: "
						+ diff(recordingStartedAt, start())
						+ ", stopped: "
						+ diff(recordingStartedAt, stop())
						+ ")";
		}

		private static String diff(long start, long stop) {
				return "" + (stop - start);
		}

		public static String timeSpendIn(final List<Record> records) {
				final List<Long> timeSpend = records.stream()
						.map(r -> r.stop() - r.start())
						.collect(Collectors.toList());

				final Long sum = timeSpend
						.stream()
						.reduce(0L, Long::sum);
				final String min = timeSpend.stream().min(Comparator.naturalOrder()).map(it -> "" + it + "ms").orElse("-");
				final String max = timeSpend.stream().max(Comparator.naturalOrder()).map(it -> "" + it + "ms").orElse("-");

				return "" + sum + "ms (" + records.size() + " loops, min: " + min + ", max: " + max + ")";
		}
}
