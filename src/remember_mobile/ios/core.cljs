(ns remember-mobile.ios.core
  (:require [om.next :as om :refer-macros [defui]]
            [cljs.nodejs :as node]
            [re-natal.support :as sup]
            [remember-mobile.state :as state]
            [remember-mobile.components.common :as uic]
            [remember-mobile.util.common :as util]
            [untangled.client.core :as uc]))

(defn js-tl->clj [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(def ReactNative (node/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def logo-img (node/require "./images/cljs.png"))

(om/defui ^:once MemoRow
  static uc/InitialAppState
  (initial-state [_ data] (merge {:db/id (random-uuid) :memo/title "sample"} data))

  static om/IQuery
  (query [_] [:db/id :memo/title])

  static om/Ident
  (ident [_ props] [:memo/by-id (:db/id props)])

  Object
  (render [this]
    (let [{:memo/keys [title]} (om/props this)]
      (uic/text #js {:style #js {:fontSize 20 :fontWeight "100" :marginBottom 10 :textAlign "center"}}
        title))))

(def memo-row (om/factory MemoRow))

(def big-font #js {:fontSize 30 :fontWeight "100" :marginBottom 20 :marginTop 30 :textAlign "center"})

(declare PlainScene)

(om/defui ^:once MyScene
  Object
  (render [this]
    (let [{:keys [navigator] :as props} (om/props this)]
      (uic/view #js {:style #js {:marginTop 64 :flex 1}}
        (uic/text #js {:style #js {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} "Bla")
        (uic/touchable-highlight #js {:onPress       (fn [] (.push navigator #js {:title "second" :component PlainScene
                                                                                  :passProps (util/map->js props)}))
                                      :underlayColor "transparent"}
          (uic/image #js {:source logo-img
                          :style  #js {:width 80 :height 80 :marginBottom 30 :alignSelf "center"}}))
        (uic/list-view {:renderRow memo-row
                        :rows      (-> this om/props :memo/children)
                        :style     #js {:flex 1}})))))

(def my-scene (om/factory MyScene))

(def PlainScene
  (js/React.createClass #js {:render #(my-scene (util/js->map (.-props (js-this))))}))

(defui AppRoot
  static uc/InitialAppState
  (initial-state [_ _] {:app/msg       "Hello Clojure in iOS and Android!"
                        :app/counter   1
                        :memo/children (->> (range)
                                            (map #(uc/initial-state MemoRow {:memo/title (str "Sample " %)}))
                                            (take 30)
                                            (vec))})

  static om/IQuery
  (query [this]
    [:ui/react-key :app/msg :app/counter {:memo/children (om/get-query MemoRow)}])

  Object
  (render [this]
    (let [{:app/keys [msg counter]
           :keys     [ui/react-key memo/children]
           :as       props} (om/props this)]
      (uic/navigator-ios #js {:key          react-key
                              :initialRoute #js {:title "bla" :component PlainScene :passProps (util/map->js props)}
                              :style        #js {:flex 1}}))))

(defonce RootNode (sup/root-node! 1))
(defonce app-root (om/factory RootNode))

(defn reload []
  (reset! state/app (uc/mount @state/app AppRoot 1)))

(defn init []
  (reload)
  (.registerComponent app-registry "RememberMobile" (fn [] app-root)))
