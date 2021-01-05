package piero.aldinucci.apt.bookstore.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

public class ComposeBookSwingView extends JDialog implements ComposeBookView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient BookstoreController controller;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	private DefaultListModel<Author> modelAvailableAuthors;
	private DefaultListModel<Author> modelBookAuthors;
	private JButton addAuthorButton;
	private JButton removeAuthorButton;
	private JList<Author> availableAuthors;
	private JList<Author> bookAuthors;

	private JButton okButton;

	/**
	 * Create the dialog.
	 */
	public ComposeBookSwingView() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gblcontentPanel = new GridBagLayout();
		gblcontentPanel.columnWidths = new int[] { 30, 100, 30, 100, 0 };
		gblcontentPanel.rowHeights = new int[] { 30, 30, 30, 0, 0 };
		gblcontentPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gblcontentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gblcontentPanel);

		createTextField();
		createBookAuthorsList();
		createAddAuthorButton();
		createAvailableAuthorsList();
		createDescriptionLabels();
		createRemoveAuthorButton();
		createExitButtonPane();
	}

	private void createExitButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		createOKButton(buttonPane);
		createCancelButton(buttonPane);
	}

	private void createCancelButton(JPanel buttonPane) {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}

	private void createOKButton(JPanel buttonPane) {
		okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			Book book = new Book(null, textField.getText(), new HashSet<>());
			Arrays.stream(modelBookAuthors.toArray()).forEach(a -> book.getAuthors().add((Author) a));
			setVisible(false);
			controller.newBook(book);
		});
		okButton.setEnabled(false);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	}

	private void createRemoveAuthorButton() {
		removeAuthorButton = new JButton(">");
		removeAuthorButton.addActionListener(e -> {
			Author author = bookAuthors.getSelectedValue();
			modelBookAuthors.removeElement(author);
			modelAvailableAuthors.addElement(author);
		});
		removeAuthorButton.setEnabled(false);
		removeAuthorButton.setName("buttonRemoveAuthor");
		GridBagConstraints gbcbutton = new GridBagConstraints();
		gbcbutton.insets = new Insets(0, 0, 5, 5);
		gbcbutton.gridx = 2;
		gbcbutton.gridy = 2;
		contentPanel.add(removeAuthorButton, gbcbutton);
	}

	private void createDescriptionLabels() {
		JLabel lblTitle = new JLabel("Title");
		GridBagConstraints gbclblTitle = new GridBagConstraints();
		gbclblTitle.insets = new Insets(0, 0, 5, 5);
		gbclblTitle.anchor = GridBagConstraints.EAST;
		gbclblTitle.gridx = 0;
		gbclblTitle.gridy = 0;
		contentPanel.add(lblTitle, gbclblTitle);

		JLabel lblBook = new JLabel("Book's");
		GridBagConstraints gbclblBook = new GridBagConstraints();
		gbclblBook.anchor = GridBagConstraints.SOUTH;
		gbclblBook.insets = new Insets(0, 0, 5, 5);
		gbclblBook.gridx = 0;
		gbclblBook.gridy = 1;
		contentPanel.add(lblBook, gbclblBook);

		JLabel lblAuthors = new JLabel("authors");
		GridBagConstraints gbclblAuthors = new GridBagConstraints();
		gbclblAuthors.anchor = GridBagConstraints.NORTH;
		gbclblAuthors.insets = new Insets(0, 0, 5, 5);
		gbclblAuthors.gridx = 0;
		gbclblAuthors.gridy = 2;
		contentPanel.add(lblAuthors, gbclblAuthors);
	}

	private void createAvailableAuthorsList() {
		modelAvailableAuthors = new DefaultListModel<>();
		availableAuthors = new JList<>();
		availableAuthors
				.addListSelectionListener(e -> addAuthorButton.setEnabled(!availableAuthors.isSelectionEmpty()));
		availableAuthors.setModel(modelAvailableAuthors);
		availableAuthors.setName("AvailableAuthors");
		availableAuthors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbcavailableAuthors = new GridBagConstraints();
		gbcavailableAuthors.gridheight = 3;
		gbcavailableAuthors.fill = GridBagConstraints.BOTH;
		gbcavailableAuthors.gridx = 3;
		gbcavailableAuthors.gridy = 1;
		contentPanel.add(availableAuthors, gbcavailableAuthors);
	}

	private void createAddAuthorButton() {
		addAuthorButton = new JButton("<");
		addAuthorButton.addActionListener(e -> {
			Author author = availableAuthors.getSelectedValue();
			modelAvailableAuthors.removeElement(author);
			modelBookAuthors.addElement(author);
		});
		addAuthorButton.setName("buttonAddAuthor");
		addAuthorButton.setEnabled(false);
		GridBagConstraints gbcbutton = new GridBagConstraints();
		gbcbutton.insets = new Insets(0, 0, 5, 5);
		gbcbutton.gridx = 2;
		gbcbutton.gridy = 1;
		contentPanel.add(addAuthorButton, gbcbutton);
	}

	private void createBookAuthorsList() {
		modelBookAuthors = new DefaultListModel<>();
		bookAuthors = new JList<>();
		bookAuthors.addListSelectionListener(e -> removeAuthorButton.setEnabled(!bookAuthors.isSelectionEmpty()));
		bookAuthors.setModel(modelBookAuthors);
		bookAuthors.setName("BookAuthors");
		bookAuthors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbcbookAuthors = new GridBagConstraints();
		gbcbookAuthors.insets = new Insets(0, 0, 0, 5);
		gbcbookAuthors.gridheight = 3;
		gbcbookAuthors.fill = GridBagConstraints.BOTH;
		gbcbookAuthors.gridx = 1;
		gbcbookAuthors.gridy = 1;
		contentPanel.add(bookAuthors, gbcbookAuthors);
	}

	private void createTextField() {
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				okButton.setEnabled(!textField.getText().trim().isEmpty());
			}
		});
		textField.setName("titleTextField");
		GridBagConstraints gbctextField = new GridBagConstraints();
		gbctextField.gridwidth = 3;
		gbctextField.insets = new Insets(0, 0, 5, 0);
		gbctextField.fill = GridBagConstraints.HORIZONTAL;
		gbctextField.gridx = 1;
		gbctextField.gridy = 0;
		contentPanel.add(textField, gbctextField);
		textField.setColumns(10);
	}

	DefaultListModel<Author> getModelAvailableAuthors() {
		return modelAvailableAuthors;
	}

	DefaultListModel<Author> getModelBookAuthors() {
		return modelBookAuthors;
	}


	@Override
	public void composeNewBook(List<Author> authors) {
		textField.setText(null);
		modelAvailableAuthors.clear();
		modelBookAuthors.clear();
		authors.stream().forEach(a -> modelAvailableAuthors.addElement(a));
		setVisible(true);
	}

	public void setController(BookstoreController controller) {
		this.controller = controller;
	}

}
