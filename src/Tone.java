import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

/**
 * Main class that reads a song from a file and plays it using generated audio tones.
 */

public class Tone {

    // got the idea of using a list from John Botonakis
    private static List<Members> membersList = new ArrayList();


    /**
     * Main method that reads a song from a file and plays it.
     *
     * @param args Command-line arguments. The first argument should be the filename containing the song to play.
     * @throws Exception if there is an error reading the song file or playing the notes.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java Tone <filename>");
            return;
        }

        List<BellNote> song = readSongFromFile(args[0]);

        final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Tone t = new Tone(af);
        t.playSong(song);


    }

    // Mapping of note length strings to corresponding NoteLength enum values
    // I added some more so that December 1963 would sound better
    private static final Map<String, NoteLength> LENGTH_MAP = Map.of(
            "1", NoteLength.WHOLE,
            "2", NoteLength.HALF,
            "4", NoteLength.QUARTER,
            "8", NoteLength.EIGTH,
            "3", NoteLength.THIRD,
            "16", NoteLength.SIXTEENTH,
            "68", NoteLength.SIXEIGTH,
            "316", NoteLength.THREESIXTEENTHS,
            "34", NoteLength.THREEQUARTERS,
            "38", NoteLength.THREEEIGTHS
    );


    /**
     * Assigns {@link Members} to notes in the song if they haven't been assigned yet.
     *
     * @param song List of {@link BellNote} objects representing the song.
     * @return the updated list of members assigned to notes.
     */
    private static List<Members> assignNotes(List<BellNote> song) {
        for (int i = 0; i < song.size(); i++) {
            Note note = song.get(i).note;
            boolean noteAlreadyAssigned = false;

            // Check if a member is already assigned to this note
            for (int j = 0; j < membersList.size(); j++) {
                Members m = membersList.get(j);
                if (m.getNote().equals(note)) {
                    noteAlreadyAssigned = true;
                    break;
                }
            }

            // if not then create a new member
            if (!noteAlreadyAssigned) {
                Members newMember = new Members("Member " + i, note, song.get(i).length);
                membersList.add(newMember);
                System.out.println("Assigned " + newMember.getName() + " to note " + note + " with length " + song.get(i).length);
            }
        }

        return membersList;
    }

    /**
     * Reads a song from a file where each line contains a note and its corresponding length.
     *
     * @param filename the name of the file to read the song from.
     * @return a list of {@link BellNote} objects representing the song.
     * @throws IOException if there is an error reading the file.
     */

    public static List<BellNote> readSongFromFile(String filename) throws IOException {
        List<BellNote> song = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    try {
                        Note note = Note.valueOf(parts[0]);
                        NoteLength length = LENGTH_MAP.get(parts[1]);
                        if (length == null) throw new IllegalArgumentException();
                        song.add(new BellNote(note, length));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Skipping invalid line: " + line);
                    }

                }
            }
            assignNotes(song);

        }
        return song;
    }


    private final AudioFormat af;


    /**
     * Constructs a Tone object with the specified audio format.
     *
     * @param af the audio format used to generate tones.
     */

    Tone(AudioFormat af) {
        this.af = af;
    }


    //got this from here: https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
    void playSong(List<BellNote> song) throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();
            for (BellNote bn : song) {
                playNote(line, bn);
            }
            line.drain();
        }
    }


    /**
     * Plays an individual note by writing its audio sample to the {@link SourceDataLine}.
     *
     * @param line the {@link SourceDataLine} used to output the audio.
     * @param bn   the {@link BellNote} representing the note to play.
     */
    private void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }
}


/**
 * The actual note that is being played
 */
class BellNote {
    final Note note;
    final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}

/**
 * Enum representing the length of musical notes, such as whole, half, quarter, etc.
 * Each note length has a corresponding time in milliseconds for playback.
 */

//I added a bunch more to get the rhythm as close to right for december1963
enum NoteLength {
    WHOLE(1.0f), HALF(0.5f), QUARTER(0.25f), EIGTH(0.125f), THIRD(0.33333f), SIXTEENTH(0.0625f),
    SIXEIGTH(0.09375f), THREESIXTEENTHS(0.1875f), THREEQUARTERS(0.75f), THREEEIGTHS(0.375f),
    THIRTYSECOND(0.03125f);

    //shows the time in ms
    private final int timeMs;

    /**
     * Constructs a NoteLength with the corresponding time duration based on a float value.
     *
     * @param length the duration of the note as a fraction of a whole note.
     */
    private NoteLength(float length) {
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    public int timeMs() {
        return timeMs;
    }
}

/**
 * Enum representing musical notes, each corresponding to a frequency and waveform for sound generation.
 * Includes both the standard notes and their sharps.
 */
// I added the next octave up
enum Note {
    REST, A4, A4S, B4, C4, C4S, D4, D4S, E4, F4, F4S, G4, G4S,
    A5, A5S, B5, C5, C5S, D5, D5S, E5, F5, F5S, G5, G5S;

    public static final int SAMPLE_RATE = 48 * 1024;
    public static final int MEASURE_LENGTH_SEC = 1;

    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;
    private final double FREQUENCY_A_HZ = 440.0d;
    private final double MAX_VOLUME = 127.0d;
    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];


    /**
     * Constructs a Note and generates the waveform for the note's frequency.
     * If the note is not a REST, a sine wave sample is created corresponding to the note's pitch.
     */

    private Note() {
        int n = this.ordinal();
        if (n > 0) {
            final double halfStepUpFromA = n - 1;
            final double exp = halfStepUpFromA / 12.0d;
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

            final double sinStep = freq * step_alpha;
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    public byte[] sample() {
        return sinSample;
    }
}

