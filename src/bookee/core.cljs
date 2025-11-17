(ns bookee.core
  (:require [bookee.ui :as ui]
            [bookee.logic :as logic]
            [bookee.events :as events]
            [replicant.dom :as r]
            [taoensso.telemere :as tel]))

(defonce !store (atom {:ui               {}
                       :navigation       logic/initial-navigation
                       :booking-details  {:selected-service     nil
                                          :selected-team-member nil
                                          :selected-date        nil}}))

(defn init! []
  (tel/set-min-level! :debug)
  (add-watch !store ::render
             (fn [_ _ _ new-state]
               (r/render
                 js/document.body
                 (ui/render-ui new-state))))
  (r/set-dispatch!
    (fn [replicant-event event-data]
      (events/dispatch-event! !store replicant-event event-data)))

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
  (ds/inspect "Bookee app state" @!store)

  )
