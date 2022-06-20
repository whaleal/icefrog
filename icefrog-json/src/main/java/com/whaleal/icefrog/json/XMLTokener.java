package com.whaleal.icefrog.json;

/**
 * XML分析器，继承自JSONTokener，提供XML的语法分析
 *
 * @author JSON.org
 */
public class XMLTokener extends com.whaleal.icefrog.json.JSONTokener {

	/**
	 * The table of entity values.
	 * It initially contains Character values for amp, apos, gt, lt, quot.
	 */
	public static final java.util.HashMap<String, Character> entity;

	static {
		entity = new java.util.HashMap<>(8);
		entity.put("amp", com.whaleal.icefrog.json.XML.AMP);
		entity.put("apos", com.whaleal.icefrog.json.XML.APOS);
		entity.put("gt", com.whaleal.icefrog.json.XML.GT);
		entity.put("lt", com.whaleal.icefrog.json.XML.LT);
		entity.put("quot", com.whaleal.icefrog.json.XML.QUOT);
	}

	/**
	 * Construct an XMLTokener from a string.
	 *
	 * @param s      A source string.
	 * @param config JSON配置
	 */
	public XMLTokener(CharSequence s, com.whaleal.icefrog.json.JSONConfig config) {
		super(s, config);
	}

	/**
	 * Get the text in the CDATA block.
	 *
	 * @return The string up to the {@code ]]>}.
	 * @throws com.whaleal.icefrog.json.JSONException If the {@code ]]>} is not found.
	 */
	public String nextCDATA() throws com.whaleal.icefrog.json.JSONException {
		char c;
		int i;
		StringBuilder sb = new StringBuilder();
		for (; ; ) {
			c = next();
			if (end()) {
				throw syntaxError("Unclosed CDATA");
			}
			sb.append(c);
			i = sb.length() - 3;
			if (i >= 0 && sb.charAt(i) == ']' && sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
				sb.setLength(i);
				return sb.toString();
			}
		}
	}

	/**
	 * Get the next XML outer token, trimming whitespace.
	 * There are two kinds of tokens: the '&gt;' character which begins a markup tag, and the content text between markup tags.
	 *
	 * @return A string, or a '&gt;' Character, or null if there is no more source text.
	 * @throws com.whaleal.icefrog.json.JSONException JSON
	 */
	public Object nextContent() throws com.whaleal.icefrog.json.JSONException {
		char c;
		StringBuilder sb;
		do {
			c = next();
		} while (Character.isWhitespace(c));
		if (c == 0) {
			return null;
		}
		if (c == '<') {
			return com.whaleal.icefrog.json.XML.LT;
		}
		sb = new StringBuilder();
		for (; ; ) {
			if (c == '<' || c == 0) {
				back();
				return sb.toString().trim();
			}
			if (c == '&') {
				sb.append(nextEntity(c));
			} else {
				sb.append(c);
			}
			c = next();
		}
	}

	/**
	 * Return the next entity. These entities are translated to Characters: {@code &  '  >  <  "}.
	 *
	 * @param ampersand An ampersand character.
	 * @return A Character or an entity String if the entity is not recognized.
	 * @throws com.whaleal.icefrog.json.JSONException If missing ';' in XML entity.
	 */
	public Object nextEntity(char ampersand) throws com.whaleal.icefrog.json.JSONException {
		StringBuilder sb = new StringBuilder();
		for (; ; ) {
			char c = next();
			if (Character.isLetterOrDigit(c) || c == '#') {
				sb.append(Character.toLowerCase(c));
			} else if (c == ';') {
				break;
			} else {
				throw syntaxError("Missing ';' in XML entity: &" + sb);
			}
		}
		String string = sb.toString();
		Object object = entity.get(string);
		return object != null ? object : ampersand + string + ";";
	}

