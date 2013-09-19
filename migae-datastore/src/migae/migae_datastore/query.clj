(ns migae.migae-datastore.query
  (:refer-clojure :exclude [ancestors count hash name])
  (:import [com.google.appengine.api.datastore
            Entity
            Query
            Query$Filter
            Query$FilterPredicate
            Query$FilterOperator
            Query$CompositeFilter
            Query$CompositeFilterOperator
            Query$SortDirection
            FetchOptions
            FetchOptions$Builder
            PreparedQuery
            KeyFactory
            Key
            DatastoreService
            DatastoreServiceFactory])
           ;; [org.apache.log4j PatternLayout FileAppender
           ;;  EnhancedPatternLayout])
;; [clj-logging-config.log4j])
  (:require [migae.migae-datastore.service :as dss]
            [migae.migae-datastore.entity :as dsentity]
            [migae.migae-datastore.key :as dskey]
            [clojure.tools.logging :as log :only [trace debug info]]))

;; client:  (:require [migae.migae-datastore.query :as dsqry]...

;; ################
(defmulti entities
  (fn [& {kind :kind key :key name :name id :id}]
    (cond
     name :kindname
     id   :kindid
     kind :kind
     key  :key
     ;; (= (type s) java.lang.String) :kind
     ;; (= (type rest) com.google.appengine.api.datastore.Key) :key
     :else :kindless)))

(defmethod entities :kindless
  [& {kind :kind key :key name :name id :id}]
  (Query.)
  )

(defmethod entities :kind
  [& {kind :kind key :key name :name id :id}]
  (Query. (clojure.core/name kind))
  )

(defmulti ancestors
  (fn [& {kind :kind key :key name :name id :id}]
    (cond
     name :kindname
     id   :kindid
     kind :kind
     key  :key
     ;; (= (type s) java.lang.String) :kind
     ;; (= (type rest) com.google.appengine.api.datastore.Key) :key
     :else :kindless)))

(defmethod ancestors :key
  [& {kind :kind key :key name :name id :id}]
  (Query. key)
  )

(defmethod ancestors :kindname
  [& {kind :kind key :key name :name id :id}]
  (let [k (dskey/make kind name)]
        (Query. k))
  )

(defmethod ancestors :kindid
  [& {kind :kind key :key name :name id :id}]
  (let [k (dskey/make kind id)]
        (Query. k))
  )

(defn predicate
  [property]
  )

(defn prepare
  [query]
  (.prepare (dss/get-datastore-service) query))

(defn run
  [prepared-query]
  (.asIterable prepared-query))

(defn count
  [prepared-query]
  (.countEntities prepared-query (FetchOptions$Builder/withDefaults)))

;; ################################################################

;; Filter heightMinFilter =
;;   new FilterPredicate("height",
;;                       FilterOperator.GREATER_THAN_OR_EQUAL,
;;                       minHeight);

;; (def heightMinFilter (dsqry/predicate :prop :height :ge minheight))
;; (def heightMaxFilter (dsqry/predicate :prop :height :ge maxheight))

;; //Use CompositeFilter to combine multiple filters
;; Filter heightRangeFilter =
;;   CompositeFilterOperator.and(heightMinFilter, heightMaxFilter);

;; (dsqry/comp heightMinFilter :and heightMaxFilter)

;; // Use class Query to assemble a query
;; Query q = new Query("Person").setFilter(heightRangeFilter);

;; (-> (dsqry/entities :kind :Person)
;;     (dsqry/filter heightRangeFilter))
;; or
;; (-> (dsqry/entities :kind :Person)
;;     (dsqry/filters heightMinFilter :and heightMaxFilter))

;; (-> (dsqry/entities :kind :Person)
     ;; (dsqry/filters {:prop "height" :op :ge :val minHeight})
     ;;                {:prop "height" :op :le :val maxHeight}))

;; Or write a macro so we can use >, <, etc. e.g.
;;    :height >= minHeight :and :height <= maxHeight

;; i.e. we want some kind of higher-order function combinator


;; filter operators:

;; Query.FilterOperator.EQUAL
;; Query.FilterOperator.GREATER_THAN
;; Query.FilterOperator.GREATER_THAN_OR_EQUAL
;; Query.FilterOperator.IN
;; Query.FilterOperator.LESS_THAN
;; Query.FilterOperator.LESS_THAN_OR_EQUAL
;; Query.FilterOperator.NOT_EQUAL
