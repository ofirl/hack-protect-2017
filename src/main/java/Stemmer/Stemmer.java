package Stemmer;
/**
 * Stemmer, implementing the Porter Stemming Algorithm
 *
 * The Stemmer class transforms a word into its root form.  The input
 * word can be provided a character at time (by calling add()), or at once
 * by calling one of the various stem(something) methods.
 */

public class Stemmer {
	private char[] b;
	private int i,     /* offset into b */
			i_end, /* offset to end of stemmed word */
			j, k;

private static final int INC = 50;
/* unit of size whereby b is increased */
public Stemmer()
{  b = new char[INC];
i = 0;
i_end = 0;
}

/**
 * Add a character to the word being stemmed.  When you are finished
 * adding characters, you can call stem(void) to stem the word.
 */

public void add(char ch)
{  if (i == b.length)
{  char[] new_b = new char[i+INC];
for (int c = 0; c < i; c++) new_b[c] = b[c];
b = new_b;
}
b[i++] = ch;
}


/** Adds wLen characters to the word being stemmed contained in a portion
 * of a char[] array. This is like repeated calls of add(char ch), but
 * faster.
 */

public void add(char[] w, int wLen)
{  if (i+wLen >= b.length)
{  char[] new_b = new char[i+wLen+INC];
for (int c = 0; c < i; c++) new_b[c] = b[c];
b = new_b;
}
for (int c = 0; c < wLen; c++) b[i++] = w[c];
}

/**
 * After a word has been stemmed, it can be retrieved by toString(),
 * or a reference to the internal buffer can be retrieved by getResultBuffer
 * and getResultLength (which is generally more efficient.)
 */
public String toString() { return new String(b,0,i_end); }

/**
 * Returns the length of the word resulting from the stemming process.
 */
public int getResultLength() { return i_end; }

/**
 * Returns a reference to a character buffer containing the results of
 * the stemming process.  You also need to consult getResultLength()
 * to determine the length of the result.
 */
public char[] getResultBuffer() { return b; }

/* cons(i) is true <=> b[i] is a consonant. */

private final boolean cons(int i)
{  switch (b[i])
	{  case 'a': case 'e': case 'i': case 'o': case 'u': return false;
	case 'y': return (i==0) ? true : !cons(i-1);
	default: return true;
	}
}

/* m() measures the number of consonant sequences between 0 and j. if c is
      a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
      presence,

         <c><v>       gives 0
         <c>vc<v>     gives 1
         <c>vcvc<v>   gives 2
         <c>vcvcvc<v> gives 3
         ....
 */

private final int m()
{  int n = 0;
int i = 0;
while(true)
{  if (i > j) return n;
if (! cons(i)) break; i++;
}
i++;
while(true)
{  while(true)
{  if (i > j) return n;
if (cons(i)) break;
i++;
}
i++;
n++;
while(true)
{  if (i > j) return n;
if (! cons(i)) break;
i++;
}
i++;
}
}

/* vowelinstem() is true <=> 0,...j contains a vowel */

private final boolean vowelinstem()
{  int i; for (i = 0; i <= j; i++) if (! cons(i)) return true;
return false;
}

/* doublec(j) is true <=> j,(j-1) contain a double consonant. */

private final boolean doublec(int j)
{  if (j < 1) return false;
if (b[j] != b[j-1]) return false;
return cons(j);
}

/* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
      and also if the second c is not w,x or y. this is used when trying to
      restore an e at the end of a short word. e.g.

         cav(e), lov(e), hop(e), crim(e), but
         snow, box, tray.

 */

private final boolean cvc(int i)
{  if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)) return false;
{  int ch = b[i];
if (ch == 'w' || ch == 'x' || ch == 'y') return false;
}
return true;
}

private final boolean ends(String s)
{  int l = s.length();
int o = k-l+1;
if (o < 0) return false;
for (int i = 0; i < l; i++) if (b[o+i] != s.charAt(i)) return false;
j = k-l;
return true;
}

/* setto(s) sets (j+1),...k to the characters in the string s, readjusting
      k. */

private final void setto(String s)
{  int l = s.length();
int o = j+1;
for (int i = 0; i < l; i++) b[o+i] = s.charAt(i);
k = j+l;
}

/* r(s) is used further down. */

private final void r(String s) { if (m() > 0) setto(s); }

