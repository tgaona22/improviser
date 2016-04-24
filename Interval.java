import java.util.List;

public class Interval { 
    public enum IntervalType { MAJOR, MINOR, PERFECT }
    public IntervalType type;
    public int degree;

    public Interval(int degree, IntervalType type) {
	this.degree = degree;
	this.type = type;
    }

    // Returns an array of intervals between the given chords.
    public static Interval[] getChordIntervals(List<Chord> chords) {
	Interval[] intervals = new Interval[chords.size() - 1];
	for (int i = 0; i < intervals.length; i++) {
	    intervals[i] = new Interval(chords.get(i).root, chords.get(i+1).root);
	}
	return intervals;
    }

    public String toString() {
	return type + " " + degree;
    }

    // Constructs the interval starting at one and going up to two.
    public Interval(Note one, Note two) {
	int diff = one.toneDifference(two, true);

	switch (diff) {
	case 0:
	    degree = 0; type = null;
	    break;
	case 1:
	    degree = 2; type = IntervalType.MINOR;
	    break;
	case 2:
	    degree = 2; type = IntervalType.MAJOR;
	    break;
	case 3:
	    degree = 3; type = IntervalType.MINOR;
	    break;
	case 4:
	    degree = 3; type = IntervalType.MAJOR;
	    break;
	case 5:
	    degree = 4; type = IntervalType.PERFECT;
	    break;
	case 6:
	    degree = 5; type = IntervalType.MINOR;
	    break;
	case 7:
	    degree = 5; type = IntervalType.PERFECT;
	    break;
	case 8:
	    degree = 6; type = IntervalType.MINOR;
	    break;
	case 9:
	    degree = 6; type = IntervalType.MAJOR;
	    break;
	case 10:
	    degree = 7; type = IntervalType.MINOR;
	    break;
	case 11:
	    degree = 7; type = IntervalType.MAJOR;
	    break;
	default:
	    throw new RuntimeException("Attempt to construct interval with tone difference " + diff);
	}
    }
}
