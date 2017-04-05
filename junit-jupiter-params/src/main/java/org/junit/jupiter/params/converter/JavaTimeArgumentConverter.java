/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.jupiter.params.converter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import org.junit.jupiter.params.support.AnnotationConsumer;

/**
 * @since 5.0
 */
class JavaTimeArgumentConverter extends SimpleArgumentConverter
		implements AnnotationConsumer<JavaTimeConversionPattern> {

	private String pattern;

	@Override
	public void accept(JavaTimeConversionPattern annotation) {
		pattern = annotation.value();
	}

	@Override
	public Object convert(Object input, Class<?> targetClass) throws ArgumentConversionException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		TemporalQuery<?> temporalQuery = (TemporalQuery<Object>) temporal -> {
			try {
				return targetClass.getMethod("from", TemporalAccessor.class).invoke(null, input);
			}
			catch (Throwable t) {
				throw new AssertionError(t);
			}
		};
		return formatter.parse(input.toString(), temporalQuery);
	}

}
