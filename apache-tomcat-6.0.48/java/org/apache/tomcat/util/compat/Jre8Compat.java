/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.util.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocket;

class Jre8Compat extends Jre7Compat {

    private static final Method getSSLParametersMethod;
    private static final Method getSSLParametersEngineMethod;
    private static final Method setUseCipherSuitesOrderMethod;
    private static final Method setSSLParametersMethod;
    private static final Method setSSLParametersEngineMethod;


    static {
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        Method m4 = null;
        Method m5 = null;
        try {
            m1 = SSLServerSocket.class.getMethod("getSSLParameters");
            m2 = SSLEngine.class.getMethod("getSSLParameters");
            m3 = sslParametersClass.getMethod("setUseCipherSuitesOrder", boolean.class);
            m4 = SSLServerSocket.class.getMethod("setSSLParameters", sslParametersClass);
            m5 = SSLEngine.class.getMethod("setSSLParameters", sslParametersClass);
        } catch (SecurityException e) {
            // Should never happen
        } catch (NoSuchMethodException e) {
            // Expected on Java < 8
        }
        getSSLParametersMethod = m1;
        getSSLParametersEngineMethod = m2;
        setUseCipherSuitesOrderMethod = m3;
        setSSLParametersMethod = m4;
        setSSLParametersEngineMethod = m5;
    }


    static boolean isSupported() {
        return setUseCipherSuitesOrderMethod != null;
    }


    @Override
    public void setUseServerCipherSuitesOrder(SSLServerSocket socket,
            boolean useCipherSuitesOrder) {
        try {
            Object sslParameters = getSSLParametersMethod.invoke(socket);
            setUseCipherSuitesOrderMethod.invoke(
                    sslParameters, Boolean.valueOf(useCipherSuitesOrder));
            setSSLParametersMethod.invoke(socket, sslParameters);
            return;
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }


    @Override
    public void setUseServerCipherSuitesOrder(SSLEngine engine,
            boolean useCipherSuitesOrder) {
        
        try {
            Object sslParameters = getSSLParametersEngineMethod.invoke(engine);
            setUseCipherSuitesOrderMethod.invoke(sslParameters, Boolean.valueOf(useCipherSuitesOrder));
            setSSLParametersEngineMethod.invoke(engine, sslParameters);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
