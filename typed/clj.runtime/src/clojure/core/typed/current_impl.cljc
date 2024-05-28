;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (https://opensource.org/license/epl-1-0/)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

; untyped, clojure.core.typed depends on this namespace
(ns ^:no-doc clojure.core.typed.current-impl
  #?(:clj (:refer-clojure :exclude [requiring-resolve])
     :cljs (:refer-clojure :exclude [-val]))
  (:require [clojure.core.typed.contract-utils :as con]
            [clojure.core.typed.util-vars :as vs]
            [clojure.set :as set]
            #?(:clj [io.github.frenchy64.fully-satisfies.requiring-resolve :refer [requiring-resolve]])
            [typed.cljc.runtime.env :as env]
            [typed.clj.runtime.env :as clj-env]
            [typed.cljs.runtime.env :as cljs-env]))

;; copied to typed.clj.runtime.env, clojure.core.typed.type-contract
(def clojure :clojure.core.typed.current-impl/clojure)
;; copied to typed.cljs.runtime.env
(def clojurescript ::clojurescript)

;; We don't have a typed.cljr.runtime.env to copy to -- is this a problem?  What does this even mean?
(def cljr ::cljr)

(def unknown ::unknown)

(derive clojure unknown)
(derive clojurescript unknown)
(derive cljr unknown)

;; :clojure = ::clojure
;; :cljs = ::clojurescript
;; :cljr = ::cljr
;; :unknown = ::unknown
#?(:cljs :ignore
:default
(defmacro impl-case [& {clj-case :clojure cljs-case :cljs unknown :unknown :as opts}]
  (let [bad (set/difference (set (keys opts)) #{:clojure :cljs :cljr :unknown})]
    (assert (empty? bad)
            (str "Incorrect cases to impl-case: " (pr-str bad))))
  `(case (current-impl)
     ~clojure ~clj-case
     ~clojurescript ~cljs-case
     ~(if (contains? opts :unknown)
        unknown
        `(assert nil (str "No case matched for impl-case " (current-impl))))))
)		
		

;; copied to typed.clj{s}.runtime.env
(def current-impl-kw ::current-impl)

(defn current-impl []
  {:post [(qualified-keyword? %)]}
  (get (some-> (env/checker-or-nil) deref)
       current-impl-kw
       unknown))

