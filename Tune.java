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
    }

    public String toString() {
	String str = "";
	for (Chord chord : changes) {
	    str += chord + " | ";
	}
	return str;
    }

    public static void main(String[] args) {
	Tune test = new Tune("test.txt");
	System.out.println(test);
	for (HashMap.Entry<Integer, Progression> prog : test.progressions.entrySet()) {
	    System.out.println(prog.getKey() + ": " + prog.getValue().root + " " + prog.getValue().type);
	}
	Phrase phrase = new Phrase(test.changes);
	/*for (PlayedNote note : phrase.notes) {
	    System.out.print(note + " ");
	    }*/
	
    }

    private void recognizeProgressions() {
	int i = 0;
	while (i < changes.size()) {
	    if (i + 4 <= changes.size() && Progression.isOneSixTwoFive(changes.subList(i, i + 4))) {
		Note root = changes.get(i).root;
		progressions.put(i, new Progression(root, Progression.ProgressionType.I_VI_II_V));
		i = i + 4;
	    }
	    else if (i + 3 <= changes.size() && Progression.isTwoFiveOne(changes.subList(i, i + 3))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.II_V_I));
		i = i + 3;
	    }
	    else if (i + 2 <= changes.size() && Progression.isTwoFive(changes.subList(i, i + 2))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.II_V));
		i = i + 2;
	    }
	    else if (i + 3 <= changes.size() && Progression.isMinorTwoFiveOne(changes.subList(i, i + 3))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.ii_v_i));
		i = i + 3;
	    }
	    else if (i + 2 <= changes.size() && Progression.isMinorTwoFive(changes.subList(i, i + 2))) {
		Note root = changes.get(i).root.plus(-2);
		progressions.put(i, new Progression(root, Progression.ProgressionType.ii_v));
		i = i + 2;
	    }
	    else {
		i = i + 1;
	    }
	}
    }

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
		    
    
