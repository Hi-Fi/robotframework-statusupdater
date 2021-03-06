package com.github.hi_fi.statusupdater.qc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;

import com.github.hi_fi.httpclient.RestClient;
import com.github.hi_fi.statusupdater.interfaces.ISearch;
import com.github.hi_fi.statusupdater.qc.infrastructure.TestInstance;
import com.github.hi_fi.statusupdater.qc.infrastructure.TestInstances;
import com.github.hi_fi.statusupdater.qc.infrastructure.Entities.Entity;
import com.github.hi_fi.statusupdater.qc.infrastructure.Entities.Entity.Fields.Field;
import com.github.hi_fi.statusupdater.utils.Configuration;
import com.github.hi_fi.statusupdater.utils.ResponseParser;
import com.github.hi_fi.statusupdater.utils.Robot;

public class Search implements ISearch{

	private static TestInstances qcTestInstances = new TestInstances();
	private static String loadedTestSet = "-1";
	
	public Search() {
		new Authentication();
	}
	
	public void loadIdsForExecution(String identifier) {
		if (qcTestInstances.size() < 1) {
			this.loadTestInstancesList();
		}
		
		TestInstance instance = qcTestInstances.getInstance(identifier);
		Robot.setRobotTestVariable("testInstanceId", instance.getId());
	}

	public TestInstances getTestInstancesList() {
		String currentTestSet = Robot.getRobotVariable("QC_TESTSET", "-1");
		if (qcTestInstances.size() < 1 || !loadedTestSet.equals(currentTestSet)) {
			this.loadTestInstancesList();
		}
		return qcTestInstances;
	}

	public void loadTestInstancesList() {
		this.loadTestInstancesList(Robot.getRobotVariable("QC_TESTSET", "-1"), Robot.getRobotVariable("QC_DOMAIN"),
				Robot.getRobotVariable("QC_PROJECT"));
	}

	public TestInstances loadTestInstancesList(String qctestSet, String qcDomain, String qcProject) {
		String URI = Configuration.url + "/qcbin/rest/domains/" + qcDomain + "/projects/" + qcProject
				+ "/test-instances";
				
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "{contains-test-set.id[" + qctestSet + "]}");
        parameters.put("fields", "id,test-id");
        parameters.put("page-size", "max");
        RestClient rc = new RestClient();
        rc.makeGetRequest("QC", URI, new HashMap<String, String>(), parameters, true);
        HttpResponse response = rc.getSession("QC").getResponse();

		List<Entity> entities = ResponseParser.parseResponseToEntities(response);
		List<Integer> testIds = new ArrayList<Integer>();
		for (Entity entity : entities) {
			TestInstance testInstance = new TestInstance();
			for (Field field : entity.getFields().getField()) {
				if (field.getName().equals("id")) {
					testInstance.setId(field.getValue());
				} else if (field.getName().equals("test-id")) {
					testInstance.setTestId(field.getValue());
					testIds.add(Integer.parseInt(field.getValue()));
				}
			}
			qcTestInstances.addInstance(testInstance);

		}
		
		//Test case name loading, as test instances doesn't give names with previous search
		if (qcTestInstances.size() > 0) {
			URI = Configuration.url + "/qcbin/rest/domains/" + qcDomain + "/projects/" + qcProject + "/tests";
			parameters = new HashMap<String, String>();
	        parameters.put("query", "{id[" + StringUtils.join(testIds, " or ") + "]}");
	        parameters.put("fields", "id,name");
	        parameters.put("page-size", "max");
	        rc.makeGetRequest("QC", URI, new HashMap<String, String>(), parameters, true);
	        response = rc.getSession("QC").getResponse();
			entities = ResponseParser.parseResponseToEntities(response);
			for (Entity entity : entities) {
				int id = -1;
				String name = "";
				for (Field field : entity.getFields().getField()) {
					if (field.getName().equals("id")) {
						id = Integer.parseInt(field.getValue());
					} else if (field.getName().equals("name")) {
						name = field.getValue();
					}
				}
				qcTestInstances.getInstance(id).setName(name);
			}
		}

		return qcTestInstances;
	}
}