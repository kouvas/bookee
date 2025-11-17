(ns bookee.events
  "Event handlers - coordinate between pure logic and side effects"
  (:require [bookee.logic :as logic]
            [bookee.map :as m]
            [taoensso.telemere :as tel]))

;;; Value Interpolation
;;; Extract values from DOM events

(defn interpolate-value
  "Extract a value from an event if needed"
  [dom-event value]
  (case value
    :event.target/value
    (some-> dom-event .-target .-value)

    ;; Default: return value as-is
    value))

(defn interpolate-values
  "Recursively interpolate values in event data"
  [dom-event event-data]
  (cond
    (vector? event-data)
    (mapv #(interpolate-values dom-event %) event-data)

    (keyword? event-data)
    (interpolate-value dom-event event-data)

    :else
    event-data))

;;; Side Effects
;;; These are the impure parts - interact with the world

(defonce !scroll-observer (atom nil))

(defn run-scroll-observer!
  "Side effect: Setup intersection observer"
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

(defn cleanup-scroll-observer!
  "Side effect: Cleanup intersection observer"
  []
  (when-let [observer @!scroll-observer]
    (.disconnect observer)
    (reset! !scroll-observer nil)))

;;; Event Handlers
;;; These coordinate pure logic with side effects

(defn handle-event!
  "Main event handler - dispatches to specific handlers"
  [store dom-event [event-type & args]]
  (tel/spy! :debug [event-type args])

  (case event-type
    ;; Simple state updates
    :action/assoc-in
    (apply swap! store assoc-in args)

    ;; UI events
    :ui/toggle-details
    (swap! store update-in [:ui :details-visibility? (first args)] not)

    :ui/stop-propagation
    (when dom-event
      (.stopPropagation dom-event))

    :ui/run-scroll-observer
    (run-scroll-observer! store)

    :ui/cleanup-scroll-observer
    (cleanup-scroll-observer!)

    :ui/set-active-nav-link
    (swap! store assoc-in [:ui :active-nav-link] (first args))

    ;; Map events
    :map/init-leaflet
    (m/init-leaflet! store)

    :map/cleanup-leaflet
    (m/destroy-leaflet! store)

    ;; Navigation and booking events
    :select-service
    (let [service-id (first args)
          state      @store
          result     (logic/select-service-nav
                       (:navigation state)
                       (:booking-details state)
                       service-id)]
      (swap! store assoc
             :navigation (:navigation result)
             :booking-details (:booking result)))

    :select-team
    (let [team-id (first args)
          state   @store
          result  (logic/select-team-nav
                    (:navigation state)
                    (:booking-details state)
                    team-id)]
      (swap! store assoc
             :navigation (:navigation result)
             :booking-details (:booking result)))

    :navigate/back
    (let [state  @store
          result (logic/navigate-back-logic
                   (:navigation state)
                   (:booking-details state))]
      (swap! store merge result))

    :navigate/forward
    (let [state  @store
          result (logic/navigate-forward-logic
                   (:navigation state)
                   (:booking-details state))]
      (swap! store assoc
             :navigation (:navigation result)
             :booking-details (:booking result)))

    ;; Calendar events
    :calendar/select-date
    (let [date-str (first args)]
      (swap! store update :booking-details
             logic/select-date-logic date-str))

    :calendar/prev-month
    (let [today        (js/Date.)
          today-year   (.getFullYear today)
          today-month  (inc (.getMonth today))
          calendar     (:calendar @store)
          new-calendar (logic/calendar-prev-month-logic
                         calendar today-year today-month)]
      (swap! store assoc :calendar new-calendar))

    :calendar/next-month
    (let [today        (js/Date.)
          today-year   (.getFullYear today)
          today-month  (inc (.getMonth today))
          calendar     (:calendar @store)
          new-calendar (logic/calendar-next-month-logic
                         calendar today-year today-month)]
      (swap! store assoc :calendar new-calendar))

    :calendar/select-time
    (let [time-str (first args)
          state    @store
          result   (logic/select-time-logic
                     (:navigation state)
                     (:booking-details state)
                     time-str)]
      (swap! store assoc
             :navigation (:navigation result)
             :booking-details (:booking result)))

    ;; Verification events
    :verification/update-name
    (swap! store assoc-in [:verification :customer-name] (first args))

    :verification/update-email
    (swap! store assoc-in [:verification :customer-email] (first args))

    :verification/confirm-booking
    (swap! store assoc-in [:ui :show-confirmation-modal?] true)

    :verification/reset-booking
    (swap! store merge (logic/reset-booking))

    :verification/close-modal
    (swap! store merge (logic/reset-booking))

    ;; Unknown event
    (tel/log! :warn
              {:msg   "Unknown event type"
               :event event-type
               :args  args})))

(defn dispatch-event!
  "Dispatch a replicant event - entry point from UI"
  [store {:replicant/keys [dom-event trigger life-cycle]} event-data]
  (tel/spy! :debug [life-cycle trigger])

  ;; Interpolate any special values in the event data
  (let [interpolated-data (interpolate-values dom-event event-data)]
    ;; Handle each event in the list
    (doseq [event interpolated-data]
      (handle-event! store dom-event event))))
