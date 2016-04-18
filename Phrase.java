import java.util.ArrayList;
import java.util.Random;

public class Phrase {
    public static Random rn = null;
    public final int sixteenth = 1;
    public final int eighth = 2;
    public final int quarter = 4;
    public final int half = 8;
    
    public ArrayList<PlayedNote> notes;
    public ArrayList<Chord> chords;
    public int duration;

    public Phrase previous;

    public Phrase(ArrayList<Chord> chords) {
	if (rn == null) {
	    rn = new Random();
	}
   
	this.chords = chords;
	duration = 0;
	for (Chord c : chords) {
	    duration += c.duration;
	}

	notes = new ArrayList<PlayedNote>();

	generatePhrase();
    }

    /*private static double evaluate(Phrase phrase) {
	// How to evaluate a phrase????
	// A good phrase has a nice shape. Dissonances should be resolved.
	// Rhythmic variety...
	}*/	

    private void generatePhrase() {
	double silenceFrequency = .2;
	boolean startOfPhrase = true;

	int[] noteDurations = chooseNoteDurations();
	int durationIndex = 0;
	
	// For each chord in the phrase.
	for (Chord chord : chords) {
	    System.out.println("Chord: " + chord);
	    int currentChordDuration = chord.duration * 4;
	    // Choose a scale to play.
	    Scale scale = Scale.getScale(chord);
	    System.out.println("Scale: " + scale);
	    PlayedNote note;
	    // While there is still time left on the chord...
	    while (currentChordDuration > 0) {
		// Choose a starting note or continue the phrase.
		// The next note could possibly be a silence.
		double probability = Math.random();
		if (startOfPhrase) {
		    note = getStartingNote(scale, noteDurations[durationIndex++]);
		    startOfPhrase = false;
		}
		else if (!notes.get(notes.size() - 1).silent && probability < silenceFrequency) {
		    note = new PlayedNote(noteDurations[durationIndex++]);
		}
		else {
		    note = nextNote(scale, noteDurations[durationIndex++]);
		}
		System.out.print(note + " ");
		notes.add(note);
		currentChordDuration -= note.duration;
	    }
	    System.out.println();
	}
    }

    //private void generatePattern

    private PlayedNote nextNote(Scale scale, int duration) {
	// Determine what direction in which to go.
	int phraseDirection = direction();
	double probability = Math.random();
	int noteDirection = (probability < .15 * Math.abs(phraseDirection)) ? -1 : 1;
	return scale.getNoteCloseTo(lastNote(), noteDirection, duration);
    }

    private int[] chooseNoteDurations() {
	// The phrase duration is measured in beats.
	// We restrict to at most 4 (16th) notes played in a beat.
	int[] durations = new int[this.duration * 4];
	int index = 0;
	for (int i = 0; i < this.duration; i++) {
	    // Split up the beat.
	    double probability = Math.random();
	    if (probability >= .75) {
		durations[index++] = quarter;
	    }
	    else if (probability >= .5) {
		for (int j = 0; j < 4; j++) {
		    durations[index++] = sixteenth;
		}
	    }
	    else {
		for (int j = 0; j < 2; j++) {
		    durations[index++] = eighth;
		}
	    }
	}
	return durations;
    }	

    // Get the last (non-silent) note in the phrase.
    private PlayedNote lastNote() {
	for (int i = notes.size() - 1; i >= 0; i--) {
	    if (!notes.get(i).silent) {
		return notes.get(i);
	    }
	}
	return null;
    }

    private int direction() {
	int direction = 0;
	for (int i = 1; i < notes.size(); i++) {
	    if (!notes.get(i-1).silent && notes.get(i).compareTo(notes.get(i-1)) > 0) {
		direction += 1;
	    }
	    else if (!notes.get(i-1).silent && notes.get(i).compareTo(notes.get(i-1)) < 0) {
		direction -= 1;
	    }
	}
	return direction;
    }

    private int averageStepSize() {
	int steps = 0;
	for (int i = 1; i < notes.size(); i++) {
	    steps += Math.abs(notes.get(i).compareTo(notes.get(i-1)));
	}
	return (int)(Math.round(steps/((double)notes.size())));
    }	

    private PlayedNote getStartingNote(Scale scale, int duration) {
	// Position will be relative to a (reasonable) C
	double probability = Math.random();
	int position;
	if (probability >= .85) {
	    if (Math.random() > .4)
		position = 64;
	    else
		position = 28;
	}
	else {
	    if (Math.random() > .25)
		position = 52;
	    else 
		position = 40;
	}
	// Pick a note in the scale.
	int scaleIndex = rn.nextInt(scale.length());
	Note start = scale.get(scaleIndex);
	// Determine its position.
	int notePosition = position + start.toneDifference(new Note("C"), false);
	return new PlayedNote(start, duration, notePosition);
    }

	
}
