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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.params.support.AnnotationConsumer;

/**
 * @since 5.0
 */
class JavaTimeArgumentConverter extends SimpleArgumentConverter
		implements AnnotationConsumer<JavaTimeConversionPattern> {

	private static final Map<Class<?>, TemporalQuery<?>> TEMPORAL_QUERIES;
	static {
		Map<Class<?>, TemporalQuery<?>> queries = new LinkedHashMap<>();
		queries.put(ChronoLocalDate.class, null);
		queries.put(ChronoLocalDateTime.class, null);
		queries.put(ChronoZonedDateTime.class, null);
		queries.put(LocalDate.class, null);
		queries.put(LocalDateTime.class, null);
		queries.put(LocalTime.class, null);
		queries.put(OffsetDateTime.class, null);
		queries.put(OffsetTime.class, null);
		queries.put(Year.class, null);
		queries.put(YearMonth.class, null);
		queries.put(ZonedDateTime.class, null);
		TEMPORAL_QUERIES = Collections.unmodifiableMap(queries);
	}

	private String pattern;

	@Override
	public void accept(JavaTimeConversionPattern annotation) {
		pattern = annotation.value();
	}

	@Override
	public Object convert(Object input, Class<?> targetClass) throws ArgumentConversionException {
		if (!TEMPORAL_QUERIES.containsKey(targetClass)) {
			throw new ArgumentConversionException("Cannot convert to " + targetClass.getName() + ": " + input);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		TemporalQuery<?> temporalQuery = new Query(targetClass);
		return formatter.parse(input.toString(), temporalQuery);
	}

	static class Query implements TemporalQuery<Object> {

		final Class<?> targetClass;

		Query(Class<?> targetClass) {
			this.targetClass = targetClass;
		}

		@Override
		public Object queryFrom(TemporalAccessor temporal) {
			try {
				return targetClass.getMethod("from", TemporalAccessor.class).invoke(null, temporal);
			}
			catch (Throwable t) {
				throw new AssertionError(t);
			}
		}
	}

}
