(ns remember-mobile.util.common
  (:refer-clojure :exclude [clj->js])
  (:require [clojure.spec :as s]
            [goog.string :as gstr]))

(defn keyword->js [k]
  (-> k name gstr/toCamelCase))

(s/fdef keyword->js
  :args (s/cat :keyword keyword?)
  :ret string?)

(defn clj->js [x]
  )

(comment (keyword->js ::some-thing?))
