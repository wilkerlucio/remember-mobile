(ns ^:figwheel-no-load env.ios.main
  (:require [om.next :as om]
            [remember-mobile.ios.core :as core]
            [remember-mobile.state :as state]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

;(figwheel/watch-and-reload
;  :websocket-url "ws://localhost:3449/figwheel-ws"
;  :heads-up-display false
;  :jsload-callback #(om/add-root! state/reconciler core/AppRoot 1))
;
;(core/init)
;
;(def root-el (core/app-root))

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :heads-up-display false
  :jsload-callback core/reload)

(core/init)

(def root-el (core/app-root))
