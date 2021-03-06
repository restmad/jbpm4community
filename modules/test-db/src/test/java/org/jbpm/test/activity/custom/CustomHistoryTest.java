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
package org.jbpm.test.activity.custom;

import java.util.List;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.activity.ActivityBehaviour;
import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.history.HistoryActivityInstance;
import org.jbpm.test.JbpmTestCase;

/**
 * 
 * @author Maciej Swiderski
 *
 */
public class CustomHistoryTest extends JbpmTestCase {
  
  public static class MyCustomAutomatic implements ActivityBehaviour {
    private static final long serialVersionUID = 1L;
    public void execute(ActivityExecution execution) throws Exception {
      execution.takeDefaultTransition();
    }
  }

  public void testCustomAutomaticClass() {
    deployJpdlXmlString(
        "<process name='CustomClass'>" +
        "  <start>" +
        "    <transition to='c' />" +
        "  </start>" +
        "  <custom name='c' class='"+MyCustomAutomatic.class.getName()+"'>" +
        "    <transition to='wait' />" +
        "  </custom>" +
        "  <state name='wait'>" +
        "    <transition to='end' />" +
        "  </state>" +
        "  <end name='end'/>" +
        "</process>"
    );
    
    ProcessInstance processInstance = executionService.startProcessInstanceByKey("CustomClass");

    assertTrue(processInstance.findActiveActivityNames().contains("wait"));
    
    processInstance = executionService.signalExecutionById(processInstance.getId());
    
    assertProcessInstanceEnded(processInstance);
    
    List<HistoryActivityInstance> histActivities = historyService
      .createHistoryActivityInstanceQuery()
      .processInstanceId(processInstance.getId())
      .orderAsc("id").list();
    
    assertEquals(2, histActivities.size());
    assertEquals("c", histActivities.get(0).getActivityName());
    assertEquals("wait", histActivities.get(1).getActivityName());
  }

}
