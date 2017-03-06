(ns remember-mobile.ios.core
  (:require [om.next :as om :refer-macros [defui]]
            [cljs.nodejs :as node]
            [re-natal.support :as sup]
            [remember-mobile.state :as state]
            [remember-mobile.components.common :as uic]
            [untangled.client.core :as uc]))

(def ReactNative (node/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def logo-img (node/require "./images/cljs.png"))

(om/defui ^:once MemoRow
  static uc/InitialAppState
  (initial-state [_ _] {:memo/title "sample"})

  static om/IQuery
  (query [_] [:db/id :memo/title])

  Object
  (render [this]
    (let [{:memo/keys [title]} (om/props this)]
      (uic/text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}}
        title))))

(def memo-row (om/factory MemoRow))

(defui AppRoot
  static uc/InitialAppState
  (initial-state [_ _] {:app/msg       "Hello Clojure in iOS and Android!"
                        :app/counter   1
                        :memo/children (->> (repeatedly #(uc/initial-state MemoRow {}))
                                            (take 20)
                                            (vec))})

  static om/IQuery
  (query [this]
    [:app/msg :app/counter {:memo/children (om/get-query MemoRow)}])

  Object
  (initLocalState [this]
    {:data-source (-> (uic/list-view-ds {:rowHasChanged #(not= % %2)})
                      (.cloneWithRows (into-array (-> this om/props :memo/children))))})

  (render [this]
    (let [{:app/keys [msg counter]} (om/props this)]
      (uic/view {:style {:flexDirection "column" :alignItems "stretch"}}
        (uic/list-view {:dataSource (om/get-state this :data-source)
                        :style {:flexGrow 1}
                        :renderRow memo-row})
        #_(uic/text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} msg)
        #_(uic/image {:source logo-img
                      :style  {:width 80 :height 80 :marginBottom 30}})
        #_(uic/text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} (str counter))
        #_(uic/touchable-highlight {:style   {:backgroundColor "#999" :padding 10 :borderRadius 5}
                                    :onPress #(om/transact! this '[(app/increment {})])}
            (uic/text {:style {:color "white" :textAlign "center" :fontWeight "bold"}} "press me"))))))

(defonce RootNode (sup/root-node! 1))
(defonce app-root (om/factory RootNode))

(defn reload []
  (reset! state/app (uc/mount @state/app AppRoot 1)))

(defn init []
  (reload)
  (.registerComponent app-registry "RememberMobile" (fn [] app-root)))
