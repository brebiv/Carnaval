/* This file contains everything that is needed for GUI */
/*              Both frontend and backend               */


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.File;


class Utils implements Music
{
    static boolean[][] generateResultsFromCheckBoxes(JCheckBox[][] checkBoxList)
    {
        boolean results[][] = new boolean[myInstruments.length][checkBoxesInTheRow];
        for (int i = 0; i < myInstruments.length; i++)
        {
            for (int j = 0; j < checkBoxesInTheRow; j++)
            {
                if(checkBoxList[i][j].isSelected())
                {
                    results[i][j] = true;
                    System.out.print("X ");
                }
                else {
                    results[i][j] = false;
                    System.out.print("o ");
                }
            }
            System.out.println("-------" + myInstruments[i].name + "-------");
        }
        System.out.println("-------------------------------");
        return results;
    }

    static boolean containsTrue(boolean[][] array)
    {
        try {
            for (int i = 0; i < array.length; i++)
            {
                for (int j = 0; j < array[i].length; j++)
                {
                    if(array[i][j]) return true;
                }
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException err)
        {
            return false;
        }
    }
}

public class MainFrame extends JFrame implements Music {
    //Frame that contains only main panel

    private MainPanel mainPanel = new MainPanel();
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("File");
    private JMenuItem importItem = new JMenuItem("Import");
    private JMenuItem exportAs = new JMenuItem("Export as");

    private JFrame parentFrame = new JFrame();
    private JFileChooser fileChooser = new JFileChooser();

    private void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);
        setResizable(false);
    }

    public MainFrame()
    {
        super("Music");
        init();
        exportAs.addActionListener(new ExportAsListener());
        importItem.addActionListener(new ImportItemListener());
        menu.add(importItem);
        menu.add(exportAs);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        add(mainPanel);

        File cwd = new File(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Midi Files", "mid"));
        fileChooser.setCurrentDirectory(cwd);

        pack();
    }

    class ExportAsListener implements ActionListener{
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("Export as clicked");
            boolean[][] results = Utils.generateResultsFromCheckBoxes(mainPanel.musicPanel.checkBoxList);
            System.out.println(Utils.containsTrue(results));
            fileChooser.setDialogTitle("Select file to export");
            int userSelection = fileChooser.showSaveDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION)
            {
                File outFile = fileChooser.getSelectedFile();
                musicalDevil.exportToFile(results, outFile.getAbsolutePath());
            }
        }
    }
    class ImportItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            fileChooser.setDialogTitle("Select file to import");
            int userSelection = fileChooser.showSaveDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION)
            {
                File inFile = fileChooser.getSelectedFile();
                musicalDevil.play(musicalDevil.importFromFile(inFile.getAbsolutePath()), mainPanel.toolBar.toolBarLeftPanel.bpm);
            }
        }
    }
}

class MainPanel extends JPanel {
    // Main panel contains all gui elements

    public MusicPanel musicPanel = new MusicPanel();    // Public because there is checkboxes.
    public ToolBar toolBar = new ToolBar(musicPanel.checkBoxList);  // Public because BPM. Something wrong, but let's continue.

    private InstrumentsPanel instrumentsPanel = new InstrumentsPanel();

    private void init()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0,5,10,5));
    }

    public MainPanel()
    {
        init();
        setBackground(Color.LIGHT_GRAY);
        add(instrumentsPanel, BorderLayout.WEST);
        add(toolBar, BorderLayout.NORTH);
        add(musicPanel, BorderLayout.CENTER);
    }

}

class InstrumentsPanel extends JPanel implements Music {
    // Panel on the left that contains labels with instruments names

    private void init()
    {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0,5,0,10));
    }

    public InstrumentsPanel()
    {
        init();
        for (int i = 0; i < myInstruments.length; i++)
        {
            JLabel label = new JLabel(myInstruments[i].name);
            label.setFont(new Font("Calibri", Font.PLAIN, 24));
            label.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            add(label);
        }
    }
}

class MusicPanel extends JPanel implements Music {
    // Big panel with checkboxes
    // Public checkboxes to access them from any part of GUI.
    public JCheckBox[][] checkBoxList = new JCheckBox[myInstruments.length][checkBoxesInTheRow];

    private void init()
    {
        GridLayout gridLayout = new GridLayout(myInstruments.length, 16, 1, 1);
        setLayout(gridLayout);
        setBackground(Color.GRAY);
    }

