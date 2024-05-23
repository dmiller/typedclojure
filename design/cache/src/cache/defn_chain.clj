(ns cache.defn-chain
  {:typed.clojure {:experimental #{:cache}}}
  (:require [typed.clojure :as t]))

;; changing any defn form should only recheck that defn, since
;; the type annotation for the others doesn't change

(t/defalias A1 ':A1)
(t/defalias A2 ':A2)
(t/defalias A3 ':A3 #_':A0) ;; changing this should recheck a3 (since it changes its ann) and a2 (since it uses a3)
(t/defalias A4 ':A4)
(t/defalias A5 ':A5)

(declare a1 a2 a3 a4 a5)

(t/ann a1 [A1 :-> A5])
(defn a1 [x]
  ^::t/dbg (a2 :A2))

(t/ann a2 [A2 :-> A5])
(defn a2 [x] ^::t/dbg (a3 :A3))

(t/ann a3 [A3 :-> A5])
(defn a3 [x]
  (identity 1 #_nil) ;; changing this should only recheck a3
  ^::t/dbg (a4 :A4))

(t/ann a4 [A4 :-> A5])
(defn a4 [x]
  (t/ann-form x A4)
  ^::t/dbg (do :A5))

(comment
  (t/cns)
  ;Start checking cache.defn-chain
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
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
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;;; changing any defn form should only recheck that defn, since
  ;;; the type annotation for the others doesn't change
  ;
  ;(t/defalias A1 ':A1)
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core.typed/tc-ignore
  ;   typed.clj.ext.clojure.core.typed__tc-ignore/defuspecial__tc-ignore},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars
  ; {t/defalias typed.clojure/defalias,
  ;  clojure.core.typed/defalias clojure.core.typed/defalias,
  ;  clojure.core.typed/tc-ignore clojure.core.typed/tc-ignore,
  ;  clojure.core/when clojure.core/when,
  ;  clojure.core/= clojure.core/=,
  ;  clojure.lang.Util clojure.lang.Util,
  ;  clojure.core/intern clojure.core/intern,
  ;  clojure.core.typed/defalias* clojure.core.typed/defalias*},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/defalias A2 ':A2)
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core.typed/tc-ignore
  ;   typed.clj.ext.clojure.core.typed__tc-ignore/defuspecial__tc-ignore},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars
  ; {t/defalias typed.clojure/defalias,
  ;  clojure.core.typed/defalias clojure.core.typed/defalias,
  ;  clojure.core.typed/tc-ignore clojure.core.typed/tc-ignore,
  ;  clojure.core/when clojure.core/when,
  ;  clojure.core/= clojure.core/=,
  ;  clojure.lang.Util clojure.lang.Util,
  ;  clojure.core/intern clojure.core/intern,
  ;  clojure.core.typed/defalias* clojure.core.typed/defalias*},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/defalias A3 ':A3 #_':A0)
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core.typed/tc-ignore
  ;   typed.clj.ext.clojure.core.typed__tc-ignore/defuspecial__tc-ignore},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars
  ; {t/defalias typed.clojure/defalias,
  ;  clojure.core.typed/defalias clojure.core.typed/defalias,
  ;  clojure.core.typed/tc-ignore clojure.core.typed/tc-ignore,
  ;  clojure.core/when clojure.core/when,
  ;  clojure.core/= clojure.core/=,
  ;  clojure.lang.Util clojure.lang.Util,
  ;  clojure.core/intern clojure.core/intern,
  ;  clojure.core.typed/defalias* clojure.core.typed/defalias*},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;;; changing this should recheck a3 (since it changes its ann) and a2 (since it uses a3)
  ;(t/defalias A4 ':A4)
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core.typed/tc-ignore
  ;   typed.clj.ext.clojure.core.typed__tc-ignore/defuspecial__tc-ignore},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars
  ; {t/defalias typed.clojure/defalias,
  ;  clojure.core.typed/defalias clojure.core.typed/defalias,
  ;  clojure.core.typed/tc-ignore clojure.core.typed/tc-ignore,
  ;  clojure.core/when clojure.core/when,
  ;  clojure.core/= clojure.core/=,
  ;  clojure.lang.Util clojure.lang.Util,
  ;  clojure.core/intern clojure.core/intern,
  ;  clojure.core.typed/defalias* clojure.core.typed/defalias*},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/defalias A5 ':A5)
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core.typed/tc-ignore
  ;   typed.clj.ext.clojure.core.typed__tc-ignore/defuspecial__tc-ignore},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars
  ; {t/defalias typed.clojure/defalias,
  ;  clojure.core.typed/defalias clojure.core.typed/defalias,
  ;  clojure.core.typed/tc-ignore clojure.core.typed/tc-ignore,
  ;  clojure.core/when clojure.core/when,
  ;  clojure.core/= clojure.core/=,
  ;  clojure.lang.Util clojure.lang.Util,
  ;  clojure.core/intern clojure.core/intern,
  ;  clojure.core.typed/defalias* clojure.core.typed/defalias*},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(declare a1 a2 a3 a4 a5)
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure},
  ; :typed.cljc.checker.check.cache/vars {declare clojure.core/declare},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/ann a1 [A1 :-> A5])
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
  ;::t/dbg id=G__69249 (a2 :A2)
  ;::t/dbg id=G__69249 expected: cache.defn-chain/A5
  ;::t/dbg id=G__69249 result: cache.defn-chain/A5
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(defn a1 [x]
  ;  ^::t/dbg (a2 :A2))
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core/defn
  ;   typed.clj.ext.clojure.core__defn/defuspecial__defn,
  ;   clojure.core/fn typed.clj.ext.clojure.core__fn/defuspecial__fn},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure,
  ;  :clojure.core.typed.current-impl/current-var-annotations
  ;  {cache.defn-chain/a1 [cache.defn-chain/A1 :-> cache.defn-chain/A5],
  ;   cache.defn-chain/a2 [cache.defn-chain/A2 :-> cache.defn-chain/A5]},
  ;  :clojure.core.typed.current-impl/current-used-vars #{},
  ;  :clojure.core.typed.current-impl/current-name-env
  ;  {cache.defn-chain/A2 (typed.clojure/Val :A2),
  ;   typed.clojure/Fn clojure.lang.Fn,
  ;   clojure.lang.Fn {}}},
  ; :typed.cljc.checker.check.cache/vars
  ; {defn clojure.core/defn,
  ;  clojure.core/fn clojure.core/fn,
  ;  a2 cache.defn-chain/a2},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms
  ; {A2 cache.defn-chain/A2, A5 cache.defn-chain/A5}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/ann a2 [A2 :-> A5])
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
  ;::t/dbg id=G__69255 (a3 :A3)
  ;::t/dbg id=G__69255 expected: cache.defn-chain/A5
  ;::t/dbg id=G__69255 result: cache.defn-chain/A5
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(defn a2 [x] ^::t/dbg (a3 :A3))
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core/defn
  ;   typed.clj.ext.clojure.core__defn/defuspecial__defn,
  ;   clojure.core/fn typed.clj.ext.clojure.core__fn/defuspecial__fn},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure,
  ;  :clojure.core.typed.current-impl/current-var-annotations
  ;  {cache.defn-chain/a2 [cache.defn-chain/A2 :-> cache.defn-chain/A5],
  ;   cache.defn-chain/a3 [cache.defn-chain/A3 :-> cache.defn-chain/A5]},
  ;  :clojure.core.typed.current-impl/current-used-vars
  ;  #{cache.defn-chain/a2},
  ;  :clojure.core.typed.current-impl/current-name-env
  ;  {cache.defn-chain/A3 (typed.clojure/Val :A3),
  ;   typed.clojure/Fn clojure.lang.Fn,
  ;   clojure.lang.Fn {}}},
  ; :typed.cljc.checker.check.cache/vars
  ; {defn clojure.core/defn,
  ;  clojure.core/fn clojure.core/fn,
  ;  a3 cache.defn-chain/a3},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms
  ; {A3 cache.defn-chain/A3, A5 cache.defn-chain/A5}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/ann a3 [A3 :-> A5])
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
  ;::t/dbg id=G__69264 (a4 :A4)
  ;::t/dbg id=G__69264 expected: cache.defn-chain/A5
  ;::t/dbg id=G__69264 result: cache.defn-chain/A5
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(defn a3 [x]
  ;  (identity 1 #_nil) ;; changing this should only recheck a3
  ;  ^::t/dbg (a4 :A4))
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core/defn
  ;   typed.clj.ext.clojure.core__defn/defuspecial__defn,
  ;   clojure.core/fn typed.clj.ext.clojure.core__fn/defuspecial__fn},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure,
  ;  :clojure.core.typed.current-impl/current-var-annotations
  ;  {cache.defn-chain/a3 [cache.defn-chain/A3 :-> cache.defn-chain/A5],
  ;   clojure.core/identity
  ;   (typed.clojure/All
  ;    [x]
  ;    [x
  ;     :->
  ;     x
  ;     :filters
  ;     {:then (! (typed.clojure/U false nil) 0),
  ;      :else (is (typed.clojure/U false nil) 0)}
  ;     :object
  ;     {:id 0}]),
  ;   cache.defn-chain/a4 [cache.defn-chain/A4 :-> cache.defn-chain/A5]},
  ;  :clojure.core.typed.current-impl/current-used-vars
  ;  #{cache.defn-chain/a3 cache.defn-chain/a2},
  ;  :clojure.core.typed.current-impl/current-name-env
  ;  {cache.defn-chain/A4 (typed.clojure/Val :A4),
  ;   typed.clojure/Fn clojure.lang.Fn,
  ;   clojure.lang.Fn {}}},
  ; :typed.cljc.checker.check.cache/vars
  ; {defn clojure.core/defn,
  ;  clojure.core/fn clojure.core/fn,
  ;  identity clojure.core/identity,
  ;  a4 cache.defn-chain/a4},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms
  ; {A4 cache.defn-chain/A4, A5 cache.defn-chain/A5}}
  ;need-to-check-top-level-expr?: found cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(t/ann a4 [A4 :-> A5])
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
  ;::t/dbg id=G__69270 (do :A5)
  ;::t/dbg id=G__69270 expected: cache.defn-chain/A5
  ;::t/dbg id=G__69270 result: [cache.defn-chain/A5 {:then tt, :else ff}]
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
  ;<<<<
  ;cache: on disk:
  ;>>>>
  ;(defn a4 [x]
  ;  (t/ann-form x A4)
  ;  ^::t/dbg (do :A5))
  ;<<<<
  ;{:typed.cljc.checker.check.cache/types
  ; {:clojure.core.typed.current-impl/unanalyzed-special
  ;  {clojure.core/defn
  ;   typed.clj.ext.clojure.core__defn/defuspecial__defn,
  ;   clojure.core/fn typed.clj.ext.clojure.core__fn/defuspecial__fn,
  ;   clojure.core.typed/ann-form
  ;   typed.cljc.ext.clojure.core.typed__ann-form/-unanalyzed-special__ann-form},
  ;  :clojure.core.typed.current-impl/current-impl
  ;  :clojure.core.typed.current-impl/clojure,
  ;  :clojure.core.typed.current-impl/current-var-annotations
  ;  {cache.defn-chain/a4 [cache.defn-chain/A4 :-> cache.defn-chain/A5]},
  ;  :clojure.core.typed.current-impl/current-name-env
  ;  {cache.defn-chain/A4 {},
  ;   cache.defn-chain/A5 (typed.clojure/Val :A5),
  ;   typed.clojure/Fn clojure.lang.Fn,
  ;   clojure.lang.Fn {}}},
  ; :typed.cljc.checker.check.cache/vars
  ; {defn clojure.core/defn,
  ;  clojure.core/fn clojure.core/fn,
  ;  t/ann-form typed.clojure/ann-form,
  ;  clojure.core.typed/ann-form clojure.core.typed/ann-form},
  ; :typed.cljc.checker.check.cache/errors false,
  ; :typed.cljc.checker.check.cache/interop {},
  ; :typed.cljc.checker.check.cache/type-syms {A4 cache.defn-chain/A4}}
  ;need-to-check-top-level-expr?: did not find cache info
  ;cache: Caching form with cache info
  ;ns form:
  ;>>>>
  ;(ns cache.defn-chain
  ;  {:typed.clojure {:experimental #{:cache}}}
  ;  (:require [typed.clojure :as t]))
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
  ;Checked cache.defn-chain in 149.886771 msecs
  :ok
  )
