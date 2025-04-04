# Tone

## Overview

The Tone Program simulates a bell choir where each member plays a musical note in sequence.
Each member is represented as a thread that plays a specific note for a defined duration. 
The program includes classes for managing the notes, length of notes, members, and conductor, 
which ensures the song is played in the correct order and timing.

This project uses Java multithreading to synchronize the members of the choir, ensuring each note plays sequentially and that rests are handled appropriately.

## Features

- **Multithreading**: Each member plays their note in a separate thread to simulate real-time performance.
- **Custom Notes and Durations**: The program supports a wide range of musical notes and durations.
- **Error Handling**: The program handles potential errors, such as missing members for notes or interruptions in the threads.

## Classes

### `BellNote`

Represents a musical note and its duration. It holds a reference to a `Note` and a `NoteLength`.

- **Attributes**:
    - `note`: The musical note (`Note` enum).
    - `length`: The length of the note (`NoteLength` enum).

- **Constructor**: Initializes a `BellNote` with a note and its assigned length.

### `NoteLength`

Represents different note lengths (e.g., whole, half, quarter notes) and the time duration in milliseconds for each note.

- **Enums**:
    - `WHOLE`, `HALF`, `QUARTER`, etc. represent the duration of the note as a fraction of a whole note.

- **Methods**:
    - `timeMs()`: Returns the time in milliseconds for the given note length.

### `Note`

Represents a musical note (e.g., A4, C4) and generates the corresponding waveform. The note is associated with a sine wave sample, which is played for the appropriate duration.

- **Attributes**:
    - `SAMPLE_RATE`: The sample rate for sound generation.
    - `MEASURE_LENGTH_SEC`: The duration of one measure in seconds.

- **Methods**:
    - `sample()`: Returns a byte array representing the waveform of the note.

### `Members`

Represents a member of the bell choir. Each member is responsible for playing a specific note for a defined duration.

- **Attributes**:
    - `note`: The musical note assigned to the member.
    - `name`: The member's name.
    - `assignedNote`: The duration the member holds the note (`NoteLength`).

- **Methods**:
    - `run()`: Implements the `Runnable` interface, making each member play their note on a separate thread.
    - `getNote()`, `getName()`, `getLength()`: Getters for the member's note, name, and note length.
    - `setLength()`: Allows changing the note length.
    - `toString()`: Provides a string representation of the member and their assigned note.

### `Conductor`

Represents the conductor of the bell choir and controls the timing of the song. It synchronizes the playing of each note and manages the resting periods between notes.

- **Attributes**:
    - `song`: A list of `BellNote` objects representing the song.
    - `membersList`: A list of `Members` objects that will play the notes.

- **Methods**:
    - `run()`: Controls the timing of the song, ensuring each note is played sequentially. It manages rests by sleeping the thread for the duration of the rest period and starts a thread for each member to play their note.

## Usage

1. **Setup**:
    - Ensure that the `BellNote`, `Members`, and `Conductor` classes are properly implemented and that each class is correctly integrated.

2. **Create the Song**:
    - Define a `List<BellNote>` containing the song. Each `BellNote` should specify the note to be played and its duration.

3. **Create the Choir Members**:
    - Create a list of `Members` representing the choir. Each `Member` should be assigned to play a specific note.

4. **Start the Performance**:
    - Create an instance of `Conductor` with the song and list of members.
    - Start the conductor in a separate thread to begin the performance.
