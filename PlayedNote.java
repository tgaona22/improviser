public class PlayedNote extends Note implements Comparable<PlayedNote> {
    
    public int duration;
    public int location;
    // If silent is true, no note is actually played. 
    public boolean silent;

    public PlayedNote(String note, int duration, int location) {
	super(note);
	this.duration = duration;
	this.location = location;
	silent = false;
    }

    public PlayedNote(int note, Augmentation aug, int duration, int location) {
	super(note, aug);
	this.duration = duration;
	this.location = location;
	silent = false;
    }

    public PlayedNote(Note note, int duration, int location) {
	super(note.note, note.augmentation);
	this.duration = duration;
	this.location = location;
	silent = false;
    }

    // Constructor for a 'silent' note.
    public PlayedNote(int duration) {
	super(-1, null);
	this.duration = duration;
	this.location = 0;
	silent = true;
    }

    public String toString() {
	if (silent) {
	    return "*" + duration;
	}
	return super.toString() + location + "-" + duration;
    }

    @Override
    public int compareTo(PlayedNote other) {
	return this.location - other.location;
    }

    @Override
    public boolean equals(Object other) {
	if (other == this) return true;
	if (!(other instanceof PlayedNote)) return false;
	if (!super.equals(other)) return false;
	return this.location == ((PlayedNote)other).location;
    }

    @Override
    public int hashCode() {
	int hash = super.hashCode();
	return hash + 37 * location;
    }
    
}
