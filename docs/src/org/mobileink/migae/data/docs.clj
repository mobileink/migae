(ns org.mobileink.migae.data.docs
  (:refer-clojure :exclude [atom find get val vals])
  (:import [com.google.appengine.api.search
            SearchServiceFactory
            Cursor
            Cursor$Builder
            DateUtil
            Document
            Document$Builder
            Field
            Field$Builder
            FieldExpression
            FieldExpression$Builder
            Field$FieldType
            GeoPoint
            GetRequest
            GetRequest$Builder
            Index
            IndexSpec
            OperationResult
            PutResponse
            PutException
            Query
            Query$Builder
            QueryOptions
            QueryOptions$Builder
            Results
            Schema
            Schema$Builder
            ScoredDocument
            SearchException
            StatusCode])
    (:require [clojure.tools.logging :as log :only [trace debug info]]))
;              [clojure.core/name :as nm]))

;; TODO: instead of making the user provide keywords, detect data type
;; dynamically and do the right thing.

;; Search Enums:  Field.FieldType, SortExpression.SortDirection,

;; Field.FieldType.HTML, etc.:
;; ATOM
;; An indivisible text content.
;; DATE
;; A Date with no time component.
;; GEO_POINT
;; Geographical coordinates of a point, in WGS84.
;; HTML
;; HTML content.
;; NUMBER
;; Double precision floating-point number.
;; TEXT
;; Text content.

;; use Joda Time?

;; StatusCode.OK, etc.:
;; CONCURRENT_TRANSACTION_ERROR
;; The last operation failed due to conflicting operations.
;; INTERNAL_ERROR
;; The last operation failed due to a internal backend error.
;; INVALID_REQUEST
;; The last operation failed due to invalid, user specified parameters.
;; OK
;; The last operation was successfully completed.
;; PERMISSION_DENIED_ERROR
;; The last operation failed due to user not having permission.
;; TIMEOUT_ERROR
;; The last operation failed to finish before its deadline.
;; TRANSIENT_ERROR
;; The last operation failed due to a transient backend error.

;; A Document is a collection of Fields. Each field is a named and typed
;; value. A document is uniquely identified by its ID and may contain
;; zero or more fields. A field with a given name can have multiple
;; occurrences. Once documents are put into the Index, they can be
;; retrieved via search queries. Typically, a program creates an
;; index. This operation does nothing if the index was already
;; created. Next, a number of documents are inserted into the
;; index. Finally, index is searched and matching documents, or their
;; snippets are returned to the user.

;; public List<ScoredDocument> indexAndSearch(
;;      String query, Document... documents) {
;;      SearchService searchService = SearchServiceFactory.getSearchService();
;;      Index index = searchService.getIndex(
;;          IndexSpec.newBuilder().setIndexName("indexName"));
;;      for (Document document : documents) {
;;        PutResponse response = index.put(document);
;;        assert response.getResults().get(0).getCode().equals(StatusCode.OK);
;;      }
;;      Results<ScoredDocument> results =
;;          index.search(Query.newBuilder().build(query));
;;      List<ScoredDocument> matched = new ArrayList<ScoredDocument>(
;;          results.getNumberReturned());
;;      for (ScoredDocument result : results) {
;;        matched.add(result);
;;      }
;;      return matched;
;;  }

(defonce ^{:dynamic true} *docs-service* (clojure.core/atom nil))

(defn get-docs-service []
  (when (nil? @*docs-service*)
    (reset! *docs-service* (SearchServiceFactory/getSearchService)))
  @*docs-service*)

;; Document doc = Document.newBuilder()
;;     .setId(myDocId) // Setting the document identifer is optional. If omitted, the search service will create an identifier.
;;     .addField(Field.newBuilder().setName("content").setText("the rain in spain"))
;;     .addField(Field.newBuilder().setName("email")
;;         .setText(currentUser.getEmail()))
;;     .addField(Field.newBuilder().setName("domain")
;;         .setAtom(currentUser.getAuthDomain()))
;;     .addField(Field.newBuilder().setName("published").setDate(new Date()))
;;     .build();

  ;; ["docid" {:fld1 ^Text"fld1text" :fld2 ^Date ...}]
