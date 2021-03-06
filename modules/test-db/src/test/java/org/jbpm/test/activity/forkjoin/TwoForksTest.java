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

import java.util.Date;
import java.util.List;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.task.Task;
import org.jbpm.test.JbpmTestCase;

import junit.framework.TestCase;

/**
 * JBPM-2553.
 *
 * @author Huisheng Xu
 */
public class TwoForksTest extends JbpmTestCase {
    public void testTwoForks() {
        deployJpdlXmlString(""
            + "<process name='TwoForks' xmlns='http://jbpm.org/4.0/jpdl'>"
            + "   <start g='179,17,32,29' name='start1'>"
            + "      <transition g='-43,-18' name='to fork1' to='fork1'/>"
            + "   </start>"
            + "   <fork g='185,95,49,50' name='fork1'>"
            + "      <transition name='left' to='task1' g='-44,-18'/>"
            + "      <transition name='right' to='task2' g='-44,-18'/>"
            + "   </fork>"
            + "   <end g='193,606,38,33' name='end1'/>"
            + "   <task name='task1' g='90,177,73,44'>"
            + "      <transition name='to fork2' to='fork2' g='-43,-18'/>"
            + "   </task>"
            + "   <task name='task2' g='249,172,83,48'>"
            + "      <transition name='to join2' to='join2' g='288,425:-41,-18'/>"
            + "   </task>"
            + "   <task name='task3' g='21,313,88,45'>"
            + "      <transition name='to join1' to='join1' g='-41,-18'/>"
            + "   </task>"
            + "   <task name='task4' g='154,313,88,48'>"
            + "      <transition name='to join1' to='join1' g='-41,-18'/>"
            + "   </task>"
            + "   <fork name='fork2' g='108,250,37,28'>"
            + "      <transition name='left' to='task3' g='-44,-18'/>"
            + "      <transition name='right' to='task4' g='-44,-18'/>"
            + "   </fork>"
            + "   <join name='join1' g='112,407,51,31'>"
            + "      <transition name='to join2' to='join2' g='-41,-18'/>"
            + "   </join>"
            + "   <join name='join2' g='192,511,57,44'>"
            + "      <transition name='to end1' to='end1' g='-42,-18'/>"
            + "   </join>"
            + "</process>");

        ProcessInstance processInstance = executionService.startProcessInstanceByKey("TwoForks");
        List<Task> tasks = taskService.createTaskQuery().list();
        while (!tasks.isEmpty()) {
            for (Task task : tasks) {
                taskService.completeTask(task.getId());
            }
            tasks = taskService.createTaskQuery().list();
        }
        Date endTime = historyService
            .createHistoryProcessInstanceQuery()
            .processInstanceId(processInstance.getId())
            .uniqueResult()
            .getEndTime();

        assertNotNull(endTime);
    }

}
