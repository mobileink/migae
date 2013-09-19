# migae

Resources for implementing GAE webapps using Clojure.

## Status

This project started life as a fork from appengine-magic, but it has
changed so radically that I started over.  It does use code from
appengine-magic, but conceptually it is a completely different
creature.  In particular, it is modularized (each GAE service is
separately packaged), supports multiple servlets and filters, and
does not use the embedded Jetty server for testing, as appengine-magic
does.  Instead it uses a hack that gets you quasi-repl interactivity
using the Google dev server.

Most of the services implementation is taken directly from
appengine-magic, but some is original (e.g. datastore).  It is not
finished, so don't use it for any serious work.  But it seems to work
well enough if you want to explore or lend a hand.

_*This documentation is reasonably accurate as of Sept 19 2013, but is
unstable and subject to frequent change.*_

## Structure

The project actually includes:

 * migae - collection of libraries wrapping GAE API, one per GAE service
 * migae-template - a leiningent template
 * lein-migae - a leiningen plugin
 * migae-examples - some examples of how to do servlet programming with or without GAE

## Libraries

 * migae.migae-blobstore
 * migae.migae-channel
 * migae.migae-datastore
 * etc.

## Installation (NOT YET RELEASED)

    [migae/migae-blobstore "0.1.0-SNAPSHOT"]
    [migae/migae-channel "0.1.0-SNAPSHOT"]
    etc. (these include migae-core)
    [migae "x.y.z"] ;; everything

For now you have to clone the repo, build the libs, and "lein install"
them to make available on your local system.

## Getting Started

  1  `$ lein new migae app <appname>:<gae-proj-id> /path/to/sdk`

  2  `$ cd <appname>`

  3  `$ lein migae config`  - instantiate templates from \<appname\>/etc

  4 `$ lein migae libdir`   - copy required jars to war/WEB-INF/lib

  5 `$ lein compile`

  6 `$ /path/to/sdk/bin/dev_appserver.sh war`

## Developing and Testing

To make the GAE dev_appserver act like a repl, use the nasty GAE REPL
hack.

First install migae-save-buffer.el.  Instructions in
etc/migae-save-buffer.el.  (If you don't use emacs, you're out of luck
for the moment.)

This will replace the ordinary save-buffer command with one that first
executes the standard save-buffer command and then copies the saved
file to war/WEB-INF/classes.  It only does this if you are editing a
*.clj file below the <proj>/src directory containing a .dir-locals.el
file.  This file, in turn, is generated from a template in <proj>/etc
when you run `$ lein migae config`.

The upshot of this is that when you edit a file, it gets copied to
war/WEB-INF/classes, which is on the classpath, so it can be reloaded
by the clojure runtime.  This, in turn, is controlled by a filter -
see reload_filter.clj in the sample project.  Edit code, save, reload
webpage.  It's not as fast as the repl, but it's usually just fast
enough.

#### Servlet Configuration

Everything is controlled by the project.clj file.  The data in the
:servlets stanza is used to generate war/WEB-INF/web.xml from the
template file etc/web.xml.mustache.  You should never need to edit the
template file.  To change a servlet name or path, edit the project.clj
file and rerun `lein migae config`.
