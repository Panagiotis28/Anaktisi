package lucene;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.FSDirectory;

import javax.swing.JScrollPane;
import javax.swing.JButton;

public class GUI {

	private static GUI singletonInstance = null;
	private JFrame frame;
	private JTextField authorsTextField;
	private Searcher searcher;
	private JTextField contensTextfield;
	private JTextArea textArea;
	private JTextField abstractTextField;
	private JTextField bodyTextTextField;
	private JTextField titleTextField;
	private JTextField combineTextField;
	private JMenu historyMenu;

	public static void main(String[] args) throws IOException {
		txtGenerator txt = new txtGenerator();
		txt.generateTxt();

		Indexer newIndexer = new Indexer();
		newIndexer.initIndex("C:\\Users\\Panagiotis\\Desktop\\anak\\indexData");
		newIndexer.addDocuments();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = getSingletonView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static GUI getSingletonView() {
		if (singletonInstance == null)
			singletonInstance = new GUI();
		return singletonInstance;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public GUI() {
		initialize();
	}

	public void passValuesAndSearch(String field, JTextField textField) {
		try {
			searcher = new Searcher();
			String split[] = textField.getText().split("\\s+");
			if (split.length == 1) {
				Searcher.searchIndex(field, textField.getText());
			} else {
				
				searcher.searchPhraseQuery(field, textField.getText());
			}

			if (Searcher.getCounter() != 0) {
				while (JOptionPane.showConfirmDialog(null, "Do you want to print more Results?", "More Results?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

					Searcher.searchIndex(field, textField.getText());
					if (Searcher.getCounter() == 0) {
						break;
					}

				}
				textField.setText("");
				Searcher.setCounter(0);
			}
			textField.setText("");

		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this window?", "Close Window?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						FileUtils.cleanDirectory(Paths.get("C:\\Users\\Panagiotis\\Desktop\\anak\\indexData").toFile());
					} catch (IOException e) {

						e.printStackTrace();
					}
					System.exit(0);
				}
			}
		});

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu SearchWholeFile = new JMenu("Search whole file");
		menuBar.add(SearchWholeFile);

