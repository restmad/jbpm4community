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
package org.jbpm.test.task;

import java.util.List;

import org.jbpm.api.task.Participation;
import org.jbpm.api.task.Task;
import org.jbpm.test.JbpmTestCase;


/**
 * @author Tom Baeyens
 */
public class TaskQueryCandidatesTest extends JbpmTestCase {
  
  String salesGroupId;
  String developmentGroupId;

  public void setUp() throws Exception {
    super.setUp();
    
    identityService.createUser("johndoe", "John", "Doe");
    identityService.createUser("joesmoe", "Joe", "Smoe");
    identityService.createUser("jackblack", "Jack", "Black");
    
    salesGroupId = identityService.createGroup("sales");
    identityService.createMembership("johndoe", salesGroupId);
    identityService.createMembership("joesmoe", salesGroupId);

    developmentGroupId = identityService.createGroup("development");
    identityService.createMembership("jackblack", developmentGroupId);
    identityService.createMembership("johndoe", developmentGroupId);
  }

  public void tearDown() throws Exception {
    identityService.deleteUser("johndoe");
    identityService.deleteUser("joesmoe");
    identityService.deleteUser("jackblack");
    identityService.deleteGroup(salesGroupId);
    identityService.deleteGroup(developmentGroupId);
   
    super.tearDown();
  }
    

  public void testDirectUserCandidate() {
    // this is the task we'll be looking for
    Task task = taskService.newTask();
    task.setName("do laundry");
    String taskId = taskService.saveTask(task);
    taskService.addTaskParticipatingUser(taskId, "johndoe", Participation.CANDIDATE);

    // this tasks are a diversion to see if the query only selects the above task
    task = taskService.newTask();
    task.setName("dishes");
    task.setAssignee("johndoe");
    String johnsOtherTaskId = taskService.saveTask(task);
    // this tasks are a diversion to see if the query only selects the above task
    task = taskService.newTask();
    task.setName("dishes");
    String joesOtherTaskId = taskService.saveTask(task);
    
    
    List<Task> groupTasks = taskService.findGroupTasks("johndoe");
    assertEquals(1, groupTasks.size());
    assertEquals(taskId, groupTasks.get(0).getId());
    
    groupTasks = taskService.findGroupTasks("joesmoe");
    assertEquals(0, groupTasks.size());
    
    groupTasks = taskService.findGroupTasks("jackblack");
    assertEquals(0, groupTasks.size());
    
    taskService.deleteTaskCascade(taskId);
    taskService.deleteTaskCascade(johnsOtherTaskId);
    taskService.deleteTaskCascade(joesOtherTaskId);
  }

  public void testGroupCandidate() {
    Task task = taskService.newTask();
    task.setName("do laundry");
    String taskId = taskService.saveTask(task);
    taskService.addTaskParticipatingGroup(taskId, salesGroupId, Participation.CANDIDATE);

    // this tasks are a diversion to see if the query only selects the above task
    task = taskService.newTask();
    task.setName("dishes");
    task.setAssignee("johndoe");
    String johnsOtherTaskId = taskService.saveTask(task);
    // this tasks are a diversion to see if the query only selects the above task
    task = taskService.newTask();
    task.setName("dishes");
    String joesOtherTaskId = taskService.saveTask(task);
    

    List<Task> groupTasks = taskService.findGroupTasks("johndoe");
    assertEquals(1, groupTasks.size());
    assertEquals(taskId, groupTasks.get(0).getId());
    
    groupTasks = taskService.findGroupTasks("joesmoe");
    assertEquals(1, groupTasks.size());
    assertEquals(taskId, groupTasks.get(0).getId());
    
    groupTasks = taskService.findGroupTasks("jackblack");
    assertEquals(0, groupTasks.size());
    
    taskService.deleteTaskCascade(taskId);
    taskService.deleteTaskCascade(johnsOtherTaskId);
    taskService.deleteTaskCascade(joesOtherTaskId);
  }
  
  public void testGroupCandidateDuplicate() {
    Task task = taskService.newTask();
    task.setName("do laundry");
    String taskId = taskService.saveTask(task);
    taskService.addTaskParticipatingGroup(taskId, salesGroupId, Participation.CANDIDATE);
    taskService.addTaskParticipatingGroup(taskId, developmentGroupId, Participation.CANDIDATE);

//    // this tasks are a diversion to see if the query only selects the above task
//    task = taskService.newTask();
//    task.setName("dishes");
//    String johnsOtherTaskId = taskService.saveTask(task);
//    
//    // this tasks are a diversion to see if the query only selects the above task
//    task = taskService.newTask();
//    task.setName("dishes");
//    String joesOtherTaskId = taskService.saveTask(task);
    
    List<Task> groupTasks = taskService.findGroupTasks("johndoe");
    assertEquals(1, groupTasks.size());
    assertEquals(taskId, groupTasks.get(0).getId());
    
    groupTasks = taskService.findGroupTasks("joesmoe");
    assertEquals(1, groupTasks.size());
    assertEquals(taskId, groupTasks.get(0).getId());
    
    groupTasks = taskService.findGroupTasks("jackblack");
    assertEquals(1, groupTasks.size());
    
    taskService.deleteTaskCascade(taskId);
//    taskService.deleteTaskCascade(johnsOtherTaskId);
//    taskService.deleteTaskCascade(joesOtherTaskId);
  }
  
  public void testCountGroupCandidateDuplicate() {
    Task task = taskService.newTask();
    task.setName("do laundry");
    String taskId = taskService.saveTask(task);
    taskService.addTaskParticipatingGroup(taskId, salesGroupId, Participation.CANDIDATE);
    taskService.addTaskParticipatingGroup(taskId, developmentGroupId, Participation.CANDIDATE);
    
    long count = taskService.createTaskQuery().candidate("johndoe").count();
    assertEquals(1, count);
    
    taskService.deleteTaskCascade(taskId);
  }

  /**
   * JBPM-2641.
   */
  public void testCountUserCandidateDuplicate() {
    Task task = taskService.newTask();
    task.setName("do laundry");
    String taskId = taskService.saveTask(task);
    taskService.addTaskParticipatingUser(taskId, "boss_rigging", Participation.CANDIDATE);
    taskService.addTaskParticipatingUser(taskId, "boss_rigging", Participation.CANDIDATE);

    assertEquals(1, taskService.createTaskQuery().candidate("boss_rigging").count());

    taskService.deleteTaskCascade(taskId);
  }
}
