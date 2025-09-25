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
            :ui/toggle-details
            [(into [:effect/ui.toggle-details] (rest action))]

            :ui/run-scroll-observer
            [[:effect/ui.run-scroll-observer]]

            :ui/cleanup-scroll-observer
            [[:effect/ui.cleanup-scroll-observer]]

            :ui/set-active-nav-link
            [(into [:effect/ui.set-active-nav-link] (rest action))]

            (prn "Unknown action:" action))))
    actions))
