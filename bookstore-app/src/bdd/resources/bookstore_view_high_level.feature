Feature: Bookstore View High Level
  Specification of the behaviour of the Bookstore View

  Background: 
    Given The database contains a few authors
    And The database contains a few books wrote by those authors
    And The application starts

  Scenario: Add a new author
    Given The "Authors" view is shown
    And The user provides the name of the new author
    When The user click the "Add" button
    Then The list constains the new author

  Scenario: Book view can open Compose Books view
    Given The "Books" view is shown
    When The user click the "New" button
    Then The Compose book view is shown

  Scenario: Add a new book
		Given The "Books" view is shown
		And The user click the "New" button
		When The user provides the title for the book
		And The user choose an author from the list of authors
		And The user click the "<" dialog button
		And The user click the "OK" dialog button
		Then The new book and its authors is shown in the Book View
		
	Scenario: delete a book
		Given The "Books" view is shown
		When The user select the first item from the list
		And The user click the "Delete" button
		Then The first book will be removed from the list of books  
		
	Scenario: delete an author
		Given The "Authors" view is shown
		When The user select the first item from the list
		And The user click the "Delete" button
		Then The first author will be removed from the list
		And The "Books" view is shown
		And The first author will be removed from the list