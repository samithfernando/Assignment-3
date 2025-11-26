package org.example;



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
        while (pos.get() < currentSong.length) {
            Main.playNote(player, notes, indices, pos, currentSong);
        }
    }
}


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
        while (pos.get() < currentSong.length) {
            Main.playNote(player, notes, indices, pos, currentSong);
        }
    }
}


class SongPosition {
    private int index = 0;
    public int get() {return index;}
    public void next() {index++;}
}


public class Main {
    private static final int[][] SONGS = {{0,1,2,3,4,5,6,7}, {0,0,4,4,5,5,4,3,3,2,2,1,1,0,4,4,3,3,2,2,1,4,4,3,3,2,2,1,0,0,4,4,5,5,4,3,3,2,2,1,1,0}};

    public static synchronized void playNote(FilePlayer player, String[] notes, int[] indices, SongPosition pos, int[] song) {
        int expected = song[pos.get()];
        if (pos.get() >= song.length) {
            return;
        }

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] == expected) {
                player.play(notes[i]);
                //System.out.println("Playing note: " + notes[i]);
                pos.next();
                Main.class.notifyAll();

                try {
                    Thread.sleep(500);
                } catch (Exception ignored) {}
                return;
            }
        }

        try {
            Main.class.wait();
        } catch (InterruptedException ignored) {}
    }


    public static void main(String[] args) {
        FilePlayer player = new FilePlayer();

        for (int s = 0; s < SONGS.length; s++) {
            SongPosition pos = new SongPosition();
            int[] currentSong = SONGS[s];
            System.out.println("Playing Task: #" + (s+1));
            Thread1 t1 = new Thread1(player, pos, currentSong);
            Thread2 t2 = new Thread2(player, pos, currentSong);

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException ignored) {}
        }
    }
}
