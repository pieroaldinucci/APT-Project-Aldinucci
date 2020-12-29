package piero.aldinucci.apt.bookstore.view.swing;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JTextField;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.view.AuthorView;

import javax.swing.JButton;
import javax.swing.JList;

public class AuthorSwingView extends JPanel implements AuthorView{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public AuthorSwingView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{27, 0, 0, 0, 21, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		createNameLabel();
		createTextField();
		createAddButton();
		createList();
		createDeleteButton();
		createErrorLabel();

	}

	private void createErrorLabel() {
		JLabel label = new JLabel(" ");
		label.setName("ErrorLabel");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.gridx = 1;
		gbc_label.gridy = 4;
		add(label, gbc_label);
	}

	private void createDeleteButton() {
		JButton btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setName("DeleteAuthor");
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete.gridx = 1;
		gbc_btnDelete.gridy = 3;
		add(btnDelete, gbc_btnDelete);
	}

	private void createList() {
		JList list = new JList();
		list.setName("AuthorList");
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.insets = new Insets(0, 0, 5, 5);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 1;
		gbc_list.gridy = 2;
		add(list, gbc_list);
	}

	private void createAddButton() {
		JButton btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.setName("AddAuthor");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 1;
		gbc_btnAdd.gridy = 1;
		add(btnAdd, gbc_btnAdd);
	}

	private void createTextField() {
		textField = new JTextField();
		textField.setName("NameTextField");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		add(textField, gbc_textField);
		textField.setColumns(10);
	}

	private void createNameLabel() {
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
	}

	@Override
	public void showAllAuthors(List<Author> authors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void authorAdded(Author author) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void authorRemoved(Author author) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String message, Author author) {
		// TODO Auto-generated method stub
		
	}

}