(defn make-docx
  [fields]
  (let [docid (:ID fields)
        bld (Document/newBuilder)
        bldr (if docid (.setId bld docid) bld)
        foo (doseq [[key val] fields]
              (cond
               (= key :ATOM) (doseq [[fldname fldval] val]
                               (-> bldr (.addField
                                         (-> (Field/newBuilder)
                                             (.setName (name fldname))
                                             (.setAtom fldval)))))
               (= key :DATE) (doseq [[fldname fldval] val]
                               (-> bldr (.addField
                                         (-> (Field/newBuilder)
                                             (.setName (name fldname))
                                             (.setDate fldval)))))
               (= key :HTML) (doseq [[fldname fldval] val]
                               (-> bldr (.addField
                                         (-> (Field/newBuilder)
                                             (.setName (name fldname))
                                             (.setHTML fldval)))))
               (= key :NUMBER) (doseq [[fldname fldval] val]
                                 (-> bldr (.addField
                                           (-> (Field/newBuilder)
                                               (.setName (name fldname))
                                               (.setNumber fldval)))))
               (= key :TEXT) (doseq [[fldname fldval] val]
                               (-> bldr (.addField
                                         (-> (Field/newBuilder)
                                             (.setName (name fldname))
                                             (.setText fldval)))))))]
    (.build bldr)))

(defn build-field
  [bldr key val]
  (cond

   (keyword? val)
   (if (= val :html)
     (throw (Exception. (format "Keywords not allowed as field values.  If you want to store an HTML value, use a map:\n\t {:html \"<foo>...</foo>\"}")))
     (throw (Exception. (format "Keywords not allowed as field values. (%s)"
                              val))))

   (= (type val) clojure.lang.MapEntry)
   (log/debug "MapEntry:" val)

   (map? val)
   (do ;; (log/debug (format "build-field: val is map: %s" val))
       (if (> (count (keys val)) 1)
         (throw
          (Exception.
           "Only one entry allowed in value map: :html or :text"))
       (cond
          (:html val)
          (-> bldr (.addField
                    (-> (Field/newBuilder)
                        (.setName (name key))
                        (.setHTML (:html val)))))
          (:text val)
          (-> bldr (.addField
                    (-> (Field/newBuilder)
                        (.setName (name key))
                        (.setText (:text val)))))
          :else (throw
                 (Exception.
                  (format "Bad value key %s.  Only one of :html or :text allowed as sole key in value map" (keys val)))))))

   (= (type val) java.lang.String)
   (cond
    (> (count val) 500) ;; text fld
    (-> bldr (.addField
              (-> (Field/newBuilder)
                  (.setName (name key))
                  (.setText val))))
    :else (-> bldr (.addField  ;; atom fld
                    (-> (Field/newBuilder)
                        (.setName (name key))
                        (.setAtom val)))))

   (= (meta val) :html) ;; SHOULD NOT HAPPEN
   (throw (Exception. ":html meta used with non-string value"))

   (= (type val) java.util.Date)
   (-> bldr (.addField
             (-> (Field/newBuilder)
                 (.setName (name key))
                 (.setDate val))))

   (instance? java.lang.Number val)
   (do
     (log/debug (format "build-field nbr: key: %s val: %s valtyp: %s"
                        key val (type val)))
     (log/debug (format "(instance? java.lang.Number %s): %s"
                        val (instance? java.lang.Number val)))
     (-> bldr (.addField
               (-> (Field/newBuilder)
                   (.setName (name key))
                   (.setNumber val)))))

   (= (type val) com.google.appengine.api.search.GeoPoint)
   (-> bldr (.addField
             (-> (Field/newBuilder)
                 (.setName (name key))
                 (.setNumber val))))

   :else (throw
          (Exception. (format "Invalid search doc field %s of type %s"
                              val (type val))))))

