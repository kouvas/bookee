(ns bookee.actions
  (:require [clojure.walk :as walk]))

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
  [state event actions]
  (mapcat
    (fn [action]
      (or false
          (case (first action)
            :action/assoc-in
            [(into [:effect/assoc-in] (rest action))]

            :ui/toggle-details
            [(into [:effect/ui.toggle-details] (rest action))]

            :ui/stop-propagation
            [[:effect/stop-propagation event]]

            :ui/run-scroll-observer
            [[:effect/ui.run-scroll-observer]]

            :ui/cleanup-scroll-observer
            [[:effect/ui.cleanup-scroll-observer]]

            :ui/set-active-nav-link
            [(into [:effect/ui.set-active-nav-link] (rest action))]

            :map/init-leaflet
            [[:effect/map.init-leaflet]]

            :select-service
            [[:effect/select-service (second action)]]

            :select-team
            [[:effect/select-team (second action)]]

            :navigate/back
            [[:effect/navigate-back]]

            :navigate/forward
            [[:effect/navigate-forward]]

            :calendar/select-date
            [[:effect/calendar.select-date (second action)]]

            :calendar/prev-month
            [[:effect/calendar.prev-month]]

            :calendar/next-month
            [[:effect/calendar.next-month]]

            :calendar/select-time
            [[:effect/calendar.select-time]]

            (prn "Unknown action:" action))))
    actions))
