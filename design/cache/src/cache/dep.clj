(ns cache.dep
  {:typed.clojure {:experimental #{:cache}}}
  (:require [cache.dep1 :as dep1]
            [typed.clojure :as t]))

(t/ann foo [:-> t/Any])
(defn foo [] (inc (dep1/a)))

(comment
  (t/check-ns-clj *ns* :check-config {:check-ns-dep :recheck})
  ;Not checking typed.clojure (tagged with :typed.clojure/ignore metadata)
  ;Not checking clojure.core.typed (tagged with :typed.clojure/ignore metadata)
  ;Start checking cache.dep1
  ;Checked cache.dep1 in 18.502651 msecs
  ;Start checking cache.dep
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(ns cache.dep
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [cache.dep1 :as dep1]
  ;            [typed.clojure :as t]))
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core/ns
  ;   typed.clj.ext.clojure.core__ns/-unanalyzed-special__ns},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars {ns clojure.core/ns},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.dep
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [cache.dep1 :as dep1]
  ;            [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/ann foo [:-> t/Any])
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core.typed/tc-ignore
  ;   typed.clj.ext.clojure.core.typed__tc-ignore/defuspecial__tc-ignore},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars
  ; {t/ann typed.clojure/ann,
  ;  clojure.core.typed/ann clojure.core.typed/ann,
  ;  clojure.core.typed/tc-ignore clojure.core.typed/tc-ignore,
  ;  clojure.core.typed/ann* clojure.core.typed/ann*},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.dep
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [cache.dep1 :as dep1]
  ;            [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(defn foo [] (inc (dep1/a)))
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core/defn
  ;   typed.clj.ext.clojure.core__defn/defuspecial__defn,
  ;   clojure.core/fn typed.clj.ext.clojure.core__fn/defuspecial__fn},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure,
  ;  :clojure.core.typed.current-impl/current-var-annotations
  ;  {cache.dep/foo [:-> typed.clojure/Any],
  ;   clojure.core/inc
  ;   (typed.clojure/IFn
  ;    [java.lang.Long :-> java.lang.Long]
  ;    [java.lang.Double :-> java.lang.Double]
  ;    [typed.clojure/AnyInteger :-> typed.clojure/AnyInteger]
  ;    [typed.clojure/Num :-> typed.clojure/Num]),
  ;   cache.dep1/a [:-> typed.clojure/Int]},
  ;  :clojure.core.typed.current-impl/current-used-vars #{},
  ;  :clojure.core.typed.current-impl/current-name-env
  ;  {typed.clojure/Int typed.clojure/AnyInteger,
  ;   typed.clojure/AnyInteger
  ;   (typed.clojure/U
  ;    java.lang.Short
  ;    java.lang.Byte
  ;    java.math.BigInteger
  ;    java.lang.Integer
  ;    clojure.lang.BigInt
  ;    java.lang.Long),
  ;   java.lang.Long {},
  ;   java.lang.Short {},
  ;   java.lang.Double {},
  ;   typed.clojure/Fn clojure.lang.Fn,
  ;   clojure.lang.Fn {}},
  ;  :clojure.core.typed.current-impl/current-rclass-env
  ;  {java.lang.Comparable
  ;   (typed.clojure/TFn
  ;    [[a :variance :invariant]]
  ;    (java.lang.Comparable a))}},
  ; :typed.cljc.checker.check.cache/vars
  ; {defn clojure.core/defn,
  ;  clojure.core/fn clojure.core/fn,
  ;  inc clojure.core/inc,
  ;  dep1/a cache.dep1/a,
  ;  clojure.lang.Numbers clojure.lang.Numbers},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop
  ; {:static-call #{clojure.lang.Numbers/inc}},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.dep
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [cache.dep1 :as dep1]
  ;            [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(comment
  ; ...
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars {comment clojure.core/comment},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;Checked cache.dep in 40.436416 msecs
  :ok
  )
