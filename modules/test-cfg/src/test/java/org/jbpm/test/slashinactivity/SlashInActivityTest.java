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
/**
 *
 */
package org.jbpm.test.slashinactivity;

import org.jbpm.api.Configuration;
import org.jbpm.api.Execution;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.JbpmException;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.test.JbpmCustomCfgTestCase;
import org.jbpm.test.JbpmTestCase;

/**
 * @author Huisheng Xu
 */
public class SlashInActivityTest extends JbpmCustomCfgTestCase {

  private static final String PROCESS_KEY = "test_process";

  private static final String TEST_PROCESS =
    "<process name='" + PROCESS_KEY + "'>" +
      "  <start name='1/2'>" +
      "    <transition to='theEnd' />" +
      "  </start>" +
      "  <end name='theEnd' />" +
      "</process>";

  public void testSlashInActivity() {
    try {
      deployJpdlXmlString(TEST_PROCESS);
      fail("shouldn't success deploy");
    } catch(JbpmException ex) {
      assertEquals("error: attribute <start name=\"1/2\" contains slash (/) [line=1 column=50 ]",
        ex.getMessage().trim());
    }
  }
}