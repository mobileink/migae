API:  assuming sth like (require '[migae.kernel :as kernel])

    kernel/magicVersion ;; version of migae
    kernel/remainingMillis
    kernel/newRequestThread (?)
    kernel/rteType
    kernel/rteVersion
    kernel/appName
    kernel/App-Id
    kernel/fileSeparator
    kernel/pathSeparator
    etc.
	line.separator
	java.version
	java.vendor
	java.vendor.url
	java.class.version
	java.specification.version
	java.specification.vendor
	java.specification.name
	java.vm.vendor
	java.vm.name
	java.vm.specification.version
	java.vm.specification.vendor
	java.vm.specification.name
	user.dir


migae.kernel is essentially a wrapper around:

# com.google.apphosting.api.ApiProxy.getCurrentEnvironment().getRemainingMillis().

# https://developers.google.com/appengine/docs/java/javadoc/com/google/apphosting/api/package-summary

# Threads ################
# (https://developers.google.com/appengine/docs/java/runtime#The_Sandbox):

(* NB: implement as migae.kernel.threads?)

A Java application can create a new thread, but there are some
restrictions on how to do it. These threads can't "outlive" the
request that creates them. (On a backend server, an application can
spawn a background thread, a thread that can "outlive" the request
that creates it.)

An application can

* Implement java.lang.Runnable; and
* Create a thread factory by calling
  com.google.appengine.api.ThreadManager.currentRequestThreadFactory()
* call the factory's newRequestThread method, passing in the Runnable,
  newRequestThread(runnable)

or use the factory object returned by
com.google.appengine.api.ThreadManager.currentRequestThreadFactory()
with an ExecutorService (e.g., call
Executors.newCachedThreadPool(factory)).

However, you must use one of the methods on ThreadManager to create your threads. You cannot invoke new Thread() yourself or use the default thread factory.

An application can perform operations against the current thread, such as thread.interrupt().

# Runtime environment ################

"App Engine sets several system properties that identify the runtime environment:

1. com.google.appengine.runtime.environment is "Production" when running on App Engine, and "Development" when running in the development server.
In addition to using System.getProperty(), you can access system properties using our type-safe API. For example:
```java
if (SystemProperty.environment.value() ==
    SystemProperty.Environment.Value.Production) {
    // The app is running on App Engine...
}
```

2. com.google.appengine.runtime.version is the version ID of the runtime environment, such as "1.3.0". You can get the version by invoking the following: String version = SystemProperty.version.get();

3. com.google.appengine.application.id is the application's ID. You can get the ID by invoking the following: String ID = SystemProperty.applicationId.get();

I.e. wrapper around com.google.appengine.api.utils.SystemProperty
(https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/utils/SystemProperty)

"App Engine also sets the following system properties when it initializes the JVM on an app server:

file.separator
path.separator
line.separator
java.version
java.vendor
java.vendor.url
java.class.version
java.specification.version
java.specification.vendor
java.specification.name
java.vm.vendor
java.vm.name
java.vm.specification.version
java.vm.specification.vendor
java.vm.specification.name
user.dir


Request ID:

com.google.apphosting.api.ApiProxy.getCurrentEnvironment().getAttributes().get("com.google.appengine.runtime.request_log_id")