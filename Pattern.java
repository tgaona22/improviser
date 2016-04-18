public final class Pattern {
    private static Random rn = null;
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

    public static int[] getRandomPattern() {
	if (rn == null) {
	    rn = new Random();
	}
	double inversionProbability = Math.random();
	int index = rn.nextInt(patterns.length);
	return (inversionProbability > .5) ? invert(patterns[index]) : patterns[index];
    }

    private static int[] invert(int[] pattern) {
	int[] inversion = new int[pattern.length];
	for (int i = 0; i < pattern.length; i++) {
	    inversion[i] = pattern[i] * -1;
	}
	return inversion;
    }

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
	    boolean direction = pattern[i] > 0;
	    // Position of next note is position of previous note +- number of half steps taken.
	    int position = notes[i].location + notes[i].toneDifference(note, direction);
	    // Duration still needs to be set by the calling code.
	    notes[i+1] = new PlayedNote(note, 0, position);
	}
	return notes;
    }
	
}
