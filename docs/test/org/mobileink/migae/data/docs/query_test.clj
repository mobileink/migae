(ns org.mobileink.migae.data.docs.query-test
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


;;;;;;;;;;;;;;;; ATOMS - search string must match ENTIRE val
;; https://developers.google.com/appengine/docs/java/search/query_strings#Java_Queries_on_atom_fields
;; "The only valid relational operator for atom fields is the equality
;; operator. The complete contents of an atom field must match the
;; query value. Stemming is not supported for atom fields."

;; CAVEAT: searching atoms for vals containing spaces seems not to
;; work at least on the dev server.  Or at least I cannot get such
;; queries to succeed.
(deftest ^:fetch fetch-atom-1
  (testing "query on atom fields"
    (let [indexKey :MyDocs
          fld2-1 "this is doc 1"
          fld2-2 "thatisdoc2"
          fld2-3 "this is document 3"
          doc1 (docs/make-doc
                ^:mydoc1
                 {:fld1 "foobar1"
                  :fld2 fld2-1})
          baz (log/debug doc1)
          doc2 (docs/make-doc
                ^:mydoc2
                {:fld1 "foobar2"
                 :fld2 fld2-2})
          bar (log/debug doc2)
          doc3 (docs/make-doc
                ^:mydoc3
                {:fld1 "foobar3"
                 :fld2 fld2-3})
          foo (log/debug doc3)
          r (docs/persist indexKey [doc1 doc2 doc3])]
      (let [r1 (docs/fetch indexKey (str "fld2: " fld2-1))
            r2 (docs/fetch indexKey (str "fld2: " fld2-2))
            r3 (docs/fetch indexKey (str "fld2: \\\"" fld2-3 "\\\""))
            bad1 (docs/fetch indexKey "fld2: doc") ; fails
            bad2 (docs/fetch indexKey "fld2: is")  ; fails
            ]
        (is (= (docs/found-count r1) 1))
        (is (= (docs/found-count r2) 1))
        (is (= (docs/found-count r3) 1))
        (is (= (docs/returned-count bad1) 0))
        (is (= (docs/returned-count bad2) 0))
        ))))
        ;; (doseq [r r1)]
        ;;   (log/warn "result doc: " (docs/val r1 :fld2))))))


;;;;;;;;;;;;;;;; HTML and TEXT searches
;; General syntax checks:

(deftest ^:syntax fetch-syntax-1
  (testing "query with too many val keys"
    (let [indexKey :MyDocs
          doc1 (try (docs/make-doc
                     ^:mydoc1
                     {:fld1 "foobar1"
                      :fld2 {:html "this is doc 1 html field"
                             :foo "this is a disallowed mapentry"}})
                    (catch Exception e
                      (log/error (.getMessage e))
                      nil))]
      (is (= doc1 nil)))))

(deftest ^:syntax fetch-syntax-2
  (testing "query with too many val keys"
    (let [indexKey :MyDocs
          doc1 (try (docs/make-doc
                     ^:mydoc1
                     {:fld1 "foobar1"
                      :fld2 {:text "this is doc 1 text field"
                             :foo "this is a disallowed mapentry"}})
                    (catch Exception e
                      (log/error (.getMessage e))
                      nil))]
      (is (= doc1 nil)))))

(deftest ^:syntax fetch-syntax-3
  (testing "query with bad val key"
    (let [indexKey :MyDocs
          doc1 (try (docs/make-doc
                     ^:mydoc1
                     {:fld1 "foobar1"
                      :fld2 {:foo "this is an invalid val key"}})
                    (catch Exception e
                      (log/error (.getMessage e))
                      nil))]
      (is (= doc1 nil)))))

