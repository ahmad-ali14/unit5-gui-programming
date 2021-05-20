package textcollage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * A panel that contains a large drawing area where strings can be drawn. The
 * strings are represented by objects of type DrawTextItem. An input box under
 * the panel allows the user to specify what string will be drawn when the user
 * clicks on the drawing area.
 */
public class DrawTextPanel extends JPanel {

	// As it now stands, this class can only show one string at at
	// a time! The data for that string is in the DrawTextItem object
	// named theString. (If it's null, nothing is shown. This
	// variable should be replaced by a variable of type
	// ArrayList<DrawStringItem> that can store multiple items.

	private ArrayList<DrawTextItem> theString = new ArrayList<DrawTextItem>(); // change to an ArrayList<DrawTextItem> !
	private String fileSeparator = " ### ";

	private Color currentTextColor = Color.BLACK; // Color applied to new strings.

	private Canvas canvas; // the drawing area.
	private JTextField input; // where the user inputs the string that will be added to the canvas
	private SimpleFileChooser fileChooser; // for letting the user select files
	private JMenuBar menuBar; // a menu bar with command that affect this panel
	private MenuHandler menuHandler; // a listener that responds whenever the user selects a menu command
	private JMenuItem undoMenuItem; // the "Remove Item" command from the edit menu

	/**
	 * An object of type Canvas is used for the drawing area. The canvas simply
	 * displays all the DrawTextItems that are stored in the ArrayList, strings.
	 */
	private class Canvas extends JPanel {
		Canvas() {
			setPreferredSize(new Dimension(800, 600));
			setBackground(Color.LIGHT_GRAY);
			setFont(new Font("Serif", Font.BOLD, 24));
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			theString.forEach(str -> {
				if (str != null)
					str.draw(g);
			});
		}
	}

