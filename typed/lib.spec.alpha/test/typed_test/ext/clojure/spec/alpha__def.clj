(ns ^:no-doc typed-test.ext.clojure.spec.alpha__def
  (:require [clojure.test :refer [deftest is testing]]
            typed.ext.clojure.spec.alpha
            [clojure.core.typed :as t]
            [typed.clj.checker.parse-unparse :as prs]
            [clojure.core.typed.test.test-utils :refer :all]))

(deftest sdef-test
  (is-tc-e (s/def :foo/bar int?)
           :requires [[clojure.spec.alpha :as s]])
  (is-tc-err (ann-form (s/def :foo/bar int?) nil)
             :requires [[clojure.spec.alpha :as s]]))