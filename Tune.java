import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Tune {
    
    private ArrayList<Chord> changes;
    private ArrayList<Phrase> phrases;
    private HashMap<Integer, Progression> progressions;

    public Tune(String filename) {
	changes = new ArrayList<Chord>();
	phrases = new ArrayList<Phrase>();
	progressions = new HashMap<Integer, Progression>();
	readChangesFromFile(filename);
	recognizeProgressions();
	alterChords();
    }

    // Constructs a tune whose chord changes are specified in a file given by command line argument.
    // Outputs the improvised melody for the tune.
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.out.println("Please enter the name of the file containing the chord changes over which to improvise.");
	    return;
	}		
	Tune tune = new Tune(args[0]);
	System.out.println(tune);
	
	int chordIndex = 0;
	while (chordIndex < tune.changes.size()) {
	    Progression progression = tune.progressions.get(chordIndex);
	    if (progression != null) {
		tune.phrases.add(new Phrase(tune.changes.subList(chordIndex, chordIndex + progression.length())));
		chordIndex += progression.length();
	    }
	    else {
		tune.phrases.add(new Phrase(tune.changes.subList(chordIndex, chordIndex + 1)));
		chordIndex += 1;
	    }
	}
	
	System.out.println("Improv: ");
	for (Phrase phrase : tune.phrases) {
	    System.out.println(phrase);
	}
    }

    public String toString() {
	String str = "";
	for (Chord chord : changes) {
	    str += chord + " | ";
	}
	return str;
    }

    // Reharmonizes some chords in the chord changes. Must be called after the chords have been read in.
    // If one wants the rules for progressions to apply, this function should be called after recognizeProgressions().
    private void alterChords() {
	for (int i = 0; i < changes.size() - 1; i++) {
	    //First, check if this is the start of a progression.
	    Progression p = progressions.get(i);
	    if (p != null) {
		// Alter the VI and V chord (if possible) of a I-VI-II-V.
		if (p.type == Progression.ProgressionType.I_VI_II_V) {
		    changes.get(i+1).altered = true;
		    Interval interval = new Interval(changes.get(i+3).root, changes.get(i+4).root);
		    if (interval.degree == 5 && interval.type == Interval.IntervalType.PERFECT) {
			changes.get(i+3).altered = true;
		    }
		}
		// Alter the V chord in a minor ii-v progression.
		if (p.type == Progression.ProgressionType.ii_v) {
		    changes.get(i+1).altered = true;
		}
	    }

	    //Look at next chord. If the chord is dominant and the next chord is a perfect fifth below, the chord can be altered.
	    if (changes.get(i).type == Chord.ChordType.DOMINANT) {
		Interval interval = new Interval(changes.get(i+1).root, changes.get(i).root);
		if (interval.degree == 5 && interval.type == Interval.IntervalType.PERFECT) {
		    // With some probability we can alter the chord.
		    if (Math.random() > .3) {
			changes.get(i).altered = true;
		    }
		}
	    }
	}
    }

    // Recognizes and marks any chord progressions present in the chord changes.
    // The hashmap maps chord indices to progressions. For example, if a II-V-I occurs starting with the 4th chord in the progression,
    // an entry for the progression will be put in the hashmap with key 3.
    private void recognizeProgressions() {
	int i = 0;
	while (i < changes.size()) {
	    if (i + 4 <= changes.size() && Progression.isOneSixTwoFive(changes.subList(i, i + 4))) {
		Note root = changes.get(i).root;
		progressions.put(i, new Progression(root, Progression.ProgressionType.I_VI_II_V, changes.subList(i, i+4)));
		i = i + 4;
	    }
	    else if (i + 3 <= changes.size() && Progression.isTwoFiveOne(changes.subList(i, i + 3))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.II_V_I, changes.subList(i, i+3)));
		i = i + 3;
	    }
	    else if (i + 2 <= changes.size() && Progression.isTwoFive(changes.subList(i, i + 2))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.II_V, changes.subList(i, i+2)));
		i = i + 2;
	    }
	    else if (i + 3 <= changes.size() && Progression.isMinorTwoFiveOne(changes.subList(i, i + 3))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.ii_v_i, changes.subList(i, i+3)));
		i = i + 3;
	    }
	    else if (i + 2 <= changes.size() && Progression.isMinorTwoFive(changes.subList(i, i + 2))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.ii_v, changes.subList(i, i+2)));
		i = i + 2;
	    }
	    else {
		i = i + 1;
	    }
	}
    }

    // Reads a file of chord changes. Chords should be specified by giving the following three pieces of information:
    // Root ChordType Length of Chord (in beats)
    // Example -- Bb MAJOR 4
    private void readChangesFromFile(String filename) {
	BufferedReader in = null;
	String line;
	try { 
	    in = new BufferedReader(new FileReader(filename));
	    while ((line = in.readLine()) != null) {
		StringTokenizer tokens = new StringTokenizer(line);
		while (tokens.hasMoreTokens()) {
		    // First token should be the root of the chord.
		    String root = tokens.nextToken();
		    // Next token should by the type of the chord.
		    Chord.ChordType type = Chord.ChordType.valueOf(tokens.nextToken());
		    // Final token should be the length (in beats) of the chord.
		    int beats = Integer.parseInt(tokens.nextToken());
		    changes.add(new Chord(root, type, beats));
		}
	    }
	}
	catch (IOException e) {
	    System.out.println(e);
	}
	finally {
	    try {
		in.close();
	    }
	    catch (IOException e) {
		System.out.println(e);
	    }
	}
    }

}
		    
    
