(ns typed-test.cljc.checker.check.fn-methods
  (:require [clojure.test :refer [deftest is]]
            [typed.clj.checker.test-utils :refer :all]
            [typed.cljs.checker.test-utils :as cljs]))

;;TODO play with checking in cljs, I found an error msg that contained: (fn [nil nil nil nil nil])
(deftest not-a-function-type-error-test
  (is-tc-e (do (typed.clojure/ann item-validator [Validator :-> [t/Any t/Any t/Int t/Any t/Any :-> t/Any]])
               (defn item-validator [valid?]
                 (fn [_ _ pos coll k])))))