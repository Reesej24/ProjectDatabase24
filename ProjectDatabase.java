import java.sql.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ProjectDatabase extends JFrame implements ActionListener, ChangeListener {
    private static float hpMaxVal = 0.0f;
    private static float mpgMaxVal = 0.0f;
    private static float hpMinVal = 0.0f;
    private static float mpgMinVal = 0.0f;
    private static int colNum;
    private static final int hpMin = 0;
    private static final int mpgMin = 0;
    private static int mpgMax;
    private static int hpMax;
    private static int hpInit = hpMin;
    private static int mpgInit = mpgMin;
    private static int size = 0;
    private static int mpgVal;
    private static int hpVal;
    private static String DB;
    private static String DB_URL;
    private static String user;
    private static String pass;
    private static String tableName;
    private static String [] columnHeads = {"MPG", "Cylinders", "Displacement",
            "Horsepower", "Weight (lbs)", "Acceleration",
            "Model Year", "Origin", "Car Name"};
    private static Object [][] tableVals;
    private JFormattedTextField mpgField;        // holds selected mpg
    private JFormattedTextField hpField;         // holds selected horsepower
    private JLabel hpLabel;
    private JLabel mpgLabel;
    private JLabel tableLabel;
    private JButton searchButton;
    private JButton clearButton;
    private JSlider hpSlider;
    private JSlider mpgSlider;
    private JTable resultsTable;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;

    ProjectDatabase() {

        GridBagConstraints layoutConst = null;
        colNum = columnHeads.length;
        tableVals = new Object[size][colNum];

        tableModel = new DefaultTableModel(tableVals, columnHeads);

        // Frame title
        setTitle("Auto MPG Data");

        // Create results table
        tableLabel = new JLabel("Results:");
        hpLabel = new JLabel("Horsepower:");
        mpgLabel = new JLabel("MPG:");
        hpMax = (int) (10 * (Math.ceil(hpMaxVal / 10)));    // round up to the nearest 10 from hpMaxVal.
        mpgMax = (int) (10 * (Math.ceil(mpgMaxVal / 10)));  // round up to the nearest 10 from mpgMaxVal.

        int mpgMajor = (int) Math.ceil(mpgMax / 10);    //round up from mpgMax / 10 for each mpgMajorTick interval
        int mpgMinor = (int) Math.ceil(mpgMax / 20);    //round up from mpgMax / 20 for each mpgMinorTick interval
        int hpMajor = (int) Math.ceil(hpMax / 5);       //round up from hpMax / 5 for each hpMajorTick interval

        // build JSlider for mpg filter
        mpgSlider = new JSlider(mpgMin, mpgMax, mpgInit);
        mpgSlider.addChangeListener(this);
        mpgSlider.setMajorTickSpacing(mpgMajor);
        mpgSlider.setMinorTickSpacing(mpgMinor);
        mpgSlider.setPaintTicks(true);
        mpgSlider.setPaintTrack(true);
        mpgSlider.setPaintLabels(true);

        // build text field for mpg filter
        mpgField = new JFormattedTextField(10);
        mpgField.setEditable(false);
        mpgField.setText("0.0");

        // build JSlider for horsepower filter
        hpSlider = new JSlider(hpMin, hpMax, hpInit);
        hpSlider.addChangeListener(this);
        hpSlider.setMajorTickSpacing(hpMajor);
        hpSlider.setPaintTicks(true);
        hpSlider.setPaintTrack(true);
        hpSlider.setPaintLabels(true);

        // build text field for hp filter
        hpField = new JFormattedTextField(10);
        hpField.setEditable(false);
        hpField.setText("0.0");

        // build 'search' button
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        // build 'clear' button
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);


        // Initialize table to default model
        resultsTable = new JTable(tableModel);
        // resize the 'car name' column to fit text
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        TableColumnModel colModel = resultsTable.getColumnModel();
        colModel.getColumn(8).setPreferredWidth(300);

        resultsTable.setEnabled(false); // Prevent user input via table

        // build scroll pane for table
        tableScroll = new JScrollPane(resultsTable);
        tableScroll.setVisible(true);
        add(tableScroll);

        // set layout for components added to frame
        setLayout(new GridBagLayout());

        // place table label
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 10, 1, 0);
        layoutConst.fill = GridBagConstraints.LINE_START;
        layoutConst.gridx = 0;
        layoutConst.gridy = 0;
        layoutConst.gridwidth = 1;
        add(tableLabel, layoutConst);

        // place table
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 10, 0, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 1;
        layoutConst.gridwidth = 8;
        add(resultsTable.getTableHeader(), layoutConst);

        // place scroll pane for table
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 10, 10, 10);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 3;
        layoutConst.gridwidth = 8;
        add(tableScroll, layoutConst);

        // place mpg label
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 10, 1, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 4;
        layoutConst.gridwidth = 1;
        add(mpgLabel, layoutConst);

        // place mpg text field
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 1, 1, 10);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 1;
        layoutConst.gridy = 4;
        layoutConst.gridwidth = 1;
        add(mpgField, layoutConst);

        // place horsepower label
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 10, 1, 0);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 2;
        layoutConst.gridy = 4;
        layoutConst.gridwidth = 1;
        add(hpLabel, layoutConst);

        // place horsepower text field
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 1, 1, 10);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 3;
        layoutConst.gridy = 4;
        layoutConst.gridwidth = 1;
        add(hpField, layoutConst);

        // place mpg slider
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 10, 10, 10);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 0;
        layoutConst.gridy = 5;
        layoutConst.gridwidth = 2;
        add(mpgSlider, layoutConst);

        // place horsepower slider
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(1, 10, 10, 10);
        layoutConst.fill = GridBagConstraints.HORIZONTAL;
        layoutConst.gridx = 2;
        layoutConst.gridy = 5;
        layoutConst.gridwidth = 2;
        add(hpSlider, layoutConst);

        // place search button
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 10, 10, 5);
        layoutConst.anchor = GridBagConstraints.LINE_END;
        layoutConst.gridx = 4;
        layoutConst.gridy = 5;
        layoutConst.gridwidth = 1;
        add(searchButton, layoutConst);

        // place clear button
        layoutConst = new GridBagConstraints();
        layoutConst.insets = new Insets(10, 10, 10, 5);
        layoutConst.anchor = GridBagConstraints.LINE_END;
        layoutConst.gridx = 5;
        layoutConst.gridy = 5;
        layoutConst. gridwidth = 1;
        add(clearButton, layoutConst);
    }
    @Override
    public void stateChanged(ChangeEvent event) {
        // when slider is adjusted, ChangeEvent is called.
        // when called, mpgVal and hpVal will update
        // mpg text field will update with slider value

        String strSliderVal;

        JSlider sourceEvent = (JSlider) event.getSource();

        if (sourceEvent == mpgSlider) {
             mpgVal = mpgSlider.getValue();
            strSliderVal = Integer.toString(mpgVal) + ".0";
            mpgField.setText(strSliderVal);
        } else if (sourceEvent == hpSlider) {
            hpVal = hpSlider.getValue();
            strSliderVal = Integer.toString(hpVal) +".0";
            hpField.setText(strSliderVal);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // ActionEvent is called when either the 'Search'
        // or 'Clear' button is pressed.
        int i;

        JButton sourceEvent = (JButton) event.getSource();

        // setting table to default
        tableModel = new DefaultTableModel(tableVals, columnHeads);

        // establish connection with SQL server
        Connection conn;
        Statement stmt;

        try {
            conn = DriverManager.getConnection(DB_URL, user, pass);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ResultSet resultSet;

        try {
            if (sourceEvent == searchButton) {
                // when search button is selected, the mpgVal and
                // hpVal values are taken and placed in an SQL
                // query to search database for records that
                // match entered filters.

                // This will return the table to default every time the search
                // button is pressed.
                for (i = 0; i < size; ++i) {
                    for (int j1 = 0; j1 < colNum; ++j1) {
                        tableVals[i][j1] = "";
                    }
                }
                resultsTable.setModel(tableModel);
                resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
                TableColumnModel colModel = resultsTable.getColumnModel();
                colModel.getColumn(8).setPreferredWidth(300);

                // if sliders are either at maximum or minimum value, the entire table
                // will be returned.
                // Table will automatically update with returned values.
                if ((mpgVal == 0 || mpgVal == mpgMax) && (hpVal == 0 || hpVal == hpMax)) {

                    resultSet = stmt.executeQuery("select * from " + tableName + ";");

                    i = 0;
                    while (resultSet.next()) {
                        for (int j1 = 0; j1 < colNum; ++j1) {
                            tableVals[i][j1] = resultSet.getString(j1 + 1);
                        }
                        ++i;
                    }
                    resultsTable.setModel(tableModel);
                    resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
                    colModel = resultsTable.getColumnModel();
                    colModel.getColumn(8).setPreferredWidth(300);

                } else {
                    // This block of code is called if either of the sliders are
                    // on a value other than the minimum or the maximum.

                    // This will search and return values that are greater than or
                    // equal to the values of the sliders. Table values will
                    // automatically update.

                    resultSet = stmt.executeQuery("select * from " + tableName +
                            " where mpg >= " + mpgVal +
                            " and horsepower >=" + hpVal + ";");


                    i = 0;
                    while (resultSet.next()) {
                        for (int j1 = 0; j1 < colNum; ++j1) {
                            tableVals[i][j1] = resultSet.getString(j1 + 1);
                        }
                        ++i;
                    }
                    resultsTable.setModel(tableModel);
                    resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
                    colModel = resultsTable.getColumnModel();
                    colModel.getColumn(8).setPreferredWidth(300);
                }

            } else if (sourceEvent == clearButton) {
                // This block of code is called if the 'clear' button
                // is pressed.
                // The table will be cleared out and returned to
                // default.
                // The sliders and text fields will be returned to
                // zero.

                mpgSlider.setValue(mpgMin);
                hpSlider.setValue(hpMin);
                mpgField.setText("0.0");
                hpField.setText("0.0");

                for (i = 0; i < size; ++i) {
                    for (int j1 = 0; j1 < colNum; ++j1) {
                        tableVals[i][j1] = "";
                    }
                }
                resultsTable.setModel(tableModel);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void insertionSort(float [] numbers) {
        // I used insertion sort to find the minimum and maximum
        // values in the set of returned mpg and horsepower values.

        int i;          // for loop variable
        int j;          // while loop variable
        float temp;     // temp variable for swap

        for (i = 1; i < numbers.length; ++i) {
            j = i;
            while (j > 0 && numbers[j] < numbers[j - 1]) {
                temp = numbers[j];
                numbers[j] = numbers[j - 1];
                numbers[j - 1] = temp;

                --j;
            }
        }
    }
    public static void main (String[] args)
        throws Exception, IOException, SQLException, ClassNotFoundException {
        Boolean tableExists;

        Scanner scnr = new Scanner(System.in);

        // Load the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Driver loaded.");

        // User can enter their preferred database.
        System.out.print("Enter database name: ");
        DB = scnr.next();
        DB_URL = "jdbc:mysql://localhost/" + DB;
        System.out.println();

        // This part is intended to be for an administrative
        // person at a company that would be using this program.

        // User can enter specified userName
        System.out.print("Enter user name: ");
        user = scnr.next();
        System.out.println();

        // User can enter their password.
        System.out.print("Enter password: ");
        pass = scnr.next();
        System.out.println();

        // Establish a connection
        Connection conn = DriverManager.getConnection(DB_URL, user, pass);
        System.out.println("Connection to database '" + DB + "' successful.");

        // User chooses their table
        System.out.print("Enter table name: ");
        tableName = scnr.next();
        Statement stmt = conn.createStatement();

        // The program will first check the database to see if the table exists
        String ifExists = "select TABLE_NAME " +
                "from INFORMATION_SCHEMA.TABLES " +
                "where TABLE_SCHEMA = '" + DB.toLowerCase() + "' " +
                "and TABLE_NAME = '" + tableName.toLowerCase() + "';";


        ResultSet resultSet = stmt.executeQuery(ifExists);

        if(!resultSet.next()) {
            // If the table does not already exist, the program will ask
            // the user if they want to create a table with the entered
            // name.
            // If the user selects yes or y (not case-sensitive), then the
            // program will create the table in SQL.
            String ans1 = "answer";

            while (!(ans1.equalsIgnoreCase("yes") ||
                    ans1.equalsIgnoreCase("y") ||
                    ans1.equalsIgnoreCase("no") ||
                    ans1.equalsIgnoreCase("n"))) {
                System.out.println(tableName + " does not exist.");
                System.out.println("Would you like to create table '" + tableName + "'?");
                ans1 = scnr.next();

                if (ans1.equalsIgnoreCase("yes") ||
                        ans1.equalsIgnoreCase("y")) {
                    String createTable = "create table " + tableName + " (" +
                            "mpg varchar(5), cylinders varchar(5), displacement varchar(5), " +
                            "horsepower varchar (5), weight varchar(5), acceleration varchar(5), " +
                            "modelYear varchar(5), origin varchar(5), carName varchar(50));";
                    stmt.executeUpdate(createTable);
                    System.out.println("table '" + tableName + "' created!");
                    System.out.println();
                } else if (ans1.equalsIgnoreCase("no") ||
                        ans1.equalsIgnoreCase("n")) {
                    // if the user enteres 'no' or 'n', the program will close.
                    System.exit(0);
                } else {
                    // any other entry will give this message
                    System.out.println("Invalid entry.");
                }
            }
        }

        int ans2 = 0;
        while (!(ans2 == 5 || ans2 == 6)) {
            // This prompt will come up after every action as unless you choose 5 or 6
            // for GUI or close, respectively.
            // This gives the user the choice to describe table, insert values in chosen
            // table, show values in chosen table, drop values from chosen table, open
            // the GUI, or close out of the program.
            System.out.println("What would you like to do with " + tableName + "?");
            System.out.println("[1 = describe; 2 = insert values, 3 = show values,");
            System.out.println("4 = drop values, 5 = access GUI, 6 = close]");
            ans2 = scnr.nextInt();
            System.out.println();

            if (ans2 == 1) {
                // This will describe table using SQL query
                System.out.println("Describe table:");
                System.out.println();

                // Execute query
                resultSet = stmt.executeQuery("describe " + tableName + ";");

                while (resultSet.next()) {
                    System.out.println(resultSet.getString(1) + "\t" +
                            resultSet.getString(2) + "\t" + resultSet.getString(3) + "\t" +
                            resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" +
                            resultSet.getString(6));
                }
            }
            else if (ans2 == 2) {
                // This will read from a file to insert values into your table using SQL statement
                System.out.println("Insert Values: ");
                System.out.println();

                FileInputStream fileByteStream = null;
                float temp;
                float [] hpVal;
                float [] mpgVal;
                float [] numbers;
                int i;
                int j;
                String mpg1;
                String cyl1;
                String disp1;
                String hp1;
                String weight1;
                String accel1;
                String modYr1;
                String og1;
                String carNm1;
                String fileName;

                LinkedList<String> mpgList = new LinkedList<String>();
                LinkedList<String> hpList = new LinkedList<String>();
                ListIterator<String> listIterator;

                // user can specify the desired file
                System.out.print("Enter name of file: ");
                fileName = scnr.next();
                System.out.println("Opening file: " + fileName);
                fileByteStream = new FileInputStream(fileName);
                Scanner input = new Scanner(fileByteStream);

                // Read file content
                System.out.println("Reading file content...");

                while(input.hasNext()) {
                    // This reads values from file, gets rid of all "NA" entries
                    // I had to format the entry for any of the car names that
                    // had an apostrophe in it.

                    ++size; // increment int variable for loops.
                    mpg1 = input.next();
                    cyl1 = input.next();
                    disp1 = input.next();
                    hp1 = input.next();
                    weight1 = input.next();
                    accel1 = input.next();
                    modYr1 = input.next();
                    og1 = input.next();
                    carNm1 = input.nextLine();

                    if (mpg1.contains("NA")){
                        mpg1 = mpg1.replace("NA", "0.0");
                    }
                    if (hp1.contains("NA")) {
                        hp1 = hp1.replace("NA", "0.0");
                    }
                    if(carNm1.contains("'")) {
                        carNm1 = carNm1.replace("'", "''");
                    }
                    // add values to linked list for sorting.
                    mpgList.add(mpg1);

                    hpList.add(hp1);

                    String values = "insert into " + tableName + " (mpg, cylinders," +
                            "displacement, horsepower, weight, acceleration," +
                            "modelYear, origin, carName)" +
                            "values (" + mpg1 + ", " + cyl1 + ", " + disp1 + ", " +
                            hp1 + ", " + weight1 + ", " + accel1 + ", " + modYr1 +
                            ", " + og1 + ", '" + carNm1 + "');";

                    stmt.executeUpdate(values);
                }


                mpgVal = new float[size];
                hpVal = new float[size];
                numbers = new float[size];

                // Iterate through the values in each list, add them to the 'numbers'
                // array, sort the values in array and find the max and min values of
                // each array for automatic slider value update.

                listIterator = mpgList.listIterator();
                j = 0;
                while (listIterator.hasNext()) {
                    temp = Float.parseFloat(listIterator.next());
                    numbers[j] = temp;
                    if(j < size - 1) ++j;
                }
                insertionSort(numbers);
                mpgVal = numbers;

                listIterator = hpList.listIterator();
                numbers = new float[size];

                j = 0;
                while (listIterator.hasNext()) {
                    temp = Float.parseFloat(listIterator.next());
                    numbers[j] = temp;
                    if(j < size - 1) ++j;
                }
                insertionSort(numbers);
                hpVal = numbers;

                hpMinVal = hpVal[0];
                hpMaxVal = hpVal[j];
                mpgMinVal = mpgVal[0];
                mpgMaxVal = mpgVal[j];

                System.out.println("MPG: ");
                System.out.println(Arrays.toString(mpgVal));
                System.out.println();
                System.out.println("HP: ");
                System.out.println(Arrays.toString(hpVal));
                System.out.println();
                System.out.println("Min mpg: " + mpgMinVal);
                System.out.println("Max mpg: " + mpgMaxVal);
                System.out.println("Min hp: " + hpMinVal);
                System.out.println("Max hp: " + hpMaxVal);

                numbers = new float[size];
                hpVal = new float[size];
                mpgVal = new float[size];
            }
            else if (ans2 == 3) {
                // This will return entire table using SQL query

                float [] mpgVal;
                float [] hpVal;
                float [] numbers;
                float temp;
                int j;

                System.out.println("Show table: ");

                LinkedList<String> mpgList = new LinkedList<String>();
                LinkedList<String> hpList = new LinkedList<String>();
                ListIterator<String> listIterator;

                resultSet = stmt.executeQuery("select * from " + tableName + ";");

                // Iterate through the result and print the student names
                while (resultSet.next()) {

                    // increment variable for array size and loop count
                    ++size;

                    // add list for sorting like above.
                    mpgList.add(resultSet.getString(1));
                    hpList.add(resultSet.getString(4));

                    System.out.println(resultSet.getString(1) + "\t" +
                            resultSet.getString(2) + "\t" + resultSet.getString(3) + "\t" +
                            resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" +
                            resultSet.getString(6) + "\t" + resultSet.getString(7) + "\t" +
                            resultSet.getString(8) + "\t" + resultSet.getString(9));
                }
                mpgVal = new float[size];
                hpVal = new float[size];
                numbers = new float[size];

                listIterator = mpgList.listIterator();
                j = 0;
                while (listIterator.hasNext()) {
                    temp = Float.parseFloat(listIterator.next());
                    numbers[j] = temp;
                    if(j < size - 1) ++j;
                }
                insertionSort(numbers);
                mpgVal = numbers;

                listIterator = hpList.listIterator();
                numbers = new float[size];

                j = 0;
                while (listIterator.hasNext()) {
                    temp = Float.parseFloat(listIterator.next());
                    numbers[j] = temp;
                    if(j < size - 1) ++j;
                }
                insertionSort(numbers);
                hpVal = numbers;

                hpMinVal = hpVal[0];
                hpMaxVal = hpVal[j];
                mpgMinVal = mpgVal[0];
                mpgMaxVal = mpgVal[j];

                System.out.println("MPG: ");
                System.out.println(Arrays.toString(mpgVal));
                System.out.println();
                System.out.println("HP: ");
                System.out.println(Arrays.toString(hpVal));
                System.out.println();
                System.out.println("Min mpg: " + mpgMinVal);
                System.out.println("Max mpg: " + mpgMaxVal);
                System.out.println("Min hp: " + hpMinVal);
                System.out.println("Max hp: " + hpMaxVal);

                numbers = new float[size];
                hpVal = new float[size];
                mpgVal = new float[size];
            }
            else if (ans2 == 4) {
                System.out.println("Drop values: ");
                System.out.println();
                System.out.println("Are you sure? [y or n]");

                String ans3 = scnr.next();

                if(ans3.equalsIgnoreCase("y")) {
                    String drop = "delete from " + tableName + ";";
                    stmt.executeUpdate(drop);
                }
                System.out.println("Table " + tableName + " has been cleared.");
            }
            else if (ans2 == 5) {
                // This block will open GUI used for searching database.
                float [] mpgVal;
                float [] hpVal;
                float [] numbers;
                float temp;
                int j;

                LinkedList<String> mpgList = new LinkedList<String>();
                LinkedList<String> hpList = new LinkedList<String>();
                ListIterator<String> listIterator;

                resultSet = stmt.executeQuery("select * from " + tableName + ";");

                // Iterate through the result and print the student names
                while (resultSet.next()) {

                    ++size;

                    mpgList.add(resultSet.getString(1));
                    hpList.add(resultSet.getString(4));

                }
                mpgVal = new float[size];
                hpVal = new float[size];
                numbers = new float[size];

                listIterator = mpgList.listIterator();
                j = 0;
                while (listIterator.hasNext()) {
                    temp = Float.parseFloat(listIterator.next());
                    numbers[j] = temp;
                    if(j < size - 1) ++j;
                }

                insertionSort(numbers);
                mpgVal = numbers;

                listIterator = hpList.listIterator();
                numbers = new float[size];

                j = 0;
                while (listIterator.hasNext()) {
                    temp = Float.parseFloat(listIterator.next());
                    numbers[j] = temp;
                    if(j < size - 1) ++j;
                }

                insertionSort(numbers);
                hpVal = numbers;

                hpMinVal = hpVal[0];
                hpMaxVal = hpVal[j];
                mpgMinVal = mpgVal[0];
                mpgMaxVal = mpgVal[j];

                numbers = new float[size];
                hpVal = new float[size];
                mpgVal = new float[size];

                ProjectDatabase myFrame = new ProjectDatabase();

                myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                myFrame.pack();

                myFrame.setVisible(true);

            }
            else if (ans2 == 6) {

                conn.close();
                System.out.println("Connection closed.");
            }
        }
    }
}