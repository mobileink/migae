(ns appengine-magic.service.datastore
  (:import [com.google.appengine.api.datastore
            KeyFactory
            Key
            DatastoreService
            DatastoreServiceFactory
            DatastoreServiceConfig
            DatastoreServiceConfig$Builder
            ReadPolicy
            ReadPolicy$Consistency
            ImplicitTransactionManagementPolicy
            Entity
            FetchOptions$Builder
            Query
            Query$FilterOperator
            Query$SortDirection
            ;; Exceptions
            DatastoreFailureException
            EntityNotFoundException
           ;; types
            Blob
            ShortBlob
            Text
            Link]
           [com.google.appengine.api.blobstore BlobKey])
  (:use appengine-magic.kernel.utils))

(defonce ^{:dynamic true} *datastore-service* (atom nil))
(defn get-datastore-service []
  (when (nil? @*datastore-service*)
    ;; (do (prn "getting ds service ****************")
    (reset! *datastore-service* (DatastoreServiceFactory/getDatastoreService)))
  @*datastore-service*)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SEE http://david-mcneil.com/post/16535755677/clojure-custom-map

(def default-contents {:species "human"
                       :status :alive})
;; whatever contents are provided at construction time will be
;; augmented with the default values
(defn augment-contents [contents]
  (merge default-contents contents))

(deftype EntityMap [contents]
  clojure.lang.IPersistentMap
  (assoc [_ k v]
    ;; TODO: use .getProperty (memoize result?)
    (EntityMap. (.assoc contents k v)))
  (assocEx [_ k v]
    (EntityMap. (.assocEx contents k v)))
  (without [_ k]
    (EntityMap. (.without contents k)))

  ;; clojure.lang.IObj
  ;; (withMeta [meta]
  ;;   (EntityMap. (.withMeta meta)))

  java.lang.Iterable
  (iterator [this]
    (.iterator (augment-contents contents)))

  clojure.lang.Associative
  (containsKey [_ k]
    (.containsKey (augment-contents contents) k))
  (entryAt [_ k]
    (.entryAt (augment-contents contents) k))

  clojure.lang.IPersistentCollection
  (count [_]
    (.count (augment-contents contents)))
  (cons [_ o]
    (EntityMap. (.cons contents o)))
  (empty [_]
    (.empty (augment-contents contents)))
  (equiv [_ o]
    (and (isa? (class o) EntityMap)
         (.equiv (augment-contents contents) (.(augment-contents contents) o))))

  clojure.lang.Seqable
  (seq [_]
    (.seq (augment-contents contents)))

  clojure.lang.ILookup
  (valAt [_ k]
    (.valAt (augment-contents contents) k))
  (valAt [_ k not-found]
    (.valAt (augment-contents contents) k not-found)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn dump-entity [theEntity]
  (do
    (prn "****************")
    (prn "Dumping entity: " theEntity)
    (prn "entity: " ((meta theEntity) :entity))
    (prn "keymap: "(meta theEntity))
    (prn "entitymap: " (theEntity))
    (prn "****************")
    ))

(declare Entities)
(declare make-entity)

(defn entity-from-entitymap
  [theEntityMap]
  {:pre [;; (do (prn "e map meta: " (meta theEntityMap))
         ;;     (prn "e map id: "   (:id (meta theEntityMap))) true),
         ;; :key not allowed in EntityMap initializers
         (not (nil? (:kind (meta theEntityMap)))),
         ;; one of :id or :name or neither
         (or (nil? (:id (meta theEntityMap)))
             (nil? (:name (meta theEntityMap)))),
         (if (not (nil? (:id (meta theEntityMap))))
           (number? (:id (meta theEntityMap)))
           true),
         (if (not (nil? (:name (meta theEntityMap))))
           (or (string?  (:name (meta theEntityMap)))
               (keyword? (:name (meta theEntityMap))))
           true)
         ;; TODO: validate :parent
         ]}
  (let [{:keys [kind id name parent]} (meta theEntityMap)
        arg2 (if id id (if name name nil))
        arg3 (if (nil? parent) nil
                 (cond
                  (= (type parent)
                     :appengine-magic.service.datastore/Key)
                  ;;no yet
                  nil
                  (= (type parent)
                     :appengine-magic.service.datastore/Entity)
                  (:key (meta parent))
                  :else  ;; type parent = EntityMap
                  (:key (meta (Entities parent)))))
        ;; OR: (ds/keys ds parent)))))
        theEntity (if (nil? parent)
                    (Entity. (clojure.core/name kind)
                             (if id id (if name name)))
                    (Entity. (clojure.core/name kind)
                             (if id id (if name name))
                             arg3))]    ; arg3 = parent Key
          (doseq [[k v] theEntityMap]
            ;; TODO: handle val types
            (.setProperty theEntity
                          (clojure.core/name k)
                          (if (number? v) v
                              (clojure.core/name v))))
          ;; TODO: make-entity s/b resonsible for putting if needed
          (.put (get-datastore-service) theEntity)
          (make-entity theEntity)))