		contensTextfield = new JTextField();
		contensTextfield.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				passValuesAndSearch("contents", contensTextfield);
			}
		});
		SearchWholeFile.add(contensTextfield);
		contensTextfield.setColumns(10);

		JMenu searchByFieldMenu = new JMenu("Search by field");
		menuBar.add(searchByFieldMenu);

		JMenu titleMenu = new JMenu("title");
		searchByFieldMenu.add(titleMenu);
		titleTextField = new JTextField();
		titleMenu.add(titleTextField);
		titleTextField.setColumns(10);
		titleTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				passValuesAndSearch("title", titleTextField);
			}

		});

		JMenu authorsMenu = new JMenu("authors");
		searchByFieldMenu.add(authorsMenu);
		authorsTextField = new JTextField();
		authorsMenu.add(authorsTextField);
		authorsTextField.setColumns(10);
		authorsTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				passValuesAndSearch("authors", authorsTextField);
			}

		});

		JMenu abstractMenu = new JMenu("abstract");
		searchByFieldMenu.add(abstractMenu);
		abstractTextField = new JTextField();
		abstractMenu.add(abstractTextField);
		abstractTextField.setColumns(10);
		abstractTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				passValuesAndSearch("abstract", abstractTextField);
			}

		});

		JMenu bodytextMenu = new JMenu("bodyText");
		searchByFieldMenu.add(bodytextMenu);
		bodyTextTextField = new JTextField();
		bodytextMenu.add(bodyTextTextField);
		bodyTextTextField.setColumns(10);

		bodyTextTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				passValuesAndSearch("bodyText", bodyTextTextField);
			}

		});

		JMenu combineMenu = new JMenu("Combine Fields");
		menuBar.add(combineMenu);

		JCheckBoxMenuItem titleBox = new JCheckBoxMenuItem("title");
		combineMenu.add(titleBox);
		titleBox.setUI(new StayOpenCheckBoxMenuItemUI());

		JCheckBoxMenuItem authorsBox = new JCheckBoxMenuItem("authors");
		combineMenu.add(authorsBox);
		authorsBox.setUI(new StayOpenCheckBoxMenuItemUI());

		JCheckBoxMenuItem abstractBox = new JCheckBoxMenuItem("abstract");
		combineMenu.add(abstractBox);
		abstractBox.setUI(new StayOpenCheckBoxMenuItemUI());

		JCheckBoxMenuItem bodyTextBox = new JCheckBoxMenuItem("bodyText");
		combineMenu.add(bodyTextBox);
		bodyTextBox.setUI(new StayOpenCheckBoxMenuItemUI());

		combineTextField = new JTextField();
		combineMenu.add(combineTextField);
		combineTextField.setColumns(10);

		combineTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searcher = new Searcher();
				if (titleBox.isSelected()) {
					if (authorsBox.isSelected()) {
						try {
							resultsWindow("title", "authors");
							titleBox.setSelected(false);
							authorsBox.setSelected(false);
						} catch (InvalidTokenOffsetsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (abstractBox.isSelected()) {
						try {
							resultsWindow("title", "abstract");
							titleBox.setSelected(false);
							abstractBox.setSelected(false);
						} catch (InvalidTokenOffsetsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

					} else if (bodyTextBox.isSelected()) {
						try {
							resultsWindow("bodyText", "abstract");
							titleBox.setSelected(false);
							bodyTextBox.setSelected(false);
						} catch (InvalidTokenOffsetsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

					}

				} else if (authorsBox.isSelected()) {
					if (abstractBox.isSelected()) {
						try {
							resultsWindow("authors", "abstract");
							authorsBox.setSelected(false);
							abstractBox.setSelected(false);
						} catch (InvalidTokenOffsetsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

					} else if (bodyTextBox.isSelected()) {
						try {
							resultsWindow("authors", "bodyText");
							authorsBox.setSelected(false);
							bodyTextBox.setSelected(false);
						} catch (InvalidTokenOffsetsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

					}

				} else if (abstractBox.isSelected()) {
					if (bodyTextBox.isSelected()) {
						try {
							resultsWindow("abstract", "bodyText");
							abstractBox.setSelected(false);
							bodyTextBox.setSelected(false);
						} catch (InvalidTokenOffsetsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

				}
			}

		});

		historyMenu = new JMenu("History");
		menuBar.add(historyMenu);

		JButton groupButton = new JButton("GroupResults");
		groupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Map<String, ArrayList<String>> map = new HashMap<String,ArrayList <String>>();
				IndexReader ireader;
				try {
					ireader = DirectoryReader.open(FSDirectory.open(Paths.get("C:\\Users\\Panagiotis\\Desktop\\anak\\indexData")));
					IndexSearcher isearcher = new IndexSearcher(ireader);
					
					try {
						for (int i = 0; i < Searcher.getHits().length; i++) {
							Document hitDoc;
							try {
								hitDoc = isearcher.doc(Searcher.getHits()[i].doc);
								ArrayList<String> list;
								if(map.containsKey(hitDoc.get("published_time"))){
								   
								    list = map.get(hitDoc.get("published_time"));
								    list.add(hitDoc.get("filename"));
								} else {
								    list = new ArrayList<String>();
								    list.add(hitDoc.get("filename"));
								    map.put(hitDoc.get("published_time"), list);
								}

							} catch (IOException e) {
								e.printStackTrace();
							}
							
						}
						
					
						ireader.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
				for (String key: map.keySet()) {
					textArea.append("published_time: "+key+'\n');
					for(String value:map.get(key)) {
						textArea.append(value+'\n');
					}
					textArea.append("\n");
				}
				
				
			}

		});
		menuBar.add(groupButton);

		textArea = new JTextArea();
		textArea.setBounds(56, 52, 477, 324);
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(56, 52, 477, 324);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		frame.getContentPane().add(scrollPane);

	}

	public void resultsWindow(String field1, String field2) throws InvalidTokenOffsetsException {
		try {
			searcher.searchBooleanIndex(field1, field2, combineTextField.getText());
			if (Searcher.getCounter() == 0) {
				combineTextField.setText("");
				Searcher.setCounter(0);
				return;
			} else {
				while (JOptionPane.showConfirmDialog(null, "Do you want to print more Results?", "More Results?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

					searcher.searchBooleanIndex(field1, field2, combineTextField.getText());
					if (Searcher.getCounter() == 0) {
						break;
					}

				}
				combineTextField.setText("");
				Searcher.setCounter(0);
			}

		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public JMenu getHistoryMenu() {
		return historyMenu;
	}

	public void setHistoryMenu(JMenu historyMenu) {
		this.historyMenu = historyMenu;
	}

}
