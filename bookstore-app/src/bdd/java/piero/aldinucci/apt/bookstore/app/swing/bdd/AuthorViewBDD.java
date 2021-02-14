package piero.aldinucci.apt.bookstore.app.swing.bdd;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources", monochrome = true)
public class AuthorViewBDD {

	@BeforeClass
	public static void setUpClass() {
		FailOnThreadViolationRepaintManager.install();
	}
}
