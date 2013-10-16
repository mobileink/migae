(ns org.mobileink.migae.search
  (:refer-clojure :exclude [atom find get])
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

(defonce ^{:dynamic true} *search-service* (clojure.core/atom nil))

(defn get-search-service []
  (when (nil? @*search-service*)
    (reset! *search-service* (SearchServiceFactory/getSearchService)))
  @*search-service*)

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
(defn make-doc
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

;; IndexSpec.newBuilder().setName(indexName).build();
;; SearchServiceFactory.getSearchService().getIndex(indexSpec);

(defn persist
  [idx fields]
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-search-service) indexSpec)
        docid (:docid fields)
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
                                             (.setText fldval)))))))
        theDoc (.build bldr)]
    (do
      ;; (try
        (.put theIndex (into-array [theDoc]))
    )))

(defn persistn
  [idx docs]
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-search-service) indexSpec)]
    (.put theIndex (into-array docs))))

(defn get
  [^String idx ^String docid]
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-search-service) indexSpec)]
    (.get theIndex docid)))

(defn find
  [idx query-string]
  (let [indexSpec (.. (IndexSpec/newBuilder) (setName (name idx)) (build))
        theIndex (.getIndex (get-search-service) indexSpec)]
    (.search theIndex query-string)))

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
