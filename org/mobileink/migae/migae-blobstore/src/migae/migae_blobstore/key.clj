(ns 'migae.migae-blobstore.key
  (:import [com.google.appengine.api.blobstore
            BlobKey])


(defn make
  [^String s]
  (BlobKey. s))

;; from aem datastore impl
;; (defn as-blob-key [x]
;;   (if (instance? BlobKey x)
;;       x
;;       (BlobKey. x)))

(defn compare
  [^BlobKey key1 ^Blobkey key2]
  (.compareTo key1 key2))

(defn equals
  [^BlobKey key1 ^Object key2]
  (.equals key1 key2))

(defn key-string
  [^BlobKey key]
  (.getKeyString key))

(defn to-string
  [^BlobKey key]
  (.toString key))

(defn hash-code
  [^BlobKey key]
  (.hashCode key))

