(ns remember-mobile.state
  (:require [om.next :as om]
            [re-natal.support :as sup]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as m]))

(defonce app-state (atom {:app/msg "Hello Clojure in iOS and Android!"}))

(defmulti read om/dispatch)
(defmethod read :default
  [{:keys [state]} k _]
  (let [st @state]
    (if-let [[_ v] (find st k)]
      {:value v}
      {:value :not-found})))

(defonce reconciler
  (om/reconciler
    {:state        app-state
     :parser       (om/parser {:read read})
     :root-render  sup/root-render
     :root-unmount sup/root-unmount}))

(defmethod m/mutate 'app/increment [{:keys [state]} _ _]
  {:action (fn []
             (swap! state update :app/counter inc))})

(defonce app (atom (uc/new-untangled-client :reconciler-options {:root-render  sup/root-render
                                                             :root-unmount sup/root-unmount})))