    public MusicPanel()
    {
        init();
        for (int i = 0; i < myInstruments.length; i++)
        {
            for (int j = 0; j < checkBoxesInTheRow; j++)
            {
                JCheckBox box = new JCheckBox();
                box.setName(myInstruments[i].name + Integer.toString(i));
                box.setSelected(false);
                box.setBackground(Color.LIGHT_GRAY);
                checkBoxList[i][j] = box;
                add(box);
            }
        }

    }
}

class ToolBar extends JPanel implements Music {
    // Panel on the top with buttons, contains two panels left and right.

    public ToolBarLeftPanel toolBarLeftPanel;   // To get access from main panel.

    private void init()
    {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);
    }

    public ToolBar(JCheckBox[][] checkBoxList)
    {
        init();
        toolBarLeftPanel = new ToolBarLeftPanel(checkBoxList);
        add(toolBarLeftPanel, BorderLayout.WEST);
    }
}

class ToolBarLeftPanel extends JPanel implements Music {

    // Some terrible code for scaling icons
    private ImageIcon originalIconDown = new ImageIcon(getClass().getResource("src/icons/down-arrow.png"));
    private Image rawIconDown = originalIconDown.getImage();
    private Image scaledIconDown = rawIconDown.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
    private ImageIcon tempIconDown = new ImageIcon(scaledIconDown);
    // Same code for the second icon
    private ImageIcon originalIconUp = new ImageIcon(getClass().getResource("src/icons/up-arrow.png"));
    private Image rawIconUp = originalIconUp.getImage();
    private Image scaledIconUp = rawIconUp.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
    private ImageIcon tempIconUp = new ImageIcon(scaledIconUp);

    // Buttons
    private JButton startBtn = new JButton("Start");
    private JButton stopBtn = new JButton("Stop");
    private JButton tempDownBtn = new JButton("", tempIconDown);
    private JButton tempUpBtn = new JButton("", tempIconUp);

    private JFrame popUpFrame = new JFrame();
    private JLabel bpmSign = new JLabel("BPM:");
    private JTextField bpmField = new JTextField();

    // For buttons backend
    int bpm = 120;          // Bits per minute
    JCheckBox[][] checkBoxList;
    boolean[][] results;  // = new boolean[myInstruments.length][checkBoxesInTheRow];

    void init()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
    }

    public ToolBarLeftPanel(JCheckBox[][] checkBoxList)
    {
        init();
        this.checkBoxList = checkBoxList;
        startBtn.addActionListener(new StartListener());
        stopBtn.addActionListener(new StopListener());
        tempDownBtn.addActionListener(new tempoDownListener());
        tempUpBtn.addActionListener(new tempoUpListener());
        bpmField.setText(Integer.toString(bpm));
        bpmSign.setFont(new Font("Calibri", Font.PLAIN, 20));

        add(startBtn); add(stopBtn);
        add(bpmSign);add(tempDownBtn); add(bpmField); add(tempUpBtn);
    }

    void showLowBpmPopUp()
    {
        JOptionPane.showMessageDialog(popUpFrame, "BPM can't be lower than zero", "Low BPM", JOptionPane.OK_OPTION);
    }
    void showIncorrectBpmPopUp()
    {
        JOptionPane.showMessageDialog(popUpFrame, "1. BPM can't be lower than zero.\n2. BPM must be integer.", "Incorrect BPM", JOptionPane.OK_OPTION);
    }

    class StartListener implements ActionListener, Music {
        public void actionPerformed(ActionEvent e)
        {
            results = Utils.generateResultsFromCheckBoxes(checkBoxList);
            try {
                bpm = Integer.parseInt(bpmField.getText());
            } catch (NumberFormatException err) {
                showIncorrectBpmPopUp();
            }
            if(bpm<1) showLowBpmPopUp();
            else {
                musicalDevil.stop();
                musicalDevil.play(musicalDevil.buildMusic(results), bpm);
            }
        }
    }

    class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            musicalDevil.stop();
        }
    }
    class tempoDownListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            if(bpm<1) showLowBpmPopUp();
            else {
                bpm -= 10;
                bpmField.setText(Integer.toString(bpm));
            }
        }
    }

    class tempoUpListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            bpm += 10;
            bpmField.setText(Integer.toString(bpm));
        }
    }
}
