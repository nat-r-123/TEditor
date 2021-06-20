import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.undo.*;
import java.util.regex.*;

public class TEditor extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JTextField searchField;
    private JPanel searchPanel;
    private UndoManager textAreaUndoManager;

    TEditor() {
        super("Text Editor");

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem itemNew = new JMenuItem("New");
        JMenuItem itemOpen = new JMenuItem("Open");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemQuit = new JMenuItem("Quit");
        menuFile.add(itemNew);
        menuFile.add(itemOpen);
        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemQuit);
        JMenu menuEdit = new JMenu("Edit");
        JMenuItem itemUndo = new JMenuItem("Undo");
        JMenuItem itemRedo = new JMenuItem("Redo");
        JMenuItem itemCut = new JMenuItem("Cut");
        JMenuItem itemCopy = new JMenuItem("Copy");
        JMenuItem itemPaste = new JMenuItem("Paste");
        JMenuItem itemSelect = new JMenuItem("Select All");
        JMenuItem itemFind = new JMenuItem("Find");
        menuEdit.add(itemUndo);
        menuEdit.add(itemRedo);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemCut);
        menuEdit.add(itemCopy);
        menuEdit.add(itemPaste);
        menuEdit.add(itemSelect);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemFind);
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        setJMenuBar(menuBar);

        itemNew.addActionListener(this);
        itemOpen.addActionListener(this);
        itemSave.addActionListener(this);
        itemQuit.addActionListener(this);
        itemUndo.addActionListener(this);
        itemRedo.addActionListener(this);
        itemCut.addActionListener(this);
        itemCopy.addActionListener(this);
        itemPaste.addActionListener(this);
        itemSelect.addActionListener(this);
        itemFind.addActionListener(this);

        itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        itemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        itemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        itemSelect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem itemCutContext = new JMenuItem("Cut");
        JMenuItem itemCopyContext = new JMenuItem("Copy");
        JMenuItem itemPasteContext = new JMenuItem("Paste");
        JMenuItem itemSelectContext = new JMenuItem("Select All");
        contextMenu.add(itemCutContext);
        contextMenu.add(itemCopyContext);
        contextMenu.add(itemPasteContext);
        contextMenu.add(itemSelectContext);
        textArea.setComponentPopupMenu(contextMenu);

        itemCutContext.addActionListener(this);
        itemCopyContext.addActionListener(this);
        itemPasteContext.addActionListener(this);
        itemSelectContext.addActionListener(this);

        setLayout(new BorderLayout());

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        searchField = new JTextField(16);
        JButton searchNext = new JButton("Next");
        JButton searchEnd = new JButton("Close");
        searchPanel.add(searchField);
        searchPanel.add(searchNext);
        searchPanel.add(searchEnd);
        add(searchPanel, BorderLayout.SOUTH);
        searchPanel.setVisible(false);

        searchNext.addActionListener(this);
        searchEnd.addActionListener(this);

        textAreaUndoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(textAreaUndoManager);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        setSize(640, 480);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("New")) {
			textArea.setText("");
		}
        else if (command.equals("Open")) {
			JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();

				try {                    
					BufferedReader br = new BufferedReader(new FileReader(f));

                    textArea.setText(br.readLine());
                    String line;
                    while ((line = br.readLine()) != null) {
                        textArea.append("\n" + line);
                    }

                    br.close();
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}			
		}
        else if (command.equals("Save")) {
			JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));            

            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();

				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(f, false));
                     
                    textArea.write(bw);                  
					bw.close();
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
        else if (command.equals("Quit")) {
			System.exit(0);
		}
		else if (command.equals("Cut")) {
			textArea.cut();
		}
		else if (command.equals("Copy")) {
			textArea.copy();
		}
		else if (command.equals("Paste")) {
			textArea.paste();
		}
        else if (command.equals("Select All")) {
			textArea.selectAll();
		}
        else if (command.equals("Undo")) {
			try {
                textAreaUndoManager.undo();
            } catch (CannotUndoException ex) {}
		}
        else if (command.equals("Redo")) {
			try {
                textAreaUndoManager.redo();
            } catch (CannotRedoException ex) {}
		}
        else if (command.equals("Find")) {
			searchPanel.setVisible(true);
            searchField.requestFocus();
		}
        else if (command.equals("Next")) {
			Pattern pattern = Pattern.compile(searchField.getText(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(textArea.getText());
            boolean found = matcher.find((textArea.getSelectedText() == null) ? 0 : textArea.getSelectionEnd());            
            if(found){
                textArea.requestFocus();
                textArea.select(matcher.start(), matcher.end());
            } else { textArea.setSelectionEnd(0); }
		}
        else if (command.equals("Close")) {
			searchPanel.setVisible(false);
		}
    }

    public static void main(String args[])
	{
		new TEditor();
	}
}