(deftest ^:syntax fetch-syntax-4
  (testing "query with bad val key - expect ERROR message"
    (let [indexKey :MyDocs
          doc1 (try (docs/make-doc
                     ^:mydoc1
                     {:fld1 "foobar1"
                      :fld2 #{{:text "this is a text value"}
                              {:text "and another"
                               :foo "extra key"}}})
                    (catch Exception e
                      (log/error (.getMessage e))
                      e))]
      ;; TODO: define exceptions, so:  (is (= (code e) FooException))
      (is (= doc1 nil)))))

;;;;;;;;;;;;;;;; HTML searches
;; https://developers.google.com/appengine/docs/java/search/query_strings#Java_Queries_on_text_and_HTML_fields

;; The only valid relational operator for text and HTML fields is
;; equality. In this case the operator means "field includes value"
;; not "field equals value." You can use the stemming operator to search
;; for variants on a word. You can also use the OR and AND operators to
;; specify complex boolean expressions for the field value. If a boolean
;; operator appears within a quoted string, it is not treated specially,
;; it's just another piece of the character string to be
;; matched. Remember that when searching HTML fields, the text within
;; HTML markup tags is ignored.

(deftest ^:fetch fetch-html-1
  (testing "query on html field"
    (let [indexKey :MyDocs
          doc1 (docs/make-doc
                ^:mydoc1
                 {:fld1 "foobar1"
                  :fld2 {:html "this is doc 1"}})
          baz (log/debug doc1)
          doc2 (docs/make-doc
                ^:mydoc2
                {:fld1 "foobar2"
                 :fld2 {:html "that is doc 2"}})
          bar (log/debug doc2)
          doc3 (docs/make-doc
                ^:mydoc3
                {:fld1 "foobar3"
                 :fld2 {:html "<p>this is document 3</p>"}})
          foo (log/debug doc3)
          r (docs/persist indexKey [doc1 doc2 doc3])
          response (docs/fetch indexKey "fld2: doc")]
      (is (= (docs/found-count response) 2))
      (is (= (docs/returned-count response) 2))
      (doseq [r (iterator-seq (.iterator response))]
        (log/warn "result doc: " (docs/val r :fld2))))))

(deftest ^:fetch fetch-html-2
  (testing "query on html field"
    (let [indexKey :MyDocs
          doc1 (docs/make-doc
                ^:mydoc3
                {:fld1 "foobar3"
                 :fld2 {:html #{"<p>this is html 1</p>"
                                "<p>this is html 2</p>"}}})
          foo (log/debug doc1)
          r (docs/persist indexKey [doc1])
          response (docs/fetch indexKey "fld2: doc")]
      (is (= (docs/found-count response) 2))
      (is (= (docs/returned-count response) 2))
      (doseq [r (iterator-seq (.iterator response))]
        (log/warn "result doc: " (docs/val r :fld2))))))


;;;;;;;;;;;;;;;; TEXT searches
;; see above, HTML searches, for semantics

(deftest ^:fetch fetch-text-1
  (testing "query on text field"
    (let [indexKey :MyDocs
          doc1 (docs/make-doc
                ^:mydoc1
                 {:fld1 "foobar1"
                  :fld2 {:text "this is doc 1"}})
          baz (log/debug doc1)
          doc2 (docs/make-doc
                ^:mydoc2
                {:fld1 "foobar2"
                 :fld2 {:text "that is doc 2"}})
          bar (log/debug doc2)
          doc3 (docs/make-doc
                ^:mydoc3
                {:fld1 "foobar3"
                 :fld2 {:text "<p>this is document 3</p>"}})
          foo (log/debug doc3)
          r (docs/persist indexKey [doc1 doc2 doc3])
          response (docs/fetch indexKey "fld2: doc")]
      (is (= (docs/found-count response) 2))
      (is (= (docs/returned-count response) 2))
      (doseq [r (iterator-seq (.iterator response))]
        (log/warn "result doc: " (docs/val r :fld2))))))

;; ################################################################
;;     TOKENS

;; e.g. search for "foo" should match {:text "foo.bar"},
;; {:html "<p>foo-bar</p>}, etc