(def register-clj!
  (let [d (delay ((requiring-resolve 'clojure.core.typed.runtime.jvm.configs/register-clj-config-anns)))]
    (fn [] @d)))
(def register-cljs!
  (let [d (delay ((requiring-resolve 'clojure.core.typed.runtime.jvm.configs/register-cljs-config-anns)))]
    (fn [] @d)))

(def current-var-annotations-kw ::current-var-annotations)
(def current-nocheck-var?-kw ::current-nocheck-var?)
(def current-used-vars-kw ::current-used-vars)
(def current-checked-var-defs-kw ::current-checked-var-defs)
(def cljs-jsvar-annotations-kw ::cljs-jsvar-annotations)
(def untyped-var-annotations-kw ::untyped-var-annotations)
(def current-name-env-kw ::current-name-env)
(def method-return-nonnilable-env-kw ::method-return-nonnilable-env)
(def method-param-nilable-env-kw ::method-param-nilable-env)
(def method-override-env-kw ::method-override-env)
(def field-override-env-kw ::field-override-env)
(def constructor-override-env-kw ::constructor-override-env)
(def protocol-name-type ::protocol-name)
(def current-protocol-env-kw ::current-protocol-env)
(def current-datatype-env-kw ::current-datatype-env)
(def current-dt-ancestors-kw ::current-dt-ancestors)
(def current-deps-kw ::current-deps)
(def datatype-name-type ::datatype-name)
(def ns-opts-kw ::ns-options)
(def unanalyzed-special-kw ::unanalyzed-special)
(def current-rclass-env-kw ::current-rclass-env)

(defn add-tc-var-type [sym type]
  (env/swap-checker! assoc-in [current-var-annotations-kw sym] type)
  nil)

(defn add-nocheck-var [sym]
  (env/swap-checker! update current-nocheck-var?-kw (fnil conj #{}) sym)
  nil)

(defn remove-nocheck-var [sym]
  (env/swap-checker! update current-nocheck-var?-kw (fnil disj #{}) sym)
  nil)

(defn var-no-checks []
  {:post [(set? %)]}
  (get (env/deref-checker) current-nocheck-var?-kw #{}))

(defn check-var? [sym]
  (not (contains? (var-no-checks) sym)))

(defn add-tc-type-name [sym ty]
  (env/swap-checker! assoc-in
                     [current-name-env-kw sym]
                     ty
                     #_(if (r/Type? ty)
                       (vary-meta ty assoc :from-name sym)
                       ty))
  nil)

(def declared-name-type ::declared-name)

(defn declare-name* [sym]
  {:pre [(qualified-symbol? sym)]}
  (add-tc-type-name sym declared-name-type)
  nil)

(defn declare-protocol* [sym]
  {:pre [(qualified-symbol? sym)]}
  (add-tc-type-name sym protocol-name-type)
  nil)

(defn declare-datatype* [sym]
  (add-tc-type-name sym datatype-name-type)
  nil)

(defn add-untyped-var [nsym sym t]
  {:pre [(symbol? nsym)
         (symbol? sym)
         ; enforced in var-env/get-untyped-var,
         ; not worth loading/importing r/Type? for this
         ; assertion.
         #_(or (r/Type? t)
               (delay? t)
               (fn? t))]
   :post [(nil? %)]}
  (env/swap-checker! assoc-in [untyped-var-annotations-kw nsym sym] t)
  nil)

(defn add-nonnilable-method-return [sym m]
  {:pre [(qualified-symbol? sym)
         ((some-fn #{:all}
                   (con/set-c? nat-int?))
          m)]}
  (env/swap-checker! assoc-in [method-return-nonnilable-env-kw sym] m)
  nil)

(defn add-method-nilable-param [sym a]
  {:pre [(qualified-symbol? sym)
         ((con/hash-c? (some-fn #{:all} nat-int?)
                       (some-fn #{:all} (con/set-c? nat-int?)))
          a)]}
  (env/swap-checker! assoc-in [method-param-nilable-env-kw sym] a)
  nil)

(defn add-method-override [sym t]
  {:pre [(qualified-symbol? sym)
         ;; checked at `get-method-override`
         #_
         ((some-fn fn? delay? r/Poly? r/FnIntersection?)
          t)]}
  (env/swap-checker! assoc-in [method-override-env-kw sym] t)
  nil)

(defn add-field-override [sym t]
  {:pre [(qualified-symbol? sym)
         ;; checked by `get-field-override`
         #_
         ((some-fn fn? delay? r/Type?)
          t)]}
  (env/swap-checker! assoc-in [field-override-env-kw sym] t)
  nil)

(defn add-constructor-override [sym t]
  {:pre [(simple-symbol? sym)
         ;; checked at `get-constructor-override`
         #_((some-fn fn? delay? r/Type?) t)]}
  (env/swap-checker! assoc-in [constructor-override-env-kw sym] t)
  nil)

(defn add-protocol [sym t]
  {:pre [(qualified-symbol? sym)
         ;; checked in get-protocol
         #_
         ((some-fn fn? delay? r/Type?) t)]}
  (env/swap-checker! assoc-in [current-protocol-env-kw sym] t)
  nil)

(defn add-rclass [sym t]
  {:pre [(simple-symbol? sym)
         ;; checked in get-protocol
         #_
         ((some-fn fn? delay? r/RClass? r/Poly?) t)]}
  (env/swap-checker! assoc-in [current-rclass-env-kw sym] t)
  nil)

(defn add-datatype [sym t]
  {:pre [(impl-case
           :clojure ((every-pred simple-symbol?
                                 (fn [k] (some #{\.} (str k))))
                     sym)
           :cljs (qualified-symbol? sym))
         ;; checked in get-datatype
         #_
         ((some-fn fn? delay? r/Type?) t)]
   :post [(nil? %)]}
  (env/swap-checker! assoc-in [current-datatype-env-kw sym] t)
  nil)

(defn add-ns-deps [nsym deps]
  {:pre [(simple-symbol? nsym)
         ((con/set-c? symbol?) deps)]
   :post [(nil? %)]}
  (env/swap-checker! update-in [current-deps-kw nsym] (fnil set/union #{}) deps)
  nil)

(defn register-warn-on-unannotated-vars [nsym]
  (env/swap-checker! assoc-in [ns-opts-kw nsym :warn-on-unannotated-vars] true)
  nil)

#?(:cljs :ignore 
:default
(def ^:private force-type #((requiring-resolve 'typed.cljc.runtime.env-utils/force-type) %)))
#?(:cljs :ignore :default
(defmacro ^:private delay-type [& body]
  `((requiring-resolve 'typed.cljc.runtime.env-utils/delay-type*)
    (fn [] (do ~@body)))))

#?(:cljs :ignore
:default
(defmacro create-env
  "For name n, creates defs for {n}, {n}-kw, add-{n},
  and reset-{n}!"
  [n]
  {:pre [(simple-symbol? n)]}
  (let [kw-def (symbol (str n "-kw"))
        add-def (symbol (str "add-" n))
        reset-def (symbol (str "reset-" n "!"))]
    `(do (def ~kw-def ~(keyword (str (ns-name *ns*)) (str n)))
         (defn ~n []
           {:post [(map? ~'%)]}
           (get (env/deref-checker) ~kw-def {}))
         (defn ~add-def [sym# t#]
           {:pre [(symbol? sym#)]
            :post [(nil? ~'%)]}
           (env/swap-checker! assoc-in [~kw-def sym#] t#)
           nil)
         (defn ~reset-def [m#]
           (env/swap-checker! assoc ~kw-def m#)
           nil)
         nil))))

;; runtime environments
#?(:cljs :ignore :default
(create-env var-env))
#?(:cljs :ignore :default
(create-env alias-env))
#?(:cljs :ignore :default
(create-env protocol-env))
#?(:cljs :ignore :default
(create-env rclass-env))
#?(:cljs :ignore :default
(create-env datatype-env))
#?(:cljs :ignore :default
(create-env jsnominal-env))

#?(:cljs :ignore :default
(defn v [vsym]
  {:pre [(qualified-symbol? vsym)]}
  (let [ns (find-ns (symbol (namespace vsym)))
        _ (assert ns (str "Cannot find namespace: " (namespace vsym)))
        var (ns-resolve ns (symbol (name vsym)))]
    (assert (var? var) (str "Cannot find var: " vsym))
    @var)))

#?(:cljs :ignore :default
(defn the-var [vsym]
  {:pre [(qualified-symbol? vsym)]
   :post [(var? %)]}
  (let [ns (find-ns (symbol (namespace vsym)))
        _ (assert ns (str "Cannot find namespace: " (namespace vsym)))
        var (ns-resolve ns (symbol (name vsym)))]
    (assert (var? var) (str "Cannot find var: " vsym))
    var)))


(declare bindings-for-impl)

(defn with-impl* [impl f]
  (with-bindings (or (get (bindings-for-impl) impl)
                     (throw (ex-info (str "No impl found for " (pr-str impl)))))
     (f)))

#?(:cljs :ignore :default
(defmacro with-impl [impl & body]
  `(with-impl* ~impl #(do ~@body))))

(def clj-checker-atom clj-env/clj-checker-atom)

(defn clj-checker []
  clj-checker-atom)

(defn clj-bindings []
  {#'env/*checker* (clj-checker)})

#?(:cljs :ignore :default
(defmacro with-clojure-impl [& body]
  `(with-impl clojure
     ~@body)))

(defn with-clojure-impl* [f]
  (with-clojure-impl
    (f)))

(def cljs-checker-atom cljs-env/cljs-checker-atom)

(defn cljs-checker []
  {:post [#?(:clj (instance? clojure.lang.IAtom %)
             :cljs (instance? Atom %))]}
  cljs-checker-atom)

(defn cljs-bindings []
  {#'env/*checker* (cljs-checker)})

#?(:cljs :ignore :default
(defmacro with-cljs-impl [& body]
  `(with-impl clojurescript
     ~@body)))

(defn bindings-for-impl []
  {:clojure (clj-bindings)
   :cljs (cljs-bindings)
   clojure (clj-bindings)
   clojurescript (cljs-bindings)})

#?(:cljs :ignore :default
(defmacro with-full-impl [impl & body]
  `(with-impl ~impl
     ~@body)))

(defn implementation-specified? []
  (not= unknown (current-impl)))

(defn ensure-impl-specified []
  (assert (implementation-specified?) "No implementation specified"))

(defn checking-clojure? []
  (ensure-impl-specified)
  (= clojure (current-impl)))

(defn checking-clojurescript? []
  (ensure-impl-specified)
  (= clojurescript (current-impl)))

(defmacro assert-clojure 
  ([] `(assert-clojure nil))
  ([msg] `(assert (= clojure (current-impl))
                  (let [msg# ~msg]
                    (str "Clojure implementation only"
                         (when (seq msg#)
                           (str ": " msg#)))))))

(defmacro assert-cljs
  ([] `(assert-cljs nil))
  ([msg] `(assert (= clojurescript (current-impl))
                  (let [msg# ~msg]
                    (str "Clojurescript implementation only"
                         (when (seq msg#)
                           (str ": " msg#)))))))


#?(:cljs :ignore :default
(defn var->symbol [^clojure.lang.Var var]
  {:pre [(var? var)]
   :post [(qualified-symbol? %)]}
  (symbol (str (ns-name (.ns var)))
          (str (.sym var)))))

#?(:cljs :ignore :default
(defn Class->symbol [^#?(:cljr Type :clj Class) cls]
  {:pre [(class? cls)]
   :post [(simple-symbol? %)]}
  (symbol (#?(:cljr .FullName :clj .getName) cls))))

; for type-contract
(defn hmap-c? [& {:keys [mandatory optional absent-keys complete?]}]
  (every-pred map?
              #(cond
                 complete? (set/subset? (set (keys %))
                                        (into #{} (mapcat keys) [mandatory optional]))
                 :else
                 (let [actual-ks (set (keys %))]
                   (and 
                     ;required keys is a subset of actual keys
                     (set/subset? 
                       (set (keys mandatory))
                       actual-ks)
                     ;no absent-keys are present
                     (empty?
                       (set/intersection
                         absent-keys
                         actual-ks)))))
              #(every? (fn [[k vc]]
                         (and (contains? % k)
                              (vc (get % k))))
                       mandatory)
              #(every? (fn [[k vc]]
                         (or (not (contains? % k))
                             (vc (get % k))))
                       optional)))

#?(:cljs :ignore :default
(def ^:private int-error #(apply (requiring-resolve 'clojure.core.typed.errors/int-error) %&)))
#?(:cljs :ignore :default
(def ^:private parse-free-binder-with-variance #((requiring-resolve 'typed.clj.checker.parse-unparse/parse-free-binder-with-variance) %)))
#?(:cljs :ignore :default
(def ^:private with-parse-ns* #((requiring-resolve 'typed.clj.checker.parse-unparse/with-parse-ns*) %1 %2)))
#?(:cljs :ignore :default
(def ^:private with-bounded-frees* #((requiring-resolve 'typed.cljc.checker.free-ops/with-bounded-frees*) %1 %2)))
#?(:cljs :ignore :default
(def ^:private unparse-type #((requiring-resolve 'typed.clj.checker.parse-unparse/unparse-type) %)))
#?(:cljs :ignore :default
(def ^:private parse-type #((requiring-resolve 'typed.clj.checker.parse-unparse/parse-type) %)))
#?(:cljs :ignore :default
(def ^:private fully-resolve-type #((requiring-resolve 'typed.cljc.checker.type-ctors/fully-resolve-type) %)))
#?(:cljs :ignore :default
(def ^:private Poly? #((requiring-resolve 'typed.cljc.checker.type-rep/Poly?) %)))
#?(:cljs :ignore :default
(def ^:private Poly-fresh-symbols* #((requiring-resolve 'typed.cljc.checker.type-ctors/Poly-fresh-symbols*) %)))
#?(:cljs :ignore :default
(def ^:private Poly-body* #(apply (requiring-resolve 'typed.cljc.checker.type-ctors/Poly-body*) %&)))
#?(:cljs :ignore :default
(def ^:private PolyDots? #((requiring-resolve 'typed.cljc.checker.type-rep/PolyDots?) %)))
#?(:cljs :ignore :default
(def ^:private PolyDots-fresh-symbols* #((requiring-resolve 'typed.cljc.checker.type-ctors/PolyDots-fresh-symbols*) %)))
#?(:cljs :ignore :default
(def ^:private PolyDots-body* #((requiring-resolve 'typed.cljc.checker.type-ctors/PolyDots-body*) %1 %2)))
#?(:cljs :ignore :default
(def ^:private FnIntersection? #((requiring-resolve 'typed.cljc.checker.type-rep/FnIntersection?) %)))
#?(:cljs :ignore :default
(def ^:private Protocol* #(apply (requiring-resolve 'typed.cljc.checker.type-ctors/Protocol*) %&)))
#?(:cljs :ignore :default
(def ^:private Protocol-var->on-class #((requiring-resolve 'typed.cljc.checker.type-ctors/Protocol-var->on-class) %)))
#?(:cljs :ignore :default
(def ^:private -any #(deref (requiring-resolve 'typed.cljc.checker.type-rep/-any))))
#?(:cljs :ignore :default
(def ^:private protocol-method-var-ann #(apply (requiring-resolve 'typed.cljc.checker.collect-utils/protocol-method-var-ann) %&)))
#?(:cljs :ignore :default
(def ^:private make-F #((requiring-resolve 'typed.cljc.checker.type-rep/make-F) %)))
#?(:cljs :ignore :default
(def ^:private DataType* #(apply (requiring-resolve 'typed.cljc.checker.type-ctors/DataType*) %&)))
#?(:cljs :ignore :default
(def ^:private Poly* #(apply (requiring-resolve 'typed.cljc.checker.type-ctors/Poly*) %&)))
#?(:cljs :ignore :default
(def ^:private make-FnIntersection #((requiring-resolve 'typed.cljc.checker.type-rep/make-FnIntersection) %)))
#?(:cljs :ignore :default
(def ^:private make-Function #(apply (requiring-resolve 'typed.cljc.checker.type-rep/make-Function) %&)))
#?(:cljs :ignore :default
(def ^:private DataType-of #(apply (requiring-resolve 'typed.cljc.checker.type-ctors/DataType-of) %&)))
#?(:cljs :ignore :default
(def ^:private subtype? #((requiring-resolve 'typed.clj.checker.subtype/subtype?) %1 %2)))

#?(:cljs :ignore :default
(defn gen-protocol* [current-env current-ns vsym binder mths]
  {:pre [(symbol? current-ns)
         ((some-fn nil? map?) mths)]}
  (let [_ (when-not (symbol? vsym)
            (int-error
              (str "First argument to ann-protocol must be a symbol: " vsym)))
        s (if (namespace vsym)
            vsym
            (symbol (str current-ns) (name vsym)))
        protocol-defined-in-nstr (namespace s)
        _ (when-let [[m] (seq (remove symbol? (keys mths)))]
            (int-error (str "Method names to ann-protocol must be symbols, found: " (pr-str m))))
        _ (doseq [n1 (keys mths)
                  n2 (keys mths)]
            (when (and (not= n1 n2)
                       (= (munge n1) (munge n2)))
              (int-error 
                (str "Protocol methods for " vsym " must have distinct representations: "
                     "both " n1 " and " n2 " compile to " (munge n1)))))
        ; add a Name so the methods can be parsed
        _ (declare-protocol* s)
        parsed-binder (when binder 
                        (delay-type
                          (with-parse-ns* current-ns
                            #(parse-free-binder-with-variance binder))))
        fs (when parsed-binder
             (delay-type 
               (map (comp make-F :fname) (force-type parsed-binder))))
        bnds (when parsed-binder
               (delay-type (map :bnd (force-type parsed-binder))))
        ms (into {} (map (fn [[knq v*]]
                           (let [_ (when (namespace knq)
                                     (int-error "Protocol method should be unqualified"))
                                 mtype 
                                 (delay-type
                                   (let [mtype (with-bounded-frees* (zipmap (force-type fs) (force-type bnds))
                                                 #(binding [vs/*current-env* current-env]
                                                    (with-parse-ns* current-ns
                                                      (fn []
                                                        (parse-type v*)))))
                                         _ (let [rt (fully-resolve-type mtype)
                                                 fin? (fn [f]
                                                        (let [f (fully-resolve-type f)]
                                                          (boolean
                                                            (when (FnIntersection? f)
                                                              (every? seq (map :dom (:types f)))))))]
                                             (when-not 
                                               (or
                                                 (fin? rt)
                                                 (when (Poly? rt) 
                                                   (let [names (Poly-fresh-symbols* rt)]
                                                     (fin? (Poly-body* names rt))))
                                                 (when (PolyDots? rt) 
                                                   (let [names (PolyDots-fresh-symbols* rt)]
                                                     (fin? (PolyDots-body* names rt)))))
                                               ;(prn "throwing method type")
                                               (int-error (str "Protocol method " knq " should be a possibly-polymorphic function intersection"
                                                               " taking at least one fixed argument: "
                                                               (unparse-type mtype)))))]
                                     mtype))]
                             [knq mtype])))
                 mths)
        ;_ (prn "collect protocol methods" (into {} ms))
        t (delay-type
            (Protocol* (map :name (force-type fs)) (map :variance (force-type parsed-binder))
                       (force-type fs) s (Protocol-var->on-class s) 
                       (into {} (map (fn [[k v]] [k (force-type v)])) ms) 
                       (map :bnd (force-type parsed-binder))))]
    ;(prn "Adding protocol" s t)
    (add-protocol s t)
    ; annotate protocol var as Any
    (add-nocheck-var s)
    (add-tc-var-type s (delay-type (-any)))
    (doseq [[kuq mt] ms]
      (assert (not (namespace kuq))
              "Protocol method names should be unqualified")
      ;qualify method names when adding methods as vars
      (let [kq (symbol protocol-defined-in-nstr (name kuq))
            mt-ann (delay-type 
                     (protocol-method-var-ann (force-type mt) (map :name (force-type fs)) (force-type bnds)))]
        (add-nocheck-var kq)
        (add-tc-var-type kq mt-ann)))
    #_
    (prn "end gen-protocol" s (when (= "cljs.core" (namespace s))
                                (force-type (get-in (env/deref-checker) [current-protocol-env-kw s]))))
    nil)))

(defn add-datatype-ancestors
  "Add a mapping of ancestor overrides (from the type syntax of the override
  to the actual parsed type) for the datatype named sym."
  [sym tmap]
  {:pre [(symbol? sym)
         (map? tmap)]
   :post [(nil? %)]}
  (env/swap-checker! update-in [current-dt-ancestors-kw sym] merge tmap)
  nil)

#?(:cljs :ignore :default
(def ^:private demunge #((requiring-resolve 'clojure.repl/demunge) %)))
#?(:cljs :ignore :default
(def ^:private abstract-many #((requiring-resolve 'typed.cljc.checker.type-ctors/abstract-many) %1 %2)))
#?(:cljs :ignore :default
(def ^:private with-frees* #((requiring-resolve 'typed.cljc.checker.free-ops/with-frees*) %1 %2)))
#?(:cljs :ignore :default
(def ^:private -val #((requiring-resolve 'typed.cljc.checker.type-rep/-val) %1)))
#?(:cljs :ignore :default
(def ^:private -nil #(deref (requiring-resolve 'typed.cljc.checker.type-rep/-nil))))
#?(:cljs :ignore :default
(def ^:private fv #((requiring-resolve 'typed.cljc.checker.frees/fv) %)))
#?(:cljs :ignore :default
(def ^:private fi #((requiring-resolve 'typed.cljc.checker.frees/fi) %)))
#?(:cljs :ignore :default
(def ^:private make-HMap #(apply (requiring-resolve 'typed.cljc.checker.type-ctors/make-HMap) %&)))

#?(:cljs :ignore :default
(defn gen-datatype* [current-env current-ns provided-name fields vbnd opt record?]
  {:pre [(symbol? current-ns)
         (impl-case
           :clojure (simple-symbol? provided-name)
           :cljs (qualified-symbol? provided-name))]}
  (let [{ancests :unchecked-ancestors} opt
        ancests (or ancests (:extends opt))
        parsed-binders (when vbnd
                         (let [bfn (bound-fn []
                                     (with-parse-ns* current-ns
                                       #(parse-free-binder-with-variance vbnd)))]
                           (delay-type (bfn))))
        ;variances
        vs (when parsed-binders
             (delay-type (seq (map :variance (force-type parsed-binders)))))
        args (when parsed-binders
               (delay-type (seq (map :fname (force-type parsed-binders)))))
        bnds (when parsed-binders
               (delay-type (seq (map :bnd (force-type parsed-binders)))))
        provided-name-str (str provided-name)
        ;_ (prn "provided-name-str" provided-name-str)
        munged-ns-str (impl-case
                        :clojure (if (some #(= \. %) provided-name-str)
                                   (apply str (butlast (apply concat (butlast (partition-by #(= \. %) provided-name-str)))))
                                   (str (munge current-ns)))
                        :cljs nil)
        ;_ (prn "munged-ns-str" munged-ns-str)
        demunged-ns-str (impl-case
                          :clojure (str (demunge munged-ns-str))
                          :cljs (-> provided-name namespace))
        ;_ (prn "demunged-ns-str" demunged-ns-str)
        local-name (impl-case
                     :clojure (if (some #(= \. %) provided-name-str)
                                (symbol (apply str (last (partition-by #(= \. %) (str provided-name-str)))))
                                provided-name-str)
                     :cljs (-> provided-name name symbol))
        ;_ (prn "local-name" local-name)
        s (impl-case
            :clojure (symbol (str (name munged-ns-str) \. local-name))
            :cljs provided-name)
        fs (let [bfn (bound-fn []
                       (let [parse-field (fn [[n colon t]]
                                           (when (not= :- colon)
                                             (int-error (format "Missing :- after field %s in ann-record."
                                                                n)))
                                           [n (parse-type t)])]
                         (apply array-map (apply concat (with-frees* (mapv make-F (force-type args))
                                                          (fn []
                                                            (binding [vs/*current-env* current-env]
                                                              (with-parse-ns* current-ns
                                                                #(mapv parse-field (partition 3 fields))))))))))]
             (delay-type (bfn)))
        as (into {}
                 (map
                   (fn [an]
                     [an (let [bfn (bound-fn []
                                     (with-frees* (mapv make-F (force-type args))
                                       (fn []
                                         (binding [vs/*current-env* current-env]
                                           (with-parse-ns* current-ns
                                             #(let [t (parse-type an)]
                                                (abstract-many (force-type args) t)))))))]
                           (delay-type (bfn)))]))
                 ancests)
        ;_ (prn "collected ancestors" as)
        _ (add-datatype-ancestors s as)
        pos-ctor-name (symbol demunged-ns-str (str "->" local-name))
        map-ctor-name (symbol demunged-ns-str (str "map->" local-name))
        dt (let [bfn (bound-fn []
                       (DataType* (force-type args) (force-type vs) (map make-F (force-type args)) s (force-type bnds) (force-type fs) record?))]
             (delay-type (bfn)))
        _ (add-datatype s dt)
        pos-ctor (let [bfn (bound-fn []
                             (if args
                               (Poly* (force-type args) (force-type bnds)
                                      (make-FnIntersection
                                        (make-Function (vec (vals (force-type fs)))
                                                       (with-bounded-frees* (zipmap (map make-F (force-type args)) (force-type bnds))
                                                         #(DataType-of s (map make-F (force-type args)))))))
                               (make-FnIntersection
                                 (make-Function (vec (vals (force-type fs))) (DataType-of s)))))]
                   (delay-type (bfn)))]
    (do 
      ;(when vs
      ;  (let [f (mapv r/make-F (repeatedly (count vs) gensym))]
      ;    ;TODO replacements and unchecked-ancestors go here
      ;    (rcls/alter-class* s (c/RClass* (map :name f) (force-type vs) f s {} {} (force-type bnds)))))
      (add-tc-var-type pos-ctor-name pos-ctor)
      (add-nocheck-var pos-ctor-name)
      (when record?
        (let [map-ctor (let [bfn (bound-fn []
                                   (let [hmap-arg ; allow omission of keys if nil is allowed and field is monomorphic
                                         (let [{optional true mandatory false} 
                                               (group-by (fn [[_ t]] (and (empty? (fv t))
                                                                          (empty? (fi t))
                                                                          (subtype? (-nil) t)))
                                                         (zipmap (map (comp -val keyword) (keys (force-type fs)))
                                                                 (vals (force-type fs))))]
                                           (make-HMap :optional (into {} optional)
                                                      :mandatory (into {} mandatory)))]
                                     (if args
                                       (Poly* (force-type args) (force-type bnds)
                                              (make-FnIntersection
                                                (make-Function [hmap-arg] (DataType-of s (map make-F (force-type args))))))
                                       (make-FnIntersection
                                         (make-Function [hmap-arg] (DataType-of s))))))]
                         (delay-type (bfn)))]
          (impl-case
            :clojure (add-method-override (symbol (str s) "create") map-ctor)
            :cljs nil)
          (add-tc-var-type map-ctor-name map-ctor)
          (add-nocheck-var map-ctor-name)))))))
