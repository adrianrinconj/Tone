
/**
 * Member in the Bell Choir, which is responsible for playing a specific musical note.
 * Each member has a name, a note to play, and a duration for how long they hold the note.
 * The class implements {@link Runnable} to allow the member's note to be played on a separate thread.
 */

// I used this as reference: https://www.youtube.com/watch?v=taI7G6U29L8

public class Members implements Runnable {
    //musical note
    private final Note note;

    //member name
    private final String name;

    //length of time note is held
    private volatile NoteLength assignedNote = null;


    /**
     * Constructor for Member with defaults
     *
     * @param name         the name of the member.
     * @param note         the musical note that the member plays.
     * @param assignedNote the duration the note is held (cannot be null).
     */
    public Members(String name, Note note, NoteLength assignedNote) {
        this.name = name;
        this.note = note;
        this.assignedNote = assignedNote;
    }


    /**
     * Executes the member's responsibility of holding the assigned note for its duration.
     * The thread sleeps for the duration of the note length.
     */

    @Override
    public void run() {
        if (assignedNote == null) {
            System.err.println(name + " has no note length set!");
            return;
        }
        try {
            Thread.sleep(assignedNote.timeMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Returns the musical note that this member is responsible for playing.
     *
     * @return the note assigned to this member.
     */

    public Note getNote() {
        return note;
    }


    /**
     * Returns the name of this member.
     *
     * @return the name of the member.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the length of time this member holds the note (the note's duration).
     *
     * @return the note length assigned to this member.
     */
    public NoteLength getLength() {
        return assignedNote;
    }


    /**
     * Sets a new note length for this member, indicating how long they will hold their note.
     *
     * @param length the new duration for which the note is held.
     */
    public void setLength(NoteLength length) {
        this.assignedNote = length;
    }


}


