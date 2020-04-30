import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.sound.midi.*;

public class Constructor implements Music
{
    // Class for working with midi api
    private Sequencer sequencer;
    
    void stop()
    {
        if (sequencer != null) {
            sequencer.stop();
        }
    }

    void play (Sequence seq, int bpm)
    {
        try {   // To change BPM I have to update(create new one) sequencer. Perhaps there is a solution, but I think it works fine.
            System.out.println("Start play function");
            sequencer = MidiSystem.getSequencer();      // Here I create a new sequencer.
            sequencer.open();
            sequencer.setTempoInBPM(bpm);               // Set BPM wich came from GUI class as a func parameter.
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.setSequence(seq);
            System.out.println("BMP = " + bpm);
            sequencer.start();
        }
        catch (InvalidMidiDataException err)
        {
            System.out.println("Sorry, but here is InvalidMidiDataException");
        }
        catch (MidiUnavailableException err)
        { 
            System.out.println("Sorry, buut here is MidiUnavailableException");
        }
    }

    public Sequence buildMusic(boolean[][] results)
    {   // Creates and returns midi sequence, that can later will be passed to player.
        try {
            Sequence seq = new Sequence(Sequence.PPQ, 4);
            // Ability to leave boxes unslected 
            System.out.println("Building track");

            for (int i = 0; i < myInstruments.length; i++)
            {
                Track track = seq.createTrack();
                if (myInstruments[i].isDrum){
                    System.out.println("Got drum");
                    // If instruments are percussion, I don't have to set instrument.
                    track.add(makeEvent(176,9,127,0,checkBoxesInTheRow));   // ability to leave boxes unused.
                    System.out.println("Set instrument to " + 9 + " channel to " + myInstruments[i].code);
                } else {
                    System.out.println("Set instrument to " + (i+1) + " channel to " + myInstruments[i].code);
                    track.add(makeEvent(192, i+1, myInstruments[i].code, 0, 1));    // Setting instrument for not percurssion instruments.
                    track.add(makeEvent(176,i+1,127,0,checkBoxesInTheRow));
                }
                for (int j = 0; j < checkBoxesInTheRow; j++)
                {
                    if(results[i][j])
                    {
                        if (myInstruments[i].isDrum)
                        {
                            System.out.println("Adding event to " + (i+1) + " channel, at tick " + j);
                            // 144 - noteOn, 9 channel used for percussion instruments, instrument code, velocity, tick.
                            track.add(makeEvent(144, 9, myInstruments[i].code, 100, j+1));  // Example: j = 0 so, set noteOn on the first tick, not zero tick.
                            // 128 - noteOff.
                            track.add(makeEvent(128, 9, myInstruments[i].code, 100, j+2));  // Example: j = 0 so, set noteOff on the second tick.
                        }
                        else {
                            System.out.println("Adding event to " + (i+1) + " channel, at tick " + j);
                            track.add(makeEvent(144, i+1, myInstruments[i].note, 100, j+1));
                            track.add(makeEvent(128, i+1, myInstruments[i].note, 100, j+2));
                        }
                    }
                }
            }
            return seq;
        } catch (Exception err) {
            System.out.println("Everythings is broken. Unknown error");
            err.printStackTrace();
            return null;
        }
    }

    public MidiEvent makeEvent (int command, int channel, int note,
                                int velocity, int tick)
    {
        // I borrowed this function from https://www.geeksforgeeks.org/java-midi/
        // To be clear commands 144 = noteOn, 128 = noteOff, 192 = changeInstrument, 176 = Control/Mode change
        MidiEvent event = null;
        try {
            ShortMessage m = new ShortMessage();
            m.setMessage(command, channel, note, velocity);
            event = new MidiEvent(m, tick);
        } catch (Exception err) {
            System.out.println("Everything fell apart");
            err.printStackTrace();
        }
        return event;
    }

    void exportToFile(boolean[][] results, String filename)
    {
        try {
            Sequence seq = buildMusic(results);
            Track track = seq.getTracks()[0];       // Get first (it both last) track
            MetaMessage mt = new MetaMessage();
            MidiEvent m;
            byte[] arr = {};
            mt.setMessage(0x2F, arr, 0);
            m = new MidiEvent(mt, checkBoxesInTheRow);
            track.add(m);
    
            File yourFile = new File(filename);
            yourFile.createNewFile(); // if file already exists will do nothing 
            FileOutputStream f = new FileOutputStream(filename, false);            
            MidiSystem.write(seq, 1, f);
            System.out.println("Exported successfully");
        } catch (InvalidMidiDataException err) {
            System.out.println("Sorry, but here is InvalidMidiDataException");
        } catch (IOException err) {
            System.out.println("Sorry, but here is IOException");
        }
    }
    Sequence importFromFile(String filename)
    {
        try {
            File in = new File(filename);
            Sequence seq = MidiSystem.getSequence(in);
            return seq;
        } catch (InvalidMidiDataException err) {
            System.out.println("Sorry, but here is InvalidMidiDataException");
            return null;
        } catch (IOException err) {
            System.out.println("Sorry, but here is IOException");
            err.printStackTrace();
            return null;
        }
    }
}

