# migae

## Status

This is derived from appengine-magic.  It's basically a reorganization
of the codebase to make it conform to leiningen 2 patterns.  In
particular, each GAE service is separately packaged.  Most of the
implementation is taken directly from appengine-magic, but some is
original (e.g. datastore).  It is not finished, so don't bother
downloading it and trying to use it- it won't work.

The main differences from appengine-magic: modularization of services,
removal of embedded server code, segregation of plugin and template
code.  This code assumes use of dev_appserver for testing, with the
interactive hack described in migae-examples/gae2.

See CHANGES for details.

_*This documentation is currently unstable and changes frequently.*_

## Structure

 * API - the implementation has the usual kernel-and-hull structure:
   * kernel - kernel api only; this involves very little code.
   * services - most of the GAE functionality is implemented as "services"
     (e.g. datastore, user, channel, etc.) ; each service is packaged
     as a separate jar so you can pick and choose; a "services
     uberjar" containing all of them is also provided.
 * jetty - a dev/test server embedding jetty without servlet container
   services.  Supports rapid interactive (repl-based) development at
   the cost of not completely emulating the GAE environment.
 * magic - leinigen plugin implementing tasks to build your project,
   deploy to google, etc.  In particular, commands to run either the
   official GAE devserver or the appengine-magic jetty server for
   local development and testing.

## Libraries

 * migae/migae-core
 * migae/migae-blobstore
 * migae/migae-channel
 * migae/migae-datastore
 * etc.

## Installation (NOT YET RELEASED)

    [migae/migae-blobstore "0.1.0-SNAPSHOT"]
    [migae/migae-channel "0.1.0-SNAPSHOT"]
    etc. (these include migae-core)
    [migae "x.y.z"] ;; everything

For now you have to clone the repo, build the lib, and "lein install"
to make it available on your local system.

## Developing migae applications

### devserver and magic

You can use the Google sdk-supplied dev server ("devserver" for short)
to test your app, but of course you don't get the interactive
repl-based joyosity treasured by clojurians.  For that you have to use
the appengine-magic server ("magic server" for short).  However,
unlike the magic server does not provide servlet container services.
So it doesn't behave like the devserver; for example, it does not read
your web.xml deployment descriptor and it doesn't set the context root
like a real servlet container.  On the plus side, it is much faster;
code changes are reloaded almost instantly in the magic server, but to
get the same effect in the devserver you have to reload the entire
context, which is painfully slow.

Here's what you need to know to use the magic server for development.
This assumes that you are using [compojure](git://github.com/weavejester/compojure.git) and [ring](https://github.com/ring-clojure/ring).

#### Servlet config and routing.

Google App Engine for java is basically a servlet container.  So your
application will implement one or more servlets, and you use
war/WEB-INF/web.xml to configure them.  For example, if you want
servlet "frob.nicate" (that's a clojure namespace, corresponding to
source code in src/frob/nicate.clj) to service requests to
http://example.org/frobnicate, you would put the following in your
web.xml:

```xml
<servlet>
  <servlet-name>frobber</servlet-name>
  <servlet-class>frob.nicate</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>frobber</servlet-name>
  <url-pattern>/frobnicate/*</url-pattern>
</servlet-mapping>
```

Note that you can have one servlet service multiple paths.  So in addition to the above let's add:

```xml
<servlet>
  <servlet-name>defrobber</servlet-name>
  <servlet-class>frob.nicate</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>defrobber</servlet-name>
  <url-pattern>/defrob/*</url-pattern>
</servlet-mapping>
```

Now a request to frob a doobsnickers
(http://example.org/frobnicate/doobsnickers) or defrob
(http://example.org/defrob/doobsnickers) will be routed to your
frob.nicate servlet for handling:

```clojure
(GET "/frobnicate/:widget" [widget] ... handle request
(GET "/defrob/:widget" [widget] ... handle request
```

The important thing to note here is the role of the servlet container.
You can run the same "webapp" code with or without a servlet container
(provided you do not make explicit calls to the container service,
etc).  [Etc....]

#### Multiple Servlets

The magic server only supports a single handler, and it
programmatically sets its context to "/".  You tell it which handler
to use when you start it by calling

```clojure
(appengine-magic.jetty/start myhandler)
```

*Note* that we call it a "handler"; that's because it isn't a servlet
 if it isn't running in a servlet container.

In other words, the magic server does **not** read your web.xml file.
But it's easy to test multiple servlets; all it takes is is a few
trivial clojure functions that load the relevant code and then execute
a restart command on the server.  For an example, see the
:repl-options key of the project.clj file example produced by

```shell
$ lein new appengine-magic ...
```

That code defines two functions named after the two servlets
implemented by the project.  To switch from one servlet to another all
you need to do is execute the appropriate function as a command at the
repl prompt; for example:
```clojure
user=> (user)
```

This reloads (and thus re-evaluates) the code in user.clj and then
restarts the magic server with myproj-user as the handler.

The only major drawback is you won't be able to test servlets that
talk to each other in the magic server; you'll have to use the
devserver for that.

The "lein magic jetty myhandler" command launches the magic server
with in a repl myhandler as the root context ("/") handler.

##### devserver

To reload servlets in the devserver (or in dev_appserver.sh) go to
localhost:8080/_ah/reloadwebapp/.  You'll get a 404, but it will cause
a context reload.  So you can recompile your code and use this to
reload the classes; but note that just evaluating your code isn't
enough.