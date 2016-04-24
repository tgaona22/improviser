public class Note {
    public enum Augmentation { SHARP, FLAT, NATURAL }
    // Start at C and go up by half steps.
    // C=0, D=2, E=4, F=5, G=7, A=9, B=11
    private final static int[] notes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
    private final static String[] letters = { "C", "D", "E", "F", "G", "A", "B" };
    
    public int note;
    public Augmentation augmentation;

    // Constructs a note from its string representation. For example, 'C#' will construct the note C sharp.
    public Note(String note) {
	this.note = noteToInt(note);
	if (note.length() == 2) {
	    if (note.charAt(1) == '#') {
		augmentation = Augmentation.SHARP;
	    }
	    if (note.charAt(1) == 'b') {
		augmentation = Augmentation.FLAT;
	    }
	    else {
		augmentation = Augmentation.NATURAL;
	    }
	}
    }

    public Note(int note, Augmentation aug) {
	this.note = note;
	augmentation = aug;
    }

    // Returns the note obtained by adding 'step' half-steps to the given note.
    public Note plus(int step) {
	if (note + step < 0) {
	    return new Note(note + step + 12, augmentation);
	}
	return new Note((note + step) % 12, augmentation);
    }
    
    // Returns the difference in the tones between the two given notes.
    // 'up' indicates we want the interval starting at the first note, going up to the second note.
    // If 'up' is true, we take the second note's tone minus the first note's tone.
    // Otherwise, we take the first note's tone minus the second note's tone.
    public int toneDifference(Note other, boolean up) {
	int diff;
	if (up) {
	    diff = other.note - this.note;
	}
	else {
	    diff = this.note - other.note;
	}
	return (diff < 0) ? diff + 12 : diff;
    }

    // Similar to toneDifference(), but returns an Interval object instead of an int.
    public Interval getInterval(Note other) {
	return new Interval(this, other);
    }

    // Converts a Note to a PlayedNote by setting the given position and duration.
    public PlayedNote toPlayedNote(int position, int duration) {
	return new PlayedNote(this, duration, position);
    }

    public String toString() {
	return intToLetter(note, augmentation);
    }

    @Override
    public boolean equals(Object other) {
	if (other == this) return true;
       	if (!(other instanceof Note)) return false;
	return this.note == ((Note)other).note;
    }

    @Override
    public int hashCode() {
	return 73 * note;
    }	    

    // Converts a note from the internal representation to a string representation.
    private String intToLetter(int note, Augmentation aug) {
	// Name of black keys is determined by augmentation
	if (note < 5) {
	    if (note % 2 == 0) {
		return letters[note/2];
	    }
	    else {
		if (aug == Augmentation.SHARP) {
		    return letters[(note-1)/2] + "#";
		}
		else {
		    return letters[(note+1)/2] + "b";
		}
	    }
	}
	else {
	    if (note % 2 == 1) {
		return letters[(note + 1)/2];
	    }
	    else {
		if (aug == Augmentation.SHARP) {
		    return letters[note/2] + "#";
		}
		else {
		    return letters[note/2 + 1] + "b";
		}
	    }
	}
    }
    
    // Converts the string representation of a note to its internal representation.
    // Observe that enharmonic notes get mapped to the same int.
    private int noteToInt(String note) {
	switch (note) {
	case "B#":
	case "C": 
	    return 0;
	case "C#":
	case "Db": 
	    return 1;
	case "D": 
	    return 2;
	case "D#": 
	case "Eb":
	    return 3;
	case "E":
	case "Fb":
	    return 4;
	case "E#":
	case "F":
	    return 5;
	case "F#":
	case "Gb":
	    return 6;
	case "G":
	    return 7;
	case "G#":
	case "Ab":
	    return 8;
	case "A":
	    return 9;
	case "A#":
	case "Bb":
	    return 10;
	case "B":
	case "Cb":
	    return 11;
	default:
	    return -1;
	}
    }
}

    

