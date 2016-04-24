import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Random;

public class Phrase {
    private static Random rn = null;
    // Constants related to note durations.
    private final int sixteenth = 1;
    private final int eighth = 2;
    private final int quarter = 4;
    private final int half = 8;
    
    private ArrayList<PlayedNote> notes;
    private ArrayList<Chord> chords;
    public int duration;

    public Phrase(List<Chord> chords) {
	if (rn == null) {
	    rn = new Random();
	}
   
	this.chords = new ArrayList<Chord>(chords);
	duration = 0;
	for (Chord c : chords) {
	    duration += c.duration;
	}

	notes = new ArrayList<PlayedNote>();
	generatePhrase();
    }
    
    // This is the evaluation function for a phrase.
    public static double evaluate(Phrase phrase) {
	double score = 0.0;
	int maxNoteRepetitions = 4;
	int medianRange = 20;

	HashMap<PlayedNote, Integer> noteFrequencies = getNoteFrequencies(phrase);

	// Find the highest and lowest frequency notes. There shouldn't be a huge discrepancy in the frequencies.
	// The number of repetitions should not be very high.
	int highestFreq = Integer.MIN_VALUE, lowestFreq = Integer.MAX_VALUE;
	for (HashMap.Entry<PlayedNote, Integer> entry : noteFrequencies.entrySet()) {
	    int freq = entry.getValue();
	    highestFreq = (freq > highestFreq) ? freq : highestFreq;
	    lowestFreq = (freq < lowestFreq) ? freq : lowestFreq;
	}
	if (highestFreq <= maxNoteRepetitions) {
	    int freqDist = highestFreq - lowestFreq;
	    score += (freqDist == 0) ? 10 : (10 / freqDist);
	}

	// Define range to be the position of the highest note - position of the lowest note.
	// Determine range of the phrase. Should be neither too high or too low.	    
	int phraseRange = range(phrase);
	int phraseDist = Math.abs(phraseRange - medianRange);
	score += (phraseDist == 0) ? 15 : (15 / phraseDist);
	
	// In an attempt to measure the "shape" of a phrase, define an arc to be a sequence of notes in the same direction.
	// A "well-shaped" phrase should have a good number of arcs, none of which should have a very high (> 20) length.
	int[] arcs = getPhraseArcs(phrase);
	double sum = 0.0;
	for (int i = 0; i < arcs.length; i++) {
	    sum += Math.abs(arcs[i]);
	}
	double avgArcLength = sum / arcs.length;
	if (avgArcLength < 5 || avgArcLength > 11) {
	    score -= 10;
	}
	int upArcs = 0, downArcs = 0;
	// Number of upward and downward arcs should be roughly equal.
	for (int i = 0; i < arcs.length; i++) {
	    upArcs += (arcs[i] > 0) ? 1 : 0;
	    downArcs += (arcs[i] < 0) ? 1 : 0;
	}
	int arcDist = Math.abs(upArcs - downArcs);
	score += (arcDist == 0) ? 10 : (10 / arcDist);

	return score;		
    }	

    // Returns an array of ints containing the lengths of each arc.
    private static int[] getPhraseArcs(Phrase phrase) {
	ArrayList<PlayedNote> nonSilentNotes = new ArrayList<PlayedNote>(phrase.notes);
	nonSilentNotes.removeIf(n -> n.silent);

	// Count the # of arcs.
	int arcCount = 0;
	int direction = nonSilentNotes.get(1).location - nonSilentNotes.get(0).location;
	int prevDirection = direction;
	for (int i = 1; i < nonSilentNotes.size() - 1; i++) {
	    // direction > 0 --> going up;  direction < 0 --> going down.
	    direction = nonSilentNotes.get(i+1).location - nonSilentNotes.get(i).location;
	    // If direction changes we are going to increase the number of arcs.
	    if (direction * prevDirection < 0) {
		arcCount++;
	    }
	    prevDirection = direction;
	}
	
	// Create an array that contains the length of each arc.
	int[] arcs = new int[arcCount + 1];
	direction = nonSilentNotes.get(1).location - nonSilentNotes.get(0).location;
	prevDirection = direction;
	int arcLength = direction;
	int arcIndex = 0;
	for (int i = 1; i < nonSilentNotes.size() - 1; i++) {
	    direction = nonSilentNotes.get(i+1).location - nonSilentNotes.get(i).location;
	    // If direction changes we save the length of the arc and start over.
	    if (direction * prevDirection < 0) {
		arcs[arcIndex++] = arcLength;
		arcLength = 0;
	    }
	    arcLength += direction;
	    prevDirection = direction;
	}
	// Have to set the last arc at the end of the loop since no change will be recognized.
	arcs[arcIndex++] = arcLength;
	return arcs;
    }
	    
