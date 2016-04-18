public class Scale {
    public final static int[] Major = { 2, 2, 1, 2, 2, 2 };
    public final static int[] JazzMelodic = { 2, 1, 2, 2, 2, 2 };
    public final static int[] HalfDiminished = { 2, 1, 2, 1, 2, 2 };
    public final static int[] Dorian = { 2, 1, 2, 2, 2, 1 };
    public final static int[] Mixolydian = { 2, 2, 1, 2, 2, 1 };
    public final static int[] Diminished1 = { 2, 1, 2, 1, 2, 1 };
    public final static int[] Diminished2 = { 1, 2, 1, 2, 1, 2 };

    public Note[] notes;
    public int[] pattern;
    
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

    public Note get(int index) {
	return (index >= 0 && index < notes.length) ? notes[index] : null;
    }	

    public int length() {
	return notes.length;
    }

    public boolean isDissonant(PlayedNote note) {
	if (this.pattern == Scale.Major || this.pattern == Scale.Mixolydian) {
	    return indexOfNote(note) == 3;
	}
	return false;
    }

    public PlayedNote getNoteCloseTo(PlayedNote base, int direction, int duration) {
	// Find location of note in the scale.
	// POSSIBLE BUG: This won't work if base isn't in the scale.
	int index = indexOfNote(base);
	// Go up or down according to the step pattern.
	double probability = Math.random();
	int steps;
	// If base is a dissonant tone of the scale (4th on a major or mixolydian scale),
	// then we need to resolve up or down by a single step.
	if (isDissonant(base)) {
	    steps = 1;
	}
	else if (probability > .9) {
	    steps = 4;
	}
	else if (probability > .7) {
	    steps = 3;
	}
	else if (probability > .55) {
	    steps = 2;
	}
	else if (probability > .05) {
	    steps = 1;
	}
	else {
	    steps = 0;
	}

	int noteIndex = (index + (direction * steps)) % notes.length;
	if (noteIndex < 0) {
	    noteIndex += notes.length;
	}
	// Determine the position of the new note.
	int position;
	if (direction == 1) {
	    position = notes[noteIndex].toneDifference(base, false) + base.location;
	}
	else {
	    position = base.location - notes[noteIndex].toneDifference(base, true);
	}
	return new PlayedNote(notes[noteIndex], duration, position);
    }	    

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

    public static void main(String[] args) {
	Scale DMajor2 = new Scale(new Note("D"), Scale.Major, Note.Augmentation.SHARP);
	System.out.println(DMajor2);

	Scale CMinor = new Scale("C", Scale.JazzMelodic);
	System.out.println(CMinor);

	Scale DHalfDim = new Scale("D", Scale.HalfDiminished);
	System.out.println(DHalfDim);
    }
}