	/**
	 * Returns the next XML meta token. This is used for skipping over &lt;!...&gt; and &lt;?...?&gt; structures.
	 *
	 * @return Syntax characters ({@code < > / = ! ?}) are returned as Character, and strings and names are returned as Boolean. We don't care what the values actually are.
	 * @throws com.whaleal.icefrog.json.JSONException 字符串中属性未关闭或XML结构错误抛出此异常。If a string is not properly closed or if the XML is badly structured.
	 */
	public Object nextMeta() throws com.whaleal.icefrog.json.JSONException {
		char c;
		char q;
		do {
			c = next();
		} while (Character.isWhitespace(c));
		switch (c) {
			case 0:
				throw syntaxError("Misshaped meta tag");
			case '<':
				return com.whaleal.icefrog.json.XML.LT;
			case '>':
				return com.whaleal.icefrog.json.XML.GT;
			case '/':
				return com.whaleal.icefrog.json.XML.SLASH;
			case '=':
				return com.whaleal.icefrog.json.XML.EQ;
			case '!':
				return com.whaleal.icefrog.json.XML.BANG;
			case '?':
				return com.whaleal.icefrog.json.XML.QUEST;
			case '"':
			case '\'':
				q = c;
				for (; ; ) {
					c = next();
					if (c == 0) {
						throw syntaxError("Unterminated string");
					}
					if (c == q) {
						return Boolean.TRUE;
					}
				}
			default:
				for (; ; ) {
					c = next();
					if (Character.isWhitespace(c)) {
						return Boolean.TRUE;
					}
					switch (c) {
						case 0:
						case '<':
						case '>':
						case '/':
						case '=':
						case '!':
						case '?':
						case '"':
						case '\'':
							back();
							return Boolean.TRUE;
					}
				}
		}
	}

	/**
	 * Get the next XML Token. These tokens are found inside of angle brackets. <br>
	 * It may be one of these characters: {@code / > = ! ?} or it may be a string wrapped in single quotes or double
	 * quotes, or it may be a name.
	 *
	 * @return a String or a Character.
	 * @throws com.whaleal.icefrog.json.JSONException If the XML is not well formed.
	 */
	public Object nextToken() throws com.whaleal.icefrog.json.JSONException {
		char c;
		char q;
		StringBuilder sb;
		do {
			c = next();
		} while (Character.isWhitespace(c));
		switch (c) {
			case 0:
				throw syntaxError("Misshaped element");
			case '<':
				throw syntaxError("Misplaced '<'");
			case '>':
				return com.whaleal.icefrog.json.XML.GT;
			case '/':
				return com.whaleal.icefrog.json.XML.SLASH;
			case '=':
				return com.whaleal.icefrog.json.XML.EQ;
			case '!':
				return com.whaleal.icefrog.json.XML.BANG;
			case '?':
				return com.whaleal.icefrog.json.XML.QUEST;

			// Quoted string

			case '"':
			case '\'':
				q = c;
				sb = new StringBuilder();
				for (; ; ) {
					c = next();
					if (c == 0) {
						throw syntaxError("Unterminated string");
					}
					if (c == q) {
						return sb.toString();
					}
					if (c == '&') {
						sb.append(nextEntity(c));
					} else {
						sb.append(c);
					}
				}
			default:

				// Name

				sb = new StringBuilder();
				for (; ; ) {
					sb.append(c);
					c = next();
					if (Character.isWhitespace(c)) {
						return sb.toString();
					}
					switch (c) {
						case 0:
							return sb.toString();
						case '>':
						case '/':
						case '=':
						case '!':
						case '?':
						case '[':
						case ']':
							back();
							return sb.toString();
						case '<':
						case '"':
						case '\'':
							throw syntaxError("Bad character in a name");
					}
				}
		}
	}

	/**
	 * Skip characters until past the requested string. If it is not found, we are left at the end of the source with a result of false.
	 *
	 * @param to A string to skip past.
	 * @return 是否成功skip
	 * @throws com.whaleal.icefrog.json.JSONException JSON异常
	 */
	public boolean skipPast(String to) throws com.whaleal.icefrog.json.JSONException {
		boolean b;
		char c;
		int i;
		int j;
		int offset = 0;
		int length = to.length();
		char[] circle = new char[length];

		/*
		 * First fill the circle buffer with as many characters as are in the to string. If we reach an early end, bail.
		 */

		for (i = 0; i < length; i += 1) {
			c = next();
			if (c == 0) {
				return false;
			}
			circle[i] = c;
		}

		/* We will loop, possibly for all of the remaining characters. */

		for (; ; ) {
			j = offset;
			b = true;

			/* Compare the circle buffer with the to string. */

			for (i = 0; i < length; i += 1) {
				if (circle[j] != to.charAt(i)) {
					b = false;
					break;
				}
				j += 1;
				if (j >= length) {
					j -= length;
				}
			}

			/* If we exit the loop with b intact, then victory is ours. */

			if (b) {
				return true;
			}

			/* Get the next character. If there isn't one, then defeat is ours. */

			c = next();
			if (c == 0) {
				return false;
			}
			/*
			 * Shove the character in the circle buffer and advance the circle offset. The offset is mod n.
			 */
			circle[offset] = c;
			offset += 1;
			if (offset >= length) {
				offset -= length;
			}
		}
	}
}