;; QUESTION: do we want to implement a
;; :appengine-magic.service.datastore/Key clojo to go with our
;; :appengine-magic.service.datastore/Entity clojo?

(defn- make-entity
  ;; "make-entity wraps an Entity in a function.  It memoizes
  ;; metadata (key, kind, id, name, etc.)  as a 'keymap' for use as
  ;; clojure metadata; since this data is immutable, there is no reason
  ;; not to memoize it.  (TODO: see about using deftype for Entities;
  ;; prob. is metadata)"
  [theEntity]
  (do ;;(prn "making entity " theEntity)
      (let [theKey (.getKey theEntity)]
        ;; then construct function
        ^{:entity theEntity
          :parent (.getParent theEntity)
          :type ::Entity ;; :appengine-magic.service.datastore/Entity
          :key (.getKey theEntity)
          :kind (keyword (.getKind theEntity))
          :namespace (.getNamespace theEntity)
          :name (.getName theKey)
          :id (.getId theKey)
          :keystring (.toString theKey)
          :keystringrep (KeyFactory/keyToString theKey)}
        (fn [& kw]
          ;; the main job of the function is to lookup properties
          ;; TODO: accomodate iteration, seq-ing, etc
          ;; e.g.  (into myEnt {:foo "bar"})
          ;; also conj, into, etc.
          ;; e.g.  (conj myEnt {:foo "bar"})
          ;; etc.
          ;; only way I see to do this as of now is local replacement
          ;; funcs in our namespace
          (if (nil? kw)
            (let [props (.getProperties theEntity)]
              (into {} (map (fn [item]
                              {(keyword (.getKey item))
                               (.getValue item)}) props)))
            (.getProperty theEntity (name kw)))))))

(defprotocol GAEDS
  (get-entity-with-fields [keymap])
  ;; (meta? [theEntity])
  (ds [this])
  (Keys [keymap])
  (Entities [e]))

