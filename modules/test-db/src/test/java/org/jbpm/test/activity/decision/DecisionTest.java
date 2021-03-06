/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.test.activity.decision;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.api.ProcessInstance;
import org.jbpm.test.JbpmTestCase;


/**
 * @author Tom Baeyens
 */
public class DecisionTest extends JbpmTestCase {

  public void testDecisionExpression() {
    deployJpdlXmlString(
      "<process name='Poolcar'>" +
      "  <start>" +
      "    <transition to='How far?' />" +
      "  </start>" +
      "  <decision name='How far?' expr='#{distance}'>" +
      "    <transition name='far'    to='Big car' />" +
      "    <transition name='nearby' to='Small car' />" +
      "  </decision>" +
      "  <state name='Big car' />" +
      "  <state name='Small car' />" +
      "</process>"
    );

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("distance", "far");
    ProcessInstance processInstance = executionService.startProcessInstanceByKey("Poolcar", variables);
    assertTrue(processInstance.isActive("Big car"));

    variables.put("distance", "nearby");
    processInstance = executionService.startProcessInstanceByKey("Poolcar", variables);
    assertTrue(processInstance.isActive("Small car"));
  }

  public void testDecisionWithConditions() {
    deployJpdlXmlString(
      "<process name='Poolcar'>" +
      "  <start>" +
      "    <transition to='How far?' />" +
      "  </start>" +
      "  <decision name='How far?'>" +
      "    <transition to='Big car'>" +
      "      <condition expr='#{distance &gt; 10}' />" +
      "    </transition>" +
      "    <transition to='Small car'>" +
      "      <condition expr='#{distance &gt;= 3}' />" +
      "    </transition>" +
      "    <transition to='No car' />" +
      "  </decision>" +
      "  <state name='Big car' />" +
      "  <state name='Small car' />" +
      "  <state name='No car' />" +
      "</process>"
    );

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("distance", new Integer(69));
    ProcessInstance processInstance = executionService.startProcessInstanceByKey("Poolcar", variables);
    assertTrue(processInstance.isActive("Big car"));

    variables.put("distance", new Integer(6));
    processInstance = executionService.startProcessInstanceByKey("Poolcar", variables);
    assertTrue(processInstance.isActive("Small car"));

    variables.put("distance", new Integer(2));
    processInstance = executionService.startProcessInstanceByKey("Poolcar", variables);
    assertTrue(processInstance.isActive("No car"));
  }
}
