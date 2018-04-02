import java.util.Map;

import org.robotframework.javalib.library.AnnotationLibrary;

import com.github.hi_fi.statusupdater.keywords.Listener;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.Logger;

public class StatusUpdateLibrary extends AnnotationLibrary  {
	
    public static final Listener ROBOT_LIBRARY_LISTENER = new Listener();
    public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

    public StatusUpdateLibrary () {
        super("com/github/hi_fi/statusupdater/keywords/**");
    }
}
