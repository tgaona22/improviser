public class Scale {
    public final static int[] Major = { 2, 2, 1, 2, 2, 2 };
    public final static int[] JazzMelodic = { 2, 1, 2, 2, 2, 2 };
    public final static int[] HalfDiminished = { 2, 1, 2, 1, 2, 2 };
    public final static int[] Dorian = { 2, 1, 2, 2, 2, 1 };
    public final static int[] Mixolydian = { 2, 2, 1, 2, 2, 1 };
    public final static int[] Diminished1 = { 2, 1, 2, 1, 2, 1 };
    public final static int[] Diminished2 = { 1, 2, 1, 2, 1, 2 };
    public final static int[] AlteredDominant = { 1, 2, 1, 2, 2, 2 };

    private Note[] notes;
    private int[] pattern;
    
    public Scale(Note start, int[] stepPattern) {
	notes = new Note[stepPattern.length + 1];
	notes[0] = start;
	for (int i = 1; i < notes.length; i++) {
	    notes[i] = notes[i-1].plus(stepPattern[i-1]);
	}
	pattern = stepPattern;
    }

    public Scale(String start, int[] stepPattern) {
	this(new Note(start), stepPattern);
    }
    
    // Use this constructor to coerce the notes in the scale to be printed either sharp or flat.
    public Scale(Note start, int[] stepPattern, Note.Augmentation aug) {
	this(new Note(start.note, aug), stepPattern);
    }

    // Returns a scale that can be associated with the given chord.
    public static Scale getScale(Chord chord) {
	if (chord.type == Chord.ChordType.MAJOR) {
	    return new Scale(chord.root, Scale.Major);
	}
	if (chord.type == Chord.ChordType.MINOR) {
	    double probability = Math.random();
	    if (probability >= .7) {
		return new Scale(chord.root, Scale.JazzMelodic);
	    }
	    else {
		return new Scale(chord.root, Scale.Dorian);
	    }
	}
	if (chord.type == Chord.ChordType.DOMINANT) {
	    if (chord.altered) {
		return new Scale(chord.root, Scale.AlteredDominant);
	    }
	    double probability = Math.random();
	    if (probability >= .85) {
		return new Scale(chord.root, Scale.Diminished2);
	    }
	    else {
		return new Scale(chord.root, Scale.Mixolydian);
	    }
	}
	if (chord.type == Chord.ChordType.DIMINISHED) {
	    return new Scale(chord.root, Scale.Diminished1);
	}
	if (chord.type == Chord.ChordType.HALF_DIMINISHED) {
	    return new Scale(chord.root, Scale.HalfDiminished);
	}
	throw new RuntimeException("Attempt to determine scale for unknown chord type " + chord.type);
    }

    // Returns the note in the scale at the given index, or null if the index is out of range.
    public Note get(int index) {
	return (index >= 0 && index < notes.length) ? notes[index] : null;
    }	

    // Returns the number of notes in the scale.
    public int length() {
	return notes.length;
    }

    // Returns true iff the given note is considered a dissonant tone of the scale.
    public boolean isDissonant(PlayedNote note) {
	if (this.pattern == Scale.Major || this.pattern == Scale.Mixolydian) {
	    return indexOfNote(note) == 3;
	}
	return false;
    }

    // Returns the position of the note in the scale, or -1 if the note is not in the scale.
    public int indexOfNote(Note note) {
	for (int i = 0; i < notes.length; i++) {
	    if (notes[i].equals(note)) {
		return i;
	    }
	}
	return -1;
    }    
    
    public String toString() {
	String str = "";
	for (int i = 0; i < notes.length; i++) {
	    str += notes[i] + " ";
	}
	return str;
    }
}
