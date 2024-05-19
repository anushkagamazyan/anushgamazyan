import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

    private JPanel screen = new JPanel(), tools = new JPanel();
    private JButton tool[];
    private JTextField field[];
    private CourseArray courses;
    private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};
    private LogFile logFile;
    private Autoassociator autoassociator;

    public TimeTable() {
        super("Dynamic Time Table");
        setSize(500, 800);
        setLayout(new FlowLayout());

        screen.setPreferredSize(new Dimension(400, 800));
        add(screen);

        setTools();
        add(tools);
        try {
            String clashFileName = field[2].getText();
            logFile = new LogFile("/Users/anushgamazyan/Downloads/testttt/timetable_log.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setVisible(true);
    }

    public void setTools() {
        String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
        field = new JTextField[capField.length];

        String capButton[] = {"Load", "Start", "Step", "Print", "Exit", "Continue"};
        tool = new JButton[capButton.length];

        tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));

        for (int i = 0; i < field.length; i++) {
            tools.add(new JLabel(capField[i]));
            field[i] = new JTextField(5);
            tools.add(field[i]);
        }

        for (int i = 0; i < tool.length; i++) {
            tool[i] = new JButton(capButton[i]);
            tool[i].addActionListener(this);
            tools.add(tool[i]);
        }

        field[0].setText("10");
        field[1].setText("381");
        field[2].setText("uta-s-93");
        field[3].setText("1");
    }

    public void draw() {
        Graphics g = screen.getGraphics();
        int width = Integer.parseInt(field[0].getText()) * 10;
        for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
            g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
            g.drawLine(0, courseIndex, width, courseIndex);
            g.setColor(CRScolor[CRScolor.length - 1]);
            g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
        }
    }

    private int getButtonIndex(JButton source) {
        int result = 0;
        while (source != tool[result]) result++;
        return result;
    }

    public void actionPerformed(ActionEvent click) {
        int min = 0, step = 0, clashes = 0;

        switch (getButtonIndex((JButton) click.getSource())) {
            case 0:
                int slots = Integer.parseInt(field[0].getText());
                courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
                courses.readClashes(field[2].getText());
                draw();
                break;
            case 1:
                min = Integer.MAX_VALUE;
                step = 0;
                for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);
                for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
                    courses.iterate(Integer.parseInt(field[4].getText()));
                    draw();
                    clashes = courses.clashesLeft();
                    if (clashes < min) {
                        min = clashes;
                        step = iteration;
                    }
                }
                System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                setVisible(true);
                logFile.write("Iterations = " + field[3].getText() + "\tShift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                break;
            case 2:
                courses.iterate(Integer.parseInt(field[4].getText()));
                draw();
                break;
            case 3:
                System.out.println("Exam\tSlot\tClashes");
                for (int i = 1; i < courses.length(); i++)
                    System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
                break;
            case 4:
                System.exit(0);
            case 5:
                min = Integer.MAX_VALUE;
                step = 0;
                if (courses.clashesLeft() > 0) {
                    for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
                        courses.iterate(Integer.parseInt(field[4].getText()));
                        draw();
                        clashes = courses.clashesLeft();
                        if (clashes < min) {
                            min = clashes;
                            step = iteration;
                        }
                    }
                    System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                    logFile.write("Iterations = " + field[3].getText() + "\tShift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                    setVisible(true);
                } else {
                    System.out.println("The number of clashes is 0. The algorithm is complete.");
                }
        }
    }

    public void trainAutoassociatorWithClashFreeSlots(int[] clashFreeSlots) {
        autoassociator = new Autoassociator(courses);
        autoassociator.training(clashFreeSlots);
    }

    public boolean isClashFree(int slotIndex) {
        for (int i = 1; i < courses.length(); i++) {
            if (courses.slot(i) == slotIndex && courses.status(i) > 0) {
                return false;
            }
        }
        return true;
    }

    public int[] findClashFreeSlots(int numSlots) {
        int[] clashFreeSlots = new int[numSlots];
        int count = 0;

        for (int i = 0; i < numSlots; i++) {
            if (isClashFree(i)) {
                clashFreeSlots[count++] = i;
            }
        }

        return Arrays.copyOf(clashFreeSlots, count);
    }

    public void saveUsedTimeSlotsLog(int numSlots, int shift, int iteration, int[] usedTimeSlots) {
        try {
            logFile = new LogFile("/Users/anushgamazyan/Downloads/testttt/used_time_slots_log.txt");
            logFile.write("Number of Slots: " + numSlots);
            logFile.write("Shift: " + shift);
            logFile.write("Iteration Index: " + iteration);
            logFile.write("Used Time Slots: " + Arrays.toString(usedTimeSlots));
            logFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeLogFile() {
        logFile.close();
    }
    
    public void interruptIterationsWithUnitUpdates(int iterations, int shifts) {
        for (int i = 1; i <= iterations; i++) {
            tool[2].doClick(); 
            int[] clashFreeSlots = findClashFreeSlots(Integer.parseInt(field[0].getText()));
            trainAutoassociatorWithClashFreeSlots(clashFreeSlots);
            saveUsedTimeSlotsLog(Integer.parseInt(field[0].getText()), shifts, i, clashFreeSlots);
            int[] updatedSlots = new int[]{autoassociator.unitUpdate(clashFreeSlots)};

            for (int j = 0; j < updatedSlots.length; j++) {
                saveUnitUpdateLog(i, j, clashFreeSlots[j], updatedSlots[j]);
            }
        }
    }

    public void saveUnitUpdateLog(int iteration, int index, int originalSlot, int updatedSlot) {
        try {
            logFile = new LogFile("/Users/anushgamazyan/Downloads/testttt/unit_update_log.txt"); 
            logFile.write("Iteration: " + iteration + ", Index: " + index +
                    ", Original Slot: " + originalSlot + ", Updated Slot: " + updatedSlot);
            logFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();

        timeTable.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timeTable.closeLogFile(); 
                System.exit(0);
            }
        });

        timeTable.field[0].setText("10");
        timeTable.field[1].setText("381");
        timeTable.field[2].setText("uta-s-93");
        timeTable.field[3].setText("4");
        timeTable.field[4].setText("7");
        timeTable.tool[0].doClick(); 
        timeTable.tool[1].doClick(); 

        int iterations = Integer.parseInt(timeTable.field[3].getText());
        int shifts = Integer.parseInt(timeTable.field[4].getText());
        timeTable.interruptIterationsWithUnitUpdates(iterations, shifts);
    }
}