	/**
	 * An object of type MenuHandler is registered as the ActionListener for all the
	 * commands in the menu bar. The MenuHandler object simply calls doMenuCommand()
	 * when the user selects a command from the menu.
	 */
	private class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			doMenuCommand(evt.getActionCommand());
		}
	}

	/**
	 * Creates a DrawTextPanel. The panel has a large drawing area and a text input
	 * box where the user can specify a string. When the user clicks the drawing
	 * area, the string is added to the drawing area at the point where the user
	 * clicked.
	 */
	public DrawTextPanel() {
		fileChooser = new SimpleFileChooser();
		undoMenuItem = new JMenuItem("Remove Item");
		undoMenuItem.setEnabled(false);
		menuHandler = new MenuHandler();
		setLayout(new BorderLayout(3, 3));
		setBackground(Color.BLACK);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		bottom.add(new JLabel("Text to add: "));
		input = new JTextField("Hello World!", 40);
		bottom.add(input);
		add(bottom, BorderLayout.SOUTH);
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doMousePress(e);
			}
		});
	}

	/**
	 * This method is called when the user clicks the drawing area. A new string is
	 * added to the drawing area. The center of the string is at the point where the
	 * user clicked.
	 * 
	 * @param e the mouse event that was generated when the user clicked
	 */
	public void doMousePress(MouseEvent e) {
		String text = input.getText().trim();
		if (text.length() == 0) {
			input.setText("Hello World!");
			text = "Hello World!";
		}
		DrawTextItem s = new DrawTextItem(text, e.getX(), e.getY());
		s.setTextColor(currentTextColor); // Default is null, meaning default color of the canvas (black).

		// SOME OTHER OPTIONS THAT CAN BE APPLIED TO TEXT ITEMS:
		// s.setFont( new Font( "Serif", Font.ITALIC + Font.BOLD, 12 )); // Default is
		// null, meaning font of canvas.
		// s.setMagnification(3); // Default is 1, meaning no magnification.
		// s.setBorder(true); // Default is false, meaning don't draw a border.
		// s.setRotationAngle(25); // Default is 0, meaning no rotation.
		// s.setTextTransparency(0.3); // Default is 0, meaning text is not at all
		// transparent.
		// s.setBackground(Color.BLUE); // Default is null, meaning don't draw a
		// background area.
		// s.setBackgroundTransparency(0.7); // Default is 0, meaning background is not
		// transparent.

		theString.add(s); // Set this string as the ONLY string to be drawn on the canvas!
		undoMenuItem.setEnabled(true);
		canvas.repaint();
	}

	/**
	 * Returns a menu bar containing commands that affect this panel. The menu bar
	 * is meant to appear in the same window that contains this panel.
	 */
	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();

			String commandKey; // for making keyboard accelerators for menu commands
			if (System.getProperty("mrj.version") == null)
				commandKey = "control "; // command key for non-Mac OS
			else
				commandKey = "meta "; // command key for Mac OS

			JMenu fileMenu = new JMenu("File");
			menuBar.add(fileMenu);
			JMenuItem saveItem = new JMenuItem("Save...");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "N"));
			saveItem.addActionListener(menuHandler);
			fileMenu.add(saveItem);
			JMenuItem openItem = new JMenuItem("Open...");
			openItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "O"));
			openItem.addActionListener(menuHandler);
			fileMenu.add(openItem);
			fileMenu.addSeparator();
			JMenuItem saveImageItem = new JMenuItem("Save Image...");
			saveImageItem.addActionListener(menuHandler);
			fileMenu.add(saveImageItem);

			JMenu editMenu = new JMenu("Edit");
			menuBar.add(editMenu);
			undoMenuItem.addActionListener(menuHandler); // undoItem was created in the constructor
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "Z"));
			editMenu.add(undoMenuItem);
			editMenu.addSeparator();
			JMenuItem clearItem = new JMenuItem("Clear");
			clearItem.addActionListener(menuHandler);
			editMenu.add(clearItem);

			JMenuItem add30strings = new JMenuItem("add 30 strings");
			add30strings.addActionListener(menuHandler);
			editMenu.add(add30strings);

			JMenu optionsMenu = new JMenu("Options");
			menuBar.add(optionsMenu);
			JMenuItem colorItem = new JMenuItem("Set Text Color...");
			colorItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "T"));
			colorItem.addActionListener(menuHandler);
			optionsMenu.add(colorItem);
			JMenuItem bgColorItem = new JMenuItem("Set Background Color...");
			bgColorItem.addActionListener(menuHandler);
			optionsMenu.add(bgColorItem);

		}
		return menuBar;
	}

	/**
	 * Carry out one of the commands from the menu bar.
	 * 
	 * @param command the text of the menu command.
	 * @throws FileNotFoundException
	 */
	private void doMenuCommand(String command) {
		if (command.equals("Save...")) { // save all the string info to a file
			File fileToSave = fileChooser.getOutputFile(this, "Select File Name",
					theString.get(0).getString() + ".txt");
			if (fileToSave == null)
				return;
			try {
				PrintWriter pw = new PrintWriter(fileToSave);
				pw.println("String" + fileSeparator + "x" + fileSeparator + "Y" + fileSeparator + "color: R"
						+ fileSeparator + "color: G" + fileSeparator + "color: B");

				theString.forEach(str -> {
					pw.println(str.getString() + fileSeparator + str.getX() + fileSeparator + str.getY() + fileSeparator
							+ str.getTextColor().getRed() + fileSeparator + str.getTextColor().getGreen()
							+ fileSeparator + str.getTextColor().getBlue());
				});
				pw.flush();
				pw.close();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Sorry, an error occurred while trying to save the file:\n" + e);
			}
		} else if (command.equals("Open...")) { // read a previously saved file, and reconstruct the list of strings
			File fileToOpen = fileChooser.getInputFile(this, "Select File Name");
			if (fileToOpen == null)
				return;
			try {
				Scanner fs = new Scanner(fileToOpen);
				theString.clear(); // remove the existing image
				for (int lineNum = 1; fs.hasNext(); lineNum++) {
					String line = fs.nextLine();
					String[] lineData = line.split(fileSeparator);
					System.out.println(Arrays.toString(lineData));
					if (lineNum >= 2) { // skip the first line
						DrawTextItem s = new DrawTextItem(lineData[0], Integer.parseInt(lineData[1]),
								Integer.parseInt(lineData[2]));
						s.setTextColor(new Color(Integer.parseInt(lineData[3]), Integer.parseInt(lineData[4]),
								Integer.parseInt(lineData[5])));
						theString.add(s); // Set this string as the ONLY string to be drawn on the canvas!
						undoMenuItem.setEnabled(true);
					}

				}

				fs.close();
				canvas.repaint(); // (you'll need this to make the new list of strings take effect)

			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Sorry, an error occurred while trying to open the file:\n" + e);
			}
		} else if (command.equals("Clear")) { // remove all strings
			theString.clear(); // Remove the ONLY string from the canvas.
			undoMenuItem.setEnabled(false);
			canvas.repaint();
		} else if (command.equals("Remove Item")) { // remove the most recently added string
			if (theString.size() > 1) {
				theString.remove(theString.get(theString.size() - 1)); // Remove the ONLY string from the canvas.
				undoMenuItem.setEnabled(false);
				canvas.repaint();
			}
		} else if (command.equals("Set Text Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Text Color", currentTextColor);
			if (c != null)
				currentTextColor = c;
		} else if (command.equals("Set Background Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Background Color", canvas.getBackground());
			if (c != null) {
				canvas.setBackground(c);
				canvas.repaint();
			}
		} else if (command.equals("Save Image...")) { // save a PNG image of the drawing area
			File imageFile = fileChooser.getOutputFile(this, "Select Image File Name", "textimage.png");
			if (imageFile == null)
				return;
			try {
				// Because the image is not available, I will make a new BufferedImage and
				// draw the same data to the BufferedImage as is shown in the panel.
				// A BufferedImage is an image that is stored in memory, not on the screen.
				// There is a convenient method for writing a BufferedImage to a file.
				BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics g = image.getGraphics();
				g.setFont(canvas.getFont());
				canvas.paintComponent(g); // draws the canvas onto the BufferedImage, not the screen!
				boolean ok = ImageIO.write(image, "PNG", imageFile); // write to the file
				if (ok == false)
					throw new Exception("PNG format not supported (this shouldn't happen!).");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Sorry, an error occurred while trying to save the image:\n" + e);
			}
		} else if (command.equals("add 30 strings")) {
			try {
				for (int i = 0; i < 30; i++) {
					DrawTextItem s = new DrawTextItem("lorem ipsum", generateRandomInt("x"), generateRandomInt("y"));
					s.setTextColor(new Color(generateRandomInt("color"), generateRandomInt("color"),
							generateRandomInt("color")));
					theString.add(s); // Set this string as the ONLY string to be drawn on the canvas!
					undoMenuItem.setEnabled(true);
				}
				canvas.repaint();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Sorry, an error occurred while trying to add 30 strings:\n" + e);
			}
		}
	}

	public static int generateRandomInt(String type) {
		int min = 0;
		int max = 0;

		if (type.equals("x"))
			max = 800;
		if (type.equals("y"))
			max = 600;
		if (type.equals("color"))
			max = 250;

		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
		return random_int;
	}

}