/* step1() gets rid of plurals and -ed or -ing. e.g.

          caresses  ->  caress
          ponies    ->  poni
          ties      ->  ti
          caress    ->  caress
          cats      ->  cat

          feed      ->  feed
          agreed    ->  agree
          disabled  ->  disable

          matting   ->  mat
          mating    ->  mate
          meeting   ->  meet
          milling   ->  mill
          messing   ->  mess

          meetings  ->  meet

 */

private final void step1()
{  if (b[k] == 's')
{  if (ends("sses")) k -= 2; else
	if (ends("ies")) setto("i"); else
		if (b[k-1] != 's') k--;
}
if (ends("eed")) { if (m() > 0) k--; } else
	if ((ends("ed") || ends("ing")) && vowelinstem())
	{  k = j;
	if (ends("at")) setto("ate"); else
		if (ends("bl")) setto("ble"); else
			if (ends("iz")) setto("ize"); else
				if (doublec(k))
				{  k--;
				{  int ch = b[k];
				if (ch == 'l' || ch == 's' || ch == 'z') k++;
				}
				}
				else if (m() == 1 && cvc(k)) setto("e");
	}
}

/* step2() turns terminal y to i when there is another vowel in the stem. */

private final void step2() { if (ends("y") && vowelinstem()) b[k] = 'i'; }

/* step3() maps double suffices to single ones. so -ization ( = -ize plus
      -ation) maps to -ize etc. note that the string before the suffix must give
      m() > 0. */

private final void step3() { if (k == 0) return; /* For Bug 1 */ switch (b[k-1])
{
case 'a': if (ends("ational")) { r("ate"); break; }
if (ends("tional")) { r("tion"); break; }
break;
case 'c': if (ends("enci")) { r("ence"); break; }
if (ends("anci")) { r("ance"); break; }
break;
case 'e': if (ends("izer")) { r("ize"); break; }
break;
case 'l': if (ends("bli")) { r("ble"); break; }
if (ends("alli")) { r("al"); break; }
if (ends("entli")) { r("ent"); break; }
if (ends("eli")) { r("e"); break; }
if (ends("ousli")) { r("ous"); break; }
break;
case 'o': if (ends("ization")) { r("ize"); break; }
if (ends("ation")) { r("ate"); break; }
if (ends("ator")) { r("ate"); break; }
break;
case 's': if (ends("alism")) { r("al"); break; }
if (ends("iveness")) { r("ive"); break; }
if (ends("fulness")) { r("ful"); break; }
if (ends("ousness")) { r("ous"); break; }
break;
case 't': if (ends("aliti")) { r("al"); break; }
if (ends("iviti")) { r("ive"); break; }
if (ends("biliti")) { r("ble"); break; }
break;
case 'g': if (ends("logi")) { r("log"); break; }
} }

/* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */

private final void step4() { switch (b[k])
	{
	case 'e': if (ends("icate")) { r("ic"); break; }
	if (ends("ative")) { r(""); break; }
	if (ends("alize")) { r("al"); break; }
	break;
	case 'i': if (ends("iciti")) { r("ic"); break; }
	break;
	case 'l': if (ends("ical")) { r("ic"); break; }
	if (ends("ful")) { r(""); break; }
	break;
	case 's': if (ends("ness")) { r(""); break; }
	break;
	} }

/* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */

private final void step5()
{   if (k == 0) return; /* for Bug 1 */ switch (b[k-1])
{  case 'a': if (ends("al")) break; return;
case 'c': if (ends("ance")) break;
if (ends("ence")) break; return;
case 'e': if (ends("er")) break; return;
case 'i': if (ends("ic")) break; return;
case 'l': if (ends("able")) break;
if (ends("ible")) break; return;
case 'n': if (ends("ant")) break;
if (ends("ement")) break;
if (ends("ment")) break;
/* element etc. not stripped before the m */
if (ends("ent")) break; return;
case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
/* j >= 0 fixes Bug 2 */
if (ends("ou")) break; return;
/* takes care of -ous */
case 's': if (ends("ism")) break; return;
case 't': if (ends("ate")) break;
if (ends("iti")) break; return;
case 'u': if (ends("ous")) break; return;
case 'v': if (ends("ive")) break; return;
case 'z': if (ends("ize")) break; return;
default: return;
}
if (m() > 1) k = j;
}

/* step6() removes a final -e if m() > 1. */

private final void step6()
{  j = k;
if (b[k] == 'e')
{  int a = m();
if (a > 1 || a == 1 && !cvc(k-1)) k--;
}
if (b[k] == 'l' && doublec(k) && m() > 1) k--;
}

