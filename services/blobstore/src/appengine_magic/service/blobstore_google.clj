(in-ns 'appengine-magic.service.blobstore)


(defn uploaded-blobs [ring-request-map]
  (let [^:HttpServletRequest request (:request ring-request-map)]
    (into {} (.getUploadedBlobs (get-blobstore-service) request))))
