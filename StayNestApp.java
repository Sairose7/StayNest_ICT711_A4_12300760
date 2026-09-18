import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/** Entry point: lets the user choose GUI or Text-Based Interface. */
public class StayNestApp {
    public static void main(String[] args) {
        StayNestSystem system = new StayNestSystem();
        String dataFile = findDataFile();
        try { system.loadUsers(dataFile); }
        catch (Exception e) { System.out.println("Warning: " + e.getMessage()); }
        system.seedDemoData();

        if (args.length > 0 && args[0].equalsIgnoreCase("tbi")) { new StayNestTBI(system, dataFile).run(); return; }
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            if (GraphicsEnvironment.isHeadless()) {
                System.out.println("GUI is not available in this environment. Starting TBI instead.");
                new StayNestTBI(system, dataFile).run();
            } else SwingUtilities.invokeLater(() -> new StayNestGUI(system, dataFile).setVisible(true));
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Graphical display not detected. Starting Text-Based Interface.");
            new StayNestTBI(system, dataFile).run();
            return;
        }

        String[] options = {"Graphical User Interface (GUI)", "Text-Based Interface (TBI)"};
        int choice = JOptionPane.showOptionDialog(null, "Choose your preferred StayNest interface:", "StayNest",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) SwingUtilities.invokeLater(() -> new StayNestGUI(system, dataFile).setVisible(true));
        else if (choice == 1) new StayNestTBI(system, dataFile).run();
        else saveAndExit(system, dataFile);
    }

    static String findDataFile() {
        File direct = new File("data/users.csv");
        if (direct.exists()) return direct.getPath();
        File parent = new File("../data/users.csv");
        if (parent.exists()) return parent.getPath();
        return direct.getPath();
    }

    static void saveAndExit(StayNestSystem system, String dataFile) {
        try { system.saveUsers(dataFile); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}

class StayNestTBI {
    private final StayNestSystem system;
    private final String dataFile;
    private final java.util.Scanner scanner = new java.util.Scanner(System.in);
    StayNestTBI(StayNestSystem system, String dataFile) { this.system = system; this.dataFile = dataFile; }

    void run() {
        int choice;
        do {
            printMenu(); choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> addUser(); case 2 -> printUsers(system.getUsers()); case 3 -> searchUser();
                    case 4 -> updateUser(); case 5 -> deleteUser(); case 6 -> printProperties(system.getProperties());
                    case 7 -> addProperty(); case 8 -> deleteProperty(); case 9 -> addBooking();
                    case 10 -> printBookings(system.getBookings()); case 11 -> updateBooking(); case 12 -> deleteBooking(); case 13 -> searchBookings();
                    case 14 -> sortUsers(); case 15 -> sortProperties(); case 16 -> sortBookings();
                    case 17 -> addFeedback(); case 18 -> printFeedback(); case 0 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) { System.out.println("Operation failed: " + e.getMessage()); }
        } while (choice != 0);
        StayNestApp.saveAndExit(system, dataFile); scanner.close();
    }

    private void printMenu() {
        System.out.println("\n========== STAYNEST TBI ==========");
        System.out.println(" 1. Add User");
        System.out.println(" 2. View Users");
        System.out.println(" 3. Search User");
        System.out.println(" 4. Update User");
        System.out.println(" 5. Delete User");
        System.out.println(" 6. View Properties");
        System.out.println(" 7. Add Property");
        System.out.println(" 8. Delete Property");
        System.out.println(" 9. Add Booking");
        System.out.println("10. View Bookings");
        System.out.println("11. Update Booking");
        System.out.println("12. Delete Booking");
        System.out.println("13. Search Bookings");
        System.out.println("14. Sort Users");
        System.out.println("15. Sort Properties");
        System.out.println("16. Sort Bookings");
        System.out.println("17. Add Feedback");
        System.out.println("18. View Feedback");
        System.out.println(" 0. Exit");
        System.out.println("==================================");
    }
    private int readInt(String prompt) {
        while (true) { System.out.print(prompt); try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); } }
    }
    private void addUser() { int id=readInt("ID: "); System.out.print("Name: "); String n=scanner.nextLine(); System.out.print("Email: "); String e=scanner.nextLine(); System.out.print("Role (Guest/Host): "); String r=scanner.nextLine(); system.addUser(r.equalsIgnoreCase("Host")?new Host(id,n,e):new Guest(id,n,e)); System.out.println("User added."); }
    private void searchUser() { int id=readInt("ID: "); Person p=system.findUserLinear(id); System.out.println(p==null?"User not found.":p); }
    private void updateUser() { int id=readInt("ID: "); System.out.print("New name: "); String n=scanner.nextLine(); System.out.print("New email: "); String e=scanner.nextLine(); System.out.println(system.updateUser(id,n,e)?"User updated.":"User not found."); }
    private void deleteUser() { System.out.println(system.deleteUser(readInt("ID: "))?"User deleted.":"User not found."); }
    private void addProperty() { int id=readInt("Property ID: "); System.out.print("Name: "); String n=scanner.nextLine(); System.out.print("Location: "); String l=scanner.nextLine(); double p=Double.parseDouble(readText("Price/night: ")); int h=readInt("Host ID: "); system.addProperty(new Property(id,n,l,p,h)); System.out.println("Property added."); }
    private void deleteProperty() { System.out.println(system.deleteProperty(readInt("Property ID: "))?"Property deleted.":"Property not found."); }
    private String readText(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
    private void addBooking() { int id=readInt("Booking ID: "); int g=readInt("Guest ID: "); int p=readInt("Property ID: "); String d=readText("Date (YYYY-MM-DD): "); system.addBooking(new Booking(id,g,p,d,"Confirmed")); System.out.println("Booking added."); }
    private void updateBooking() { int id=readInt("Booking ID: "); Booking b=system.findBooking(id); if(b==null){System.out.println("Booking not found.");return;} b.setDate(readText("New date (YYYY-MM-DD): ")); b.setStatus(readText("New status (Confirmed/Cancelled/Completed): ")); System.out.println("Booking updated."); }
    private void deleteBooking() { System.out.println(system.deleteBooking(readInt("Booking ID: "))?"Booking deleted.":"Booking not found."); }
    private void searchBookings() { String q=readText("Search ID/date/status: "); printBookings(system.searchBookings(q)); }
    private void sortUsers() { System.out.println("1 ID  2 Name  3 Email"); int c=readInt("Sort by: "); if(c==1)system.sortUsersById(); else if(c==2)system.sortUsersByName(); else system.sortUsersByEmail(); printUsers(system.getUsers()); }
    private void sortProperties() { System.out.println("1 Name  2 Price"); int c=readInt("Sort by: "); if(c==1)system.sortPropertiesByName(); else system.sortPropertiesByPrice(); printProperties(system.getProperties()); }
    private void sortBookings() { System.out.println("1 ID  2 Date"); int c=readInt("Sort by: "); if(c==1)system.sortBookingsById(); else system.sortBookingsByDate(); printBookings(system.getBookings()); }
    private void addFeedback() { int b=readInt("Booking ID: "); int r=readInt("Rating 1-5: "); String c=readText("Comment: "); System.out.println(system.evaluate(b,r,c)); }
    private void printUsers(List<Person> x) { x.forEach(System.out::println); }
    private void printProperties(List<Property> x) { x.forEach(System.out::println); }
    private void printBookings(List<Booking> x) { x.forEach(System.out::println); }
    private void printFeedback() { system.getFeedbackList().forEach(System.out::println); }
}