(extend-protocol GAEDS
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  com.google.appengine.api.datastore.Key
  (ds [theKey]
    (do ;; (prn "ds applied to Key" theKey)
        (let [theEntity (.get (get-datastore-service) theKey)]
          ;; (prn "ds applied to map")
          ;; (prn (str "made key: " theKey))
          ;; (prn (str "fetched entity: " theEntity))
          (make-entity theEntity))))
  (Entities [theKey]
    ;; if entity already exists return it as ds/Entity else create it
    (let [theEntity
          (try (.get (get-datastore-service) theKey)
               (catch EntityNotFoundException e1 ) ;; (prn "NOT FOUND"))
               (catch IllegalArgumentException e2 (prn "ILLEGAL ARG TO GET"))
               (catch DatastoreFailureException e3
                 (prn "DatastoreFailureException")))]
      (if theEntity
        (do (prn "FOUND")
            (make-entity theEntity))
        (do (prn "NOT FOUND")
            (let [theEntity (Entity. theKey)]
              (do (.put (get-datastore-service) theEntity)
                  (make-entity theEntity)))))))


  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  com.google.appengine.api.datastore.Entity
  (ds [theEntity] (throw (Exception. "ds applied to Entity")))
  (Entities [theEntity] (throw (Exception. "Entities applied to Entity")))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  clojure.lang.PersistentArrayMap       ; e.g.  {:a 100 :b 200}
    (get-entity-with-fields
    [{:keys [kind name id] :as keymap}]
;;    [{:keys [kind id theKey] :as keymap}]
    ;; TODO: validate keymap
    ;; TODO: handle both string and nbr ids
    (do ;;(prn "get-entity-with-fields applied to keymap" keymap)
        (let [theKey (KeyFactory/createKey
                      (clojure.core/name kind)
                      (if id id name))
              theEntity (.get (get-datastore-service) theKey)
              props (.getProperties theEntity)]
          ;; props = java.util.Collections$UnmodifiableMap
          ;; prop = java.util.Collections
          ;;		$UnmodifiableMap$UnmodifiableEntrySet$UnmodifiableEntry
          (into {} (map (fn [item]
                          {(keyword (.getKey item))
                           (.getValue item)}) props)))))
    (Keys [{:keys [kind name id] :as keymap}]
       (do (prn "Keys applied to keymap" keymap)
           (let [theKey  (KeyFactory/createKey (clojure.core/name kind)
                                      (if id id
                                          name))]
             theKey)))
    (ds [{:keys [kind name id] :as keymap}]
      ;; TODO: handle bad keymap
      ;; use :pre ?
      (do ;;(prn "ds applied to keymap")
        (let [theKey (KeyFactory/createKey
                      (clojure.core/name kind)
                      (if id id name))
              theEntity (.get (get-datastore-service) theKey)]
          (make-entity theEntity))))
    (Entities
      [theEntityMap]
      (let [{:keys [kind name id]} (meta theEntityMap)]
        (do ;; (prn "ds/Entities applied to EntityMap" theEntityMap)
            (if (nil? kind)
              (throw (Exception. "EntityMap must be metadata containing :kind")))
            (if id (if (not (number? id))
                     (throw
                      (Exception. ":id must be numeric"))))
            (entity-from-entitymap theEntityMap))))
        ;; (doseq [[k v] theAugment]
        ;;   (.setProperty (:theEntity theEntity)
        ;;                 ;; todo: deal with val types
        ;;                 (name k) (if (number? v)
        ;;                            v
        ;;                            (name v))))))

  ;; clojure.lang.PersistentHashMap        ; e.g.  #{:a 100 :b 200}

  clojure.lang.IFn
  (ds [theEntity] (throw (Exception. "ds applied to fn")))
  (Entities [theEntity]
    ;; TODO: validate theEntity
    {:pre [;; (do (prn "e map meta: " (meta theEntity))
           ;;     (prn "e map id: "   (:id (meta theEntity))) true),
           (if (nil? ( :kind (meta theEntity)))
             (and (not (nil? ((meta theEntity) :key)))
                  (and (nil? ((meta theEntity) :id))
                       (nil? ((meta theEntity) :name))))
             (and (nil? (:key (meta theEntity)))
                  (or (nil? (:id (meta theEntity)))
                      (nil? (:name (meta theEntity)))))),
           (if (not (nil? (:id (meta theEntity))))
             (number? (:id (meta theEntity)))
             true),
           (if (not (nil? (:name (meta theEntity))))
             (or (string?  (:name (meta theEntity)))
                 (keyword? (:name (meta theEntity))))
             true)
           ;; TODO: validate :parent
           ]}
    (do (prn "Entities applied to fn" (meta theEntity))
        (let [{:keys [key kind id name parent]} (meta theEntity)
;;              arg1 (if (nil? kind) key 
              arg2 (if id id (if name name nil))
              arg3 (if (nil? parent) nil
                       (cond
                        (= (type parent)
                           :appengine-magic.service.datastore/Key)
                           ;;no yet
                           nil
                        (= (type parent)
                           :appengine-magic.service.datastore/Entity)
                           (:key (meta parent))
                        :else  ;; type parent = EntityMap
                           (:key (meta (Entities parent)))))
                           ;; OR: (ds/keys ds parent)))))
              theEntity (if (nil? kind) (Entity. key)
                            (if (nil? parent)
                              (Entity. (clojure.core/name kind)
                                       (if id id (if name name)))
                              (Entity. (clojure.core/name kind)
                                       (if id id (if name name))
                                       ;; stipulate: arg3 = :Key
                                       arg3)))]
          (doseq [[k v] theEntity]
            ;; TODO: handle val types
            (.setProperty theEntity
                          (clojure.core/name k)
                          (clojure.core/name v)))
          ;; TODO: make-entity s/b resonsible for putting if needed
          (.put (get-datastore-service) theEntity)
          (make-entity theEntity))))
          ;; {:theKey (.put (get-datastore-service) theEntity)
          ;;  :theEntity theEntity})))
  ) ;; extend-protocol
