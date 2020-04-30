
public interface Music {
    int checkBoxesInTheRow = 16*2;
    Constructor musicalDevil = new Constructor();
    MyInstrument[] myInstruments = {new MyInstrument(1, 50, "Grand Piano"), new MyInstrument(57, 70, "Trumpet"),
                                    new MyInstrument(38, 40, "Synth Bass"), new MyInstrument(26, 100, "Steel Ding"),
                                    new MyInstrument(9, 60, "Celesta"), new MyInstrument(15, 67, "Tubular Bells"),
                                    new MyInstrument(19, 50, "Error(Height)"), new MyInstrument(19, 30, "Error(Low)"),
                                    new MyInstrument(127, 80, "Cheap Gunshot"), new MyInstrument(42, true, "Closed Hi-Hat"),
                                    new MyInstrument(113, 100, "Agogo")};
}

class MyInstrument {

    // My preset for the instruments

    public int code;
    public int note;
    public String name;
    public boolean isDrum;

    public MyInstrument(int code, int note, String name)
    {
        if(!(note <= 0 || note > 127) && !(code <= 0 || code > 127))
        {
            this.note = note;
            this.code = code;
            this.name = name;
        } else {System.out.println("You can't do that!");}
    }
    public MyInstrument(int code, boolean isDrum, String name)
    {
        if(!(code <= 0 || code > 127))
        {
            this.isDrum = isDrum;
            this.code = code;
            this.name = name;
        } else {System.out.println("You can't do that!");}
    }
}