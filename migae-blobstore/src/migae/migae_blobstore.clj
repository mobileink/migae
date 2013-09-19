(ns migae.migae-blobstore
  (:require [migae.migae-blobstore.key :as blobkey])
  (:import [com.google.appengine.api.blobstore
            BlobstoreServiceFactory
            BlobstoreService
            BlobInfoFactory
            BlobInfo
            BlobstoreInputStream
            BlobKey
            ByteRange
            FileInfo
            UploadOptions
            UploadOptions$Builder])


(defonce ^{:dynamic true} *blobstore-service* (atom nil))


(defn get-blobstore-service []
  (when (nil? @*blobstore-service*)
    (reset! *blobstore-service* (BlobstoreServiceFactory/getBlobstoreService)))
  @*blobstore-service*)


(defn upload-url [success-path]
  (.createUploadUrl (get-blobstore-service) success-path))

(defn delete! [& blobs]
  (let [blobs (map as-blob-key blobs)]
    (.delete (get-blobstore-service) (into-array blobs))))


(defn fetch-data [^:BlobKey blob-key, start-index, end-index]
  (.fetchData (get-blobstore-service) blob-key start-index end-index))


(defn byte-range [^:HttpServletRequest request]
  (.getByteRange (get-blobstore-service) request))


(defn- serve-helper
  ([blob-key, ^:HttpServletResponse response]
     (.serve (get-blobstore-service) (as-blob-key blob-key) response))
  ([blob-key, start, end, ^:HttpServletResponse response]
     (.serve (get-blobstore-service) (as-blob-key blob-key) (ByteRange. start end) response)))


(defn serve
  [^HttpServletResponse request blob-key]
  (serve-helper blob-key (:response request))
  ;; This returns a special Ring response map. The serve-helper primes the HTTP
  ;; response object, but this response must not be committed by the running servlet.
  {:commit? false})


(defn callback-complete [request destination]
  (.sendRedirect (:response request) destination)
  {:commit? false})

;; ...local.clj
;; (defn uploaded-blobs [ring-request-map]
;;   (let [^:HttpServletRequest request (:request ring-request-map)
;;         raw-uploaded-blobs (slurp (.getInputStream request))
;;         uploaded-blobs (read-string raw-uploaded-blobs)
;;         processed-blobs (reduce (fn [acc [upload-name blob-key-str]]
;;                                   (assoc acc upload-name (BlobKey. blob-key-str)))
;;                                 {}
;;                                 uploaded-blobs)]
;;     processed-blobs))
