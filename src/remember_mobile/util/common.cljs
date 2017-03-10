(ns remember-mobile.util.common
  (:refer-clojure :exclude [clj->js])
  (:require [clojure.spec :as s]
            [goog.string :as gstr]
            [clojure.string :as str]
            [goog.object :as gobj]))

(s/def ::key (s/or :string string? :keyword keyword?))

(defn keyword->js [k]
  (-> k name gstr/toCamelCase))

(s/fdef keyword->js
  :args (s/cat :keyword keyword?)
  :ret string?)

(defn encode-key [x]
  (cond
    (string? x) x
    (keyword? x) (pr-str x)
    :else x))

(s/fdef encode-key
  :args (s/cat :x ::key)
  :ret string?)

(defn decode-key [x]
  (if (str/starts-with? x ":")
    (keyword (subs x 1))
    (keyword x)))

(s/fdef decode-key
  :args (s/cat :x string?)
  :ret ::key)

(defn map->js [m]
  (let [obj (js-obj)]
    (run! (fn [[k v]] (gobj/set obj (encode-key k) v)) m)
    obj))

(defn js->map [x]
  (into {} (map (fn [k] [(decode-key k) (gobj/get x k)])) (js-keys x)))

(comment
  (def a (map->js {::keyword "value"
                   :bla "hey"
                   "sample" "test"}))
  (js->map a)
  (encode-key "bla")
  (encode-key :keyword)
  (encode-key ::keyword)
  (decode-key (encode-key ::keyword)))
