(ns bookee.calendar
  (:require [taoensso.telemere :as tel]
            [tick.core :as t]
            [bookee.data :as data]
            [bookee.css :as css]
            [bookee.icons :as icons]
            [clojure.string :as str]))

(def day-initials ["M" "T" "W" "T" "F" "S" "S"])

(defn get-month-data [year month]
  (let [first-day            (t/date (str year "-" (if (< month 10) (str "0" month) month) "-01"))
        first-day-weekday    (keyword (str (t/day-of-week first-day)))
        days-to-subtract     (case first-day-weekday
                               :MONDAY 0
                               :TUESDAY 1
                               :WEDNESDAY 2
                               :THURSDAY 3
                               :FRIDAY 4
                               :SATURDAY 5
                               :SUNDAY 6)
        start-date           (t/<< first-day (t/new-period days-to-subtract :days))
        today                (t/today)
        two-weeks-from-today (t/>> today (t/new-period 14 :days))]

    (->> (range 42)
         (map #(t/>> start-date (t/new-period % :days)))
         (partition 7)
         (map (fn [week]
                (map (fn [date]
                       (let [day-num        (t/day-of-month date)
                             current-month  (t/month date)
                             current-month? (= (t/int current-month) month)
                             today?         (= date today)
                             past?          (t/< date today)
                             sunday?        (= "SUNDAY" (t/format (t/day-of-week date)))
                             unavailable?   (or sunday? (t/> date two-weeks-from-today))
                             available?     (and current-month?
                                                 (not past?)
                                                 (not sunday?)
                                                 (not unavailable?))
                             classes        (cond-> []
                                                    (not current-month?) (conj "other-month")
                                                    today? (conj "today")
                                                    past? (conj "past")
                                                    unavailable? (conj "unavailable")
                                                    available? (conj "available"))]
                         {:day            day-num
                          :date           date
                          :current-month? current-month?
                          :today?         today?
                          :past?          past?
                          :unavailable    unavailable?
                          :available?     available?
                          :classes        classes}))
                     week))))))



(defn calendar-header [state]
  (let [today         (t/today)
        today-year    (t/int (t/year today))
        today-month   (t/int (t/month today))
        year          (get-in state [:calendar :year] today-year)
        month         (get-in state [:calendar :month] today-month)
        two-weeks-out (t/>> today (t/new-period 14 :days))
        max-year      (t/int (t/year two-weeks-out))
        max-month     (t/int (t/month two-weeks-out))
        day-name      (str/capitalize (t/format (t/day-of-week t/today)))
        month-name    (str/capitalize (t/format (t/month)))
        can-go-prev?  (or (> year today-year)
                          (and (= year today-year) (> month today-month)))
        can-go-next?  (or (< year max-year)
                          (and (= year max-year) (< month max-month)))]
    (css/calendar-header
      [:div.month-year (str day-name ", " month-name " " (t/int (t/month (t/today))))]
      [:div.nav-buttons
       [:button.nav-button
        {:disabled (not can-go-prev?)
         :on       {:click [[:calendar/prev-month]]}}
        icons/caret-left-icon]
       [:button.nav-button
        {:disabled (not can-go-next?)
         :on       {:click [[:calendar/next-month]]}}
        icons/caret-right-icon]])))

(defn days-header []
  (css/calendar-days-header
    (for [day day-initials]
      [:div.day-initial day])))


(defn day-cell [{:keys [day classes date available?]} state]
  (let [selected?   (= (str date) (get-in state [:booking-details :selected-date]))
        all-classes (cond-> classes
                            selected? (conj "selected"))]
    (css/calendar-day-cell
      {:class all-classes
       :on    (when available?
                {:click [[:calendar/select-date (str date)]]})}
      day)))

(defn calendar-grid [state]
  (let [year       (get-in state [:calendar :year] (t/int (t/year (t/today))))
        month      (get-in state [:calendar :month] (t/int (t/month (t/today))))
        month-data (get-month-data year month)]
    (css/calendar-grid
      (for [week month-data
            day  week]
        (day-cell day state)))))

(defn calendar [state]
  (css/calendar-date
    (calendar-header state)
    (days-header)
    (calendar-grid state)))

(defn time-slots
  [state]
  (let [selected-date  (get-in state [:booking-details :selected-date])
        selected-time  (get-in state [:booking-details :selected-time])
        date           (when selected-date (t/date selected-date)) ;; fixme pass t/date already?
        day-of-week    (when date (str/capitalize (str (t/day-of-week date))))
        day-number     (when date (str (t/day-of-month date)))
        month-name     (when date (str/capitalize (t/format (t/month t/date))))
        formatted-date (when date (str day-of-week ", " month-name " " day-number))
        slots          (when date (data/time-slots date))]
    (css/calendar-time-slots
      [:div.date-label (or formatted-date "Select a time")]
      (if (and date (seq slots))
        [:div.slots
         (for [{:keys [time available?]} slots]
           (let [selected? (= time selected-time)]
             (css/time-slot-button
               {:key   time
                :class (when selected? "selected")
                :on    (when available?
                         {:click [[:calendar/select-time]]})}
               time)))]
        [:div.no-slots
         (if date
           "No available slots"
           "Please select a date")]))))

(defn main [state]
  (css/calendar-date-time
    (calendar state)
    (time-slots state)))
