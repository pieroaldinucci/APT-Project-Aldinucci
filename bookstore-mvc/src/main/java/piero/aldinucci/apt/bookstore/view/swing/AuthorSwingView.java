package piero.aldinucci.apt.bookstore.view.swing;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashSet;
import java.util.List;

import javax.swing.JTextField;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.view.AuthorView;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.awt.Color;

public class AuthorSwingView extends JPanel implements AuthorView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient BookstoreController controller;
	private JTextField nameTextField;
	private JButton btnAddAuthor;
	private DefaultListModel<Author> authorListModel;

	private JList<Author> authorList;

	private JButton btnDeleteAuthor;

	private JLabel errorLabel;

	/**
	 * Create the panel.
	 */
	@Inject
	public AuthorSwingView(@Assisted BookstoreController controller) {
		this.controller = controller;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 20, 200, 30, 20 };
		gridBagLayout.rowHeights = new int[] { 27, 20, 90, 30, 21, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		createNameLabel();
		createTextField();
		createAddButton();
		createList();
		createDeleteButton();
		createErrorLabel();

	}

	private void createErrorLabel() {
		errorLabel = new JLabel(" ");
		errorLabel.setForeground(Color.RED);
		errorLabel.setName("AuthorErrorLabel");
		GridBagConstraints gbclabel = new GridBagConstraints();
		gbclabel.insets = new Insets(0, 0, 0, 5);
		gbclabel.gridx = 1;
		gbclabel.gridy = 4;
		add(errorLabel, gbclabel);
	}

	private void createDeleteButton() {
		btnDeleteAuthor = new JButton("Delete");
		btnDeleteAuthor.addActionListener(e -> controller.deleteAuthor(authorList.getSelectedValue()));
		btnDeleteAuthor.setEnabled(false);
		btnDeleteAuthor.setName("DeleteAuthor");
		GridBagConstraints gbcbtnDelete = new GridBagConstraints();
		gbcbtnDelete.insets = new Insets(0, 0, 5, 5);
		gbcbtnDelete.gridx = 1;
		gbcbtnDelete.gridy = 3;
		add(btnDeleteAuthor, gbcbtnDelete);
	}

	private void createList() {
		authorListModel = new DefaultListModel<>();
		authorList = new JList<>(authorListModel);
		authorList.addListSelectionListener(e -> btnDeleteAuthor.setEnabled(authorList.getSelectedIndex() != -1));
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		authorList.setName("AuthorList");
		GridBagConstraints gbclist = new GridBagConstraints();
		gbclist.insets = new Insets(0, 0, 5, 5);
		gbclist.fill = GridBagConstraints.BOTH;
		gbclist.gridx = 1;
		gbclist.gridy = 2;
		add(authorList, gbclist);
	}

	private void createAddButton() {
		btnAddAuthor = new JButton("Add");
		btnAddAuthor.addActionListener(e -> controller.newAuthor(new Author(null, nameTextField.getText(), new HashSet<>())));
		btnAddAuthor.setEnabled(false);
		btnAddAuthor.setName("AddAuthor");
		GridBagConstraints gbcbtnAdd = new GridBagConstraints();
		gbcbtnAdd.insets = new Insets(0, 0, 5, 5);
		gbcbtnAdd.gridx = 1;
		gbcbtnAdd.gridy = 1;
		add(btnAddAuthor, gbcbtnAdd);
	}

	private void createTextField() {
		nameTextField = new JTextField();
		nameTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddAuthor.setEnabled(!nameTextField.getText().trim().isEmpty());
			}
		});
		nameTextField.setName("NameTextField");
		GridBagConstraints gbctextField = new GridBagConstraints();
		gbctextField.insets = new Insets(0, 0, 5, 5);
		gbctextField.fill = GridBagConstraints.HORIZONTAL;
		gbctextField.gridx = 1;
		gbctextField.gridy = 0;
		add(nameTextField, gbctextField);
		nameTextField.setColumns(10);
	}

	private void createNameLabel() {
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbclblName = new GridBagConstraints();
		gbclblName.anchor = GridBagConstraints.EAST;
		gbclblName.insets = new Insets(0, 0, 5, 5);
		gbclblName.gridx = 0;
		gbclblName.gridy = 0;
		add(lblName, gbclblName);
	}

	@Override
	public void showAllAuthors(List<Author> authors) {
		authorListModel.clear();
		authors.stream().forEach(a -> authorListModel.addElement(a));

	}

	@Override
	public void authorAdded(Author author) {
		authorListModel.addElement(author);
		clearErrorLabel();

	}

	@Override
	public void authorRemoved(Author author) {
		authorListModel.removeElement(author);
		clearErrorLabel();
	}

	@Override
	public void showError(String message, Author author) {
		errorLabel.setText(message + ": " + author);

	}

	DefaultListModel<Author> getAuthorListModel() {
		return authorListModel;
	}

	private void clearErrorLabel() {
		errorLabel.setText("");
	}

}
