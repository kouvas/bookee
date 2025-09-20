(ns bookee.core
  (:require [bookee.ui :as ui]
            [clojure.walk :as walk]
            [replicant.dom :as r]))

;; Global application store/state
(defonce !store (atom {:ui {}}))

(defn render-ui
  [new-state]
  (ui/main new-state))

(defn interpolate-actions
  [dom-event event-data]
  (walk/postwalk
    (fn [a]
      (case a
        :event.target/value
        (some-> dom-event .-target .-value)
        a))
    event-data))

(defn action->effect
  [state dom-event actions]
  (mapcat
    (fn [action]
      (or false
          (case (first action)
            :toggle-details
            [(into [:effect/toggle-details] (rest action))]
            (prn "Unknown action:" action))))
    actions))

(defn effect-execute!
  [store [effect & args]]
  (case effect
    :effect/toggle-details
    (swap! store update-in [:ui :details-visibility? (first args)] not)
    nil))

(defn init! []
  (add-watch !store ::render
             (fn [_ _ _ new-state]
               (r/render
                 js/document.body
                 (render-ui new-state))))
  (r/set-dispatch!
    (fn [{:replicant/keys [dom-event trigger life-cycle]} event-data]
      (js/console.log "la dom: " dom-event "le trigger: " trigger "le lifecycle: " life-cycle "la event: " event-data)
      (->> (interpolate-actions dom-event event-data)
           (action->effect @!store dom-event)
           (run! #(effect-execute! !store %)))))

  ;; Trigger the initial render
  (swap! !store assoc :initialised-at (.getTime (js/Date.))))

(defn main []
  (init!)
  (println "Loaded!"))

(defn ^:dev/after-load reloaded []
  (init!)
  (println "Reloaded!"))

(comment
  @!store
  (require '[dataspex.core :as ds])
  (ds/inspect "Bookee app state" @!store))