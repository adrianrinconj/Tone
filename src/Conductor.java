import java.util.List;

/**
 * The {@code Conductor} class dictates the tempo of the program.
 * The conductor is implemented as a {@link Runnable} to allow for multithreading, with each member playing their note in
 * sequence.
 *
 * <p>The conductor controls the timing for the bell choir, including rests and notes. It synchronizes the playing of each
 * note using {@code Thread}s, ensuring that each note is played sequentially. If a note is a rest, the conductor will
 * pause the thread for the duration of the rest period before continuing to the next note in the song.</p>
 */

public class Conductor implements Runnable {

    // Got the List ideas from John Botonakis
    private final List<BellNote> song;
    private final List<Members> membersList;

    public Conductor(List<BellNote> song, List<Members> membersList) {
        this.song = song;
        this.membersList = membersList;
    }


    /**
     * Executes the conductor's responsibility of controlling the timing of the song.
     * This method iterates through the song, playing each note in sequence. It handles rests by pausing the thread
     * and plays each note in order by assigning a {@link Thread} to each member responsible for that note.
     */
    @Override
    public void run() {
        for (BellNote bellN : song) {
            if (bellN.note.name().equals("REST")) {
                System.out.println("Rest for " + bellN.length.timeMs() + "ms");
                try {
                    Thread.sleep(bellN.length.timeMs());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            // Find the member that corresponds to the note that is being played
            Members member = memberToNote(bellN.note);
            if (member != null) {
                // Set the note length for the member and start the thread to play the note
                member.setLength(bellN.length);
                Thread t = new Thread(member);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.err.println("No member found for note: " + bellN.note);
            }
        }
    }

    /**
     * Finds the member in the {@code membersList} who is assigned to play the specified note.
     *
     * @param note the note to find a member for.
     * @return the member responsible for playing the note, or {@code null} if no member is assigned to the note.
     */

    private Members memberToNote(Note note) {
        for (Members m : membersList) {
            if (m.getNote().equals(note)) {
                return m;
            }
        }
        return null;
    }
}
