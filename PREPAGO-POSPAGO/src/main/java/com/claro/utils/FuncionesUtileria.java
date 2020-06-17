package com.claro.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class FuncionesUtileria {

	private static final String DATE_ZONE = "America/Bogota";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private FuncionesUtileria() {

	}

	public static String fechaT() {
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(DATE_ZONE));
		return DateTimeFormatter.ofPattern(DATE_FORMAT).format(zonedDateTime);
	}

	public static String fechaWithT() {
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(DATE_ZONE));
		return zonedDateTime.truncatedTo(ChronoUnit.SECONDS)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
	}

	public static String completeIdReference(String input) {
		StringBuilder data = new StringBuilder(input);
		while (data.length() < 24) {
			data.insert(0, "0");
		}
		return data.toString();
	}

	public static int[] canales() {
		return new int[] { 0, 1, 2, 3 };
	}

}