/** Stem the word placed into the Stemmer buffer through calls to add().
 * Returns true if the stemming process resulted in a word different
 * from the input.  You can retrieve the result with
 * getResultLength()/getResultBuffer() or toString().
 */
public void stem()
{  k = i - 1;
if (k > 1) { step1(); step2(); step3(); step4(); step5(); step6(); }
i_end = k+1; i = 0;
}

/** Test program for demonstrating the Stemmer.  It reads text from a
 * a list of files, stems each word, and writes the result to standard
 * output. Note that the word stemmed is expected to be in lower case:
 * forcing lower case must be done outside the Stemmer class.
 * Usage: Stemmer file-name file-name ...
 */


public static String[] removStopWords (String[] s, String[] stopWords){
	int left= s.length;	
	for (int i=0; i<s.length; i++){
		for (int j=0; j< stopWords[i].length(); j++){
			if (s[i].equals(stopWords[j])){
				s[i]="";
				left--;
			}
			if (left==0){
				break;
			}
		}
	}
	return s;
}

public static String parseString (String st, String[] stopWords){
	String[] stSplited= st.split(" ");
	String stFinal = "";
	String[] newS= removStopWords(stSplited, stopWords);
	Stemmer s= new Stemmer();
	for (int i=0; i<stSplited.length; i++){
		char[] c= stSplited[i].toCharArray();
		for (int j=0; j<c.length; j++){
			c[j] = Character.toLowerCase((char) c[j]);
			s.add(c[j]);
		}
		s.stem();
		if (!s.toString().equals("")){
			stFinal+=s.toString()+ " ";
		}
	}
	return stFinal;

}

