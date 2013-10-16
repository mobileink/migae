(ns org.mobileink.migae.data.docs.persist-test
  (:import [com.google.appengine.tools.development.testing
            LocalServiceTestHelper
            LocalServiceTestConfig
            LocalSearchServiceTestConfig]
           [java.util Date]
           [java.text SimpleDateFormat])
  ;; (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [clojure.math.numeric-tower :as math :only [expt]]
            [org.mobileink.migae.data.docs :as docs]
            [clojure.tools.logging :as log :only [debug info warn]]))
            ;; [ring-zombie.core :as zombie]))

(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
  `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))

(defn- search-fixture
  [test-fn]
  (let [helper (LocalServiceTestHelper.
                (into-array LocalServiceTestConfig
                            [(LocalSearchServiceTestConfig.)]))]
    (do (.setUp helper)
        (docs/get-docs-service) 
        (test-fn)
        (.tearDown helper))))

;(use-fixtures :once (fn [test-fn] (docs/get-docs-service) (test-fn)))
(use-fixtures :each search-fixture)

(deftest ^:init search-init
  (testing "DS init"
    (is (= com.google.appengine.api.search.SearchServiceImpl
           (class (docs/get-docs-service))))
    (is (= com.google.appengine.api.search.SearchServiceImpl
           (class @docs/*docs-service*)))))

;; من معلقة لبيد بن ربيعة
(def atom_ar1 "بِمِنىً تَأَبَّدَ غَوْلُهَا فرِجَامُهَا")
(def atom_ar2 "عَفَتِ الدِّيَارُ مَحَلّهَا فَمُقَامُهَا")
(def atom_ar3 "خَلَقاً كما ضَمِنَ الوِحيُ سِلامُهَا")
(def atom_ar4 "فَمَدافِعُ الرّيَّانِ عُرِّيَ رَسْمُهَا")
(def html_ar1 "<section>قال دمنة: زعموا أن غديراً كان فيه ثلاث سمكاتٍ: كيسةٌ وأكيس منها وعاجزةٌ؛ وكان ذلك الغدير بنجوةً من الأرض لا يكاد يقربه أحدٌ وبقربه نهر جارٍ. فاتفق أنه اجتاز بذلك النهر صيادان؛ فأبصرا الغدير، فتواعدا أن يرجعا إليه بشباكهما فيصيدا ما فيه من السمك. فسمع السمكات قولهما: فأما أكيسهن لما سمعت قولهما، وارتابت بهما، وتخوفت منهما؛ فلم تعرج على شيءٍ حتى خرجت من المكان الذي يدخل فيه الماء من النهر إلى الغدير</section>")
(def html_ar2 "<p>قال الأسد: <b>قد فهمت ذلك</b> ؛ ولا أظن الثور يغشني ويرجو لي الغوائل . وكيف يفعل ولم ير مني سوءاً قطُّ? ولم أدع خيراً إلا فعلته معه? ولا أمنيةً إلا بلغته إياها?. قال دمنة: إن اللئيم لا يزال نافعاً ناصحاً حتى يرفع إلى المنزلة التي ليس لها بأهل؛ فإذا بلغها التمس ما فوقها؛ ولا سيما أهل الخيانة والفجور: فإن اللئيم الفاجر لا يخدم السلطان ولا ينصح له إلا من فرقٍ . فإذا استغنى وذهبت الهيبة عاد إلى جوهره؛ كذنب الكلب الذي يربط ليستقيم فلا يزال مستوياً ما دام مربوطاً؛ فإذا حل انحنى واعوج كما كان. واعلم أيها الملك أنه من لم يقبل من نُصائِحه ما يثقل عليه مما ينصحون له به، لم يحمد رأيه؛ كالمريض الذي يدع ما يبعث له الطبيب؛ ويعمد إلى ما يشتهيه. وحق على موازر السلطان أن يبالغ في التحضيض له على ما يزيد من سلطانه قوةً ويزينه؛ والكف عما يضره ويشينه؛ وخير الإخوان والأعوان أقلهم مداهنة في النصيحة؛ وخير الثناء ما كان على أفواه الأخيار؛ وأشرف الملوك من لم يخالطه بطرٌ؛ وخير الأخلاق أعونها على الورع. وقد قيل: لو أن أمراً توسد النار وافترش الحيات، كان أحق ألا يهنئه النوم. والرجل إذا أحس من صاحبه بعداوةٍ يريده بها؛ لا يطمئن إليه؛ وأعجز الملوك آخذهم بالهوينى، وأقلهم نظراً في مستقبل الأمور</p>")
(def text_ar1 "قال دمنة: زعموا أن غديراً كان فيه ثلاث سمكاتٍ: كيسةٌ وأكيس منها وعاجزةٌ؛ وكان ذلك الغدير بنجوةً من الأرض لا يكاد يقربه أحدٌ وبقربه نهر جارٍ. فاتفق أنه اجتاز بذلك النهر صيادان؛ فأبصرا الغدير، فتواعدا أن يرجعا إليه بشباكهما فيصيدا ما فيه من السمك. فسمع السمكات قولهما: فأما أكيسهن لما سمعت قولهما، وارتابت بهما، وتخوفت منهما؛ فلم تعرج على شيءٍ حتى خرجت من المكان الذي يدخل فيه الماء من النهر إلى الغدير")
(def text_ar2 " قال الأسد: قد فهمت ذلك؛ ولا أظن الثور يغشني ويرجو لي الغوائل . وكيف يفعل ولم ير مني سوءاً قطُّ? ولم أدع خيراً إلا فعلته معه? ولا أمنيةً إلا بلغته إياها?. قال دمنة: إن اللئيم لا يزال نافعاً ناصحاً حتى يرفع إلى المنزلة التي ليس لها بأهل؛ فإذا بلغها التمس ما فوقها؛ ولا سيما أهل الخيانة والفجور: فإن اللئيم الفاجر لا يخدم السلطان ولا ينصح له إلا من فرقٍ . فإذا استغنى وذهبت الهيبة عاد إلى جوهره؛ كذنب الكلب الذي يربط ليستقيم فلا يزال مستوياً ما دام مربوطاً؛ فإذا حل انحنى واعوج كما كان. واعلم أيها الملك أنه من لم يقبل من نُصائِحه ما يثقل عليه مما ينصحون له به، لم يحمد رأيه؛ كالمريض الذي يدع ما يبعث له الطبيب؛ ويعمد إلى ما يشتهيه. وحق على موازر السلطان أن يبالغ في التحضيض له على ما يزيد من سلطانه قوةً ويزينه؛ والكف عما يضره ويشينه؛ وخير الإخوان والأعوان أقلهم مداهنة في النصيحة؛ وخير الثناء ما كان على أفواه الأخيار؛ وأشرف الملوك من لم يخالطه بطرٌ؛ وخير الأخلاق أعونها على الورع. وقد قيل: لو أن أمراً توسد النار وافترش الحيات، كان أحق ألا يهنئه النوم. والرجل إذا أحس من صاحبه بعداوةٍ يريده بها؛ لا يطمئن إليه؛ وأعجز الملوك آخذهم بالهوينى، وأقلهم نظراً في مستقبل الأمور")

(def atom_en1 "Charles Dickens")
(def two-cities-author atom_en1)
(def atom_en1-search-str "atom_en1: Dickens")
(def atom_en2 "A Tale of Two Cities")
(def two-cities-title atom_en2)
(def atom_en2-search-str "atom_en2: Two")

(def html_en1 "<section><p>It was the best of times, it was the worst of times...</p></section>")
(def two-cities-text html_en1)
(def html_en1-search-str "times")
(def html_en2 "<subsection><p>There were a king with a large jaw and a
queen with a plain face, on the throne of <a
href='http:/example.org/england'>England</a>; there were a king with a
large jaw and a queen with a fair face, on the throne of <a
href='http:/example.org/france'>France</a>.</p></subsection>")
(def html_en1-search-str "England")

(def text_en1 "It was the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way-- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison only.
")
(def text_en1-search-str "foolishness")

(def text_en2 "
There were a king with a large jaw and a queen with a plain face, on the
throne of England; there were a king with a large jaw and a queen with
a fair face, on the throne of France. In both countries it was clearer
than crystal to the lords of the State preserves of loaves and fishes,
that things in general were settled for ever.

It was the year of Our Lord one thousand seven hundred and seventy-five.
Spiritual revelations were conceded to England at that favoured period,
as at this. Mrs. Southcott had recently attained her five-and-twentieth
blessed birthday, of whom a prophetic private in the Life Guards had
heralded the sublime appearance by announcing that arrangements were
made for the swallowing up of London and Westminster. Even the Cock-lane
ghost had been laid only a round dozen of years, after rapping out its
messages, as the spirits of this very year last past (supernaturally
deficient in originality) rapped out theirs. Mere messages in the
earthly order of events had lately come to the English Crown and People,
from a congress of British subjects in America: which, strange
to relate, have proved more important to the human race than any
communications yet received through any of the chickens of the Cock-lane
brood.")
(def text_en2-search-str "Westminster")

(def mirth-author "Edith Wharton")
(def mirth-title "The House of Mirth")
(def mirth-text "Selden paused in surprise. In the afternoon rush of
the Grand Central Station his eyes had been refreshed by the sight of
Miss Lily Bart.")

(def usher-author "Edgar Allen Poe")
(def usher-title "The Fall of the House of Usher")
(def usher-text "During the whole of a dull, dark, and soundless day
in the autumn of the year, when the clouds hung oppressively low in
the heavens, I had been passing alone, on horseback, through a
singularly dreary tract of country; and at length found myself, as the
shades of the evening drew on, within view of the melancholy House of
Usher.")

;; dates are java.util.Date objects.  To create such objects, use
;; java.text.DateFormat$parse or java.text.SimpleDateFormat$parse
;; or java.util.Calendar$set
(def today (Date.))
(def date1 (.parse (SimpleDateFormat. "MM/dd/yyyy") "01/01/1901"))
(def date1-search-str "date1:date < 1965-01-01")
(def date2 (.parse (SimpleDateFormat. "MM/dd/yyyy") "07/14/1789"))
(def date2-search-str "date1:date < 1965-01-01")

(def nbr1 (double 1024))
(def nbr1-search-str "nbr1 > 0")
(def nbr2 (double 19999999))
(def nbr2-search-str "nbr2 > 0")

(def fldName500 (apply str (repeat 500 "x")))
(def fldName501 (apply str (repeat 501 "x")))

(def MB (math/expt 1024 2))

;; (with-private-fns [org.mobileink.migae.data.docs [make-doc]]

(deftest ^:en make-doc-en1
  (testing "make en doc without id"
    (let [theDoc (docs/make-doc
                  ;; ^:my_id
                  {:atom_en1 atom_en1
                   :atom_en2 atom_en2
                   :date1 date1
                   :date2 date2
                   :html_en1 {:html html_en1}
                   :html_en2 {:html html_en2}
                   :nbr1 nbr1
                   :nbr2 nbr2
                   :text_en1 text_en1
                   :text_en2 text_en2})]
      (is (= (docs/atom theDoc "atom_en1") atom_en1))
      (is (= (docs/atom theDoc "atom_en2") atom_en2))
      (is (= (docs/date theDoc "date1") date1))
      (is (= (docs/date theDoc "date2") date2))
      (is (= (docs/html theDoc "html_en1") html_en1))
      (is (= (docs/html theDoc "html_en2") html_en2))
      (is (= (docs/nbr  theDoc "nbr1") nbr1))
      (is (= (docs/nbr  theDoc "nbr2") nbr2))
      (is (= (docs/text theDoc "text_en1") text_en1))
      (is (= (docs/text theDoc "text_en2") text_en2)))))

(deftest ^:en make-doc-en2
  (testing "make en doc with en id"
    (let [theDoc (docs/make-doc
                  ^:my_id
                  {:atom_en1 atom_en1
                   :atom_en2 atom_en2
                   :date1 date1
                   :date2 date2
                   :html_en1 {:html html_en1}
                   :html_en2 {:html html_en2}
                   :nbr1 nbr1
                   :nbr2 nbr2
                   :text_en1 text_en1
                   :text_en2 text_en2})]
      (is (= (docs/atom theDoc "atom_en1") atom_en1))
      (is (= (docs/atom theDoc "atom_en2") atom_en2))
      (is (= (docs/date theDoc "date1") date1))
      (is (= (docs/date theDoc "date2") date2))
      (is (= (docs/html theDoc "html_en1") html_en1))
      (is (= (docs/html theDoc "html_en2") html_en2))
      (is (= (docs/nbr  theDoc "nbr1") nbr1))
      (is (= (docs/nbr  theDoc "nbr2") nbr2))
      (is (= (docs/text theDoc "text_en1") text_en1))
      (is (= (docs/text theDoc "text_en2") text_en2)))))

;; ################################
;;    MULTIVALS - fields can store multiple vals
(deftest ^:multivals multivals-1
  (testing "one field, two atoms"
    (let [theDoc (docs/make-doc
                  ;; ^:my_id
                  {:field_en1 #{atom_en1 atom_en2}})]
      (let [vals (docs/vals theDoc :field_en1)]
        (do
          ;; (doseq [val vals] (log/debug (format "key: %s val: %s"
          ;;                                      :field_en1 val)))
          (is (=  (seq #{atom_en1 atom_en2}) vals))
          (is (= (count vals) 2)))))))

(deftest ^:multivals multivals-2
  (testing "one field, two html vals"
    (let [theDoc (docs/make-doc
                  ;; ^:my_id
                  {:fld #{ {:html html_en1} {:html html_en2}}})]
      (let [htmls (docs/vals theDoc :fld)]
        (do
          ;; (log/debug (format "theDoc %s" theDoc))
          ;; (doseq [t ts]
          ;;   (log/debug (format "key: %s val: %s" :field_en1 t)))
          (is (= (count htmls) 2))
          (is (= (seq #{html_en1 html_en2}) htmls))
          )))))

(deftest ^:multivals multivals-3
  (testing "one field, two text vals"
    (let [theDoc (docs/make-doc
                  ;; ^:my_id
                  {:fld #{ {:text text_en1} {:text text_en2}}})]
      (let [texts (docs/vals theDoc :fld)]
        (do
          ;;(log/debug (format "theDoc %s" theDoc))
          ;; (doseq [t ts]
          ;;   (log/debug (format "key: %s val: %s" :field_en1 t)))
          (is (= (count texts) 2))
          (is (= (seq #{text_en1 text_en2}) texts))
          )))))

;; TODO:  fail test: multivals for date and nbr fields disallowed

;; TODO: more tests: all combinations of value types for single field
;; name

;; Expected fails:
;; (deftest ^:multivals multivals-5
;;   (testing "one field, multivals, expected Exception: misuse of :html")
;;     (let [textval (apply str (repeat 1000 "x"))
;;           ;; x (log/debug (format "1000 xs:%s" textval))
;;           theDoc (docs/make-doc
;;                   ;; ^:my_id
;;                   {:field_en1 #{atom_en1
;;                                 :html html_en1
;;                                 textval}})]
;;       (let [vals (docs/vals theDoc :field_en1)
;;             ts   (docs/types theDoc :field_en1)]
;;         (do
;;           (doseq [t ts]
;;             (log/debug (format "key: %s val: %s" :field_en1 t)))
;;           (is (=  (seq #{atom_en1 textval (:html {:html html_en1})})
;;                   vals))
;;           (is (= (count vals) 3))))))

;; (deftest ^:multivals multivals-6
;;   (testing "one field, multivals, expected Exception: keywords not allowed as field vals")
;;     (let [textval (apply str (repeat 1000 "x"))
;;           ;; x (log/debug (format "1000 xs:%s" textval))
;;           theDoc (docs/make-doc
;;                   ;; ^:my_id
;;                   {:field_en1 #{atom_en1
;;                                 :foo
;;                                 textval}})]
;;       (let [vals (docs/vals theDoc :field_en1)
;;             ts   (docs/types theDoc :field_en1)]
;;         (do
;;           (doseq [t ts]
;;             (log/debug (format "key: %s val: %s" :field_en1 t)))
;;           (is (=  (seq #{atom_en1 textval :foo})
;;                   vals))
;;           (is (= (count vals) 3))))))

;; ################################
;;  MIXEDVALS: fields can store multiple val types

(deftest ^:mixedvals mixedvals-1
  (testing "one field, two val types: atom and html"
    (let [theDoc (docs/make-doc
                  ;; ^:my_id
                  {:field_en1 #{atom_en1
                                {:html html_en2}}})]
      (let [vals (docs/vals theDoc :field_en1)]
        (do
          ;; (doseq [val vals]
          ;;   (log/debug (format "key: %s val: %s" :field_en1 val)))
          (is (=  (seq #{atom_en1 (:html {:html html_en2})})
                  vals))
          (is (= (count vals) 2)))))))

(deftest ^:mixedvals mixedvals-2
  (testing "one field, two val types: atom and text"
    (let [textval (apply str (repeat 1000 "x"))
          ;; x (log/debug (format "1000 xs:%s" textval))
          theDoc (docs/make-doc
                  ;; ^:my_id
                  {:field_en1 #{atom_en1 textval}})]
      (let [vals (docs/vals theDoc :field_en1)
            ts   (docs/types theDoc :field_en1)]
        (do
          ;; (doseq [t ts]
          ;;   (log/debug (format "key: %s val: %s" :field_en1 t)))
          (is (=  (seq #{atom_en1 textval})
                  vals))
          (is (= (count vals) 2)))))))

(deftest ^:mixedvals mixedvals-3
  (testing "one field, three types: atom, html, text"
    (let [textval (apply str (repeat 1000 "x"))
          ;; x (log/debug (format "1000 xs:%s" textval))
          theDoc (docs/make-doc
                  ;; ^:my_id
                  {:field_en1 #{atom_en1
                                {:html html_en1}
                                textval}})]
      (let [vals (docs/vals theDoc :field_en1)
            ts   (docs/types theDoc :field_en1)]
        (do
          ;; (doseq [t ts]
          ;;   (log/debug (format "key: %s typ: %s" :field_en1 t)))
          (is (=  (seq #{atom_en1 textval (:html {:html html_en1})})
                  vals))
          (is (= (count vals) 3)))))))


;; ################################
;;     PERSIST  (PUT)
(deftest ^:persist persist-1
  (testing "persist one doc, no id"
    (let [usher (docs/make-doc
                  ;; ^:my_id
                  {:title usher-title
                   :author usher-author
                   :text usher-text})
          putrs (docs/persist :MyDocs [usher])
          ids (.getIds putrs)]
      (do
        ;; (log/debug "persist response: " (.toString putrs))
        (doseq [id ids]
          (log/debug "doc id: " (.toString id)))
        (doseq [putr putrs]
          (log/debug (format "persist result: %s" (str putr))))))))

(deftest ^:persist persist-2
  (testing "persist one doc with id"
    (let [usher (docs/make-doc
                  ^:my_id
                  {:title usher-title
                   :author usher-author
                   :text usher-text})
          putrs (docs/persist :MyDocs [usher])
          ids (.getIds putrs)]
      (do
        ;; (log/debug "persist response: " (.toString putrs))
        (doseq [id ids]
          (log/debug "doc id: " (.toString id)))
        (doseq [putr putrs]
          (log/debug (format "persist result: %s" (str putr))))))))

(deftest ^:persist persist-3
  (testing "persist two docs without ids"
    (let [usher (docs/make-doc {:title usher-title
                                :author usher-author
                                :text usher-text})
          mirth (docs/make-doc {:title mirth-title
                                :author mirth-author
                                :text mirth-text})
          putrs (docs/persist :MyDocs [usher mirth])
          ids (.getIds putrs)]
      (do
        ;; (log/debug "persist response: " (.toString putrs))
        (doseq [id ids]
          (log/debug "doc id: " (.toString id)))
        (doseq [putr putrs]
          (log/debug (format "persist result: %s" (str putr))))))))

(deftest ^:persist persist-4
  (testing "persist two docs with ids"
    (let [usher (docs/make-doc ^:usher {:title usher-title
                                        :author usher-author
                                        :text usher-text})
          mirth (docs/make-doc ^:mirth {:title mirth-title
                                        :author mirth-author
                                        :text mirth-text})
          putrs (docs/persist :MyDocs [usher mirth])
          ids (.getIds putrs)]
      (do
        (log/debug "persist response: " (.toString putrs))
        (doseq [id ids]
          (log/debug "doc id:" (.toString id)))
        (doseq [putr putrs]
          (log/debug (format "persist result: %s" (str putr))))))))

(deftest ^:persist persist-5
  (testing "persist two docs, one with one without id"
    (let [usher (docs/make-doc ^:usher {:title usher-title
                                        :author usher-author
                                        :text usher-text})
          mirth (docs/make-doc {:title mirth-title
                                :author mirth-author
                                :text mirth-text})
          putrs (docs/persist :MyDocs [usher mirth])
          ids (.getIds putrs)]
      (do
        (log/debug "persist response: " (.toString putrs))
        (doseq [id ids]
          (log/debug "doc id:" (.toString id)))
        (doseq [putr putrs]
          (log/debug (format "persist result: %s" (str putr))))))))

;; ################################
;;     DELETE docs from index

;; ################################
;;     FETCH (get docs from index by doc id)

;; java api:
;; get(String) - doc-id string
;; getRange(...)
;; same, with Async

;; ################################
;;     FETCH (get docs from index by search)

;; java api:
    ;; search(Query)
    ;; search(String)
    ;; searchAsync(Query)
    ;; searchAsync(String)


(deftest ^:fetch fetch-atoms-1
  (testing "query on atom fields"
    (let [queryString "here"
          indexKey :MyDocs
          doc1 (docs/make-doc
                ^:mydoc1
                 {:fld1 "foobar1"
                  :fld2 "this is doc 1"})
          doc2 (docs/make-doc
                ^:mydoc2
                {:fld1 "foobar2"
                 :fld2 "that is doc 2"})
          doc3 (docs/make-doc
                ^:mydoc3
                {:fld1 "foobar3"
                 :fld2 "this is document 3"})
          r (docs/persist :MyDocs [doc1 doc2 doc3])
          response (docs/fetch :MyDocs "fld2: is")]
      ;; (is (= (.getNumberFound response) 2))
      (is (= (docs/found-count response) 2))
      (is (= (docs/returned-count response) 2))
      ;; (log/warn "query result: " response)
      (doseq [r (iterator-seq (.iterator response))]
        (log/warn "result doc: " (docs/html r :html_en1))))))
      ;; (is (= (docs/html doc :html_en1)
      ;;        "<html><p>this is doc 2</p></html>"))


