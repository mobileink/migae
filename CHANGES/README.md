## CAVEAT: outdated.  Read the source, Duke!

Removed:

install-artifacts.sh - no longer needed with leiningen 2

resources/  -  replaced by template

leiningen_helpers.clj  - functionality no longer needed, replaced by template and plugin

swank.clj  -  swank obsoleted by nrepl

Moved:

testing.clj - should not be in main lib

services stuff all under services subproj, each with its own sub subproj

Restructured:

server code removed from core_local.clj and added to
server/src/appengine-magic/server.clj

move (defn make-appengine-request-environment-filter [] ... from
core_local.clj to server.clj.  It's only used by server/start.

move (defn appengine-base-url to blobstore service, remove from both core_local and core_google.  It's only use by the blobstore upload.
