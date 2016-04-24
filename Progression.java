import java.util.List;

public class Progression {
    // Lowercase indicates minor progression.
    public enum ProgressionType { I_VI_II_V, II_V_I, II_V, ii_v_i, ii_v }
    public ProgressionType type;
    public Note root;
    private List<Chord> chords;

    public Progression(Note root, ProgressionType type, List<Chord> chords) {
	this.root = root;
	this.type = type;
	this.chords = chords;
    }

    // Returns the number of chords in the progression.
    public int length() {
	return chords.size();
    }
    
    // The following methods implement the rules necessary to classify chord progressions.
    public static boolean isOneSixTwoFive(List<Chord> chords) {
	Interval[] intervals = Interval.getChordIntervals(chords);
	return (chords.get(0).type == Chord.ChordType.MAJOR && intervals[0].degree == 6 && intervals[0].type == Interval.IntervalType.MAJOR)
	    && (intervals[1].degree == 4 && intervals[1].type == Interval.IntervalType.PERFECT) 
	    && (chords.get(2).type == Chord.ChordType.MINOR && intervals[2].degree == 4 && intervals[2].type == Interval.IntervalType.PERFECT) 
	    && (chords.get(3).type == Chord.ChordType.DOMINANT);
    }

    public static boolean isTwoFiveOne(List<Chord> chords) {
	Interval[] intervals = Interval.getChordIntervals(chords);
	return (chords.get(0).type == Chord.ChordType.MINOR && intervals[0].degree == 4 && intervals[0].type == Interval.IntervalType.PERFECT)
	    && (chords.get(1).type == Chord.ChordType.DOMINANT && intervals[1].degree == 4 && intervals[1].type == Interval.IntervalType.PERFECT) 
	    && (chords.get(2).type == Chord.ChordType.MAJOR);
    }

    public static boolean isTwoFive(List<Chord> chords) {
	Interval[] intervals = Interval.getChordIntervals(chords);
	return (chords.get(0).type == Chord.ChordType.MINOR && intervals[0].degree == 4 && intervals[0].type == Interval.IntervalType.PERFECT)
	    && (chords.get(1).type == Chord.ChordType.DOMINANT);
    }

    public static boolean isMinorTwoFiveOne(List<Chord> chords) {
	Interval[] intervals = Interval.getChordIntervals(chords);
	return (chords.get(0).type == Chord.ChordType.HALF_DIMINISHED
		&& intervals[0].degree == 4 && intervals[0].type == Interval.IntervalType.PERFECT)
	    && (chords.get(1).type == Chord.ChordType.DOMINANT && intervals[1].degree == 4 && intervals[1].type == Interval.IntervalType.PERFECT)
	    && (chords.get(2).type == Chord.ChordType.MINOR);
    }

    public static boolean isMinorTwoFive(List<Chord> chords) {
	Interval[] intervals = Interval.getChordIntervals(chords);
	return (chords.get(0).type == Chord.ChordType.HALF_DIMINISHED
		&& intervals[0].degree == 4 && intervals[0].type == Interval.IntervalType.PERFECT)
	    && (chords.get(1).type == Chord.ChordType.DOMINANT);
    }

}
