(ns remember-mobile.ios.core
  (:require [om.next :as om :refer-macros [defui]]
            [re-natal.support :as sup]
            [remember-mobile.state :as state]
            [untangled.client.core :as uc]))

(set! js/window.React (js/require "react"))
(def ReactNative (js/require "react-native"))

(defn create-element [rn-comp opts & children]
  (apply js/React.createElement rn-comp (clj->js opts) children))

(def app-registry (.-AppRegistry ReactNative))
(def view (partial create-element (.-View ReactNative)))
(def text (partial create-element (.-Text ReactNative)))
(def image (partial create-element (.-Image ReactNative)))
(def touchable-highlight (partial create-element (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(defui AppRoot
  static uc/InitialAppState
  (initial-state [_ _] {:app/msg "Hello Clojure in iOS and Android!"
                        :app/counter 1})

  static om/IQuery
  (query [this]
    '[:app/msg :app/counter])

  Object
  (render [this]
    (let [{:app/keys [msg counter]} (om/props this)]
      (view {:style {:flexDirection "column" :margin 40 :alignItems "center"}}
        (text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} msg)
        (image {:source logo-img
                :style  {:width 80 :height 80 :marginBottom 30}})
        (text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} (str counter))
        (touchable-highlight {:style   {:backgroundColor "#999" :padding 10 :borderRadius 5}
                              :onPress #(om/transact! this '[(app/increment {})])}
          (text {:style {:color "white" :textAlign "center" :fontWeight "bold"}} "press me"))))))

(defonce RootNode (sup/root-node! 1))
(defonce app-root (om/factory RootNode))

(defn reload []
  (reset! state/app (uc/mount @state/app AppRoot 1)))

(defn init []
  (reload)
  (.registerComponent app-registry "RememberMobile" (fn [] app-root)))
