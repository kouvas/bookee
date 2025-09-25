(ns bookee.effects
  (:require [bookee.map :as m]))

(defonce !scroll-observer (atom nil))

(defn effect-run-scroll-observer
  [store]
  (when-not @!scroll-observer
    (let [observer (js/IntersectionObserver.
                     (fn [entries _]
                       (let [best-entry (->> entries
                                             array-seq
                                             (filter #(.-isIntersecting %))
                                             (sort-by #(.-intersectionRatio %) >)
                                             first)]
                         (when best-entry
                           (let [id        (.. best-entry -target -id)
                                 hash-link (str "#" id)]
                             (when (not= hash-link (get-in @store [:ui :active-nav-link]))
                               (swap! store assoc-in [:ui :active-nav-link] hash-link))))))
                     #js {:rootMargin "-30% 0px -55% 0px"})]
      (reset! !scroll-observer observer)
      (doseq [section (array-seq (.querySelectorAll js/document "section[id]"))]
        (.observe observer section)))))

(defn effect-cleanup-scroll-observer
  [store]
  (when-let [observer @!scroll-observer]
    (.disconnect observer)
    (reset! !scroll-observer nil)))

(defn effect-execute!
  [store [effect & args]]
  (case effect
    :effect/ui.toggle-details
    (swap! store update-in [:ui :details-visibility? (first args)] not)

    :effect/ui.run-scroll-observer
    (effect-run-scroll-observer store)

    :effect/ui.cleanup-scroll-observer
    (effect-cleanup-scroll-observer store)

    :effect/ui.set-active-nav-link
    (swap! store assoc-in [:ui :active-nav-link] (first args))

    :effect/map.init-leaflet
    (m/init-leaflet! store)

    nil))
