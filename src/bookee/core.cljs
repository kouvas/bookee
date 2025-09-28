(ns bookee.core
  (:require [bookee.ui :as ui]
            [bookee.actions :as actions]
            [bookee.effects :as effects]
            [replicant.dom :as r]))

;; Global application store/state
(defonce !store (atom {:ui {}}))

(defn render-ui
  [new-state]
  (ui/main new-state))

(defn init! []
  (add-watch !store ::render
             (fn [_ _ _ new-state]
               (r/render
                 js/document.body
                 (render-ui new-state))))
  (r/set-dispatch!
    (fn [{:replicant/keys [dom-event trigger life-cycle]} event-data]
      (when js/goog.DEBUG
        (js/console.log "DOM-EVENT: " dom-event "TRIGGER: " trigger "LIFECYCLE: " life-cycle "EVENT-DATA: " event-data))
      (->> (actions/interpolate-actions dom-event event-data)
           (actions/action->effect @!store dom-event)
           (run! #(effects/effect-execute! !store %)))))

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