(defn make-doc
  [fields]
  (let [docid (if-let [m (meta fields)]
                (let [ks (keys m)
                      ct (count ks)]
                  (if (> ct 1)
                    (throw (Exception. "Only one meta key allowed for
                    doc, which serves as doc_id"))
                    (do
                      ;; (log/debug "meta:" m)
                      ;; (log/debug "key count" (count ks))
                      (clojure.core/name (first ks)))))
                nil)
        bld (Document/newBuilder)
        bldr (if docid (.setId bld docid) bld)
        res (doseq [[key val] fields]
              (do ;; (log/debug (type val) val (set? val))
              (cond
               (set? val) ;; multiple vals, same key
               (do ;; (log/debug "multiple vals, same key:" key)
                   (dorun (map #(build-field bldr key %) val)))

               (map? val)
               (do ;; (log/debug "make-doc: val is map")
                   (if (> (count (keys val)) 1)
                     (throw
                      (Exception.
                       "Too many entries in value map.  Only one of :html or :text allow as key."))
                     (build-field bldr key val)))

               (= (type val) java.lang.String)
               (cond
                (> (count val) 500) ;; text fld
                (-> bldr (.addField
                          (-> (Field/newBuilder)
                              (.setName (name key))
                              (.setText val))))
                :else (-> bldr (.addField  ;; atom fld
                                (-> (Field/newBuilder)
                                    (.setName (name key))
                                    (.setAtom val)))))

               (= (meta val) :html) ;; SHOULD NOT HAPPEN
               (throw (Exception. ":html meta used with non-string value"))

               (= (meta val) :text) ;; SHOULD NOT HAPPEN
               (throw (Exception. ":text meta used with non-string value"))

               (= (type val) java.util.Date)
               (-> bldr (.addField
                         (-> (Field/newBuilder)
                             (.setName (name key))
                             (.setDate val))))

               (= (instance? java.lang.Number val))
               (-> bldr (.addField
                         (-> (Field/newBuilder)
                             (.setName (name key))
                             (.setNumber val))))

               (= (type val) com.google.appengine.api.search.GeoPoint)
               (-> bldr (.addField
                         (-> (Field/newBuilder)
                             (.setName (name key))
                             (.setNumber val))))

               :else
               (do
                 (log/error (format "val type: %s class %s"
                                    (type val) (class val)))
                 (throw (Exception. "Invalid search doc field type"))))))]
    (.build bldr)))

(defn persist
  [idx docs] ;; docs = vector
  ;; validate docs contains docs
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-docs-service) indexSpec)]
    (do
      ;; (try (.put theIndex (into-array [doc]))
      (try (.put theIndex docs)
           (catch PutException e
             (let [code (.getCode (.getOperationResult e))]
               (if (= code StatusCode/TRANSIENT_ERROR)
                 nil ;; retry putting the document
                 (log/error (.getMessage e)))))
           (catch Exception e (log/error (.getMessage e)))))))

;; (defn persistn
;;   [idx docs]
;;   (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
;;         theIndex (.getIndex (get-docs-service) indexSpec)]
;;     (.put theIndex (into-array docs))))

(defn get
  [^String idx ^String docid]
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-docs-service) indexSpec)]
    (.get theIndex docid)))


;; api:  fetch :index :fld val :fld2 val2 ...
(defn fetch
  [idx query-string]
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-docs-service) indexSpec)]
    (.search theIndex (str query-string))))

(defn found-count
  [resp]
  (.getNumberFound resp))

(defn returned-count
  [resp]
  (.getNumberReturned resp))

(defn atom
  [doc fldkey]
  (.getAtom (.getOnlyField doc (name fldkey))))

(defn date
  [doc fldkey]
  (.getDate (.getOnlyField doc (name fldkey))))

(defn html
  [doc fldkey]
  (.getHTML (.getOnlyField doc (name fldkey))))

(defn nbr
  [doc fldkey]
  (.getNumber (.getOnlyField doc (name fldkey))))

(defn text
  [doc fldkey]
  (.getText (.getOnlyField doc (name fldkey))))

(defn- val-for-fld
  [fld]
  (let [ftype (.getType fld)]
    (cond
     (= ftype Field$FieldType/ATOM)
     (.getAtom fld)
     (= ftype Field$FieldType/DATE)
     (.getDate fld)
     (= ftype Field$FieldType/GEO_POINT)
     (.getGeoPoint fld)
     (= ftype Field$FieldType/HTML)
     (.getHTML fld)
     (= ftype Field$FieldType/NUMBER)
     (.getNumber fld)
     (= ftype Field$FieldType/TEXT)
     (.getText fld)
     :default (throw (Exception. "Invalid search doc field type")))))

(defn val
  [doc fldkey]
    (val-for-fld (.getOnlyField doc (name fldkey))))

(defn types
  [doc fldkey]
  (let [flds (.getFields doc (name fldkey))]
    (do ;; (log/debug "fields" flds)
    (map (fn [fld]
           #(.getType fld))
         flds))))

(defn vals
  [doc fldkey]
  (let [flds (.getFields doc (name fldkey))]
    (map #(val-for-fld %) flds)))

