package dddes.example.infrastructure;

public final class Strings {

	public static void requireNonNullOrWhitespace(String string) {
		if (isNullOrWhitespace(string))
			throw new IllegalArgumentException();
	}

	public static void requireNonNullOrWhitespace(String string, String paramName) {
		if (isNullOrWhitespace(string))
			throw new IllegalArgumentException(paramName + " must be not null or whitespace");
	}

	/**
	 * Returns true if the given string is null or empty.
	 * 
	 * @param s
	 * @return true if the given string is null or empty.
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * Returns true if the given string is null, empty or whitespace.
	 * 
	 * @param s
	 * @return true if the given string is null, empty or whitespace.
	 */
	public static boolean isNullOrWhitespace(String s) {
		return isNullOrEmpty(s) || isWhitespace(s);
	}

	private static boolean isWhitespace(String s) {
		int length = s.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				if (!Character.isWhitespace(s.charAt(i)))
					return false;
			}
			return true;
		}
		return false;
	}
}
