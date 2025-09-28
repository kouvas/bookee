(ns bookee.effects
  (:require [bookee.map :as m]
            [bookee.navigation :as nav]))

(defonce !scroll-observer (atom nil))

(defn effect-run-scroll-observer
  [store]
  (js/setTimeout
    (fn []
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
    50))

(defn effect-cleanup-scroll-observer
  [store]
  (when-let [observer @!scroll-observer]
    (.disconnect observer)
    (reset! !scroll-observer nil)))

(defn effect-execute!
  [store [effect & args]]
  (case effect
    :effect/assoc-in
    (apply swap! store assoc-in args)

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

    :effect/select-service
    (let [service-id    (first args)
          current-wmem  (:nav-wmem @store)
          nav-state     (nav/get-current-state current-wmem)
          selected-team (:selected-team @store)]
      (cond
        (and selected-team (= nav-state :->service))
        (let [new-wmem (nav/process-event current-wmem :select-service)]
          (swap! store assoc
                 :selected-service service-id
                 :nav-wmem new-wmem))

        (= nav-state :->team)
        (swap! store assoc
               :selected-service service-id
               :selected-team nil)

        :else
        (let [new-wmem (nav/process-event current-wmem :select-service)]
          (swap! store assoc
                 :selected-service service-id
                 :nav-wmem new-wmem))))

    :effect/select-team
    (let [team-id          (first args)
          current-wmem     (:nav-wmem @store)
          nav-state        (nav/get-current-state current-wmem)
          selected-service (:selected-service @store)]
      (cond
        (and selected-service (= nav-state :->team))
        (let [new-wmem (nav/process-event current-wmem :select-team)]
          (swap! store assoc
                 :selected-team team-id
                 :nav-wmem new-wmem))

        (= nav-state :->service)
        (swap! store assoc
               :selected-team team-id
               :selected-service nil)

        :else
        (let [new-wmem (nav/process-event current-wmem :select-team)]
          (swap! store assoc
                 :selected-team team-id
                 :nav-wmem new-wmem))))

    :effect/navigate-forward
    (let [{:keys [nav-wmem selected-service selected-team]} @store
          nav-state    (nav/get-current-state nav-wmem)
          can-proceed? (nav/can-go-forward? nav-state selected-service selected-team)]
      (when can-proceed?
        (let [new-wmem (nav/process-event nav-wmem :go-forward)]
          (swap! store assoc :nav-wmem new-wmem))))

    :effect/navigate-back
    (let [current-wmem (:nav-wmem @store)
          nav-state    (nav/get-current-state current-wmem)]
      (when (nav/can-go-back? nav-state)
        (let [new-wmem  (nav/process-event current-wmem :go-back)
              new-state (nav/get-current-state new-wmem)
              new-view  (nav/get-view new-state)]
          (if (= new-view :main)
            (swap! store assoc
                   :nav-wmem new-wmem
                   :selected-service nil
                   :selected-team nil)
            (swap! store assoc :nav-wmem new-wmem)))))

    nil))
