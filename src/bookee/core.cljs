(ns bookee.core
  (:require [bookee.ui :as ui]
            [clojure.walk :as walk]
            [replicant.dom :as r]))

;; Global application store/state
(defonce !store (atom {:ui {:active-section "services"}}))

(defonce !scroll-observer (atom nil))

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

            :run-scroll-observer
            [[:effect/run-scroll-observer]]

            :set-active-nav-link
            [(into [:effect/set-active-nav-link] (rest action))]

            (prn "Unknown action:" action))))
    actions))

(defn effect-execute!
  [store [effect & args]]
  (case effect
    :effect/toggle-details
    (swap! store update-in [:ui :details-visibility? (first args)] not)

    :effect/run-scroll-observer
    (when-not @!scroll-observer
      (let [observer (js/IntersectionObserver.
                       (fn [entries _]
                         ;; Find the section with the highest intersection ratio
                         (let [best-entry (->> entries
                                               array-seq
                                               (filter #(.-isIntersecting %))
                                               (sort-by #(.-intersectionRatio %) >)
                                               first)]
                           (when best-entry
                             (let [id        (.. best-entry -target -id)
                                   hash-link (str "#" id)]
                               (when (not= hash-link (get-in @!store [:ui :active-nav-link]))
                                 (swap! store assoc-in [:ui :active-nav-link] hash-link))))))
                       #js {:rootMargin "-30% 0px -55% 0px"})]
        (reset! !scroll-observer observer)
        (doseq [section (array-seq (.querySelectorAll js/document "section[id]"))]
          (.observe observer section))))

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
  (swap! !store assoc :initialised-at (.getTime (js/Date.)))

  )

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