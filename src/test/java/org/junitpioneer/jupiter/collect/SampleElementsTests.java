package org.junitpioneer.jupiter.collect;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SampleElementsTests {

	@Test
	void strings() {
		var strings = SampleElements.strings();
		assertAll(
				() -> assertThat(strings.e0()).isEqualTo("a"),
				() -> assertThat(strings.e1()).isEqualTo("b"),
				() -> assertThat(strings.e2()).isEqualTo("c"),
				() -> assertThat(strings.missing()).isEqualTo("missing"));
	}

}