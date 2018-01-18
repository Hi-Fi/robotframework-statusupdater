package com.github.hi_fi.statusupdater.utils;

import org.apache.commons.lang3.StringUtils;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class Robot {

	public static String getRobotVariable(String variableName) {
		return getRobotVariable(variableName, null);
	}

	public static String getRobotVariable(String variableName, String defaultValue) {
		String robotVarName = String.format("\\${%s}", variableName);
		String variableValue = defaultValue;
		try {
			variableValue = ((PyString) pythonInterpreter.get()
					.eval("BuiltIn().get_variable_value('" + robotVarName + "','" + defaultValue + "')")).asString();
		} catch (Exception e) {
			//just preventing error to be thrown up
		}
		return variableValue;
	}

	public static PyList getRobotListVariable(String variableName) {
		String robotVarName = String.format("\\${%s}", variableName);
		PyObject variable = pythonInterpreter.get().eval("BuiltIn().get_variable_value('" + robotVarName + "')");
		Logger.logError(variable.getType());
		Logger.logError(variable);
		if (variable.getType() == PyList.TYPE) {
			return (PyList) variable;
		} else {
			return (PyList) pythonInterpreter.get().eval("BuiltIn().create_list()");
		}
	}

	public static void setGlobalListVariable(String variableName, String... content) {
		String robotVarName = String.format("\\${%s}", variableName);
		if (content.length > 0) {
			String contentString = StringUtils.join(content, "','");
			pythonInterpreter.get().eval("BuiltIn().set_global_variable('" + robotVarName + "', BuiltIn().create_list('"
					+ contentString + "')");
		} else {
			Logger.logError("Create empty list");
			pythonInterpreter.get().eval("BuiltIn().set_global_variable('" + robotVarName + "')");
			pythonInterpreter.get().eval("BuiltIn().set_suite_variable('" + robotVarName + "')");
			pythonInterpreter.get().eval("BuiltIn().set_test_variable('" + robotVarName + "')");
		}
	}

	public static void setRobotTestVariable(String variableName, Object variableValue) {
		String robotVarName = String.format("\\${%s}", variableName);
		pythonInterpreter.get().eval("BuiltIn().set_test_variable('" + robotVarName + "', '" + variableValue + "')");
	}

	protected static ThreadLocal<PythonInterpreter> pythonInterpreter = new ThreadLocal<PythonInterpreter>() {

		@Override
		protected PythonInterpreter initialValue() {
			PythonInterpreter pythonInterpreter = new PythonInterpreter();
			pythonInterpreter.exec("from robot.libraries.BuiltIn import BuiltIn");
			return pythonInterpreter;
		}
	};

}
