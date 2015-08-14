/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.wkrdp.message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.uiautomation.ios.ServerSideSession;

/**
 * Created with IntelliJ IDEA. User: freynaud Date: 17/01/2013 Time: 15:08 To change this template use File | Settings |
 * File Templates.
 */
public class MessageFactory {

  private static final Logger LOG = Logger.getLogger(MessageFactory.class.getName());

  private static final List<String> DEFAULT = Arrays.asList("Default");

  private Map<String, Class<? extends BaseIOSWebKitMessage>> iOS7_TYPES = new HashMap<>();
  private Map<String, Class<? extends BaseIOSWebKitMessage>> iOS8_TYPES = new HashMap<>();

  private String iOSVersion;

  private ReentrantLock versionDeterminingLock;

  private Condition determiningVersion;

  public MessageFactory(ServerSideSession session) {
    iOSVersion = session.getDevice().getCapability().getSDKVersion();
    versionDeterminingLock = new ReentrantLock();
    determiningVersion = versionDeterminingLock.newCondition();
    populateDefaultTypes();
    populateIOS7Types();
    populateIOS8Types();
  }

  private Map<String, Class<? extends BaseIOSWebKitMessage>> populateDefaultTypes() {
    Map<String, Class<? extends BaseIOSWebKitMessage>> types = new HashMap<>();
    types.put("_rpc_reportSetup:", org.uiautomation.ios.wkrdp.message.ReportSetupMessageImpl.class);
    types.put("_rpc_applicationSentData:", org.uiautomation.ios.wkrdp.message.ApplicationDataMessageImpl.class);
    return types;
  }

  private void populateIOS7Types() {
    iOS7_TYPES = populateDefaultTypes();
    iOS7_TYPES.put("_rpc_reportConnectedApplicationList:",
        org.uiautomation.ios.wkrdp.message.ios7.ReportConnectedApplicationsMessageImpl.class);
    iOS7_TYPES.put("_rpc_applicationSentListing:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationSentListingMessageImpl.class);
    iOS7_TYPES.put("_rpc_applicationConnected:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationConnectedMessageImpl.class);
    iOS7_TYPES.put("_rpc_applicationDisconnected:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationDisconnectedMessageImpl.class);
    iOS7_TYPES.put("_rpc_applicationUpdated:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationUpdatedMessageImpl.class);
  }

  private void populateIOS8Types() {
    iOS8_TYPES = populateDefaultTypes();
    iOS8_TYPES.put("_rpc_reportConnectedApplicationList:",
        org.uiautomation.ios.wkrdp.message.ios8.ReportConnectedApplicationsMessageImpl.class);
    iOS8_TYPES.put("_rpc_applicationSentListing:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationSentListingMessageImpl.class);
    iOS8_TYPES.put("_rpc_applicationConnected:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationConnectedMessageImpl.class);
    iOS8_TYPES.put("_rpc_applicationDisconnected:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationDisconnectedMessageImpl.class);
    iOS8_TYPES.put("_rpc_applicationUpdated:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationUpdatedMessageImpl.class);
  }

  public IOSMessage create(String rawMessage) {
    try {
      if (LOG.isLoggable(Level.FINE)) {
        LOG.log(Level.FINE, "Raw message:   " + rawMessage);
      }
      BaseIOSWebKitMessage m = new BaseIOSWebKitMessage(rawMessage);
      waitOnVersionDetermination(m);
      Class<? extends BaseIOSWebKitMessage> implementationClass = getImplementationClassFor(m);
      if (implementationClass == null) {
        throw new RuntimeException("NI " + m.getSelector());
      }
      IOSMessage iOSMessage = createObjectOf(implementationClass, rawMessage);
      return iOSMessage;
    } catch (Exception e1) {
      LOG.log(Level.SEVERE, "format error", e1);
    }
    return null;
  }

  private void waitOnVersionDetermination(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    try {
      versionDeterminingLock.lock();
      if (isWaitingRequiredFor(baseIOSWebKitMessage)) {
        try {
          determiningVersion.await(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
      }
    } finally {
      versionDeterminingLock.unlock();
    }
  }

  private boolean isWaitingRequiredFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    return !ReportSetupMessage.SELECTOR.equals(baseIOSWebKitMessage.selector) && iOSVersion == null;
  }
  private Class<? extends BaseIOSWebKitMessage> getImplementationClassFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    if (iOSVersion == null) {
      determineIOSVersion(baseIOSWebKitMessage);
    }
    if (ServerSideSession.getVersionInt(iOSVersion) < 8) {
      return iOS7_TYPES.get(baseIOSWebKitMessage.selector);
    } else {
      return iOS8_TYPES.get(baseIOSWebKitMessage.selector);
    }
  }

  private boolean isImplementationClassDistinctFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    if (ReportSetupMessage.SELECTOR.equals(baseIOSWebKitMessage.selector)
        || ApplicationDataMessage.SELECTOR.equals(baseIOSWebKitMessage.selector)) {
      return false;
    }
    return true;
  }

  private void determineIOSVersion(BaseIOSWebKitMessage baseIOSWebkitMesssage) {
    try {
      versionDeterminingLock.lock();
      try {
        iOSVersion = baseIOSWebkitMesssage.arguments.objectForKey(WebkitDevice.WIRSIMULATORPRODUCTVERSIONKEY).toString();
      } finally {
        determiningVersion.signal();
      }
      if (LOG.isLoggable(Level.FINE)) {
        LOG.log(Level.FINE, "IOS version determined = " + iOSVersion);
      }
    } finally {
      versionDeterminingLock.unlock();
    }
  }

  private IOSMessage createObjectOf(Class<? extends BaseIOSWebKitMessage> implementationClass, String argument)
      throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    Object[] args = new Object[] { argument };
    Class<?>[] argsClass = new Class[] { String.class };
    Constructor<?> c = implementationClass.getConstructor(argsClass);
    IOSMessage iOSMessage = (IOSMessage) c.newInstance(args);
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine("IOS Message: " + iOSMessage);
    }
    return iOSMessage;
  }

  private Document getDocument(String rawMessage) throws DocumentException {
    String message = rawMessage.replace(
        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">",
        "");
    SAXReader reader = new SAXReader();
    Document document = reader.read(IOUtils.toInputStream(message));
    return document;
  }

  private Node getWebKitDebugMessage(Document d) {
    Node n = d.selectSingleNode("/plist/dict/dict/data");
    return n;
  }
}
