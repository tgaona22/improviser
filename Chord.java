public class Chord {
    // The int array associated with each chord type represents the intervals between chord tones.
    public enum ChordType { 
	MAJOR(new int[] {4, 3, 4}),
	MINOR(new int[] {3, 4, 3}),
	DOMINANT(new int[] {4, 3, 3}),
	DIMINISHED(new int[] {3, 3, 3}),
	HALF_DIMINISHED(new int[] {3, 3, 4});

	private final int[] pattern;
	private ChordType(int[] pattern) {
	    this.pattern = pattern;
	}
	public int[] pattern() { return pattern; }
    };
    
    public Note root;
    public ChordType type;
    public boolean altered;
    public int duration;
    
    public Note[] chordTones;

    public Chord(Note root, ChordType type, int duration) {
	this.root = root;
	this.type = type;
	this.duration = duration;
	this.altered = false;

	// Construct the chord tones based on the root note and the type of chord.
	chordTones = new Note[4];
	chordTones[0] = root;
	int[] intervalPattern = type.pattern();
	for (int i = 1; i < 4; i++) {
	    chordTones[i] = chordTones[i-1].plus(intervalPattern[i-1]);
	}
    }

    public Chord(String root, ChordType type, int duration) {
	this(new Note(root), type, duration);
    }

    public String toString() {
	String str = root.toString();
	if (type == ChordType.MAJOR) str += "maj7: ";
	if (type == ChordType.MINOR) str += "-7: ";
	if (type == ChordType.DOMINANT && !altered) str += "7: ";
	if (type == ChordType.DOMINANT && altered) str += "alt7: ";
	if (type == ChordType.DIMINISHED) str += "o7: ";
	if (type == ChordType.HALF_DIMINISHED) str += "-7b5: ";
	str += duration;
	return str;
    }
}
