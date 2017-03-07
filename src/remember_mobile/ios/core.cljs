(ns remember-mobile.ios.core
  (:require [om.next :as om :refer-macros [defui]]
            [cljs.nodejs :as node]
            [re-natal.support :as sup]
            [remember-mobile.state :as state]
            [remember-mobile.components.common :as uic]
            [untangled.client.core :as uc]))

(defn js-tl->clj [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(def ReactNative (node/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def logo-img (node/require "./images/cljs.png"))

(om/defui ^:once MemoRow
  static uc/InitialAppState
  (initial-state [_ data] (merge {:memo/title "sample"} data))

  static om/IQuery
  (query [_] [:db/id :memo/title])

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
  (initLocalState [this]
    {:data-source (-> (uic/list-view-ds #js {:rowHasChanged #(not= % %2)})
                      (.cloneWithRows (into-array (-> this om/props :memo/children))))})

  (render [this]
    (let [{:keys [navigator] :as props} (om/props this)]
      (println "======== rendering scene" props)
      (uic/view nil
        (uic/text #js {:style #js {:fontSize 30 :fontWeight "100" :marginBottom 20 :marginTop 30 :textAlign "center"}} "Some text")
        (uic/touchable-highlight #js {:onPress (fn [] (.push navigator #js {:title "second" :component PlainScene}))}
          (uic/image #js {:source logo-img
                          :style  #js {:width 80 :height 80 :marginBottom 30 :alignSelf "center"}}))
        #_(uic/list-view #js {:dataSource (om/get-state this :data-source)
                              :style      #js {:flexGrow 1}
                              :renderRow  memo-row})))))

(def my-scene (om/factory MyScene))

(def PlainScene
  (js/React.createClass #js {:render #(my-scene (js-tl->clj (.-props (js-this))))}))

(defui AppRoot
  static uc/InitialAppState
  (initial-state [_ _] {:app/msg       "Hello Clojure in iOS and Android!"
                        :app/counter   1
                        :memo/children (->> (range)
                                            (map #(uc/initial-state MemoRow {:memo/title (str "Sample " %)}))
                                            (take 20)
                                            (vec))})

  static om/IQuery
  (query [this]
    [:ui/react-key :app/msg :app/counter {:memo/children (om/get-query MemoRow)}])

  Object
  (render [this]
    (let [{:app/keys [msg counter]
           :keys     [ui/react-key]} (om/props this)]
      (uic/navigator-ios #js {:key          react-key
                              :initialRoute #js {:title "bla" :component PlainScene}
                              :style        #js {:flex 1}}))))

(defonce RootNode (sup/root-node! 1))
(defonce app-root (om/factory RootNode))

(defn reload []
  (reset! state/app (uc/mount @state/app AppRoot 1)))

(defn init []
  (reload)
  (.registerComponent app-registry "RememberMobile" (fn [] app-root)))
