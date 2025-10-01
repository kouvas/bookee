(ns bookee.calendar
  (:require [tick.core :as t]
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
                       (let [day-num           (t/day-of-month date)
                             current-month     (t/month date)
                             is-current-month  (= (t/int current-month) month)
                             is-today          (= date today)
                             is-past           (t/< date today)
                             is-too-far-future (t/> date two-weeks-from-today)
                             is-available      (and is-current-month
                                                    (not is-past)
                                                    (not is-too-far-future))
                             classes           (cond-> []
                                                       (not is-current-month) (conj "other-month")
                                                       is-today (conj "today")
                                                       is-past (conj "past")
                                                       is-too-far-future (conj "too-far-future")
                                                       is-available (conj "available"))]
                         {:day            day-num
                          :date           date
                          :current-month? is-current-month
                          :today?         is-today
                          :past?          is-past
                          :unavailable    is-too-far-future
                          :available?     is-available
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
        month-name    (case month
                        1 "January"
                        2 "February"
                        3 "March"
                        4 "April"
                        5 "May"
                        6 "June"
                        7 "July"
                        8 "August"
                        9 "September"
                        10 "October"
                        11 "November"
                        12 "December"
                        "")
        can-go-prev?  (or (> year today-year)
                          (and (= year today-year) (> month today-month)))
        can-go-next?  (or (< year max-year)
                          (and (= year max-year) (< month max-month)))]
    (css/calendar-header
      [:div.month-year (str month-name " " year)]
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
  (let [selected?   (= (str date) (get-in state [:calendar :selected-date]))
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
  (css/calendar-container
    (calendar-header state)
    (days-header)
    (calendar-grid state)))

(defn main [state]
  (calendar state))
