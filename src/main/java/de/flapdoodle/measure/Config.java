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

import java.util.function.Consumer;
import java.util.function.Supplier;

@Value.Immutable
public interface Config {

		@Value.Default
		default Supplier<Long> timeStampProvider() {
				return System::currentTimeMillis;
		}

		@Value.Default
		default Consumer<String> reportConsumer() {
				return System.out::println;
		}

		static ImmutableConfig.Builder builder() {
				return ImmutableConfig.builder();
		}

		static ImmutableConfig defaults() {
				return builder().build();
		}
}
