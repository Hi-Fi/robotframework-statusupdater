import java.util.Map;

import org.robotframework.javalib.library.AnnotationLibrary;

import com.github.hi_fi.statusupdater.keywords.Listener;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;

public class StatusUpdateLibrary extends AnnotationLibrary  {
	
    public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
    public static final int ROBOT_LISTENER_API_VERSION = 2;

    public StatusUpdateLibrary () {
        super("com/github/hi_fi/statusupdater/keywords/**");
    }
    
	public void startTest(String name, Map attrs) {
		if (new Configuration().listenTestStart()) {
			new Listener().startTest(name, attrs);
		}
    }
	
	public void endTest(String name, Map attrs) {
		if (new Configuration().listenTestStop()) {
			new Listener().endTest(name, attrs);
		}
    }
}
