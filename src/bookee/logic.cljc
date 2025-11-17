(ns bookee.logic
  "Pure business logic functions - no side effects, fully testable")

;;; Navigation State
;;; Simple, transparent navigation instead of opaque statechart

(def initial-navigation
  {:current-view  :main
   :flow-type     nil  ; :service-first or :team-first
   :history       []})

(defn can-go-back?
  "Check if user can navigate back"
  [navigation]
  (not= (:current-view navigation) :main))

(defn can-go-forward?
  "Check if user can proceed to next step"
  [navigation booking-details]
  (and (#{:calendar} (:current-view navigation))
       (:selected-service booking-details)
       (:selected-team-member booking-details)))

(defn navigate-to
  "Navigate to a new view, tracking history"
  [navigation view]
  (-> navigation
      (update :history conj (:current-view navigation))
      (assoc :current-view view)))

(defn navigate-back
  "Navigate back to previous view"
  [navigation]
  (if-let [previous-view (last (:history navigation))]
    (-> navigation
        (assoc :current-view previous-view)
        (update :history #(vec (butlast %))))
    navigation))

(defn select-service-nav
  "Pure navigation logic when selecting a service"
  [navigation booking-details service-id]
  (let [current-view (:current-view navigation)
        has-team?    (:selected-team-member booking-details)]
    (cond
      ;; From main page, selecting service
      (= current-view :main)
      {:navigation (-> navigation
                       (assoc :flow-type :service-first)
                       (navigate-to :team))
       :booking    (assoc booking-details :selected-service service-id)}

      ;; Already selected team, now selecting service -> go to calendar
      (and (= current-view :services) has-team?)
      {:navigation (navigate-to navigation :calendar)
       :booking    (assoc booking-details :selected-service service-id)}

      ;; In team-first flow, selecting service
      (= current-view :services)
      {:navigation navigation
       :booking    (-> booking-details
                       (assoc :selected-service service-id)
                       (dissoc :selected-team-member))}

      :else
      {:navigation navigation
       :booking    booking-details})))

(defn select-team-nav
  "Pure navigation logic when selecting a team member"
  [navigation booking-details team-id]
  (let [current-view   (:current-view navigation)
        has-service?   (:selected-service booking-details)]
    (cond
      ;; From main page, selecting team
      (= current-view :main)
      {:navigation (-> navigation
                       (assoc :flow-type :team-first)
                       (navigate-to :services))
       :booking    (assoc booking-details :selected-team-member team-id)}

      ;; Already selected service, now selecting team -> go to calendar
      (and (= current-view :team) has-service?)
      {:navigation (navigate-to navigation :calendar)
       :booking    (assoc booking-details :selected-team-member team-id)}

      ;; In service-first flow, selecting team
      (= current-view :team)
      {:navigation navigation
       :booking    (-> booking-details
                       (assoc :selected-team-member team-id)
                       (dissoc :selected-service))}

      :else
      {:navigation navigation
       :booking    booking-details})))

(defn select-date-logic
  "Pure logic for selecting a date"
  [booking-details date-str]
  (assoc booking-details :selected-date date-str))

(defn select-time-logic
  "Pure logic for selecting a time - also advances to verification"
  [navigation booking-details time-str]
  {:navigation (navigate-to navigation :verification)
   :booking    (assoc booking-details :selected-time time-str)})

(defn reset-booking
  "Reset booking to initial state"
  []
  {:navigation initial-navigation
   :booking    {:selected-service     nil
                :selected-team-member nil
                :selected-date        nil
                :selected-time        nil}
   :verification {:customer-name  nil
                  :customer-email nil}
   :ui {:show-confirmation-modal? false}})

(defn navigate-back-logic
  "Pure logic for back navigation with state cleanup"
  [navigation booking-details]
  (let [new-nav (navigate-back navigation)]
    (if (= (:current-view new-nav) :main)
      ;; Going back to main - reset everything
      (merge (reset-booking) {:navigation new-nav})
      ;; Just navigation
      {:navigation new-nav
       :booking    booking-details})))

(defn navigate-forward-logic
  "Pure logic for forward navigation"
  [navigation booking-details]
  (if (can-go-forward? navigation booking-details)
    {:navigation (navigate-to navigation :verification)
     :booking    booking-details}
    {:navigation navigation
     :booking    booking-details}))

(defn calendar-prev-month-logic
  "Calculate previous month"
  [calendar today-year today-month]
  (let [current-year  (:year calendar)
        current-month (:month calendar)]
    (if current-month
      (if (= current-month 1)
        {:year (dec current-year) :month 12}
        {:year current-year :month (dec current-month)})
      (if (= today-month 1)
        {:year (dec today-year) :month 12}
        {:year today-year :month (dec today-month)}))))

(defn calendar-next-month-logic
  "Calculate next month"
  [calendar today-year today-month]
  (let [current-year  (:year calendar)
        current-month (:month calendar)]
    (if current-month
      (if (= current-month 12)
        {:year (inc current-year) :month 1}
        {:year current-year :month (inc current-month)})
      (if (= today-month 12)
        {:year (inc today-year) :month 1}
        {:year today-year :month (inc today-month)}))))
