(ns remember-mobile.components.common
  (:require [cljs.nodejs :as node]
            [cljs.spec :as s]
            [remember-mobile.util.common :as uc]
            [om.next :as om]))

(set! js/window.React (node/require "react"))
(def ReactNative (node/require "react-native"))

(defn create-element [rn-comp opts & children]
  (apply js/React.createElement rn-comp opts children))

(def ListView (.-ListView ReactNative))
(def DataSource (.-DataSource ListView))

(defn list-view-ds [opts]
  (DataSource. opts))

(s/def ::component any?)
(s/def ::row-has-changed? boolean?)

(s/fdef list-view-ds
        :args (s/cat :options (s/keys :opt [::row-has-changed?]))
        :ret ::component)

(def view (partial create-element (.-View ReactNative)))
(def text (partial create-element (.-Text ReactNative)))
(def image (partial create-element (.-Image ReactNative)))
(def touchable-highlight (partial create-element (.-TouchableHighlight ReactNative)))
(def raw-list-view (partial create-element ListView))
(def navigator (partial create-element (.-Navigator ReactNative)))
(def navigator-ios (partial create-element (.-NavigatorIOS ReactNative)))
(def navigator-bar (partial create-element (.. ReactNative -Navigator -NavigationBar)))

(defn debug
  ([v] (debug "" v))
  ([l v] (js/console.info l) (js/console.log v) v))

(om/defui ^:once OmListView
  Object
  (initLocalState [this]
    {:data-source (-> (list-view-ds #js {:rowHasChanged #(not= % %2)})
                      (.cloneWithRows (into-array (-> this om/props :rows))))})

  (render [this]
    (let [props (om/props this)]
      (raw-list-view (clj->js (merge {:dataSource (om/get-state this :data-source)}
                                     props))))))

(def list-view (om/factory OmListView))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))
