// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Describes a series of key/value pairs that encapsulate aspects of a browser.
 */
public interface Capabilities {

  default String getBrowserName() {
    return String.valueOf(Optional.ofNullable(getCapability("browserName")).orElse(""));
  }

  default Platform getPlatform() {
    Object rawPlatform = getCapability("platform");

    if (rawPlatform == null) {
      rawPlatform = getCapability("platformName");
    }

    if (rawPlatform == null) {
      return null;
    }

    if (rawPlatform instanceof String) {
      return Platform.valueOf((String) rawPlatform);
    } else if (rawPlatform instanceof Platform) {
      return (Platform) rawPlatform;
    }

    throw new IllegalStateException("Platform was neither a string or a Platform: " + rawPlatform);
  }

  default String getVersion() {
    return String.valueOf(Optional.ofNullable(getCapability("version")).orElse(""));
  }

  /**
   * @deprecated Use is(SUPPORTS_JAVASCRIPT) instead
   * @see #is(String)
   * @see org.openqa.selenium.remote.CapabilityType#SUPPORTS_JAVASCRIPT
   */
  @Deprecated
  default boolean isJavascriptEnabled() {
    return is("javascriptEnabled");
  }

  /**
   * @return The capabilities as a Map
   */
  Map<String, ?> asMap();

  /**
   * @see org.openqa.selenium.remote.CapabilityType
   * @param capabilityName The capability to return.
   * @return The value, or null if not set.
   */
  Object getCapability(String capabilityName);

  /**
   * @see org.openqa.selenium.remote.CapabilityType
   * @param capabilityName The capability to check.
   * @return Whether or not the value is not null and not false.
   */
  default boolean is(String capabilityName) {
    Object cap = getCapability(capabilityName);
    if (cap == null) {
      // javascriptEnabled is true if not set explicitly
      return "javascriptEnabled".equals(capabilityName);
    }
    return cap instanceof Boolean ? (Boolean) cap : Boolean.parseBoolean(String.valueOf(cap));
  }

  /**
   * Merge two {@link Capabilities} together and return the union of the two as a new
   * {@link Capabilities} instance. Capabilities from {@code other} will override those in
   * {@code this}.
   */
  default Capabilities merge(Capabilities other) {
    HashMap<String, Object> map = new HashMap<>();
    map.putAll(asMap());
    if (other != null) {
      map.putAll(other.asMap());
    }
    return new ImmutableCapabilities(map);
  }
}
