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
package org.jbpm.test.activity.forkjoin;

import java.util.HashMap;

import org.jbpm.api.Execution;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.history.HistoryProcessInstance;
import org.jbpm.api.task.Task;
import org.jbpm.test.JbpmTestCase;

/**
 * 
 * @author Maciej Swiderski
 *
 */
public class ForkJoinWithMutliplicityTest extends JbpmTestCase {

  public void testForkWithMultiplicityOne() {
    deployJpdlXmlString(""
        + "<process name='ForkJoingMultiplicity' xmlns='http://jbpm.org/4.3/jpdl'>"
        + "   <start g='179,17,32,29' name='start1'>"
        + "      <transition g='-43,-18' name='to fork1' to='fork1'/>"
        + "   </start>"
        + "   <fork g='185,95,49,50' name='fork1' >"
        + "      <transition name='left' to='task1' g='-44,-18'/>"
        + "      <transition name='right' to='task2' g='-44,-18'/>"
        + "   </fork>"
        + "   <task name='task1' g='90,177,73,44' assignee='mike'>"
        + "      <transition name='to join1' to='join1' g='-43,-18'/>"
        + "   </task>"
        + "   <task name='task2' g='90,177,73,44' assignee='alex'>"
        + "      <transition name='to join1' to='join1' g='-43,-18'/>"
        + "   </task>"
        + "   <join name='join1' g='192,511,57,44' multiplicity='1'>"
        + "      <transition name='to Big car' to='Big car' g='-42,-18'/>"
        + "   </join>"
        + "   <state name='Big car' > "
        + "     <transition name='to end1' to='end1' g='-43,-18'/>"
        + "   </state> "
        + "   <end g='193,606,38,33' name='end1'/>"
        + "</process>");

    ProcessInstance processInstance = executionService.startProcessInstanceByKey("ForkJoingMultiplicity");
    
    Task taskAlex = taskService.createTaskQuery().assignee("alex").uniqueResult();
    assertNotNull(taskAlex);
    assertEquals("task2", taskAlex.getActivityName());
    
    taskService.completeTask(taskAlex.getId());
    
    processInstance = executionService.findProcessInstanceById(processInstance.getId());


    executionService.signalExecutionById(processInstance.getId());
    

    HistoryProcessInstance history = historyService
        .createHistoryProcessInstanceQuery()
        .processInstanceId(processInstance.getId())
        .uniqueResult();

    assertNotNull(history);
    assertEquals(ProcessInstance.STATE_ENDED, history.getState());
    assertEquals("end1", history.getEndActivityName());
  }
  
  public void testForkWithMultiplicityAsExpression() {
    deployJpdlXmlString(""
        + "<process name='ForkJoingMultiplicity' xmlns='http://jbpm.org/4.3/jpdl'>"
        + "   <start g='179,17,32,29' name='start1'>"
        + "      <transition g='-43,-18' name='to fork1' to='fork1'/>"
        + "   </start>"
        + "   <fork g='185,95,49,50' name='fork1' >"
        + "      <transition name='left' to='task1' g='-44,-18'/>"
        + "      <transition name='right' to='task2' g='-44,-18'/>"
        + "   </fork>"
        + "   <task name='task1' g='90,177,73,44' assignee='mike'>"
        + "      <transition name='to join1' to='join1' g='-43,-18'/>"
        + "   </task>"
        + "   <task name='task2' g='90,177,73,44' assignee='alex'>"
        + "      <transition name='to join1' to='join1' g='-43,-18'/>"
        + "   </task>"
        + "   <join name='join1' g='192,511,57,44' multiplicity='#{multiplicity}'>"
        + "      <transition name='to Big car' to='Big car' g='-42,-18'/>"
        + "   </join>"
        + "   <state name='Big car' > "
        + "     <transition name='to end1' to='end1' g='-43,-18'/>"
        + "   </state> "
        + "   <end g='193,606,38,33' name='end1'/>"
        + "</process>");

    HashMap<String, Integer> variables = new HashMap<String, Integer>();
    variables.put("multiplicity", 1);
    ProcessInstance processInstance = executionService.startProcessInstanceByKey("ForkJoingMultiplicity", variables);
    
    Task taskAlex = taskService.createTaskQuery().assignee("alex").uniqueResult();
    assertNotNull(taskAlex);
    assertEquals("task2", taskAlex.getActivityName());
    
    taskService.completeTask(taskAlex.getId());
    
    processInstance = executionService.findProcessInstanceById(processInstance.getId());


    executionService.signalExecutionById(processInstance.getId());
    

    HistoryProcessInstance history = historyService
        .createHistoryProcessInstanceQuery()
        .processInstanceId(processInstance.getId())
        .uniqueResult();

    assertNotNull(history);
    assertEquals(ProcessInstance.STATE_ENDED, history.getState());
    assertEquals("end1", history.getEndActivityName());
  }
}