public static void main(String[] args)
{
	String[] stopWords={  "a",
			  "able",
			  "about",
			  "above",
			  "abroad",
			  "according",
			  "accordingly",
			  "across",
			  "actually",
			  "adj",
			  "after",
			  "afterwards",
			  "again",
			  "against",
			  "ago",
			  "ahead",
			  "aint",
			  "all",
			  "allow",
			  "allows",
			  "almost",
			  "alone",
			  "along",
			  "alongside",
			  "already",
			  "also",
			  "although",
			  "always",
			  "am",
			  "amid",
			  "amidst",
			  "among",
			  "amongst",
			  "an",
			  "and",
			  "another",
			  "any",
			  "anybody",
			  "anyhow",
			  "anyone",
			  "anything",
			  "anyway",
			  "anyways",
			  "anywhere",
			  "apart",
			  "appear",
			  "appreciate",
			  "appropriate",
			  "are",
			  "arent",
			  "around",
			  "as",
			  "as",
			  "aside",
			  "ask",
			  "asking",
			  "associated",
			  "at",
			  "available",
			  "away",
			  "awfully",
			  "b",
			  "back",
			  "backward",
			  "backwards",
			  "be",
			  "became",
			  "because",
			  "become",
			  "becomes",
			  "becoming",
			  "been",
			  "before",
			  "beforehand",
			  "begin",
			  "behind",
			  "being",
			  "believe",
			  "below",
			  "beside",
			  "besides",
			  "best",
			  "better",
			  "between",
			  "beyond",
			  "both",
			  "brief",
			  "but",
			  "by",
			  "c",
			  "came",
			  "can",
			  "cannot",
			  "cant",
			  "cant",
			  "caption",
			  "cause",
			  "causes",
			  "certain",
			  "certainly",
			  "changes",
			  "clearly",
			  "cmon",
			  "co",
			  "co.",
			  "com",
			  "come",
			  "comes",
			  "concerning",
			  "consequently",
			  "consider",
			  "considering",
			  "contain",
			  "containing",
			  "contains",
			  "corresponding",
			  "could",
			  "couldnt",
			  "course",
			  "cs",
			  "currently",
			  "d",
			  "dare",
			  "darent",
			  "definitely",
			  "described",
			  "despite",
			  "did",
			  "didnt",
			  "different",
			  "directly",
			  "do",
			  "does",
			  "doesnt",
			  "doing",
			  "done",
			  "dont",
			  "down",
			  "downwards",
			  "during",
			  "e",
			  "each",
			  "edu",
			  "eg",
			  "eight",
			  "eighty",
			  "either",
			  "else",
			  "elsewhere",
			  "end",
			  "ending",
			  "enough",
			  "entirely",
			  "especially",
			  "et",
			  "etc",
			  "even",
			  "ever",
			  "evermore",
			  "every",
			  "everybody",
			  "everyone",
			  "everything",
			  "everywhere",
			  "ex",
			  "exactly",
			  "example",
			  "except",
			  "f",
			  "fairly",
			  "far",
			  "farther",
			  "few",
			  "fewer",
			  "fifth",
			  "first",
			  "five",
			  "followed",
			  "following",
			  "follows",
			  "for",
			  "forever",
			  "former",
			  "formerly",
			  "forth",
			  "forward",
			  "found",
			  "four",
			  "from",
			  "further",
			  "furthermore",
			  "g",
			  "get",
			  "gets",
			  "getting",
			  "given",
			  "gives",
			  "go",
			  "goes",
			  "going",
			  "gone",
			  "got",
			  "gotten",
			  "greetings",
			  "h",
			  "had",
			  "hadnt",
			  "half",
			  "happens",
			  "hardly",
			  "has",
			  "hasnt",
			  "have",
			  "havent",
			  "having",
			  "he",
			  "hed",
			  "hell",
			  "hello",
			  "help",
			  "hence",
			  "her",
			  "here",
			  "hereafter",
			  "hereby",
			  "herein",
			  "heres",
			  "hereupon",
			  "hers",
			  "herself",
			  "hes",
			  "hi",
			  "him",
			  "himself",
			  "his",
			  "hither",
			  "hopefully",
			  "how",
			  "howbeit",
			  "however",
			  "hundred",
			  "i",
			  "id",
			  "ie",
			  "if",
			  "ignored",
			  "ill",
			  "im",
			  "immediate",
			  "in",
			  "inasmuch",
			  "inc",
			  "inc.",
			  "indeed",
			  "indicate",
			  "indicated",
			  "indicates",
			  "inner",
			  "inside",
			  "insofar",
			  "instead",
			  "into",
			  "inward",
			  "is",
			  "isnt",
			  "it",
			  "itd",
			  "itll",
			  "its",
			  "its",
			  "itself",
			  "ive",
			  "j",
			  "just",
			  "k",
			  "keep",
			  "keeps",
			  "kept",
			  "know",
			  "known",
			  "knows",
			  "l",
			  "last",
			  "lately",
			  "later",
			  "latter",
			  "latterly",
			  "least",
			  "less",
			  "lest",
			  "let",
			  "lets",
			  "like",
			  "liked",
			  "likely",
			  "likewise",
			  "little",
			  "look",
			  "looking",
			  "looks",
			  "low",
			  "lower",
			  "ltd",
			  "m",
			  "made",
			  "mainly",
			  "make",
			  "makes",
			  "many",
			  "may",
			  "maybe",
			  "maynt",
			  "me",
			  "mean",
			  "meantime",
			  "meanwhile",
			  "merely",
			  "might",
			  "mightnt",
			  "mine",
			  "minus",
			  "miss",
			  "more",
			  "moreover",
			  "most",
			  "mostly",
			  "mr",
			  "mrs",
			  "much",
			  "must",
			  "mustnt",
			  "my",
			  "myself",
			  "n",
			  "name",
			  "namely",
			  "nd",
			  "near",
			  "nearly",
			  "necessary",
			  "need",
			  "neednt",
			  "needs",
			  "neither",
			  "never",
			  "neverf",
			  "neverless",
			  "nevertheless",
			  "new",
			  "next",
			  "nine",
			  "ninety",
			  "no",
			  "nobody",
			  "non",
			  "none",
			  "nonetheless",
			  "noone",
			  "no-one",
			  "nor",
			  "normally",
			  "not",
			  "nothing",
			  "notwithstanding",
			  "novel",
			  "now",
			  "nowhere",
			  "o",
			  "obviously",
			  "of",
			  "off",
			  "often",
			  "oh",
			  "ok",
			  "okay",
			  "old",
			  "on",
			  "once",
			  "one",
			  "ones",
			  "ones",
			  "only",
			  "onto",
			  "opposite",
			  "or",
			  "other",
			  "others",
			  "otherwise",
			  "ought",
			  "oughtnt",
			  "our",
			  "ours",
			  "ourselves",
			  "out",
			  "outside",
			  "over",
			  "overall",
			  "own",
			  "p",
			  "particular",
			  "particularly",
			  "past",
			  "per",
			  "perhaps",
			  "placed",
			  "please",
			  "plus",
			  "possible",
			  "presumably",
			  "probably",
			  "provided",
			  "provides",
			  "q",
			  "que",
			  "quite",
			  "qv",
			  "r",
			  "rather",
			  "rd",
			  "re",
			  "really",
			  "reasonably",
			  "recent",
			  "recently",
			  "regarding",
			  "regardless",
			  "regards",
			  "relatively",
			  "respectively",
			  "right",
			  "round",
			  "s",
			  "said",
			  "same",
			  "saw",
			  "say",
			  "saying",
			  "says",
			  "second",
			  "secondly",
			  "see",
			  "seeing",
			  "seem",
			  "seemed",
			  "seeming",
			  "seems",
			  "seen",
			  "self",
			  "selves",
			  "sensible",
			  "sent",
			  "serious",
			  "seriously",
			  "seven",
			  "several",
			  "shall",
			  "shant",
			  "she",
			  "shed",
			  "shell",
			  "shes",
			  "should",
			  "shouldnt",
			  "since",
			  "six",
			  "so",
			  "some",
			  "somebody",
			  "someday",
			  "somehow",
			  "someone",
			  "something",
			  "sometime",
			  "sometimes",
			  "somewhat",
			  "somewhere",
			  "soon",
			  "sorry",
			  "specified",
			  "specify",
			  "specifying",
			  "still",
			  "sub",
			  "such",
			  "sup",
			  "sure",
			  "t",
			  "take",
			  "taken",
			  "taking",
			  "tell",
			  "tends",
			  "th",
			  "than",
			  "thank",
			  "thanks",
			  "thanx",
			  "that",
			  "thatll",
			  "thats",
			  "thats",
			  "thatve",
			  "the",
			  "their",
			  "theirs",
			  "them",
			  "themselves",
			  "then",
			  "thence",
			  "there",
			  "thereafter",
			  "thereby",
			  "thered",
			  "therefore",
			  "therein",
			  "therell",
			  "therere",
			  "theres",
			  "theres",
			  "thereupon",
			  "thereve",
			  "these",
			  "they",
			  "theyd",
			  "theyll",
			  "theyre",
			  "theyve",
			  "thing",
			  "things",
			  "think",
			  "third",
			  "thirty",
			  "this",
			  "thorough",
			  "thoroughly",
			  "those",
			  "though",
			  "three",
			  "through",
			  "throughout",
			  "thru",
			  "thus",
			  "till",
			  "to",
			  "together",
			  "too",
			  "took",
			  "toward",
			  "towards",
			  "tried",
			  "tries",
			  "truly",
			  "try",
			  "trying",
			  "ts",
			  "twice",
			  "two",
			  "u",
			  "un",
			  "under",
			  "underneath",
			  "undoing",
			  "unfortunately",
			  "unless",
			  "unlike",
			  "unlikely",
			  "until",
			  "unto",
			  "up",
			  "upon",
			  "upwards",
			  "us",
			  "use",
			  "used",
			  "useful",
			  "uses",
			  "using",
			  "usually",
			  "v",
			  "value",
			  "various",
			  "versus",
			  "very",
			  "via",
			  "viz",
			  "vs",
			  "w",
			  "want",
			  "wants",
			  "was",
			  "wasnt",
			  "way",
			  "we",
			  "wed",
			  "welcome",
			  "well",
			  "well",
			  "went",
			  "were",
			  "were",
			  "werent",
			  "weve",
			  "what",
			  "whatever",
			  "whatll",
			  "whats",
			  "whatve",
			  "when",
			  "whence",
			  "whenever",
			  "where",
			  "whereafter",
			  "whereas",
			  "whereby",
			  "wherein",
			  "wheres",
			  "whereupon",
			  "wherever",
			  "whether",
			  "which",
			  "whichever",
			  "while",
			  "whilst",
			  "whither",
			  "who",
			  "whod",
			  "whoever",
			  "whole",
			  "wholl",
			  "whom",
			  "whomever",
			  "whos",
			  "whose",
			  "why",
			  "will",
			  "willing",
			  "wish",
			  "with",
			  "within",
			  "without",
			  "wonder",
			  "wont",
			  "would",
			  "wouldnt",
			  "x",
			  "y",
			  "yes",
			  "yet",
			  "you",
			  "youd",
			  "youll",
			  "your",
			  "youre",
			  "yours",
			  "yourself",
			  "yourselves",
			  "youve",
			  "z",
			  "zero"};
	String stFinal = "";
	String st= "I just want checking TeST PLAYED tried about";
	stFinal= parseString(st, stopWords);
	System.out.println(stFinal);
	
	}
}
