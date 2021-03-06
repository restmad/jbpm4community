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
package org.jbpm.api.history;

import java.util.Date;
import java.util.List;

/** represents one occurrence of an activity during a process 
 * instance.  
 * 
 * @author Tom Baeyens
 */
public interface HistoryActivityInstance {

  /** name of the activity that was executed */
  String getActivityName();

  /** time when the activity was entered */
  Date getStartTime();

  /** might be null in case the activity is still active */
  Date getEndTime();

  /** duration in milleseconds */
  long getDuration();

  /** the execution that was related to this activity occurrence */
  String getExecutionId();
  
  /** 
   * The names of the transitions that were selected as outgoing transitions for the execution.
   */
  List<String> getTransitionNames();
 
}