    // Returns a hashmap that maps a note to the number of times it occurs in the phrase.
    private static HashMap<PlayedNote, Integer> getNoteFrequencies(Phrase phrase) {
	HashMap<PlayedNote, Integer> freqs = new HashMap<PlayedNote, Integer>();
	for (PlayedNote note : phrase.notes) {
	    if (freqs.containsKey(note)) {
		freqs.replace(note, freqs.get(note) + 1);
	    }
	    else {
		freqs.put(note, 1);
	    }
	}
	return freqs;
    }

    // Returns the position of the highest note in the phrase minus the position of the lowest note in the phrase.
    private static int range(Phrase phrase) {
	int highestPosition = Integer.MIN_VALUE, lowestPosition = Integer.MAX_VALUE;
	for (PlayedNote note : phrase.notes) {
	    if (!note.silent) {
		highestPosition = (note.location > highestPosition) ? note.location : highestPosition;
		lowestPosition = (note.location < lowestPosition) ? note.location : lowestPosition;
	    }
	}
	return highestPosition - lowestPosition;
    }

    // Populates the list of notes.
    private void generatePhrase() {
	double silenceFrequency = .4;
	double patternFrequency = .4;
	boolean startOfPhrase = true;

	int[] noteDurations = chooseNoteDurations();
	int durationIndex = 0;
	
	for (Chord chord : chords) {
	    int currentChordDuration = chord.duration * 4;
	    // Choose a scale to play.
	    Scale scale = Scale.getScale(chord);
	    
	    // Figure out how many notes we can play on this chord.
	    int notesOnChord = 0;
	    int sum = 0;
	    for (int k = durationIndex; k < noteDurations.length && sum < currentChordDuration; k++) {
		sum += noteDurations[k];
		notesOnChord++;
	    }
	  
	    // While there is still time left on the chord...
	    while (notesOnChord > 0) {
		// With some probability we may generate a pattern based phrase.
		double patternProbability = Math.random();		
		if (canGeneratePattern(notesOnChord) && patternProbability < patternFrequency) {
		    ArrayList<PlayedNote> pattern = generatePattern(startOfPhrase, notesOnChord, scale, durationIndex, noteDurations);

		    startOfPhrase = false;
		    notesOnChord -= pattern.size();
		    durationIndex += pattern.size();

		    notes.addAll(pattern);
		}
		else {		    		
		    // Choose a starting note or continue the phrase.
		    // The next note could possibly be a silence.
		    PlayedNote note;
		    double silenceProbability = Math.random();
		    if (startOfPhrase) {
			note = getStartingNote(scale, noteDurations[durationIndex++]);
			startOfPhrase = false;
		    }
		    else if (!notes.get(notes.size() - 1).silent && silenceProbability < silenceFrequency) {
			note = new PlayedNote(noteDurations[durationIndex++]);
		    }
		    else {
			note = getNoteCloseTo(lastNote(), scale, noteDurations[durationIndex++]);
		    }
		    notes.add(note);
		    notesOnChord -= 1;
		}
	    }
	}
    }

    // Returns true iff a pattern exists that fits the remaining notesOnChord.
    private boolean canGeneratePattern(int notesOnChord) {
	return Pattern.getRandomPattern(notesOnChord) != null;
    }

    // Returns a list of played notes representing an instantiated pattern.
    private ArrayList<PlayedNote> generatePattern(boolean startOfPhrase, int notesOnChord, Scale scale, int durationIndex, int[] noteDurations) {
	int maxRepeats = 2;
	
	PlayedNote base = null;
	boolean repeat = true;
	boolean repeatDirection = false;
	int numberOfRepeats = 0;

	int[] pattern = Pattern.getRandomPattern(notesOnChord);
	// If there is no pattern to fit the number of notes left, indicate this by returning null.
	if (pattern == null) {
	    return null;
	}

	ArrayList<PlayedNote> patternNotes = new ArrayList<PlayedNote>();
	
	while (notesOnChord >= pattern.length + 1 && repeat) {
	    repeat = false;
	    
	    // Choose a base note for the phrase.
	    if (startOfPhrase) {
		base = getStartingNote(scale, noteDurations[durationIndex++]);
		startOfPhrase = false;
	    }
	    // If we are repeating, choose a note close to the previous base.
	    else if (numberOfRepeats > 0) {
		base = getNoteCloseTo(base, scale, noteDurations[durationIndex++], repeatDirection);
	    }
	    // Otherwise, choose a note close to the previous note.
	    else {
		base = getNoteCloseTo(lastNote(), scale, noteDurations[durationIndex++]);
	    }

	    // Instantiate the pattern from the base note.
	    PlayedNote[] notePattern = Pattern.instantiate(pattern, base, scale);
	    // Set the durations of the notes in the pattern. (Base duration is already set.)
	    for (int k = 1; k < notePattern.length; k++) {
		notePattern[k].duration = noteDurations[durationIndex++];
	    }
	    // Add the notes from the pattern to the notes of the phrase.
	    patternNotes.addAll(Arrays.asList(notePattern));
	    // Decrement the number of notes left on the chord.
	    notesOnChord -= notePattern.length;

	    // Determine if we will repeat the current pattern.
	    // As the number of repeats increases this should become less likely.
	    double repeatProbability = (Math.random() * .5) - (numberOfRepeats / maxRepeats);
	    if (repeatProbability > 0) {
		repeat = true;
		numberOfRepeats += 1;
		// Choose the direction of the phrase
		if (numberOfRepeats == 1) {
		    repeatDirection = Math.random() > .5;
		}
	    }
	}
	return patternNotes;
    }	    

