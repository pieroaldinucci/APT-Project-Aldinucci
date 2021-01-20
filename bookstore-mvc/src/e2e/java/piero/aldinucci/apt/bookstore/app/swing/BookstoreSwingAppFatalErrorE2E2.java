package piero.aldinucci.apt.bookstore.app.swing;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.exception.WaitTimedOutError;
import org.assertj.swing.finder.FrameFinder;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class BookstoreSwingAppFatalErrorE2E2 extends AssertJSwingJUnitTestCase{
	
	@Override
	protected void onSetUp() throws Exception {

	}
	
	/*This is not going to be a very useful test... 
	 * it was made just for learning purposes and should be removed.
	 */
	
	@Test
	public void test_fatal_error() {
		FrameFinder findFrame =  WindowFinder.findFrame(
				new GenericTypeMatcher<JFrame>(JFrame.class) {
			
						@Override
						protected boolean isMatching(JFrame frame) {
							return frame.getTitle().equals("Bookstore View") && frame.isShowing();
						}
					});
		Robot robot = robot();
		
		assertThatCode(() -> 
			application("piero.aldinucci.apt.bookstore.app.swing.BookstoreSwingApp").start()
			).doesNotThrowAnyException();
		
		assertThatThrownBy(() -> findFrame.using(robot))
			.isInstanceOf(WaitTimedOutError.class);
	}
}
