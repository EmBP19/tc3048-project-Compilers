package lexer;

import java.io.*;
import java.util.Hashtable;

public class Lexer {
	private char peek;
	private Hashtable<String, Token> words = new Hashtable<String, Token>();
	private InputStream input;
	public static int line = 1;

	private void addReserveWord(Word w) {
		words.put(w.getLexeme(), w);
	}

	public Lexer(InputStream input) {
		this.peek = ' ';

		this.input = input;
		addReserveWord(new Word("MAKE", Tag.MAKE));
		addReserveWord(new Word("FORWARD", Tag.FORWARD));
		addReserveWord(new Word("FD", Tag.FORWARD));
	}

	private void readch() throws IOException {
		peek = (char) input.read();
	}

	private boolean readch(char c) throws IOException {
		readch();
		if (peek != c)
			return false;

		peek = ' ';
		return true;
	}

	private void skipSpaces() throws IOException {
		for ( ; ; readch() ) {
			if (peek == ' ' || peek == '\t' || peek == '\r') {
				continue;
			} else if (peek == '\n') {
				line = line + 1;
			} else {
				break;
			}
		}
	}

	public Token scan() throws IOException {
		skipSpaces();

		// ADD CODE TO SKIP COMMENTS HERE

		switch(peek) {
			case '<':
				if (readch('=')) return Word.Leq;
				else if (readch('>')) return Word.Neq;
				else return new Token('<');
			case '>':
				if (readch('=')) return Word.Geq;
				else return new Token('>');
			case '#':
				if (readch('t')) return Word.True;
				else if (readch('f')) return Word.False;
				else return new Token('#');
		}

		if (peek == '"') {
			String val = "";
			do {
				val = val + peek;
				readch();
			} while ( peek != '"' );
			val = val + peek;
			readch();
			return new Text(val);
		}

		if (Character.isDigit(peek)) {
			double v = 0;
			do {
				v = (10 * v) + Character.digit(peek, 10);
				readch();
			} while ( Character.isDigit(peek) );

			// ADD CODE TO PROCESS DECIMAL PART HERE

			return new Number(v);
		}

		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(Character.toUpperCase(peek));
				readch();
			} while ( Character.isLetterOrDigit(peek) ) ;
			String s = b.toString();
			Word w = (Word) words.get(s);
			if (w != null)
				return w;

			w = new Word(s, Tag.ID);
			words.put(s, w);
			return w;
		}

		Token tok = new Token(peek); peek = ' ';
		return tok;
	}
}
