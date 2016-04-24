import java.util.Random;
import java.util.ArrayList;

public final class Pattern {
    private static Random rn = null;
    // The 'database' of patterns.
    private static int[][] patterns = {
	{ 1, 1 }, // 2 steps
	{ 1, 1, 1 }, // 3 steps
	{ 5 }, // up a 6th
	{ 3 }, // up a 4th
	{ -2, 1 }, // down a 3rd then up a 2nd
	{ -5, 3 }, // down a 6th then up a 3rd
	{ -2, -2 }, // down 2 3rds
	{ -3, -3 }, // down 2 4ths
    };

    // Get any pattern.
    public static int[] getRandomPattern() {
	return getRandomPattern(patterns);
    }

    // Get any pattern that satisfies pattern.length + 1 <= remainingNotes.
    public static int[] getRandomPattern(int remainingNotes) {
	ArrayList<int[]> eligiblePatterns = new ArrayList<int[]>();
	int size = 0;
	for (int i = 0; i < patterns.length; i++) {
	    if (patterns[i].length + 1 <= remainingNotes) {
		eligiblePatterns.add(patterns[i]);
		size += 1;
	    }
	}
	return (size == 0) ? null : getRandomPattern(eligiblePatterns.toArray(new int[size][]));
    }	       

    private static int[] getRandomPattern(int[][] eligiblePatterns) {
	if (rn == null) {
	    rn = new Random();
	}
	double inversionProbability = Math.random();
	int index = rn.nextInt(eligiblePatterns.length);
	return (inversionProbability > .5) ? invert(eligiblePatterns[index]) : eligiblePatterns[index];
    }
    
    // Inverts the steps in the given pattern.
    private static int[] invert(int[] pattern) {
	int[] inversion = new int[pattern.length];
	for (int i = 0; i < pattern.length; i++) {
	    inversion[i] = pattern[i] * -1;
	}
	return inversion;
    }

    // Returns an array of PlayedNotes representing the pattern starting at base with notes taken from the scale.
    // The caller of this function needs to set the durations of the returned notes.
    public static PlayedNote[] instantiate(int[] pattern, PlayedNote base, Scale scale) {
	PlayedNote[] notes = new PlayedNote[pattern.length + 1];
	notes[0] = base;
	for (int i = 0; i < pattern.length; i++) {
	    // Index of next note equals index of prev note + pattern[i] 
	    int index = (scale.indexOfNote(notes[i]) + pattern[i]) % scale.length();
	    if (index < 0) {
		index += scale.length();
	    }
	    Note note = scale.get(index);
	    // If the int at pattern[i] is positive, we are going up. Otherwise, we are going down.
	    // Position of next note is position of previous note +- number of half steps taken.
	    int position;
	    if (pattern[i] > 0) {
		position = notes[i].location + notes[i].toneDifference(note, true);
	    }
	    else {
		position = notes[i].location - notes[i].toneDifference(note, false);
	    }
	    notes[i+1] = new PlayedNote(note, 0, position);
	}
	return notes;
    }	
}