class StayNestGUI extends JFrame {
    private final StayNestSystem system;
    private final String dataFile;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private final JLabel pageTitle = new JLabel("Dashboard");
    private final JLabel countLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Ready");
    private String activeTab = "Dashboard";

    StayNestGUI(StayNestSystem system, String dataFile) {
        this.system = system; this.dataFile = dataFile;
        setTitle("StayNest | Accommodation Booking System");
        setSize(1120, 700); setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { StayNestApp.saveAndExit(system, dataFile); dispose(); }
        });
        buildUI(); showDashboard();
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.addActionListener(e -> showData(text));
        return b;
    }

    private JButton actionButton(String text, Runnable action) {
        JButton b = new JButton(text); b.setFocusPainted(false); b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.addActionListener(e -> action.run()); return b;
    }

    private void buildUI() {
        setLayout(new BorderLayout(0,0));
        JPanel sidebar = new JPanel(); sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18,12,18,12)); sidebar.setPreferredSize(new Dimension(210,0));
        JLabel logo = new JLabel("STAYNEST"); logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel sub = new JLabel("Accommodation Manager"); sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sidebar.add(logo); sidebar.add(sub); sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(navButton("Dashboard")); sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navButton("Users")); sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navButton("Properties")); sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navButton("Bookings")); sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navButton("Feedback")); sidebar.add(Box.createVerticalGlue());
        sidebar.add(new JLabel("Algorithms"));
        sidebar.add(new JLabel("• Linear Search")); sidebar.add(new JLabel("• Binary Search")); sidebar.add(new JLabel("• Comparator Sorting"));
        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(15,15)); main.setBorder(BorderFactory.createEmptyBorder(20,20,15,20));
        JPanel header = new JPanel(new BorderLayout());
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        header.add(pageTitle, BorderLayout.WEST); header.add(countLabel, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14)); list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        main.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actions.add(actionButton("Add", this::addActive));
        actions.add(actionButton("Update", this::updateActive));
        actions.add(actionButton("Delete", this::deleteActive));
        actions.add(actionButton("Search", this::searchActive));
        actions.add(actionButton("Sort", this::sortActive));
        actions.add(actionButton("Refresh", this::refreshActive));
        actions.add(actionButton("Exit", () -> { StayNestApp.saveAndExit(system,dataFile); dispose(); }));
        JPanel bottom = new JPanel(new BorderLayout()); bottom.add(actions, BorderLayout.NORTH);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5,4,0,0)); bottom.add(statusLabel, BorderLayout.SOUTH);
        main.add(bottom, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);
    }

    private void showData(String tab) {
        activeTab = tab; pageTitle.setText(tab); model.clear();
        if (tab.equals("Dashboard")) { showDashboard(); return; }
        if (tab.equals("Users")) system.getUsers().forEach(p -> model.addElement(p.toString()));
        else if (tab.equals("Properties")) system.getProperties().forEach(p -> model.addElement(p.toString()));
        else if (tab.equals("Bookings")) system.getBookings().forEach(b -> model.addElement(b.toString()));
        else system.getFeedbackList().forEach(f -> model.addElement(f.toString()));
        updateCount(); status("Showing " + model.size() + " " + tab.toLowerCase() + ".");
    }
    private void refreshActive(){ showData(activeTab); }
    private void updateCount(){ countLabel.setText(model.size()+" records"); }
    private void showDashboard() {
        activeTab="Dashboard"; pageTitle.setText("Dashboard"); model.clear();
        model.addElement("WELCOME TO STAYNEST"); model.addElement("");
        model.addElement("Users       : " + system.getUsers().size());
        model.addElement("Properties  : " + system.getProperties().size());
        model.addElement("Bookings    : " + system.getBookings().size());
        model.addElement("Feedback    : " + system.getFeedbackList().size());
        model.addElement(""); model.addElement("Choose a section from the left or use the action buttons below.");
        countLabel.setText("System overview"); status("Dashboard ready.");
    }

    private String input(String label){ return JOptionPane.showInputDialog(this,label); }
    private int intInput(String label){ while(true){String s=input(label); if(s==null)throw new IllegalArgumentException("Operation cancelled."); try{return Integer.parseInt(s.trim());}catch(Exception e){JOptionPane.showMessageDialog(this,"Enter a valid integer.","Input error",JOptionPane.ERROR_MESSAGE);}}}
    private double doubleInput(String label){ while(true){String s=input(label); if(s==null)throw new IllegalArgumentException("Operation cancelled."); try{return Double.parseDouble(s.trim());}catch(Exception e){JOptionPane.showMessageDialog(this,"Enter a valid number.","Input error",JOptionPane.ERROR_MESSAGE);}}}
    private void addActive(){ try { if(activeTab.equals("Users")||activeTab.equals("Dashboard")) addUser(); else if(activeTab.equals("Properties")) addProperty(); else if(activeTab.equals("Bookings")) addBooking(); else if(activeTab.equals("Feedback")) addFeedback(); else msg("Select a section first."); } catch(Exception e){err(e);} }
    private void updateActive(){ try { if(activeTab.equals("Users")) updateUser(); else if(activeTab.equals("Bookings")) updateBooking(); else msg("Update is available for users and bookings."); } catch(Exception e){err(e);} }
    private void deleteActive(){ try { if(activeTab.equals("Users")) deleteUser(); else if(activeTab.equals("Properties")) deleteProperty(); else if(activeTab.equals("Bookings")) deleteBooking(); else msg("Delete is not available for this section."); } catch(Exception e){err(e);} }
    private void searchActive(){ try { if(activeTab.equals("Users")||activeTab.equals("Dashboard")) searchUsers(); else if(activeTab.equals("Properties")) searchProperties(); else if(activeTab.equals("Bookings")) searchBookings(); else msg("Search is not available for feedback."); } catch(Exception e){err(e);} }
    private void sortActive(){ try { if(activeTab.equals("Users")) sortUsers(); else if(activeTab.equals("Properties")) sortProperties(); else if(activeTab.equals("Bookings")) sortBookings(); else msg("Select Users, Properties or Bookings to sort."); } catch(Exception e){err(e);} }

    private void addUser(){int id=intInput("User ID:");String n=input("Name:");String em=input("Email:");String[] roles={"Guest","Host"};String r=(String)JOptionPane.showInputDialog(this,"Role:","Add User",JOptionPane.QUESTION_MESSAGE,null,roles,roles[0]);if(r==null)throw new IllegalArgumentException("Operation cancelled.");system.addUser(r.equals("Host")?new Host(id,n,em):new Guest(id,n,em));showData("Users");msg("User added successfully.");}
    private void updateUser(){int id=intInput("User ID:");String n=input("New name:");String em=input("New email:");if(!system.updateUser(id,n,em))throw new IllegalArgumentException("User not found.");showData("Users");msg("User updated successfully.");}
    private void deleteUser(){int id=intInput("User ID:");msg(system.deleteUser(id)?"User deleted.":"User not found.");showData("Users");}
    private void searchUsers(){String[] methods={"Linear search by ID","Binary search by ID","Search by name"};String m=(String)JOptionPane.showInputDialog(this,"Choose a search method:","Search Users",JOptionPane.QUESTION_MESSAGE,null,methods,methods[0]);if(m==null)return;model.clear();if(m.startsWith("Linear")){Person p=system.findUserLinear(intInput("User ID:"));if(p!=null)model.addElement(p.toString());}else if(m.startsWith("Binary")){Person p=system.findUserBinary(intInput("User ID:"));if(p!=null)model.addElement(p.toString());}else system.searchUsersByName(input("Name contains:")).forEach(p->model.addElement(p.toString()));updateCount();status(model.isEmpty()?"No matching users found.":"Search completed: "+model.size()+" result(s).");}
    private void addProperty(){int id=intInput("Property ID:");String n=input("Property name:");String l=input("Location:");double p=doubleInput("Price per night:");int h=intInput("Host ID:");system.addProperty(new Property(id,n,l,p,h));showData("Properties");msg("Property added successfully.");}
    private void deleteProperty(){int id=intInput("Property ID:");msg(system.deleteProperty(id)?"Property deleted.":"Property not found.");showData("Properties");}
    private void searchProperties(){String q=input("Search property name or location:");if(q==null)return;model.clear();system.searchProperties(q).forEach(p->model.addElement(p.toString()));updateCount();status(model.isEmpty()?"No properties found.":"Property search completed.");}
    private void addBooking(){int id=intInput("Booking ID:");int g=intInput("Guest ID:");int p=intInput("Property ID:");String d=input("Date (YYYY-MM-DD):");system.addBooking(new Booking(id,g,p,d,"Confirmed"));showData("Bookings");msg("Booking added successfully.");}
    private void updateBooking(){int id=intInput("Booking ID:");Booking b=system.findBooking(id);if(b==null)throw new IllegalArgumentException("Booking not found.");b.setDate(input("New date (YYYY-MM-DD):"));b.setStatus(input("New status (Confirmed/Cancelled/Completed):"));showData("Bookings");msg("Booking updated successfully.");}
    private void deleteBooking(){int id=intInput("Booking ID:");msg(system.deleteBooking(id)?"Booking deleted.":"Booking not found.");showData("Bookings");}
    private void searchBookings(){String q=input("Search booking ID, date or status:");if(q==null)return;model.clear();system.searchBookings(q).forEach(b->model.addElement(b.toString()));updateCount();status(model.isEmpty()?"No bookings found.":"Booking search completed.");}
    private void sortUsers(){String[] x={"ID","Name","Email"};String c=(String)JOptionPane.showInputDialog(this,"Sort users by:","Sorting",JOptionPane.QUESTION_MESSAGE,null,x,x[0]);if(c==null)return;if(c.equals("ID"))system.sortUsersById();else if(c.equals("Name"))system.sortUsersByName();else system.sortUsersByEmail();showData("Users");status("Users sorted by "+c+".");}
    private void sortProperties(){String[] x={"Name","Price"};String c=(String)JOptionPane.showInputDialog(this,"Sort properties by:","Sorting",JOptionPane.QUESTION_MESSAGE,null,x,x[0]);if(c==null)return;if(c.equals("Name"))system.sortPropertiesByName();else system.sortPropertiesByPrice();showData("Properties");status("Properties sorted by "+c+".");}
    private void sortBookings(){String[] x={"ID","Date"};String c=(String)JOptionPane.showInputDialog(this,"Sort bookings by:","Sorting",JOptionPane.QUESTION_MESSAGE,null,x,x[0]);if(c==null)return;if(c.equals("ID"))system.sortBookingsById();else system.sortBookingsByDate();showData("Bookings");status("Bookings sorted by "+c+".");}
    private void addFeedback(){int b=intInput("Booking ID:");int r=intInput("Rating (1-5):");String c=input("Comment:");msg(system.evaluate(b,r,c));showData("Feedback");}
    private void status(String s){statusLabel.setText("  "+s);}
    private void msg(String s){JOptionPane.showMessageDialog(this,s,"StayNest",JOptionPane.INFORMATION_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage()==null?"Operation failed.":e.getMessage(),"StayNest - Error",JOptionPane.ERROR_MESSAGE);status("Operation failed.");}
}

