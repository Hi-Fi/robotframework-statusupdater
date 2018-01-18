package com.github.hi_fi.statusupdater.qc.infrastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class TestInstances {

	private List<TestInstance> testInstances = new ArrayList<TestInstance>();

	public void addInstance(TestInstance testInstance) {
		this.testInstances.add(testInstance);
	}

	public TestInstance getInstance(final String testName) {
		Predicate condition = new Predicate() {
			public boolean evaluate(Object sample) {
				return ((TestInstance) sample).getName().startsWith(testName);
			}
		};
		Collection<TestInstance> object = CollectionUtils.select(this.testInstances, condition);
		if (object.isEmpty()) {
			throw new RuntimeException("No test found with given name " + testName);
		} else {
			return object.iterator().next();
		}
	}
	
	public TestInstance getInstance(final int testId) {
		Predicate condition = new Predicate() {
			public boolean evaluate(Object sample) {
				return ((TestInstance) sample).getTestId()==testId;
			}
		};
		Collection<TestInstance> object = CollectionUtils.select(this.testInstances, condition);
		if (object.isEmpty()) {
			throw new RuntimeException("No test found with given test case id " + testId);
		} else {
			return object.iterator().next();
		}
	}

	public int size() {
		return testInstances.size();
	}
}
