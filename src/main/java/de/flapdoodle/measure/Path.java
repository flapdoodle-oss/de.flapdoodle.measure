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

import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
abstract class Path implements Comparable<Path> {
		public abstract List<String> parts();

		@Value.Auxiliary
		public Path append(String part) {
				return ImmutablePath.builder()
						.addAllParts(parts())
						.addParts(part)
						.build();
		}

		@Override
		public String toString() {
				return parts().stream().collect(Collectors.joining(":"));
		}

		@Override
		public int compareTo(final Path o) {
				return toString().compareTo(o.toString());
		}

		public static Path of(String root) {
				return ImmutablePath.builder().addParts(root).build();
		}
}