    // Returns a note within a step of the given note on the given scale.
    private PlayedNote getNoteCloseTo(PlayedNote note, Scale scale, int duration) {
	boolean up = Math.random() > .5;
	return getNoteCloseTo(note, scale, duration, up);
    }
    
    // Returns a note within a step of the given note on the given scale in the direction specified by 'up'.
    private PlayedNote getNoteCloseTo(PlayedNote note, Scale scale, int duration, boolean up) {
	int index = scale.indexOfNote(note);
	// If note not in the scale, find the note in the scale closest to it.
	if (index == -1) {
	    int minDist = Integer.MAX_VALUE;
	    for (int i = 0; i < scale.length(); i++) {
		int dist = note.toneDifference(scale.get(i), up);
		if (minDist > dist) {
		    minDist = dist;
		    index = i;
		}
	    }
	}
	// If note is in the scale, go up or down by one step in the scale.
	else {
	    index = up ? (index + 1) % scale.length() : (index - 1) % scale.length();
	    if (index < 0) {
		index += scale.length();
	    }
	}
	Note nextNote = scale.get(index);
	// Position of the next note is position of current note +- steps between them.
	int position;
	if (up) {
	    position = note.location + note.toneDifference(nextNote, true);
	}
	else {
	    position = note.location - note.toneDifference(nextNote, false);
	}
	return new PlayedNote(nextNote, duration, position);
    }

    // Returns an array of ints subdividing the phrase into durations.
    private int[] chooseNoteDurations() {
	// The phrase duration is measured in beats.
	// For simplicity, we restrict to at most 4 (16th) notes played in a beat.
	ArrayList<Integer> durations = new ArrayList<Integer>();
	for (int i = 0; i < this.duration; i++) {
	    // Split up the beat.
	    double probability = Math.random();
	    if (probability > .9) {
		durations.add(quarter);
	    }
	    else if (probability >= .5) {
		for (int j = 0; j < 4; j++) {
		    durations.add(sixteenth);
		}
	    }
	    else {
		for (int j = 0; j < 2; j++) {
		    durations.add(eighth);
		}
	    }
	}
	// Convert to an array of ints.
	int[] ret = new int[durations.size()];
	for (int i = 0; i < durations.size(); i++) {
	    ret[i] = durations.get(i).intValue();
	}
	return ret;
    }	

    // Returns the last (non-silent) note in the phrase.
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

    // Returns a random note from the scale.
    private PlayedNote getStartingNote(Scale scale, int duration) {
	// Position will be relative to a (reasonable) C
	double probability = Math.random();
	int position;
	if (probability > .9) {
	    position = 40;
	}
	else if (probability > .2) {
	    position = 52;
	}
	else {
	    position = 64;
	}

	// Pick a note in the scale.
	int scaleIndex = rn.nextInt(scale.length());
	Note start = scale.get(scaleIndex);
	// Determine its position.
	int notePosition = position + start.toneDifference(new Note("C"), false);
	return new PlayedNote(start, duration, notePosition);
    }

    public String toString() {
	String str = "";
	int noteIndex = 0;
	for (Chord chord : chords) {
	    str += chord + " ";
	    int chordDuration = chord.duration * 4;
	    int sum = 0;
	    while (sum < chordDuration) {
		str += notes.get(noteIndex) + " ";
		sum += notes.get(noteIndex).duration;
		noteIndex++;
	    }
	    str += "\n";
	}
	return str;
    }	
}
