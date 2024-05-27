(ns typed.clj.provider.spec1
  (:refer-clojure :exclude [requiring-resolve])
  (:require [typed.spec1.spec-to-type :as s->t]
            [clojure.spec.alpha :as spec1]
            [clojure.core.typed.runtime.jvm.configs :as configs]
            [io.github.frenchy64.fully-satisfies.requiring-resolve :refer [requiring-resolve]]))

(defonce register!
  (delay
    (configs/register-clj-spec1-extensions)))

(defn spec->Type [m opts]
  @register!
  ((requiring-resolve 'typed.clj.checker.parse-unparse/parse-type)
   (s->t/spec->type m opts)))

(defn var-type [var-qsym]
  (some-> (spec1/get-spec var-qsym)
          (spec->Type {::s->t/source var-qsym})))
