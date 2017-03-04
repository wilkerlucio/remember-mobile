(ns cljsjs.react.dom
  (:require [goog.object :as gobj]))

(gobj/set js/window "ReactDOM" (js-obj))

