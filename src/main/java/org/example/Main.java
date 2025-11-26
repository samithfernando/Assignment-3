package org.example;




/* ---------------------- */
/* Thread 1               */
/* ---------------------- */
class Thread1 extends Thread {
    private final FilePlayer player;
    private final SongPosition pos;

    private final String[] notes = {"src/main/resources/sounds/do.wav", "src/main/resources/sounds/mi.wav", "src/main/resources/sounds/sol.wav", "src/main/resources/sounds/si.wav", "src/main/resources/sounds/do-octave.wav"};

    private final int[] indices = {0, 2, 4, 6, 7};

    private final int[] currentSong;

    public Thread1(FilePlayer player, SongPosition pos, int[] currentSong) {
        this.player = player;
        this.pos = pos;
        this.currentSong = currentSong;
    }



    @Override
    public void run() {
        for (int i = 0; i < notes.length; i++) {
            Main.playNote(player, notes[i], indices[i], pos, currentSong);
        }
    }
}

/* ---------------------- */
/* Thread 2               */
/* ---------------------- */
class Thread2 extends Thread {
    private final FilePlayer player;
    private final SongPosition pos;

    private final String[] notes = {"src/main/resources/sounds/re.wav", "src/main/resources/sounds/fa.wav", "src/main/resources/sounds/la.wav", "src/main/resources/sounds/do-octave.wav"};

    private final int[] indices = {1, 3, 5, 7};

    private final int[] currentSong;

    public Thread2(FilePlayer player, SongPosition pos, int[] currentSong) {
        this.player = player;
        this.pos = pos;
        this.currentSong = currentSong;
    }

    @Override
    public void run() {
        for (int i = 0; i < notes.length; i++) {
            Main.playNote(player, notes[i], indices[i], pos, currentSong);
        }
    }
}

class SongPosition {
    private int index = 0;

    public int get() { return index; }
    public void next() { index++; }
    public void reset() { index = 0; }
}


public class Main {

    private static final int[][] SONGS = {{0,1,2,3,4,5,6,7}, {0,0,4,4,5,5,4,3,3,2,2,1,1,0,4,4,3,3,2,2,1,4,4,3,3,2,2,1,0,0,4,4,5,5,4,3,3,2,2,1,1,0}};


    public static synchronized void playNote(FilePlayer player, String note, int noteIndex, SongPosition pos, int[] song) {
        while (pos.get() < song.length && song[pos.get()] != noteIndex) {
            try {
                Main.class.wait();
            } catch (InterruptedException ignored) {}
        }

        if (pos.get() >= song.length) {
            return;
        }

        player.play(note);
        pos.next();
        Main.class.notifyAll();

        try { Thread.sleep(500); }
        catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {

        FilePlayer player = new FilePlayer();
        SongPosition pos = new SongPosition();

        for (int s = 0; s < SONGS.length; s++) {
            int[] currentSong = SONGS[s];

            System.out.println("playing sont:"+s);
            Thread1 t1 = new Thread1(player, pos, currentSong);
            Thread2 t2 = new Thread2(player, pos, currentSong);

            pos.reset();

            t1.start();
            t2.start();


        }
    }
}
